package com.su.sakuraanimeplugin

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.su.mediabox.plugin.ComponentFactory
import com.su.mediabox.pluginapi.v2.components.IVideoDetailDataComponent
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * 组件单元测试示例
 */
@RunWith(AndroidJUnit4::class)
class DataComponentUnitTest {
    @Test
    fun testVideoDetailDataComponent() = runBlocking {
        val factory = ComponentFactory()
        val detailDataComponent =
            factory.createComponent(IVideoDetailDataComponent::class.java)?.apply {
                val data = getAnimeDetailData("/show/5540.html")
                assert(data.first.isNotBlank())
                assert(data.second.isNotBlank())
                assert(!data.third.isNullOrEmpty())

                Log.d("名称", data.second)
                Log.d("封面", data.first)
                Log.d("其他详情数据", "数量:${data.third.size}")
                data.third.forEach {
                    Log.d("数据", it.toString())
                }
            }
        assertNotNull(detailDataComponent)
    }
}