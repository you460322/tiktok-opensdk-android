package com.bytedance.sdk.open.aweme.authorize.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.bytedance.sdk.open.aweme.TikTokOpenApiFactory;
import com.bytedance.sdk.open.aweme.api.TikTokApiEventHandler;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.TikTokConstants;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.utils.ViewUtils;

/**
 * DouYin web authorization
 * author: ChangLei
 * since: 2019/5/17
 */
public class AwemeWebAuthorizeActivity extends BaseWebAuthorizeActivity {

    public static final String AUTH_HOST = "open.douyin.com";
    public static final String DOMAIN = "api.snssdk.com";
    public static final String AUTH_PATH = "/platform/oauth/connect/";

    private TiktokOpenApi ttOpenApi;
    private String mCommonParams;
    private boolean isInjectParams = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = TikTokOpenApiFactory.create(this, TikTokConstants.TARGET_APP.AWEME);
        super.onCreate(savedInstanceState);
        ViewUtils.setStatusBarColor(this, Color.TRANSPARENT);
    }

    @Override
    protected void configWebView() {
        if (!TextUtils.isEmpty(mCommonParams)) {
            mContentWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress > 70 && newProgress <= 100) {
                        if (!isInjectParams) {
                            injectCommonParams();
                            isInjectParams = !isInjectParams;
                        }
                    }

                }
            });
        }
        mContentWebView.setWebViewClient(new AuthClient());
    }

    private class AuthClient extends AuthWebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!isInjectParams) {
                injectCommonParams();
                isInjectParams = !isInjectParams;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!isInjectParams) {
                injectCommonParams();
                isInjectParams = !isInjectParams;
            }
        }
    }

    private void injectCommonParams() {
        if (!TextUtils.isEmpty(mCommonParams)) {
            String command = "javascript:(function () {" +
                    "window.secureCommonParams ='" + mCommonParams +"';" +
                    "})();";
            mContentWebView.loadUrl(command);
        }
    }

    @Override
    protected View getLoadingView(ViewGroup root) {
        View loadingView = LayoutInflater.from(this).inflate(getResources().getIdentifier("tiktok_layout_open_loading_view", "layout", getPackageName()), root, false);
        return loadingView;
    }


    @Override
    protected boolean isNetworkAvailable() {
        return true;
    }

    @Override
    protected boolean handleIntent(Intent intent, TikTokApiEventHandler eventHandler) {
        Bundle bundle = intent.getBundleExtra(ParamKeyConstants.BaseParams.EXTRA);
        if (bundle != null) {
            mCommonParams = bundle.getString("internal_secure_common_params");
        }
        return ttOpenApi.handleIntent(intent, eventHandler);
    }

    @Override
    protected void sendInnerResponse(Authorization.Request req, BaseResp resp) {
        if (resp != null && mContentWebView != null) {
            if (resp.extras == null) {
                resp.extras = new Bundle();
            }
            resp.extras.putString(WAP_AUTHORIZE_URL, mContentWebView.getUrl());
        }

        sendInnerResponse(LOCAL_ENTRY_ACTIVITY, req, resp);
    }

    @Override
    protected String getHost() {
        return AUTH_HOST;
    }

    @Override
    protected String getAuthPath() {
        return AUTH_PATH;
    }

    @Override
    protected String getDomain() {
        return DOMAIN;
    }

    @Override
    protected void setContainerViewBgColor() {
        if (mContainer != null) {
            mContainer.setBackgroundColor(Color.parseColor("#161823"));
        }
    }

    @Override
    protected String errorCode2Message(int errorCode) {
        return "";
    }
}
