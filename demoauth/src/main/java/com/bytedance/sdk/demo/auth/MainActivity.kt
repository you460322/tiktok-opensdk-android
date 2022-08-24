package com.bytedance.sdk.demo.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.auth.model.*
import com.bytedance.sdk.demo.auth.userinfo.UserInfoQuery
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.TikTokOpenConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.google.gson.Gson


class MainActivity : AppCompatActivity(), IApiEventHandler {
    private lateinit var scopesView: RecyclerView
    private lateinit var authTextView: TextView
    private lateinit var models: List<DataModel>
    private lateinit var tiktokApi: TikTokOpenApi
    private var webauthOnly: Boolean = false
    private val isBeta = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scopesView = findViewById(R.id.recycler_view)
        initData()
        scopesView.adapter = ScopeAdapter(models)
        scopesView.layoutManager = LinearLayoutManager(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (::tiktokApi.isInitialized) {
            tiktokApi.handleIntent(intent, this)
        }
    }

    private fun initData() {
        authTextView = findViewById(R.id.auth_button)
        authTextView.setOnClickListener {
            this.authorize()
        }
        models = mutableListOf<DataModel>().apply {
            add(initLogo())
            add(initConfigHeader())
            addAll(initConfigs())
            add(initScopeHeader())
            addAll(initScopes())
            addAll(initEditFields())
        }
        val tiktokOpenConfig = TikTokOpenConfig(BuildConfig.CLIENT_KEY)
        TikTokOpenApiFactory.init(tiktokOpenConfig)
    }

    private fun authorize() {
        val scopes = mutableListOf<String>()
        var additionalPermissions: Array<String>? = null
        var extraInfo: Map<String, String>? = null
        for (model in models) {
            when (model) {
                is ScopeModel -> {
                    model.title.takeIf { model.isOn.value ?: false }?.let { scopes.add(it) }
                }
                is EditTextModel -> {
                    model.gsonEditText()?.let {
                        val gson = Gson()
                        try {
                            when(model.contentType) {
                                ContentType.GSON_ARRAY -> {
                                    gson.fromJson(it, Array<String>::class.java).also { jsonArray ->
                                        additionalPermissions = jsonArray
                                    }
                                }
                                ContentType.GSON_OBJECT -> {
                                    (gson.fromJson(it, Map::class.java) as Map<String, String>)?.let { extraInfo = it }
                                }
                            }
                        } catch(e: Exception) {
                            showAlert("Input Parsing Error", "Parsing ${model.title} failed. It's of invalid format. Please try again.")
                            return@authorize
                        }
                    }
                }
            }
        }

        if (scopes.size == 0) {
            showAlert("Invalid Scope", "Please select at least one scope.")
            return
        }
         TikTokOpenApiFactory.create(this)?.let {
             tiktokApi = it
             val request = Auth.Request()
             request.scope = scopes.joinBy(",")
             request.state = "ww"
             request.callerLocalEntry = "MainActivity" // using the caller activity as the handler
             additionalPermissions?.let { permissions ->
                 if (permissions.isNotEmpty()) request.optionalScope0 = permissions[0]
                 if (permissions.size > 1) request.optionalScope1 = permissions[1]
             }
             it.authorize(request, webauthOnly)
        }
    }

    private fun showAlert(title: String, desc: String) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(desc)
        alertBuilder.setPositiveButton("OK") { dialog, _ -> dialog.cancel() }
        alertBuilder.create().show()
    }

    private fun initLogo(): LogoModel {
        return LogoModel()
    }
    private fun initConfigHeader(): HeaderModel {
        return HeaderModel("Config")
    }

    private fun initScopeHeader(): HeaderModel {
        return HeaderModel("Scope configuration")
    }

    private fun initConfigs(): List<ConfigModel> {
        val webAuth =  MutableLiveData<Boolean>()
        webAuth.observeForever { isOn ->
            webauthOnly = isOn
        }
        val webAuthModel = ConfigModel("Always in Web", "Always authorize in webview", webAuth)

        val betaMode =  MutableLiveData<Boolean>()
        betaMode.observeForever { isOn ->
            isBeta.postValue(isOn)
        }
        val betaModeModel = ConfigModel("Beta mode", "Some permissions are only available in Beta mode", betaMode)
        return arrayListOf(webAuthModel, betaModeModel)
    }

    private fun initScopes(): List<ScopeModel> {
        val scopes = arrayListOf("user.info.basic", "user.info.name", "user.info.phone",
                "user.info.email", "music.collection", "video.upload", "video.list", "user.ue")
        val descriptions = arrayListOf("Read your profile info (avatar, display name)",
                "Read username", "Read user phone number", "Read user email address", "Read songs added to your favorites on TikTok",
        "Read user's uploaded videos", "Read your public videos on TikTok", "Read user interests")
        val beans = scopes.zip(descriptions) { scope, desc ->
            ScopeModel(scope, desc,  MutableLiveData<Boolean>(false))
        }
        beans[0].isEnabled.postValue(true)
        isBeta.observeForever { isBeta ->
            for (i in 1 until beans.size) {
                beans[i].isEnabled.postValue(isBeta)
            }
        }

        return beans
    }
    private fun initEditFields(): List<EditTextModel> {
        val additionalPermission = EditTextModel("Additional Permissions", "Separated by comma, for example: \"permission1\",\"permission2\"(at most 2)\nGo to TT4D portal for more information", ContentType.GSON_ARRAY)
        val extraInfo = EditTextModel("Extra Info", "Paired by comma and separated by comma, for example: \"name1:value1, name2:value2\"\nGo to TT4D portal for more information", ContentType.GSON_OBJECT)
        return arrayListOf(additionalPermission, extraInfo)
    }
    private fun getUserBasicInfo(authCode: String) {
        UserInfoQuery.getAccessToken(authCode) { atInfo, errorMsg ->
            errorMsg?.let {
                showAlert("Access Token Error", it)
                return@getAccessToken
            }

            UserInfoQuery.getUserInfo(atInfo!!.accessToken, atInfo!!.openid) { userInfo, errorMsg ->
                errorMsg?.let {
                    return@getUserInfo showAlert("User Info Error", it)
                }
                showAlert("Getting user info succeeded", "Display name: ${userInfo!!.nickName}")
            }
        }
    }

    //  IApiEventHandler
    override fun onReq(req: Base.Request?) {

    }

    override fun onResp(resp: Base.Response?) {
        (resp as Auth.Response)?.let { authRespnose ->
            if (!authRespnose.authCode.isNullOrEmpty()) {
                getUserBasicInfo(authRespnose.authCode!!)
            } else if (authRespnose.errorCode != 0) {
                showAlert("Error", "Error Code: ${authRespnose.errorCode}\nError message: ${authRespnose.errorMsg}")
            }
        }
    }

    override fun onErrorIntent(intent: Intent?) {

    }
}