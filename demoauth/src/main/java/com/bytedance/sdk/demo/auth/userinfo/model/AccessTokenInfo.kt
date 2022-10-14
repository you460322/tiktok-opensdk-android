package com.bytedance.sdk.demo.auth.userinfo.model

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

import com.google.gson.annotations.SerializedName

data class AccessTokenInfo(
    @SerializedName("open_id")var openid: String,
    @SerializedName("scope")var scope: String,
    @SerializedName("access_token")var accessToken: String,
    @SerializedName("expires_in")var expiresIn: Long,
    @SerializedName("refresh_token")var refreshToken: String,
    @SerializedName("refresh_expires_in")var refreshExpiresIn: Long
)
