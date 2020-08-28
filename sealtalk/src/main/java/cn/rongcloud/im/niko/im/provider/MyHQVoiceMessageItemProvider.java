package cn.rongcloud.im.niko.im.provider;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

import cn.rongcloud.im.niko.R;
import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.IAudioPlayListener;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.HQVoiceMessageItemProvider;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.common.NetUtils;
import io.rong.imlib.model.Message;
import io.rong.message.HQVoiceMessage;

@ProviderTag(
        messageContent = HQVoiceMessage.class,
        showSummaryWithName = false
)
public class MyHQVoiceMessageItemProvider extends HQVoiceMessageItemProvider {

    private static final String TAG = "HQVoiceMessageItemProvider";

    public MyHQVoiceMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_hq_voice_message, (ViewGroup)null);
        MyHQVoiceMessageItemProvider.ViewHolder holder = new MyHQVoiceMessageItemProvider.ViewHolder();
        holder.left = (TextView)view.findViewById(R.id.rc_left);
        holder.right = (TextView)view.findViewById(R.id.rc_right);
        holder.img = (ImageView)view.findViewById(R.id.rc_img);
        holder.unread = (ImageView)view.findViewById(R.id.rc_voice_unread);
        holder.downloadError = (ImageView)view.findViewById(R.id.rc_voice_download_error);
        holder.downloadProcessing = (ProgressBar)view.findViewById(R.id.rc_download_progress);
        holder.sendFire = (FrameLayout)view.findViewById(R.id.fl_send_fire);
        holder.receiverFire = (FrameLayout)view.findViewById(R.id.fl_receiver_fire);
        holder.receiverFireImg = (ImageView)view.findViewById(R.id.iv_receiver_fire);
        holder.receiverFireText = (TextView)view.findViewById(R.id.tv_receiver_fire);
        view.setTag(holder);
        return view;
    }

    public void bindView(View v, int position, HQVoiceMessage content, UIMessage message) {
        MyHQVoiceMessageItemProvider.ViewHolder holder = (MyHQVoiceMessageItemProvider.ViewHolder)v.getTag();
        holder.receiverFire.setTag(message.getUId());
        if (content.isDestruct()) {
            if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                holder.sendFire.setVisibility(View.VISIBLE);
                holder.receiverFire.setVisibility(View.GONE);
            } else {
                holder.sendFire.setVisibility(View.GONE);
                holder.receiverFire.setVisibility(View.VISIBLE);
                DestructManager.getInstance().addListener(message.getUId(), new MyHQVoiceMessageItemProvider.DestructListener(holder, message), "HQVoiceMessageItemProvider");
                if (message.getMessage().getReadTime() > 0L) {
                    holder.receiverFireText.setVisibility(View.VISIBLE);
                    holder.receiverFireImg.setVisibility(View.GONE);
                    String unFinishTime;
                    if (TextUtils.isEmpty(message.getUnDestructTime())) {
                        unFinishTime = DestructManager.getInstance().getUnFinishTime(message.getUId());
                    } else {
                        unFinishTime = message.getUnDestructTime();
                    }

                    holder.receiverFireText.setText(unFinishTime);
                    DestructManager.getInstance().startDestruct(message.getMessage());
                } else {
                    holder.receiverFireText.setVisibility(View.GONE);
                    holder.receiverFireImg.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.sendFire.setVisibility(View.GONE);
            holder.receiverFire.setVisibility(View.GONE);
        }

        boolean listened;
        Uri playingUri;
        if (message.continuePlayAudio) {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri == null || !playingUri.equals(content.getLocalPath())) {
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().startPlay(v.getContext(), content.getLocalPath(), new MyHQVoiceMessageItemProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            }
        } else {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri != null && playingUri.equals(content.getLocalPath())) {
                this.setLayout(v.getContext(), holder, message, true);
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().setPlayListener(new MyHQVoiceMessageItemProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            } else {
                this.setLayout(v.getContext(), holder, message, false);
            }
        }

    }

    public void onItemClick(View view, int position, HQVoiceMessage content, UIMessage message) {
        if (content != null) {
            RLog.d("HQVoiceMessageItemProvider", "Item index:" + position + " content.getLocalPath():" + content.getLocalPath());
            MyHQVoiceMessageItemProvider.ViewHolder holder = (MyHQVoiceMessageItemProvider.ViewHolder)view.getTag();
            if (AudioPlayManager.getInstance().isPlaying()) {
                if (AudioPlayManager.getInstance().getPlayingUri().equals(content.getLocalPath())) {
                    AudioPlayManager.getInstance().stopPlay();
                    return;
                }

                AudioPlayManager.getInstance().stopPlay();
            }

            if (!AudioPlayManager.getInstance().isInNormalMode(view.getContext()) && AudioPlayManager.getInstance().isInVOIPMode(view.getContext())) {
                Toast.makeText(view.getContext(), view.getContext().getString(R.string.rc_voip_occupying), Toast.LENGTH_SHORT).show();
            } else {
                boolean listened = message.getMessage().getReceivedStatus().isListened();
                this.playOrDownloadHQVoiceMsg(view, content, message, holder, listened);
            }
        }
    }

    private void playOrDownloadHQVoiceMsg(View view, HQVoiceMessage content, UIMessage message, MyHQVoiceMessageItemProvider.ViewHolder holder, boolean listened) {
        boolean ifDownloadHQVoiceMsg = content.getLocalPath() == null || TextUtils.isEmpty(content.getLocalPath().toString());
        if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
            ifDownloadHQVoiceMsg = content.getLocalPath() == null || TextUtils.isEmpty(content.getLocalPath().toString()) || !this.isFileExists(content.getLocalPath().toString());
        }

        if (ifDownloadHQVoiceMsg) {
            this.downloadHQVoiceMsg(view, message, holder, listened);
        } else {
            this.playHQVoiceMessage(view, content, message, holder, listened);
        }

    }

    private void downloadHQVoiceMsg(final View view, final UIMessage uiMessage, final MyHQVoiceMessageItemProvider.ViewHolder holder, final boolean listened) {
        RongIM.getInstance().downloadMediaMessage(uiMessage.getMessage(), new IRongCallback.IDownloadMediaMessageCallback() {
            public void onSuccess(Message message) {
                RLog.d("HQVoiceMessageItemProvider", "playOrDownloadHQVoiceMsg onSuccess");
                holder.downloadError.setVisibility(View.GONE);
                holder.downloadProcessing.setVisibility(View.GONE);
                MyHQVoiceMessageItemProvider.this.playHQVoiceMessage(view, (HQVoiceMessage)message.getContent(), uiMessage, holder, listened);
            }

            public void onProgress(Message message, int progress) {
                holder.downloadProcessing.setVisibility(View.VISIBLE);
            }

            public void onError(Message message, RongIMClient.ErrorCode code) {
                RLog.d("HQVoiceMessageItemProvider", "playOrDownloadHQVoiceMsg onError");
                holder.downloadError.setVisibility(View.VISIBLE);
                holder.downloadProcessing.setVisibility(View.GONE);
            }

            public void onCanceled(Message message) {
            }
        });
    }

    private void playHQVoiceMessage(View view, HQVoiceMessage content, UIMessage message, MyHQVoiceMessageItemProvider.ViewHolder holder, boolean listened) {
        holder.unread.setVisibility(View.GONE);
        AudioPlayManager.getInstance().startPlay(view.getContext(), content.getLocalPath(), new MyHQVoiceMessageItemProvider.VoiceMessagePlayListener(view.getContext(), message, holder, listened));
    }

    private boolean isFileExists(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        } else {
            if (filePath.startsWith("file://")) {
                filePath = filePath.substring(7);
            }

            File file = new File(filePath);
            return file.exists();
        }
    }

    private void setLayout(Context context, MyHQVoiceMessageItemProvider.ViewHolder holder, UIMessage message, boolean playing) {
        HQVoiceMessage content = (HQVoiceMessage)message.getContent();
        int minWidth = 70;
        int maxWidth = 204;
        float scale = context.getResources().getDisplayMetrics().density;
         minWidth = (int)((float)minWidth * scale + 0.5F);
         maxWidth = (int)((float)maxWidth * scale + 0.5F);
        int duration = AudioRecordManager.getInstance().getMaxVoiceDuration();
        holder.img.getLayoutParams().width = minWidth + (maxWidth - minWidth) / duration * content.getDuration();
        AnimationDrawable animationDrawable;
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.left.setText(String.format("%s\"", content.getDuration()));
            holder.left.setVisibility(View.VISIBLE);
            holder.right.setVisibility(View.GONE);
            holder.unread.setVisibility(View.GONE);
            holder.downloadError.setVisibility(View.GONE);
            holder.downloadProcessing.setVisibility(View.GONE);
            holder.img.setScaleType(ImageView.ScaleType.FIT_END);
            holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_right_new);
            animationDrawable = (AnimationDrawable)context.getResources().getDrawable(R.drawable.rc_an_voice_sent);
            if (playing) {
                holder.img.setImageDrawable(animationDrawable);
                if (animationDrawable != null) {
                    animationDrawable.start();
                }
            } else {
                holder.img.setImageDrawable(holder.img.getResources().getDrawable(R.drawable.rc_ic_voice_sent));
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
            }
        } else {
            holder.right.setText(String.format("%s\"", content.getDuration()));
            holder.right.setVisibility(View.VISIBLE);
            holder.left.setVisibility(View.GONE);
            RLog.d("HQVoiceMessageItemProvider", "message.getExtra() = " + message.getExtra());
            holder.downloadProcessing.setVisibility(View.VISIBLE);
            holder.downloadError.setVisibility(View.GONE);
            if (((HQVoiceMessage)message.getContent()).getLocalPath() != null) {
                holder.downloadProcessing.setVisibility(View.GONE);
                holder.downloadError.setVisibility(View.GONE);
            } else if (!NetUtils.isNetWorkAvailable(context)) {
                holder.downloadError.setVisibility(View.VISIBLE);
                holder.downloadProcessing.setVisibility(View.GONE);
            }

            if (!message.getReceivedStatus().isListened() && ((HQVoiceMessage)message.getContent()).getLocalPath() != null) {
                holder.unread.setVisibility(View.VISIBLE);
            } else {
                holder.unread.setVisibility(View.GONE);
            }

            holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_left_new);
            animationDrawable = (AnimationDrawable)context.getResources().getDrawable(R.drawable.rc_an_voice_receive);
            if (playing) {
                holder.img.setImageDrawable(animationDrawable);
                if (animationDrawable != null) {
                    animationDrawable.start();
                }
            } else {
                holder.img.setImageDrawable(holder.img.getResources().getDrawable(R.drawable.rc_ic_voice_receive));
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
            }

            holder.img.setScaleType(ImageView.ScaleType.FIT_START);
        }

    }

    public Spannable getContentSummary(HQVoiceMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, HQVoiceMessage data) {
        return data.isDestruct() ? new SpannableString(context.getString(R.string.rc_message_content_burn)) : new SpannableString(context.getString(R.string.rc_message_content_voice));
    }

    private static class DestructListener implements RongIMClient.DestructCountDownTimerListener {
        private WeakReference<MyHQVoiceMessageItemProvider.ViewHolder> mHolder;
        private UIMessage mUIMessage;

        DestructListener(MyHQVoiceMessageItemProvider.ViewHolder pHolder, UIMessage pUIMessage) {
            this.mHolder = new WeakReference(pHolder);
            this.mUIMessage = pUIMessage;
        }

        public void onTick(long millisUntilFinished, String messageId) {
            if (this.mUIMessage.getUId().equals(messageId)) {
                MyHQVoiceMessageItemProvider.ViewHolder viewHolder = (MyHQVoiceMessageItemProvider.ViewHolder)this.mHolder.get();
                if (viewHolder != null && messageId.equals(viewHolder.receiverFire.getTag())) {
                    viewHolder.receiverFireText.setVisibility(View.VISIBLE);
                    viewHolder.receiverFireImg.setVisibility(View.GONE);
                    String unDestructTime = String.valueOf(Math.max(millisUntilFinished, 1L));
                    viewHolder.receiverFireText.setText(unDestructTime);
                    this.mUIMessage.setUnDestructTime(unDestructTime);
                }
            }

        }

        public void onStop(String messageId) {
            if (this.mUIMessage.getUId().equals(messageId)) {
                MyHQVoiceMessageItemProvider.ViewHolder viewHolder = (MyHQVoiceMessageItemProvider.ViewHolder)this.mHolder.get();
                if (viewHolder != null && messageId.equals(viewHolder.receiverFire.getTag())) {
                    viewHolder.receiverFireText.setVisibility(View.GONE);
                    viewHolder.receiverFireImg.setVisibility(View.VISIBLE);
                    this.mUIMessage.setUnDestructTime((String)null);
                }
            }

        }
    }

    private class VoiceMessagePlayListener implements IAudioPlayListener {
        private Context context;
        private UIMessage message;
        private MyHQVoiceMessageItemProvider.ViewHolder holder;
        private boolean listened;

        VoiceMessagePlayListener(Context context, UIMessage message, MyHQVoiceMessageItemProvider.ViewHolder holder, boolean listened) {
            this.context = context;
            this.message = message;
            this.holder = holder;
            this.listened = listened;
        }

        public void onStart(Uri uri) {
            this.message.continuePlayAudio = false;
            this.message.setListening(true);
            this.message.getReceivedStatus().setListened();
            RongIMClient.getInstance().setMessageReceivedStatus(this.message.getMessageId(), this.message.getReceivedStatus(), (RongIMClient.ResultCallback)null);
            MyHQVoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, true);
            EventBus.getDefault().post(new Event.AudioListenedEvent(this.message.getMessage()));
            if (this.message.getContent().isDestruct() && this.message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                DestructManager.getInstance().stopDestruct(this.message.getMessage());
                EventBus.getDefault().post(new Event.changeDestructionReadTimeEvent(this.message.getMessage()));
            }

        }

        public void onStop(Uri uri) {
            if (this.message.getContent() instanceof HQVoiceMessage) {
                this.message.setListening(false);
                MyHQVoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, false);
                if (this.message.getContent().isDestruct() && this.message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                    DestructManager.getInstance().startDestruct(this.message.getMessage());
                }
            }

        }

        public void onComplete(Uri uri) {
            Event.PlayAudioEvent event = Event.PlayAudioEvent.obtain();
            event.messageId = this.message.getMessageId();
            if (this.message.isListening() && this.message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                try {
                    event.continuously = this.context.getResources().getBoolean(R.bool.rc_play_audio_continuous);
                } catch (Resources.NotFoundException var4) {
                    RLog.e("HQVoiceMessageItemProvider", "VoiceMessagePlayListener.onComplete", var4);
                }
            }

            if (event.continuously && !this.message.getContent().isDestruct()) {
                EventBus.getDefault().post(event);
            }

            this.message.setListening(false);
            MyHQVoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, false);
            if (this.message.getContent().isDestruct() && this.message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                DestructManager.getInstance().startDestruct(this.message.getMessage());
            }

        }
    }

    private static class ViewHolder {
        ImageView img;
        TextView left;
        TextView right;
        ImageView unread;
        ImageView downloadError;
        ProgressBar downloadProcessing;
        FrameLayout sendFire;
        FrameLayout receiverFire;
        ImageView receiverFireImg;
        TextView receiverFireText;

        private ViewHolder() {
        }
    }
}
