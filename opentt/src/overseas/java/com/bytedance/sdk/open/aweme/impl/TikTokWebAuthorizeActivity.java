package com.bytedance.sdk.open.aweme.impl;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.BDApiEventHandler;
import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.authorize.activity.BaseBDWebAuthorizeActivity;
import com.bytedance.sdk.open.aweme.utils.ViewUtils;

import static com.bytedance.sdk.open.aweme.impl.TikTokOpenApiImpl.WAP_AUTHORIZE_URL;


/**
 * tiktok authroize wap
 *
 * @author changlei@bytedance.com
 */
public class TikTokWebAuthorizeActivity extends BaseBDWebAuthorizeActivity {

    public static final String AUTH_HOST = "open-api.musical.ly";
    public static final String DOMAIN = "api.snssdk.com";
    public static final String AUTH_PATH = "/platform/oauth/connect/";


    private TiktokOpenApi ttOpenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = TikTokOpenApiFactory.create(this);
        super.onCreate(savedInstanceState);

        ViewUtils.setStatusBarColor(this, Color.TRANSPARENT);
    }

    @Override
    protected View getLoadingView(ViewGroup root) {
        View loadingView = LayoutInflater.from(this).inflate(getResources().getIdentifier("tiktok_layout_open_loading_view", "layout", getPackageName()), root, false);
        return loadingView;
    }

    @Override
    protected View getHeaderView(ViewGroup root) {
        View headerView = LayoutInflater.from(this).inflate(getResources().getIdentifier("tiktok_layout_open_web_header_view", "layout", getPackageName()), root, false);
        ImageView cancelView = headerView.findViewById(getResources().getIdentifier("tiktok_cancel", "id", getPackageName()));
        Drawable arrowPic = ContextCompat.getDrawable(this, getResources().getIdentifier("tiktok_selector_web_authorize_titlebar_back", "drawable", getPackageName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (arrowPic != null) {
                arrowPic.setAutoMirrored(true);
            }
        }
        cancelView.setImageDrawable(arrowPic);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel(BDOpenConstants.ErrorCode.ERROR_CODE_CANCEL);
            }
        });
        return headerView;
    }

    @Override
    protected boolean isNetworkAvailable() {
        return true;
    }

    @Override
    protected boolean handleIntent(Intent intent, BDApiEventHandler eventHandler) {
        return ttOpenApi.handleIntent(intent, eventHandler);
    }

    @Override
    protected void sendInnerResponse(Authorization.Request req, BaseResp resp) {
        // 添加wap url数据
        if (resp != null && mContentWebView != null) {
            if (resp.extras == null) {
                resp.extras = new Bundle();
            }
            resp.extras.putString(WAP_AUTHORIZE_URL, mContentWebView.getUrl());
        }

       sendInnerResponse(LOCAL_ENTRY_ACTIVITY,req, resp);
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
            mContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    protected String errorCode2Message(int errorCode) {
        // 目前Tiktok没有自定义的错误码，不需要转换
        return "";
    }
}
