package com.futo.platformplayer.api.media.models.modifier

interface IRequest {
    val url: String?;
    val headers: Map<String, String>?;
    val method: String?;
    val body: String?;
}