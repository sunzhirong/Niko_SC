package cn.rongcloud.im.niko.im.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import androidx.lifecycle.Observer;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.SealApp;
import cn.rongcloud.im.niko.common.ThreadManager;
import cn.rongcloud.im.niko.db.DbManager;
import cn.rongcloud.im.niko.db.dao.ScLikeDao;
import cn.rongcloud.im.niko.db.model.ScLikeDetail;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.im.message.ScLikeMessage;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.AutoLinkTextView;
import io.rong.imkit.widget.ILinkClickListener;
import io.rong.imkit.widget.LinkTextViewMovementMethod;
import io.rong.imkit.widget.provider.TextMessageItemProvider;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

@ProviderTag( messageContent = TextMessage.class  )
public class MyTextMessageItemProvider extends TextMessageItemProvider{

    private boolean isLike;

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_destruct_text_message, (ViewGroup)null);
        MyTextMessageItemProvider.ViewHolder holder = new MyTextMessageItemProvider.ViewHolder();
        holder.message = (AutoLinkTextView)view.findViewById(R.id.text);
        holder.unRead = (TextView)view.findViewById(R.id.tv_unread);
        holder.sendFire = (FrameLayout)view.findViewById(R.id.fl_send_fire);
        holder.receiverFire = (FrameLayout)view.findViewById(R.id.fl_receiver_fire);
        holder.receiverFireImg = (ImageView)view.findViewById(R.id.iv_receiver_fire);
        holder.receiverFireText = (TextView)view.findViewById(R.id.tv_receiver_fire);
        holder.leftLikeImg = (ImageView)view.findViewById(R.id.iv_like_left);
        holder.rightLikeImg = (ImageView)view.findViewById(R.id.iv_like_right);
        view.setTag(holder);
        return view;
    }

    public Spannable getContentSummary(TextMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, TextMessage data) {
        if (data == null) {
            return null;
        } else if (data.isDestruct()) {
            //阅后即焚 不需要
            return null;
        } else {
            String content = data.getContent();
            if (content != null) {
                if (content.length() > 100) {
                    content = content.substring(0, 100);
                }

                return new SpannableString(AndroidEmoji.ensure(content));
            } else {
                return null;
            }
        }
    }

    public void onItemClick(View view, int position, TextMessage content, UIMessage message) {
        MyTextMessageItemProvider.ViewHolder holder = (MyTextMessageItemProvider.ViewHolder)view.getTag();
        if (content != null && content.isDestruct() && message.getMessage().getReadTime() <= 0L) {
            holder.unRead.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);
            holder.receiverFireText.setVisibility(View.VISIBLE);
            holder.receiverFireImg.setVisibility(View.GONE);
            this.processTextView(view, position, content, message, holder.message);
            DestructManager.getInstance().startDestruct(message.getMessage());
        }

    }

    public void bindView(View v, int position, TextMessage content, UIMessage data) {
        MyTextMessageItemProvider.ViewHolder holder = (MyTextMessageItemProvider.ViewHolder)v.getTag();
        holder.receiverFire.setTag(data.getUId());
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            //我发送的
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_right_new);
            holder.message.setTextColor(Color.parseColor("#0A0A0B"));
            holder.leftLikeImg.setVisibility(View.VISIBLE);
            holder.rightLikeImg.setVisibility(View.GONE);
        } else {
            //我接收的
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_left_new);
            holder.message.setTextColor(Color.parseColor("#ffffff"));
            holder.leftLikeImg.setVisibility(View.GONE);
            holder.rightLikeImg.setVisibility(View.VISIBLE);
        }

        holder.sendFire.setVisibility(View.GONE);
        holder.receiverFire.setVisibility(View.GONE);
        holder.unRead.setVisibility(View.GONE);
        holder.message.setVisibility(View.VISIBLE);
        AutoLinkTextView textView = holder.message;
        this.processTextView(v, position, content, data, textView);
        this.processLike(v,data, holder);
    }

    private void processLike(View v, UIMessage data, ViewHolder holder) {
        Message message = data.getMessage();
        holder.leftLikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLikeMsg(message,holder);
            }
        });

        holder.rightLikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLikeMsg(message,holder);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                String uId = data.getUId();
                Log.e("db","查询Id details = "+ uId);
                ScLikeDao scLikeDao = DbManager.getInstance(SealApp.getApplication()).getScLikeDao();
                ScLikeDetail scLikeDetail = scLikeDao.getDetailsByIdOneUser(uId,data.getSenderUserId());
                Log.e("db","查询到的数据1 details = "+ JSON.toJSONString(scLikeDetail));
                ThreadManager.getInstance().runOnWorkThread(() -> {
                    if(scLikeDetail!=null){
                        isLike = !scLikeDetail.getDescription().equals("0");
                        holder.leftLikeImg.setSelected(isLike);
                        holder.rightLikeImg.setSelected(isLike);
                    }else {
                        isLike = false;
                        holder.leftLikeImg.setSelected(isLike);
                        holder.rightLikeImg.setSelected(isLike);
                    }
                });

