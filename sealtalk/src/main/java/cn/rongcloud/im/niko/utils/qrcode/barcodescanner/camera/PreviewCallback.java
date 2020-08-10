package cn.rongcloud.im.niko.utils.qrcode.barcodescanner.camera;

import cn.rongcloud.im.niko.utils.qrcode.barcodescanner.SourceData;

/**
 * Callback for camera previews.
 */
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
    void onPreviewError(Exception e);
}
