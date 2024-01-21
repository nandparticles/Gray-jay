package com.futo.platformplayer.api.media.platforms.js.models

import com.caoccao.javet.values.reference.V8ValueObject
import com.futo.platformplayer.api.media.models.modifier.IRequest
import com.futo.platformplayer.api.media.models.modifier.IRequestModifier
import com.futo.platformplayer.api.media.platforms.js.JSClient
import com.futo.platformplayer.engine.IV8PluginConfig
import com.futo.platformplayer.engine.V8Plugin
import com.futo.platformplayer.engine.exceptions.ScriptImplementationException
import com.futo.platformplayer.getOrNull

class JSRequestModifier: IRequestModifier {
    private val _plugin: JSClient;
    private val _config: IV8PluginConfig;
    private var _modifier: V8ValueObject;
    override var allowByteSkip: Boolean;

    constructor(plugin: JSClient, modifier: V8ValueObject) {
        this._plugin = plugin;
        this._modifier = modifier;
        this._config = plugin.config;
        val config = plugin.config;

        allowByteSkip = modifier.getOrNull(config, "allowByteSkip", "JSRequestModifier") ?: true;

        if(!modifier.has("modifyRequest"))
            throw ScriptImplementationException(config, "RequestModifier is missing modifyRequest", null);
    }

    override fun modifyRequest(url: String, headers: Map<String, String>, method: String, body: String?): IRequest {
        if (_modifier.isClosed) {
            return Request(url, headers, method, body);
        }

        val result = V8Plugin.catchScriptErrors<Any>(_config, "[${_config.name}] JSRequestModifier", "builder.modifyRequest()") {
            _modifier.invoke("modifyRequest", url, headers, method, body);
        } as V8ValueObject;

        val req = JSRequest(_plugin, result, url, headers, method, body);
        result.close();
        return req;
    }


    data class Request(override val url: String, override val headers: Map<String, String>, override val method: String, override val body: String?) : IRequest;
}