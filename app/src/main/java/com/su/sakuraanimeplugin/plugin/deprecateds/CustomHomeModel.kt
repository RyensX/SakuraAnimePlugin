package com.su.sakuraanimeplugin.plugin.deprecateds

import com.su.mediabox.pluginapi.been.TabBean
import com.su.mediabox.pluginapi.components.IHomeComponent
import com.su.sakuraanimeplugin.plugin.components.CustomConst
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomHomeModel : IHomeComponent {
    override suspend fun getAllTabData(): ArrayList<TabBean> {
        return ArrayList<TabBean>().apply {
            val document = JsoupUtil.getDocument(CustomConst.host)
            val menu: Elements = document.getElementsByClass("menu")
            val dmx_l: Elements = menu.select("[class=dmx l]").select("li")
            for (i in dmx_l.indices) {
                val url = dmx_l[i].select("a").attr("href")
                add(TabBean("", url, CustomConst.host + url, dmx_l[i].text()))
            }
            val dme_r: Elements = menu.select("[class=dme r]").select("li")
            for (i in dme_r.indices) {
                val url = dme_r[i].select("a").attr("href")
                add(TabBean("", url, CustomConst.host + url, dme_r[i].text()))
            }
        }
    }
}