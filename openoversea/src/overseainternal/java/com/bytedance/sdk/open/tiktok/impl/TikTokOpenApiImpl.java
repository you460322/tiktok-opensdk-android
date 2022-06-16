package com.bytedance.sdk.open.tiktok.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.bytedance.sdk.open.tiktok.authorize.model.Auth;
import com.bytedance.sdk.open.tiktok.base.IAppCheck;
import com.bytedance.sdk.open.tiktok.common.constants.Constants;
import com.bytedance.sdk.open.tiktok.authorize.AuthImpl;
import com.bytedance.sdk.open.tiktok.authorize.handler.SendAuthDataHandler;
import com.bytedance.sdk.open.tiktok.common.constants.Keys;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler;
import com.bytedance.sdk.open.tiktok.helper.MusicallyCheck;
import com.bytedance.sdk.open.tiktok.helper.TikTokCheck;
import com.bytedance.sdk.open.tiktok.share.ShareDataHandler;
import com.bytedance.sdk.open.tiktok.share.ShareImpl;
import com.bytedance.sdk.open.tiktok.share.Share;
import com.bytedance.sdk.open.tiktok.BuildConfig;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.ui.TikTokWebAuthorizeActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiktok授权实现类
 *
 * @author changlei@bytedance.com
 */

public class TikTokOpenApiImpl implements TikTokOpenApi {

    private Context mContext;
    private final IAppCheck[] mAuthcheckApis;
    private final IAppCheck[] mSharecheckApis;
    @Nullable private IApiEventHandler apiHandler;

    private Map<Integer, IDataHandler> handlerMap = new HashMap<>(2);


    private ShareImpl shareImpl;
    private AuthImpl authImpl;

    private static final int API_TYPE_LOGIN = 0;
    private static final int API_TYPE_SHARE = 1;

    private static final String LOCAL_ENTRY_ACTIVITY = "tiktokapi.TikTokEntryActivity"; // 请求授权的结果回调Activity入口
    private static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"; // 分享的Activity入口
    private static final String US_CONTRY_CODE = "US";
    private static final String MUSICALLY_PACKAGE = "com.zhiliaoapp.musically";
    private static final String TIKTOK_PACKAGE = "com.ss.android.ugc.trill";

    private static final int TYPE_AUTH_HANDLER = 1;
    private static final int TYPE_SHARE_HANDLER = 2;

    public TikTokOpenApiImpl(Context context, AuthImpl authImpl, ShareImpl shareImpl, @Nullable IApiEventHandler apiHandler) {
        this.mContext = context;
        this.shareImpl = shareImpl;
        this.authImpl = authImpl;
        this.apiHandler = apiHandler;
        handlerMap.put(TYPE_AUTH_HANDLER, new SendAuthDataHandler());
        handlerMap.put(TYPE_SHARE_HANDLER, new ShareDataHandler());
        mAuthcheckApis = new IAppCheck[]{
                new MusicallyCheck(context),
                new TikTokCheck(context)
        };

        mSharecheckApis = new IAppCheck[]{
                new MusicallyCheck(context),
                new TikTokCheck(context)
        };

    }

    @Override
    public boolean handleIntent(Intent intent, IApiEventHandler eventHandler) {
        if (eventHandler == null) {
            return false;
        }
        if (intent == null) {
            eventHandler.onErrorIntent(intent);
            return false;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            eventHandler.onErrorIntent(intent);
            return false;
        }

        int type = bundle.getInt(Keys.Base.TYPE);//授权使用的
        if (type == 0) {
            type = bundle.getInt(Keys.Share.TYPE);//分享使用的
        }
        switch (type) {
            case Constants.TIKTOK.AUTH_REQUEST:
            case Constants.TIKTOK.AUTH_RESPONSE:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
            case Constants.TIKTOK.SHARE_REQUEST:
            case Constants.TIKTOK.SHARE_RESPONSE:
                return handlerMap.get(TYPE_SHARE_HANDLER).handle(type, bundle, eventHandler);
            default:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
        }
    }

    @Override
    public boolean isAppInstalled() {
        for (IAppCheck checkapi : mAuthcheckApis) {
            if (checkapi.isAppInstalled()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getSdkVersion() {
        return BuildConfig.SDK_OVERSEA_VERSION;
    }

    @Override
    public boolean isSupportLiteAuthorize() {
        for (IAppCheck checkapi : mAuthcheckApis) {
            if (checkapi.isAppSupportAPI(Keys.API.AUTHORIZE_FOR_TIKTOK_LITE)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public IApiEventHandler getApiHandler() {
        return apiHandler;
    }

    @Override
    public boolean isAppSupportAuthorization() {
        return getSupportApiAppInfo(API_TYPE_LOGIN) != null;
    }

    @Override
    public boolean isAppSupportShare() {
        return getSupportApiAppInfo(API_TYPE_SHARE) != null;
    }

    @Override
    public boolean isShareSupportFileProvider() {
        for (IAppCheck checkapi : mSharecheckApis) {
            if (checkapi.isShareFileProviderSupported()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean authorize(Auth.Request request) {
        IAppCheck appHasInstalled = (IAppCheck) getSupportApiAppInfo(API_TYPE_LOGIN);

        if (appHasInstalled != null) {
            return authImpl.authorizeNative(request, appHasInstalled.getPackageName(), appHasInstalled.getRemoteAuthEntryActivity(), LOCAL_ENTRY_ACTIVITY, BuildConfig.SDK_OVERSEA_NAME, BuildConfig.SDK_OVERSEA_VERSION);
        } else {
            return sendWebAuthRequest(request);
        }
    }

    @Override
    public boolean share(Share.Request request) {
        if (request == null) {
            return false;
        }

        if (isAppSupportShare()) {
            String remotePackage = getSupportApiAppInfo(API_TYPE_SHARE).getPackageName();
            return shareImpl.share(LOCAL_ENTRY_ACTIVITY, remotePackage, REMOTE_SHARE_ACTIVITY, request,
                    getSupportApiAppInfo(API_TYPE_SHARE).getRemoteAuthEntryActivity(), BuildConfig.SDK_OVERSEA_NAME, BuildConfig.SDK_OVERSEA_VERSION);
        }

        return false;
    }

    private boolean sendWebAuthRequest(Auth.Request request) {
        return authImpl.authorizeWeb(TikTokWebAuthorizeActivity.class, request);
    }

    private IAppCheck getSupportApiAppInfo(int type) {

        switch (type) {
            case API_TYPE_LOGIN:
                for (IAppCheck checkapi : mAuthcheckApis) {
                    if (checkapi.isAuthSupported()) {
                        return checkapi;
                    }
                }
                break;
            case API_TYPE_SHARE:
                for (IAppCheck checkapi : mSharecheckApis) {
                    if (checkapi.isShareSupported()) {
                        return checkapi;
                    }
                }
                break;
        }

        return null;
    }
}
