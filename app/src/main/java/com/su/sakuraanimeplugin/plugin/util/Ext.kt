package com.su.sakuraanimeplugin.plugin.util

object Text {
    private val trimRegex = Regex("\\s+")
    fun String.trimAll() = trimRegex.replace(this, "")
    fun String.safeUrl() = replace("http:","https:").trimAll()
}