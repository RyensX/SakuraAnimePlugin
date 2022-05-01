package com.su.sakuraanimeplugin.plugin.actions

import android.content.Context
import android.widget.Toast
import com.su.mediabox.pluginapi.v2.action.Action
import com.su.mediabox.pluginapi.v2.action.WebBrowserAction
import com.su.sakuraanimeplugin.plugin.components.CustomConst

/**
 * 注意不能使用匿名类自定义action
 */
class CustomAction : Action() {

    init {
        extraData = "打开网页"
    }

    override fun go(context: Context) {
        Toast.makeText(context, extraData!! as String, Toast.LENGTH_SHORT).show()
        WebBrowserAction.obtain(CustomConst.host).go(context)
    }

}