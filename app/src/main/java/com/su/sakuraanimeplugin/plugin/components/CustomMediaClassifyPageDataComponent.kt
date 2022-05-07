package com.su.sakuraanimeplugin.plugin.components

import com.su.mediabox.pluginapi.action.ClassifyAction
import com.su.mediabox.pluginapi.components.IMediaClassifyPageDataComponent
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.data.ClassifyItemData
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import com.su.sakuraanimeplugin.plugin.util.ParseHtmlUtil
import org.jsoup.select.Elements

class CustomMediaClassifyPageDataComponent : IMediaClassifyPageDataComponent {

    override suspend fun getClassifyItemData(): List<ClassifyItemData> {
        val classifyItemDataList = mutableListOf<ClassifyItemData>()
        val document = JsoupUtil.getDocument(Const.host + "/a/")
        val areaElements: Elements = document.getElementsByClass("area")
        for (i in areaElements.indices) {
            val areaChildren: Elements = areaElements[i].children()
            for (j in areaChildren.indices) {
                when (areaChildren[j].className()) {
                    "ters" -> {
                        classifyItemDataList.addAll(ParseHtmlUtil.parseClassifyEm(areaChildren[j]))
                    }
                }
            }
        }
        return classifyItemDataList
    }

    override suspend fun getClassifyData(
        classifyAction: ClassifyAction,
        page: Int
    ): List<BaseData> {
        val classifyList = mutableListOf<BaseData>()
        var url = Const.host + classifyAction.url
        if (page > 1)
            url += "/${page}.html"
        val document = JsoupUtil.getDocument(url)
        val areaElements: Elements = document.getElementsByClass("area")

        for (area in areaElements)
            for (target in area.children())
                when (target.className()) {
                    "fire l" -> {
                        val fireLChildren: Elements = target.children()
                        for (k in fireLChildren.indices) {
                            when (fireLChildren[k].className()) {
                                "lpic" -> {
                                    classifyList.addAll(
                                        ParseHtmlUtil.parseSearchEm(
                                            fireLChildren[k],
                                            url
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
        return classifyList
    }

}