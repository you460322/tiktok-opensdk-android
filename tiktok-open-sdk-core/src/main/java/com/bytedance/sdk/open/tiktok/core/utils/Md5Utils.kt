package com.bytedance.sdk.open.tiktok.core.utils

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import java.security.MessageDigest

object Md5Utils {
    private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    fun hexDigest(string: String): String? {
        return try {
            hexDigest(string.toByteArray())
        } catch (e: Exception) {
            null
        }
    }

    fun hexDigest(bytes: ByteArray): String? {
        var res: String? = null
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(bytes)
            val tmp: ByteArray = messageDigest.digest()
            val charStr = CharArray(32)
            var k = 0
            for (i in hexDigits.indices) {
                val b = tmp[i].toInt()
                charStr[k++] = hexDigits[(b ushr 4) and 15]
                charStr[k++] = hexDigits[(b and 15)]
            }
            res = String(charStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }
}