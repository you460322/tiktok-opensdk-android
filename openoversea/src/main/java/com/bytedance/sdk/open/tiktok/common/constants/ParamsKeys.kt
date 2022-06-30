package com.bytedance.sdk.open.tiktok.common.constants

object Keys {
    const val IMAGE_PATH = "AWEME_EXTRA_IMAGE_MESSAGE_PATH"
    const val VIDEO_PATH = "AWEME_EXTRA_VIDEO_MESSAGE_PATH"
    const val OAUTH_PATH = "/oauth/authorize/" // TODO: chen.wu remove?
    const val REDIRECT_URL_PATH = "/oauth/authorize/callback/"
    const val SDK_VERSION_ERROR = -1
    const val SDK_VERSION_KEY = "BD_PLATFORM_SDK_VERSION"
    const val AUTH_REQUEST_CODE = 100
//    cosnt val SHARE_TO_IM_CODE = 101
    const val VERSION = "1"

    object Base {
        const val TYPE = "_bytedance_params_type";
        const val EXTRA = "_bytedance_params_extra"
        const val ERROR_CODE = "_bytedance_params_error_code"
        const val ERROR_MSG = "_bytedance_params_error_msg"
        const val FROM_ENTRY = "_bytedance_params_from_entry"
        const val CALLER_PKG = "_bytedance_params_type_caller_package"
        const val CALLER_BASE_OPEN_VERSION = "__bytedance_base_caller_version"
        const val CALLER_BASE_OPEN_SDK_NAME = "_aweme_params_caller_open_sdk_name"
        const val CALLER_BASE_OPEN_SDK_VERSION = "_aweme_params_caller_open_sdk_version"
        const val CALLER_BASE_OPEN_SDK_COMMON_NAME = "_aweme_params_caller_open_sdk_common_name"
        const val CALLER_BASE_OPEN_SDK_COMMON_VERSION = "_aweme_params_caller_open_sdk_common_version"
    }
    object Auth {
        const val AUTH_CODE = "_bytedance_params_authcode"
        const val CLIENT_KEY = "_bytedance_params_client_key"
        const val STATE = "_bytedance_params_state"
        const val GRANTED_PERMISSION = "_bytedance_params_granted_permission"
        const val REDIRECT_URI = "_bytedance_params_redirect_uri"
        const val SCOPE = "_bytedance_params_scope"
        const val OPTIONAL_SCOPE0 = "_bytedance_params_optional_scope0"
        const val OPTIONAL_SCOPE1 = "_bytedance_params_optional_scope1"
        const val LANGUAGE = "language"
    }
    object Share {
        const val STATE = "_aweme_open_sdk_params_state"
        const val CLIENT_KEY = "_aweme_open_sdk_params_client_key"
        const val CALLER_PKG = "_aweme_open_sdk_params_caller_package"
        const val CALLER_SDK_VERSION = "_aweme_open_sdk_params_caller_sdk_version"
        const val CALLER_LOCAL_ENTRY = "_aweme_open_sdk_params_caller_local_entry"
        const val SHARE_TARGET_SCENE = "_aweme_open_sdk_params_target_landpage_scene"
        const val SHARE_DEFAULT_HASHTAG = "_aweme_open_sdk_params_target_scene"
        const val SHARE_HASHTAG_LIST = "_aweme_open_sdk_params_hashtag_list"
        const val SHARE_FORMAT = "_aweme_open_sdk_params_share_format"
        const val SHARE_MICROAPP_INFO = "_aweme_open_sdk_params_micro_app_info"
        const val TYPE = "_aweme_open_sdk_params_type"
        const val ERROR_CODE = "_aweme_open_sdk_params_error_code"
        const val ERROR_MSG = "_aweme_open_sdk_params_error_msg"
        const val SHARE_SUB_ERROR_CODE = "_aweme_open_sdk_params_sub_error_code"
        const val SHARE_ANCHOR_INFO = "_aweme_open_sdk_params_anchor_info"
        const val OPENPLATFORM_EXTRA = "extra"
        const val ANCHOR_SOURCE_TYPE = "anchor_source_type"
        const val EXTRA_SHARE_OPTIONS = "_aweme_open_sdk_extra_share_options"
        const val DISABLE_MUSIC_SELECTION = "tiktok_videokit_disable_music_selection"
    }
    object Web {
        const val QUERY_RESPONSE_TYPE = "response_type"
        const val QUERY_REDIRECT_URI = "redirect_uri"
        const val QUERY_CLIENT_KEY = "client_key"
        const val QUERY_STATE = "state"
        const val QUERY_FROM = "from"
        const val QUERY_SCOPE = "scope"
        const val QUERY_OPTIONAL_SCOPE = "optionalScope"
        const val QUERY_SIGNATURE = "signature"
        const val VALUE_FROM_OPENSDK = "opensdk"
        const val VALUE_RESPONSE_TYPE_CODE = "code"
        const val SCHEMA_HTTPS = "https"
        const val REDIRECT_QUERY_CODE = "code"
        const val REDIRECT_QUERY_STATE = "state"
        const val REDIRECT_QUERY_ERROR_CODE = "errCode"
        const val REDIRECT_QUERY_ERROR_MESSAGE = "error_string"
        const val REDIRECT_QUERY_SCOPE = "scopes"
        const val QUERY_ENCRIPTION_PACKAGE = "app_identity"
        const val QUERY_PLATFORM = "device_platform"
        const val QUERY_ACCEPT_LANGUAGE = "accept_language"
    }
    object API {
        const val AUTH_REQUIRE_API = 1
        // sharing
        const val SHARE_REQUIRED_MIN_VERSION = 2
        //adapt to old version because of changing api name
        const val MIN_SDK_NEW_VERSION_API = 3
        //skipping 4 as Tiktok repo already was using version 4
        const val SHARE_SUPPORT_FILEPROVIDER = 5
        //Special for Lite Authorize
        const val AUTHORIZE_FOR_TIKTOK_LITE = 6
    }
    object Scene { // TODO: chen.wu remove? not used?
        const val LANDPAGE_SCENE_DEFAULT = 0
        const val LANDPAGE_SCENE_EDIT = 1
        const val LANDPAGE_SCENE_CUT = 2
    }

}