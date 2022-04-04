package com.su.mediabox.plugin

import com.su.mediabox.pluginapi.v2.action.ClassifyAction
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.pluginapi.v2.been.ClassifyItemData
import com.su.mediabox.pluginapi.v2.components.IMediaClassifyDataComponent
import org.jsoup.select.Elements

class CustomMediaClassifyDataComponent : IMediaClassifyDataComponent {

    override suspend fun getClassifyItemData(): List<ClassifyItemData> {
        val classifyItemDataList = mutableListOf<ClassifyItemData>()
        val document = JsoupUtil.getDocument(CustomConst.host + "/a/")
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
        var url = CustomConst.host + classifyAction.url
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