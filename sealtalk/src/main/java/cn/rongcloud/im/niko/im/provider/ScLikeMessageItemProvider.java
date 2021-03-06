package cn.rongcloud.im.niko.im.provider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.im.message.ScLikeMessage;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;


/**
 * 点赞消息模版
 */
@ProviderTag(messageContent = ScLikeMessage.class, showProgress = false, showReadState = true)
public class ScLikeMessageItemProvider extends IContainerItemProvider.MessageProvider<ScLikeMessage> {
    private final float POKE_ICON_WIDTH_DP = 15f;
    private final float POKE_ICON_HEIGHT_DP = 18.6f;

    @Override
    public void bindView(View view, int i, ScLikeMessage pokeMessage, UIMessage uiMessage) {
//        ViewHolder viewHolder = (ViewHolder) view.getTag();
//        Context context = view.getContext();
//
//        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
//            viewHolder.contentTv.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
//        } else {
//            viewHolder.contentTv.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
//        }
//
//        String content = pokeMessage.getContent();
//        if (TextUtils.isEmpty(content)) {
//            content = context.getString(R.string.im_plugin_poke_message_default);
//        }
//        String pokeTitle = context.getString(R.string.im_plugin_poke_title);
//        String itemContent = "  " + pokeTitle + " " + content;
//        SpannableString contentSpan = new SpannableString(itemContent);
//
//        // 设置"戳一下"文字的颜色
//        ForegroundColorSpan pokeTitleSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.default_clickable_text));
//        int pokeTitleStarIndex = itemContent.indexOf(pokeTitle);
//        int pokeTitleEndIndex = pokeTitleStarIndex + pokeTitle.length();
//        contentSpan.setSpan(pokeTitleSpan, pokeTitleStarIndex, pokeTitleEndIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//
//        // 设置戳一下图标
//        Drawable pokeImg = view.getContext().getResources().getDrawable(R.drawable.im_plugin_img_dialog_send_poke);
//        float densityDpi = context.getResources().getDisplayMetrics().density;
//        int pokeImgWidth = (int) (POKE_ICON_WIDTH_DP * densityDpi);
//        int pokeImgHeight = (int) (POKE_ICON_HEIGHT_DP * densityDpi);
//        pokeImg.setBounds(0, 0, pokeImgWidth, pokeImgHeight);
//        ImageSpan imageSpan = new ImageSpan(pokeImg);
//        contentSpan.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//
//        viewHolder.contentTv.setText(contentSpan);
    }

    @Override
    public Spannable getContentSummary(ScLikeMessage pokeMessage) {
        return null;
    }

    @Override
    public Spannable getContentSummary(Context context, ScLikeMessage pokeMessage) {
//        return new SpannableString(context.getString(R.string.im_message_content_poke));
        return null;
    }

    @Override
    public void onItemClick(View view, int i, ScLikeMessage pokeMessage, UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
//        View contentView = LayoutInflater.from(context).inflate(R.layout.message_item_poke_message, viewGroup, false);
//        ViewHolder viewHolder = new ViewHolder();
//        viewHolder.contentTv = contentView.findViewById(R.id.item_tv_poke_message);
//        contentView.setTag(viewHolder);
//        return contentView;

        return new View(context);
    }

    private class ViewHolder {
        TextView contentTv;
    }


}
