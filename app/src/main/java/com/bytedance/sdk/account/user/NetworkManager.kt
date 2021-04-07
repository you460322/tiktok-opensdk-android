package com.bytedance.sdk.account.user

import android.app.Activity
import com.bytedance.sdk.account.NetUtils
import com.bytedance.sdk.account.R
import com.bytedance.sdk.account.user.bean.AccessTokenResponse
import com.bytedance.sdk.account.user.bean.UploadSoundResponse
import com.bytedance.sdk.account.user.bean.UserInfoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 主要功能：
 * author: ChangLei
 * since: 2019/4/3
 */
class NetworkManager {


    fun getUserInfo(code: String, clientKey: String, isBoe: Boolean?, activity: Activity, listener: IUserApiBack) {
        val userInfoApi = NetUtils.createApi(GetUserInfoServie::class.java, isBoe)
        userInfoApi.getAccessToken(code, clientKey)
                .enqueue(object : Callback<AccessTokenResponse> {
                    override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                        listener.onResult(false, t.toString(), null, "", "")
                    }

                    override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
                        var successMessage = ""
                        if (!activity.isFinishing) {
                            successMessage = activity.getString(R.string.success_user_info)
                        }
                        if (response.isSuccessful) {
                            response.body()?.data?.let {
                                userInfoApi.getUserInfo(it.accessToken, it.openid)
                                        .enqueue(object : Callback<UserInfoResponse> {
                                            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                                                listener.onResult(false, t.toString(), null, "", "")
                                            }

                                            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                                                if (response.isSuccessful) {
                                                    listener.onResult(true, successMessage, response.body()?.data, it.accessToken, it.openid)
                                                }
                                                else {
                                                    listener.onResult(false, response.message(), null, "", "")
                                                }
                                            }

                                        })
                            }
                        }
                        else {
                            listener.onResult(false, response.message(), null, "", "")
                        }
                    }

                })
    }

    fun uploadSound(accessToken: String, openid: String, isBoe: Boolean, body: MultipartBody.Part, listener: UploadSoundApiCallback) {
        val uploadSoundApi = NetUtils.createApi(UploadSoundService::class.java, isBoe)
        uploadSoundApi.uploadSound(accessToken, openid, body)
                .enqueue(object : Callback<UploadSoundResponse> {
                    override fun onFailure(call: Call<UploadSoundResponse>, t: Throwable) {
                        listener.onResult(false, t.toString(), null)
                    }

                    override fun onResponse(call: Call<UploadSoundResponse>, response: Response<UploadSoundResponse>) {
                        if (response.isSuccessful) {
                            listener.onResult(true, "Upload Successful", response.code())
                        } else {
                            listener.onResult(false, response.raw().message().takeIf { !it.isNullOrEmpty() } ?: "No status message", response.body()?.errorCode.takeIf { it != null } ?: response.code())
                        }
                    }

                })
    }

}