//                try {
//                    Thread.sleep(50);
//
////                    if (v.getHandler() != null ) {
////                        v.getHandler().postDelayed(new Runnable() {
////                            public void run() {
////                                if(detailsById!=null&&detailsById.size()!=0){
////                                    ScLikeDetail scLikeDetail = detailsById.get(0);
////                                    isLike = !scLikeDetail.getDescription().equals("0");
////                                    holder.leftLikeImg.setSelected(isLike);
////                                    holder.rightLikeImg.setSelected(isLike);
////                                }else {
////                                    isLike = false;
////                                    holder.leftLikeImg.setSelected(isLike);
////                                    holder.rightLikeImg.setSelected(isLike);
////                                }
////                            }
////                        }, 300L);//由于会发送一条此版本不能查看消息的提示，会先刷新ui 导致插入数据在刷新之后 所以先延迟刷新
////
////                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }).start();


    }

    private void sendLikeMsg(Message message,ViewHolder holder) {
        String targetId = message.getTargetId();//接收方ID
        String senderUserId = message.getSenderUserId();//发送方ID
        Conversation.ConversationType conversationType = message.getConversationType();//私聊或者群聊
        String uId = message.getUId();

        ScLikeMessage messageContent = ScLikeMessage.obtain();
        Message likeMsg = Message.obtain(targetId, conversationType, messageContent);
        if(conversationType == Conversation.ConversationType.PRIVATE){
            messageContent.setUserId(targetId);
        }else if(conversationType == Conversation.ConversationType.GROUP){
            messageContent.setGroupId(targetId);
        }
        messageContent.setContent("");
        messageContent.setExtra("");
        messageContent.setMessageUUID("");
        messageContent.setTargetMessageUUID(uId);
        messageContent.setSenderAvatar(GlideImageLoaderUtil.getScString("_aa_UserIcon.jpg"));
        messageContent.setSenderUserId(senderUserId);
        messageContent.setTextDescription(isLike?"0":"1");
        RongIM.getInstance().sendMessage(likeMsg, "", "", new IRongCallback.ISendMessageCallback() {
            /**
             * 消息发送前回调, 回调时消息已存储数据库
             * @param message 已存库的消息体
             */
            @Override
            public void onAttached(Message message) {
//                ToastUtils.showToast("点赞onAttached成功");
//                RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
//                    @Override
//                    public void onSuccess(Boolean aBoolean) {
//                        ToastUtils.showToast("deleteMessages"+aBoolean);
//                    }
//
//                    @Override
//                    public void onError(RongIMClient.ErrorCode errorCode) {
//
//                    }
//                });
            }
            /**
             * 消息发送成功。
             * @param message 发送成功后的消息体
             */
            @Override
            public void onSuccess(Message message) {
                ToastUtils.showToast("点赞发送成功");
                isLike = !isLike;
                holder.leftLikeImg.setSelected(isLike);
                holder.rightLikeImg.setSelected(isLike);


                ThreadManager.getInstance().runOnWorkThread(new Runnable() {
                    @Override
                    public void run() {

                        ScLikeDao scLikeDao = DbManager.getInstance(SealApp.getApplication()).getScLikeDao();
                        ScLikeDetail scLikeDetail = scLikeDao.getDetailsByIdOneUser(uId,senderUserId);
                        if(scLikeDetail==null){
                            scLikeDetail =  new ScLikeDetail();
                        }
//                        DbManager instance = DbManager.getInstance(SealApp.getApplication());
//                        ScLikeDao scLikeDao = instance.getScLikeDao();
//                        ScLikeDetail scLikeDetail = new ScLikeDetail();
                        scLikeDetail.setCreatedTime(new Date());
                        scLikeDetail.setDescription(isLike?"0":"1");
                        if(conversationType == Conversation.ConversationType.PRIVATE){
                            scLikeDetail.setUserId(targetId);
                        }else if(conversationType == Conversation.ConversationType.GROUP){
                            scLikeDetail.setGroupId(targetId);
                        }
                        scLikeDetail.setSenderAvatar(GlideImageLoaderUtil.getScString("_aa_UserIcon.jpg"));
                        scLikeDetail.setTargetMessageUuid(uId);
                        scLikeDetail.setMessageUuid(message.getUId());
                        scLikeDetail.setSenderUserId(senderUserId);
                        scLikeDao.insert(scLikeDetail);
                        Log.e("db","插入数据 details = "+ JSON.toJSONString(scLikeDetail));
                    }
                });
            }

            /**
             * 消息发送失败
             * @param message   发送失败的消息体
             * @param errorCode 具体的错误
             */
            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtils.showToast("点赞发送失败");
            }
        });


    }

    private void processTextView(final View v, int position, TextMessage content, final UIMessage data, final AutoLinkTextView pTextView) {
        if (data.getTextMessageContent() != null) {
            int len = data.getTextMessageContent().length();
            if (v.getHandler() != null && len > 500) {
                v.getHandler().postDelayed(new Runnable() {
                    public void run() {
                        pTextView.setText(data.getTextMessageContent());
                    }
                }, 50L);
            } else {
                pTextView.setText(data.getTextMessageContent());
            }
        }

        pTextView.setMovementMethod(new LinkTextViewMovementMethod(new ILinkClickListener() {
            public boolean onLinkClick(String link) {
                RongIM.ConversationBehaviorListener listener = RongContext.getInstance().getConversationBehaviorListener();
                RongIM.ConversationClickListener clickListener = RongContext.getInstance().getConversationClickListener();
                boolean result = false;
                if (listener != null) {
                    result = listener.onMessageLinkClick(v.getContext(), link);
                } else if (clickListener != null) {
                    result = clickListener.onMessageLinkClick(v.getContext(), link, data.getMessage());
                }

                if (listener == null && clickListener == null || !result) {
                    String str = link.toLowerCase();
                    if (str.startsWith("http") || str.startsWith("https")) {
                        Intent intent = new Intent("io.rong.imkit.intent.action.webview");
                        intent.setPackage(v.getContext().getPackageName());
                        intent.putExtra("url", link);
                        v.getContext().startActivity(intent);
                        result = true;
                    }
                }

                return result;
            }
        }));
        pTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                v.performClick();
            }
        });
        pTextView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                return v.performLongClick();
            }
        });
        pTextView.stripUnderlines();
    }


    private static class ViewHolder {
        AutoLinkTextView message;
        TextView unRead;
        FrameLayout sendFire;
        FrameLayout receiverFire;
        ImageView receiverFireImg;
        TextView receiverFireText;
        ImageView leftLikeImg;
        ImageView rightLikeImg;
        private ViewHolder() {
        }
    }
}