package com.bytedance.sdk.open.tiktok.authorize

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.authorize.model.Auth
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.utils.AppUtils.Companion.componentClassName

class AuthService(val activity: Activity, val clientKey: String) {
    fun authorizeNative(req: Auth.Request, packageName: String, remoteRequestEntry: String, localEntry: String, sdkName: String?, sdkVersion: String?): Boolean {
        return if (TextUtils.isEmpty(packageName) || !req.validate()) {
            false
        } else {
            val bundle = req.toBundle()
            bundle.putString(Keys.Auth.CLIENT_KEY, clientKey)
            bundle.putString(Keys.Base.CALLER_PKG, activity.packageName)
            // TODO: chen.wu check / verify callerLocalEntry yes/no cases
            if (!TextUtils.isEmpty(req.callerLocalEntry)) {
                bundle.putString(Keys.Base.FROM_ENTRY, componentClassName(activity.packageName, req.callerLocalEntry!!))
            } else if (localEntry != null) {
                bundle.putString(Keys.Base.FROM_ENTRY, componentClassName(activity.packageName, localEntry))
            }
            bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, sdkName)
            bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, sdkVersion)
            val intent = Intent()
            val componentName = ComponentName(packageName, componentClassName(packageName, remoteRequestEntry))
            intent.component = componentName
            intent.putExtras(bundle)
            try {
                activity.startActivityForResult(intent, Keys.AUTH_REQUEST_CODE)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun authorizeWeb(clazz: Class<*>, req: Auth.Request): Boolean {
        return if (!req.validate()) {
            false
        } else {
            val bundle = req.toBundle()
            bundle.putString(Keys.Auth.CLIENT_KEY, clientKey)
            bundle.putString(Keys.Base.CALLER_PKG, activity.packageName)
            val intent = Intent(activity, clazz)
            intent.putExtras(bundle)
            try {
                activity.startActivity(intent)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}