package cn.rongcloud.im.niko.im.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.SealApp;
import cn.rongcloud.im.niko.common.ThreadManager;
import cn.rongcloud.im.niko.db.DbManager;
import cn.rongcloud.im.niko.db.dao.ScLikeDao;
import cn.rongcloud.im.niko.db.model.ScLikeDetail;
import cn.rongcloud.im.niko.db.model.ScMyLike;
import cn.rongcloud.im.niko.im.IMManager;
import cn.rongcloud.im.niko.im.message.ScLikeMessage;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.provider.RichContentMessageItemProvider;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.RichContentMessage;

@ProviderTag(
        messageContent = RichContentMessage.class,
        showSummaryWithName=false
)
public class MyRichContentMessageItemProvider extends RichContentMessageItemProvider {

    private boolean isLike = false;


    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_rich_content_message, (ViewGroup)null);
        MyRichContentMessageItemProvider.ViewHolder holder = new MyRichContentMessageItemProvider.ViewHolder();
        holder.title = (TextView)view.findViewById(R.id.rc_title);
        holder.content = (TextView)view.findViewById(R.id.rc_content);
        holder.img = (AsyncImageView)view.findViewById(R.id.rc_img);
        holder.mLayout = (RelativeLayout)view.findViewById(R.id.rc_layout);

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

    public void onItemClick(View view, int position, RichContentMessage content, UIMessage message) {
        String action = "io.rong.imkit.intent.action.webview";
        Intent intent = new Intent(action);
//        intent.addFlags(268435456);
        intent.putExtra("url", content.getUrl());
        intent.setPackage(view.getContext().getPackageName());
        view.getContext().startActivity(intent);
    }

    public void bindView(View v, int position, RichContentMessage content, UIMessage message) {
        MyRichContentMessageItemProvider.ViewHolder holder = (MyRichContentMessageItemProvider.ViewHolder)v.getTag();
        holder.title.setText(content.getTitle());
        holder.content.setText(content.getContent());
        if (content.getImgUrl() != null) {
            holder.img.setResource(content.getImgUrl(), 0);
        }

        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_right_new);
            holder.title.setTextColor(Color.parseColor("#0A0A0B"));
            holder.content.setLinkTextColor(Color.parseColor("#0A0A0B"));
            holder.leftLikeImg.setVisibility(View.VISIBLE);
            holder.rightLikeImg.setVisibility(View.GONE);
            holder.leftText.setVisibility(View.GONE);
            holder.rightText.setVisibility(View.VISIBLE);
        } else {
            holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_left_new);
            holder.title.setTextColor(Color.parseColor("#ffffff"));
            holder.content.setLinkTextColor(Color.parseColor("#ffffff"));
            holder.leftLikeImg.setVisibility(View.GONE);
            holder.rightLikeImg.setVisibility(View.VISIBLE);
            holder.leftText.setVisibility(View.VISIBLE);
            holder.rightText.setVisibility(View.GONE);
        }
        this.processLike(v, message, holder);
    }

    private void processLike(View v, UIMessage data, MyRichContentMessageItemProvider.ViewHolder holder) {
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

    private void sendLikeMsg(Message messageItem, MyRichContentMessageItemProvider.ViewHolder holder) {
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
                    ThreadManager.getInstance().runOnUIThread(() -> {
                        ToastUtils.showToast("点赞发送成功" + isLike);

                    });
                }


                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    ThreadManager.getInstance().runOnUIThread(() -> {
                        ToastUtils.showToast("点赞发送失败"+errorCode.getMessage());


                    });
                }
            });

        });


    }


    public Spannable getContentSummary(RichContentMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, RichContentMessage data) {
        String text = "[图文]";
        return new SpannableString(text);
    }

    private static class ViewHolder {
        AsyncImageView img;
        TextView title;
        TextView content;
        RelativeLayout mLayout;


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
