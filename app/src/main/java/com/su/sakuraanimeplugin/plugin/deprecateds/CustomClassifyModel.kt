package com.su.sakuraanimeplugin.plugin.deprecateds

import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.ClassifyBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.IClassifyComponent
import com.su.sakuraanimeplugin.plugin.components.CustomConst
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import com.su.sakuraanimeplugin.plugin.util.ParseHtmlUtil
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomClassifyModel : IClassifyComponent {
    override suspend fun getClassifyData(partUrl: String): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val classifyList: ArrayList<AnimeCoverBean> = ArrayList()
        var pageNumberBean: PageNumberBean? = null
        val url = CustomConst.host + partUrl
        val document = JsoupUtil.getDocument(url)
        val areaElements: Elements = document.getElementsByClass("area")
        for (i in areaElements.indices) {
            val areaChildren: Elements = areaElements[i].children()
            for (j in areaChildren.indices) {
                when (areaChildren[j].className()) {
                    "fire l" -> {
                        val fireLChildren: Elements = areaChildren[j].children()
                        for (k in fireLChildren.indices) {
                            when (fireLChildren[k].className()) {
                                "lpic" -> {
                                    classifyList.addAll(
                                        ParseHtmlUtil.parseLpic(
                                            fireLChildren[k],
                                            url
                                        )
                                    )
                                }
                                "pages" -> {
                                    pageNumberBean = ParseHtmlUtil.parseNextPages(fireLChildren[k])
                                }
                            }
                        }
                    }
                }
            }
        }
        return Pair(classifyList, pageNumberBean)
    }

    override suspend fun getClassifyTabData(): ArrayList<ClassifyBean> {
        val classifyTabList: ArrayList<ClassifyBean> = ArrayList()
        val document = JsoupUtil.getDocument(CustomConst.host + "/a/")
        val areaElements: Elements = document.getElementsByClass("area")
        for (i in areaElements.indices) {
            val areaChildren: Elements = areaElements[i].children()
            for (j in areaChildren.indices) {
                when (areaChildren[j].className()) {
                    "ters" -> {
                        classifyTabList.addAll(ParseHtmlUtil.parseTers(areaChildren[j]))
                    }
                }
            }
        }
        return classifyTabList
    }
}
