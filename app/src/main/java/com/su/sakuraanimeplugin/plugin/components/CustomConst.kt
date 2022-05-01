package com.su.sakuraanimeplugin.plugin.components

import com.su.mediabox.pluginapi.components.IConstComponent

object CustomConst : IConstComponent {

    const val ANIME_RANK = "/top/"
    const val ANIME_SEARCH = "/search/"

    override val host: String = "http://www.yinghuacd.com"

    override val refererProcessor = object : IConstComponent.RefererProcessor {
        override fun processor(url: String) = host
    }

}
