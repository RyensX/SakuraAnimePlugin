package com.su.sakuraanimeplugin.plugin

import com.su.mediabox.pluginapi.components.*
import com.su.mediabox.pluginapi.IPluginFactory
import com.su.mediabox.pluginapi.util.PluginPreferenceIns
import com.su.sakuraanime2plugin.plugin.components.MediaUpdateDataComponent
import com.su.sakuraanimeplugin.plugin.components.*
import com.su.sakuraanimeplugin.plugin.danmaku.OyydsDanmaku

/**
 * 每个插件必须实现本类
 *
 * 注意包和类名都要相同，且必须提供公开的无参数构造方法
 */
class PluginFactory : IPluginFactory() {

    override val host: String = Const.host

    override fun pluginLaunch() {
        PluginPreferenceIns.initKey(OyydsDanmaku.OYYDS_DANMAKU_ENABLE, defaultValue = true)
    }

    override fun <T : IBasePageDataComponent> createComponent(clazz: Class<T>) = when (clazz) {
        IVideoPlayPageDataComponent::class.java -> CustomVideoPlayPageDataComponent()
        IMediaSearchPageDataComponent::class.java -> CustomMediaSearchPageDataComponent()
        IMediaDetailPageDataComponent::class.java -> CustomMediaDetailPageDataComponent()
        IMediaClassifyPageDataComponent::class.java -> CustomMediaClassifyPageDataComponent()
        IHomePageDataComponent::class.java -> CustomHomePageDataComponent()
        //自定义页面，需要使用具体类而不是它的基类（接口）
        RankPageDataComponent::class.java -> RankPageDataComponent()
        UpdateTablePageDataComponent::class.java -> UpdateTablePageDataComponent()
        IMediaUpdateDataComponent::class.java -> MediaUpdateDataComponent
        else -> null
    } as? T

}