package cn.rongcloud.im.niko.im.provider;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.widget.provider.SightMessageItemProvider;
import io.rong.message.SightMessage;

@ProviderTag(
        messageContent = SightMessage.class,
        showProgress = false,
        showSummaryWithName=false
)
public class MySightMessageItemProvider extends SightMessageItemProvider {
}
