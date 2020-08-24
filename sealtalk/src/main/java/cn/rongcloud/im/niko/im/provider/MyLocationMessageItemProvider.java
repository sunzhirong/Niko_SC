package cn.rongcloud.im.niko.im.provider;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.widget.provider.LocationMessageItemProvider;
import io.rong.message.LocationMessage;

@ProviderTag(
        messageContent = LocationMessage.class,
        showSummaryWithName=false
)
public class MyLocationMessageItemProvider extends LocationMessageItemProvider {
}
