package cn.rongcloud.im.niko.im.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import androidx.lifecycle.Observer;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.SealApp;
import cn.rongcloud.im.niko.common.ThreadManager;
import cn.rongcloud.im.niko.db.DbManager;
import cn.rongcloud.im.niko.db.dao.ScLikeDao;
import cn.rongcloud.im.niko.db.model.ScLikeDetail;
import cn.rongcloud.im.niko.db.model.ScMyLike;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.im.message.ScLikeMessage;
import cn.rongcloud.im.niko.ui.widget.MyAutoLinkTextView;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.ILinkClickListener;
import io.rong.imkit.widget.LinkTextViewMovementMethod;
import io.rong.imkit.widget.provider.TextMessageItemProvider;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;

@ProviderTag(messageContent = TextMessage.class,showSummaryWithName=false)
public class MyTextMessageItemProvider extends TextMessageItemProvider {

    private boolean isLike = false;

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_destruct_text_message, (ViewGroup) null);
        MyTextMessageItemProvider.ViewHolder holder = new MyTextMessageItemProvider.ViewHolder();
        holder.message = (MyAutoLinkTextView) view.findViewById(R.id.text);
        holder.unRead = (TextView) view.findViewById(R.id.tv_unread);
        holder.sendFire = (FrameLayout) view.findViewById(R.id.fl_send_fire);
        holder.receiverFire = (FrameLayout) view.findViewById(R.id.fl_receiver_fire);
        holder.receiverFireImg = (ImageView) view.findViewById(R.id.iv_receiver_fire);
        holder.receiverFireText = (TextView) view.findViewById(R.id.tv_receiver_fire);
        holder.leftLikeImg = (ImageView) view.findViewById(R.id.iv_like_left);
        holder.rightLikeImg = (ImageView) view.findViewById(R.id.iv_like_right);
        holder.rightFramlayout = (FrameLayout) view.findViewById(R.id.fl_right);
        holder.leftFramlayout = (FrameLayout) view.findViewById(R.id.fl_left);
        holder.leftText = (TextView) view.findViewById(R.id.tv_left);
        holder.rightText = (TextView) view.findViewById(R.id.tv_right);
        holder.leftLl = (LinearLayout) view.findViewById(R.id.ll_left);
        holder.rightLl = (LinearLayout) view.findViewById(R.id.ll_right);

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
        MyTextMessageItemProvider.ViewHolder holder = (MyTextMessageItemProvider.ViewHolder) view.getTag();
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
        MyTextMessageItemProvider.ViewHolder holder = (MyTextMessageItemProvider.ViewHolder) v.getTag();
        holder.receiverFire.setTag(data.getUId());
        if (data.getConversationType() == Conversation.ConversationType.GROUP) {
            holder.message.setNeedMin(true);
            holder.leftLl.setVisibility(View.VISIBLE);
            holder.rightLl.setVisibility(View.VISIBLE);
        } else {
            holder.message.setNeedMin(false);
            holder.leftLl.setVisibility(View.GONE);
            holder.rightLl.setVisibility(View.GONE);
        }
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            //我发送的
            holder.message.setGravity(Gravity.RIGHT);
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_right_new);
            holder.message.setTextColor(Color.parseColor("#0A0A0B"));
            holder.message.setLinkTextColor(Color.parseColor("#0A0A0B"));
            holder.leftLikeImg.setVisibility(View.VISIBLE);
            holder.rightLikeImg.setVisibility(View.GONE);
            holder.leftText.setVisibility(View.GONE);
            holder.rightText.setVisibility(View.VISIBLE);
        } else {
            //我接收的
            holder.message.setGravity(Gravity.LEFT);
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_left_new);
            holder.message.setTextColor(Color.parseColor("#ffffff"));
            holder.message.setLinkTextColor(Color.parseColor("#ffffff"));
            holder.leftLikeImg.setVisibility(View.GONE);
            holder.rightLikeImg.setVisibility(View.VISIBLE);
            holder.leftText.setVisibility(View.VISIBLE);
            holder.rightText.setVisibility(View.GONE);
        }

        holder.sendFire.setVisibility(View.GONE);
        holder.receiverFire.setVisibility(View.GONE);
        holder.unRead.setVisibility(View.GONE);
        holder.message.setVisibility(View.VISIBLE);
        MyAutoLinkTextView textView = holder.message;
        this.processTextView(v, position, content, data, textView);
        this.processLike(v, data, holder);
    }

    private void processLike(View v, UIMessage data, ViewHolder holder) {
        Message message = data.getMessage();

        holder.leftLikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLikeMsg(message, holder);
            }
        });

        holder.rightLikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLikeMsg(message, holder);
            }
        });

        //查询点赞数据
        ThreadManager.getInstance().runOnWorkThread(() ->{
            String uId = data.getUId();
            Log.e("db", "查询Id details = " + uId);
            ScLikeDao scLikeDao = DbManager.getInstance(SealApp.getApplication()).getScLikeDao();
            List<ScLikeDetail> detailList = scLikeDao.getDetailsById(uId);
            Log.e("db", "查询到的数据1 details = " + JSON.toJSONString(detailList));
            ThreadManager.getInstance().runOnUIThread(() -> {
                //处理小红心
                //优先过滤0的数据
                Iterator<ScLikeDetail> iterator = detailList.iterator();
                while (iterator.hasNext()) {
                    ScLikeDetail next = iterator.next();
                    if ("0".equals(next.getDescription())) {
                        iterator.remove();//使用迭代器的删除方法删除
                    }
                }
                //处理头像
                if (data.getConversationType() == Conversation.ConversationType.GROUP){
                    holder.rightFramlayout.removeAllViews();
                    holder.leftFramlayout.removeAllViews();
                    int count = detailList.size();
                    int size = 3;
                    if(count<3){
                        size = count;
                    }

                    if(count>3){
                        holder.leftText.setVisibility(View.VISIBLE);
                        holder.rightText.setVisibility(View.VISIBLE);
                        holder.leftText.setText(count+"");
                        holder.rightText.setText(count+"");
                    }else {
                        holder.leftText.setVisibility(View.GONE);
                        holder.rightText.setVisibility(View.GONE);
                    }

                    int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, v.getContext().getResources()
                            .getDisplayMetrics());

                    int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, v.getContext().getResources()
                            .getDisplayMetrics());
                    for (int i = 0; i < size; i++) {
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, width);
                        ImageView imageView = new ImageView(v.getContext());
                        GlideImageLoaderUtil.loadCircleImage(SealApp.getApplication(),imageView,detailList.get(i).getSenderAvatar());
                        layoutParams.leftMargin = (size - 1 - i) * margin;
                        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
                            holder.rightFramlayout.addView(imageView, layoutParams);
                        } else {
                            holder.leftFramlayout.addView(imageView, layoutParams);
                        }
                    }

                }

                if (detailList.size() != 0) {
                    for (ScLikeDetail detail : detailList) {
                        if (IMManager.getInstance().getCurrentId().equals(detail.getSenderUserId())) {
                            if (detail.getDescription().equals("1")) {
                                holder.leftLikeImg.setImageResource(R.drawable.img_chat_like);
                                holder.rightLikeImg.setImageResource(R.drawable.img_chat_like);
                            } else {
                                holder.leftLikeImg.setImageResource(R.drawable.img_chat_unlike);
                                holder.rightLikeImg.setImageResource(R.drawable.img_chat_unlike);
                            }
                            return;
                        }
                    }
                    holder.leftLikeImg.setImageResource(R.drawable.img_chat_other_like);
                    holder.rightLikeImg.setImageResource(R.drawable.img_chat_other_like);

                } else {
                    //无数据
//                    isLike = false;
                    holder.leftLikeImg.setImageResource(R.drawable.img_chat_unlike);
                    holder.rightLikeImg.setImageResource(R.drawable.img_chat_unlike);
                }
            });
        });

    }

    private void sendLikeMsg(Message messageItem, ViewHolder holder) {
        ThreadManager.getInstance().runOnWorkThread(() -> {
            ScLikeDao scLikeDao = DbManager.getInstance(SealApp.getApplication()).getScLikeDao();
            ScMyLike myLike = scLikeDao.getMyLike(messageItem.getUId());
            isLike = myLike!= null;
            Log.e("mylike sendLikeMsg",messageItem.getUId() + " islike = " + isLike);
            isLike = !isLike;
            String targetId = messageItem.getTargetId();//接收方ID
            String senderUserId = IMManager.getInstance().getCurrentId();//发送方ID
            Conversation.ConversationType conversationType = messageItem.getConversationType();//私聊或者群聊
            String uId = messageItem.getUId();
            ScLikeMessage messageContent = ScLikeMessage.obtain();
            Message likeMsg = Message.obtain(targetId, conversationType, messageContent);
            if (conversationType == Conversation.ConversationType.PRIVATE) {
                messageContent.setUserId(targetId);
            } else if (conversationType == Conversation.ConversationType.GROUP) {
                messageContent.setGroupId(targetId);
            }
            messageContent.setContent("");
            messageContent.setExtra("");
            messageContent.setMessageUUID("");
            messageContent.setTargetMessageUUID(uId);
//        messageContent.setSenderAvatar(GlideImageLoaderUtil.getScString(RongUserInfoManager.getInstance().getUserInfo(IMManager.getInstance().getCurrentId()).getPortraitUri().toString()));
            messageContent.setSenderAvatar(GlideImageLoaderUtil.getScString("_aa_UserIcon.jpg"));
            messageContent.setSenderUserId(senderUserId);
            messageContent.setTextDescription(isLike ? "1" : "0");

            Log.e("messageContent", "messageContent" + JSON.toJSONString(messageContent));
            RongIM.getInstance().sendMessage(likeMsg, "", "", new IRongCallback.ISendMessageCallback() {
                @Override
                public void onAttached(Message message) {
                    ThreadManager.getInstance().runOnWorkThread(new Runnable() {
                        @Override
                        public void run() {
                            //加入所有点赞列表
                            ScLikeDao scLikeDao = DbManager.getInstance(SealApp.getApplication()).getScLikeDao();
                            ScLikeDetail scLikeDetail = new ScLikeDetail();
                            scLikeDetail.setCreatedTime(new Date());
                            scLikeDetail.setDescription(isLike ? "1" : "0");
                            if (conversationType == Conversation.ConversationType.PRIVATE) {
                                scLikeDetail.setUserId(targetId);
                            } else if (conversationType == Conversation.ConversationType.GROUP) {
                                scLikeDetail.setGroupId(targetId);
                            }
                            scLikeDetail.setSenderAvatar(GlideImageLoaderUtil.getScString("_aa_UserIcon.jpg"));
                            scLikeDetail.setTargetMessageUuid(uId);
                            scLikeDetail.setMessageUuid(message.getUId());
                            scLikeDetail.setSenderUserId(senderUserId);
                            scLikeDao.insert(scLikeDetail);


                            //加入我点赞过的列表
                            if(isLike){
                                ScMyLike scMyLike = new ScMyLike();
                                scMyLike.setMessageUuid(uId);
                                scLikeDao.insertMyLike(scMyLike);
                            }else {
                                //移除我点赞过的列表
                                scLikeDao.deleteMyLike(uId);
                            }


                            RongIM.getInstance().deleteRemoteMessages(message.getConversationType(), message.getTargetId(), new Message[]{message}, new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.e("sclike4", "删除 onSuccess");
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    Log.e("sclike4", "删除 onError" + errorCode.getMessage());
                                }
                            });
                        }
                    });


                }

                @Override
                public void onSuccess(Message message) {

                }


                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                }
            });

        });


    }

    private void processTextView(final View v, int position, TextMessage content, final UIMessage data, final MyAutoLinkTextView pTextView) {
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
        MyAutoLinkTextView message;
        TextView unRead;
        FrameLayout sendFire;
        FrameLayout receiverFire;
        ImageView receiverFireImg;
        TextView receiverFireText;



        TextView leftText;
        TextView rightText;
        ImageView leftLikeImg;
        ImageView rightLikeImg;
        FrameLayout rightFramlayout;
        FrameLayout leftFramlayout;
        LinearLayout leftLl;
        LinearLayout rightLl;

        private ViewHolder() {
        }
    }
}