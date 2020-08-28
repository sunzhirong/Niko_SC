package cn.rongcloud.im.niko.im.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cn.rongcloud.im.niko.R;
import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.IAudioPlayListener;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.VoiceMessage;

@ProviderTag(
        messageContent = VoiceMessage.class,
        showSummaryWithName = false
)
public class MyVoiceMessageItemProvider extends IContainerItemProvider.MessageProvider<VoiceMessage> {
    private static final String TAG = "MyVoiceMessageItemProvider";

    public MyVoiceMessageItemProvider(Context context) {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_voice_message, (ViewGroup) null);
        MyVoiceMessageItemProvider.ViewHolder holder = new MyVoiceMessageItemProvider.ViewHolder();
        holder.left = (TextView) view.findViewById(R.id.rc_left);
        holder.right = (TextView) view.findViewById(R.id.rc_right);
        holder.img = (ImageView) view.findViewById(R.id.rc_img);
        holder.unread = (ImageView) view.findViewById(R.id.rc_voice_unread);
        holder.sendFire = (FrameLayout) view.findViewById(R.id.fl_send_fire);
        holder.receiverFire = (FrameLayout) view.findViewById(R.id.fl_receiver_fire);
        holder.receiverFireImg = (ImageView) view.findViewById(R.id.iv_receiver_fire);
        holder.receiverFireText = (TextView) view.findViewById(R.id.tv_receiver_fire);
        view.setTag(holder);
        return view;
    }

    public void bindView(View v, int position, VoiceMessage content, UIMessage message) {
        MyVoiceMessageItemProvider.ViewHolder holder = (MyVoiceMessageItemProvider.ViewHolder) v.getTag();
        holder.receiverFire.setTag(message.getUId());
        if (content.isDestruct()) {
            if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                holder.sendFire.setVisibility(View.VISIBLE);
                holder.receiverFire.setVisibility(View.GONE);
            } else {
                holder.sendFire.setVisibility(View.GONE);
                holder.receiverFire.setVisibility(View.VISIBLE);
                DestructManager.getInstance().addListener(message.getUId(), new MyVoiceMessageItemProvider.DestructListener(holder, message), "VoiceMessageItemProvider");
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
            if (playingUri == null || !playingUri.equals(content.getUri())) {
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().startPlay(v.getContext(), content.getUri(), new MyVoiceMessageItemProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            }
        } else {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri != null && playingUri.equals(content.getUri())) {
                this.setLayout(v.getContext(), holder, message, true);
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().setPlayListener(new MyVoiceMessageItemProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            } else {
                this.setLayout(v.getContext(), holder, message, false);
            }
        }

    }

    public void onItemClick(View view, int position, VoiceMessage content, UIMessage message) {
        RLog.d("VoiceMessageItemProvider", "Item index:" + position);
        if (content != null) {
            MyVoiceMessageItemProvider.ViewHolder holder = (MyVoiceMessageItemProvider.ViewHolder) view.getTag();
            if (AudioPlayManager.getInstance().isPlaying()) {
                if (AudioPlayManager.getInstance().getPlayingUri().equals(content.getUri())) {
                    AudioPlayManager.getInstance().stopPlay();
                    return;
                }

                AudioPlayManager.getInstance().stopPlay();
            }

            if (!AudioPlayManager.getInstance().isInNormalMode(view.getContext()) && AudioPlayManager.getInstance().isInVOIPMode(view.getContext())) {
                Toast.makeText(view.getContext(), view.getContext().getString(R.string.rc_voip_occupying), Toast
                .LENGTH_SHORT).show();
            } else {
                holder.unread.setVisibility(View.GONE);
                boolean listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().startPlay(view.getContext(), content.getUri(), new MyVoiceMessageItemProvider.VoiceMessagePlayListener(view.getContext(), message, holder, listened));
            }
        }
    }

    private void setLayout(Context context, MyVoiceMessageItemProvider.ViewHolder holder, UIMessage message, boolean playing) {
        VoiceMessage content = (VoiceMessage) message.getContent();
        int minWidth = 70;
        int maxWidth = 204;
        float scale = context.getResources().getDisplayMetrics().density;
         minWidth = (int) ((float) minWidth * scale + 0.5F);
         maxWidth = (int) ((float) maxWidth * scale + 0.5F);
        int duration = AudioRecordManager.getInstance().getMaxVoiceDuration();
        holder.img.getLayoutParams().width = minWidth + (maxWidth - minWidth) / duration * content.getDuration();
        AnimationDrawable animationDrawable;
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.left.setText(String.format("%s\"", content.getDuration()));
            holder.left.setVisibility(View.VISIBLE);
            holder.right.setVisibility(View.GONE);
            holder.unread.setVisibility(View.GONE);
            holder.img.setScaleType(ImageView.ScaleType.FIT_END);
            holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_right_new);
            animationDrawable = (AnimationDrawable) context.getResources().getDrawable(R.drawable.rc_an_voice_sent);
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
            if (!message.getReceivedStatus().isListened()) {
                holder.unread.setVisibility(View.VISIBLE);
            } else {
                holder.unread.setVisibility(View.GONE);
            }

            holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_left_new);
            animationDrawable = (AnimationDrawable) context.getResources().getDrawable(R.drawable.rc_an_voice_receive);
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

    public Spannable getContentSummary(VoiceMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, VoiceMessage data) {
        return data.isDestruct() ? new SpannableString(context.getString(R.string.rc_message_content_burn)) : new SpannableString(context.getString(R.string.rc_message_content_voice));
    }

    @TargetApi(View.GONE)
    private boolean muteAudioFocus(Context context, boolean bMute) {
        if (context == null) {
            RLog.d("VoiceMessageItemProvider", "muteAudioFocus context is null.");
            return false;
        } else {
            boolean bool = false;
            AudioManager am = (AudioManager) context.getSystemService("audio");
            int result;
            if (bMute) {
                result = am.requestAudioFocus((AudioManager.OnAudioFocusChangeListener) null, 3, 2);
                bool = result == 1;
            } else {
                result = am.abandonAudioFocus((AudioManager.OnAudioFocusChangeListener) null);
                bool = result == 1;
            }

            RLog.d("VoiceMessageItemProvider", "muteAudioFocus pauseMusic bMute=" + bMute + " result=" + bool);
            return bool;
        }
    }

    private static class DestructListener implements RongIMClient.DestructCountDownTimerListener {
        private WeakReference<MyVoiceMessageItemProvider.ViewHolder> mHolder;
        private UIMessage mUIMessage;

        public DestructListener(MyVoiceMessageItemProvider.ViewHolder pHolder, UIMessage pUIMessage) {
            this.mHolder = new WeakReference(pHolder);
            this.mUIMessage = pUIMessage;
        }

        public void onTick(long millisUntilFinished, String messageId) {
            if (this.mUIMessage.getUId().equals(messageId)) {
                MyVoiceMessageItemProvider.ViewHolder viewHolder = (MyVoiceMessageItemProvider.ViewHolder) this.mHolder.get();
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
                MyVoiceMessageItemProvider.ViewHolder viewHolder = (MyVoiceMessageItemProvider.ViewHolder) this.mHolder.get();
                if (viewHolder != null && messageId.equals(viewHolder.receiverFire.getTag())) {
                    viewHolder.receiverFireText.setVisibility(View.GONE);
                    viewHolder.receiverFireImg.setVisibility(View.VISIBLE);
                    this.mUIMessage.setUnDestructTime((String) null);
                }
            }

        }
    }

    private class VoiceMessagePlayListener implements IAudioPlayListener {
        private Context context;
        private UIMessage message;
        private MyVoiceMessageItemProvider.ViewHolder holder;
        private boolean listened;

        public VoiceMessagePlayListener(Context context, UIMessage message, MyVoiceMessageItemProvider.ViewHolder holder, boolean listened) {
            this.context = context;
            this.message = message;
            this.holder = holder;
            this.listened = listened;
        }

        public void onStart(Uri uri) {
            this.message.continuePlayAudio = false;
            this.message.setListening(true);
            this.message.getReceivedStatus().setListened();
            RongIMClient.getInstance().setMessageReceivedStatus(this.message.getMessageId(), this.message.getReceivedStatus(), (RongIMClient.ResultCallback) null);
            MyVoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, true);
            EventBus.getDefault().post(new Event.AudioListenedEvent(this.message.getMessage()));
            if (this.message.getContent().isDestruct() && this.message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                DestructManager.getInstance().stopDestruct(this.message.getMessage());
            }

        }

        public void onStop(Uri uri) {
            if (this.message.getContent() instanceof VoiceMessage) {
                this.message.setListening(false);
                MyVoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, false);
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
                    var4.printStackTrace();
                }
            }

            if (event.continuously && !this.message.getContent().isDestruct()) {
                EventBus.getDefault().post(event);
            }

            this.message.setListening(false);
            MyVoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, false);
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
        FrameLayout sendFire;
        FrameLayout receiverFire;
        ImageView receiverFireImg;
        TextView receiverFireText;

        private ViewHolder() {
        }
    }
}
