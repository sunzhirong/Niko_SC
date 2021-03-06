package cn.rongcloud.im.niko.task;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Locale;

import cn.rongcloud.im.niko.model.ChatRoomResult;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.VersionInfo;
import cn.rongcloud.im.niko.net.HttpClientManager;
import cn.rongcloud.im.niko.utils.NetworkOnlyResource;
import io.rong.imkit.RongConfigurationManager;
import io.rong.imkit.utilities.LangUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.common.BuildVar;

public class AppTask {

    private Context context;

    public AppTask(Context context) {
        this.context = context.getApplicationContext();
    }


    /**
     * SDK 版本号
     *
     * @return
     */
    public String getSDKVersion() {
        return BuildVar.SDK_VERSION;
    }


    /**
     * 获取当前app 的语音设置
     *
     * @return
     */
    public LangUtils.RCLocale getLanguageLocal() {

        LangUtils.RCLocale appLocale = RongConfigurationManager.getInstance().getAppLocale(context);
        if (appLocale == LangUtils.RCLocale.LOCALE_AUTO) {
            Locale systemLocale = RongConfigurationManager.getInstance().getSystemLocale();
            if (systemLocale.getLanguage().equals(Locale.CHINESE.getLanguage())) {
                appLocale = LangUtils.RCLocale.LOCALE_CHINA;
            } else if (systemLocale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                appLocale = LangUtils.RCLocale.LOCALE_US;
            } else {
                appLocale = LangUtils.RCLocale.LOCALE_CHINA;
            }
        }
        return appLocale;
    }

    /**
     * 设置当前应用的 语音
     *
     * @param selectedLocale
     */
    public boolean changeLanguage(LangUtils.RCLocale selectedLocale) {
        LangUtils.RCLocale appLocale = RongConfigurationManager.getInstance().getAppLocale(context);
        if (selectedLocale == appLocale) {
            return false;
        }

        if (selectedLocale == LangUtils.RCLocale.LOCALE_CHINA) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources resources = context.getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                Configuration config = resources.getConfiguration();
                LocaleList localeList = new LocaleList(selectedLocale.toLocale());
                LocaleList.setDefault(localeList);
                config.setLocales(localeList);
                resources.updateConfiguration(config, dm);
                // 保存语言状态
                LangUtils.saveLocale(context, selectedLocale);
            } else {
                RongConfigurationManager.getInstance().switchLocale(LangUtils.RCLocale.LOCALE_CHINA, context);
            }
            setPushLanguage(RongIMClient.PushLanguage.ZH_CN);
        } else if (selectedLocale == LangUtils.RCLocale.LOCALE_US) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources resources = context.getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                Configuration config = resources.getConfiguration();
                LocaleList localeList = new LocaleList(selectedLocale.toLocale());
                LocaleList.setDefault(localeList);
                config.setLocales(localeList);
                resources.updateConfiguration(config, dm);
                LangUtils.saveLocale(context, selectedLocale);
            } else {
                RongConfigurationManager.getInstance().switchLocale(LangUtils.RCLocale.LOCALE_US, context);
            }
            setPushLanguage(RongIMClient.PushLanguage.EN_US);
        }

        return true;
    }

    /**
     * 设置 push 的语言
     *
     * @param language
     */
    public void setPushLanguage(RongIMClient.PushLanguage language) {
        RongIMClient.getInstance().setPushLanguage(language, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                //设置成功也存起来
                RongConfigurationManager.getInstance().setPushLanguage(context, language);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    /**
     * 是否是 Debug 模式
     *
     * @return
     */
    public boolean isDebugMode() {
        //TODO 获取是否是 Debug 模式
        return false;
    }
}
