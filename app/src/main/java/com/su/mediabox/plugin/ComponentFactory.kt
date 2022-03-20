package com.su.mediabox.plugin

import com.su.mediabox.pluginapi.components.*
import com.su.mediabox.pluginapi.IComponentFactory
import com.su.mediabox.pluginapi.v2.components.IVideoDetailDataComponent
import com.su.mediabox.pluginapi.v2.components.IVideoPlayComponent

/**
 * 每个插件必须实现本类
 *
 * 注意包和类名都要相同，且必须提供公开的无参数构造方法
 */
class ComponentFactory : IComponentFactory() {

    override fun <T : IBaseComponent> createComponent(clazz: Class<T>) = when (clazz) {
        IMonthAnimeComponent::class.java -> CustomMonthAnimeModel()
        IAnimeShowComponent::class.java -> CustomAnimeShowModel()
        IClassifyComponent::class.java -> CustomClassifyModel()
        IConstComponent::class.java -> CustomConst
        IEverydayAnimeComponent::class.java -> CustomEverydayAnimeModel()
        IHomeComponent::class.java -> CustomHomeModel()
        IVideoPlayComponent::class.java -> CustomVideoPlayComponent()
        IPlayComponent::class.java -> CustomPlayModel()
        IRankListComponent::class.java -> CustomRankListModel()
        IRankComponent::class.java -> CustomRankModel()
        ISearchComponent::class.java -> CustomSearchModel()
        IVideoDetailDataComponent::class.java -> CustomVideoDetailDataComponent()
        else -> null
    } as? T

}