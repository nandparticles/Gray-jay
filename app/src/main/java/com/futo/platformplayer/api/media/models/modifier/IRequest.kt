package com.futo.platformplayer.api.media.models.modifier

interface IRequest {
    val method: String?;
    val url: String?;
    val headers: Map<String, String>?;
    val body: String?;
}