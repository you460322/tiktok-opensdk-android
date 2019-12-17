package com.bytedance.sdk.open.aweme.common.handler;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.api.TikTokApiEventHandler;


/**
 * Created by yangzhirong on 2018/10/8.
 */
public interface TikTokDataHandler {

    /**
     * @param type         type of Request/Response
     * @param bundle       data
     * @param eventHandler handle return data
     * @return if it can parse data
     */
    boolean handle(int type, Bundle bundle, TikTokApiEventHandler eventHandler);
}
