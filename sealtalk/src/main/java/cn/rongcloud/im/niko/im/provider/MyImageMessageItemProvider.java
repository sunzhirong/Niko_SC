package cn.rongcloud.im.niko.im.provider;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.widget.provider.ImageMessageItemProvider;
import io.rong.message.ImageMessage;


@ProviderTag(
        messageContent = ImageMessage.class,
        showProgress = false,
        showSummaryWithName=false
)
public class MyImageMessageItemProvider extends ImageMessageItemProvider {
}
