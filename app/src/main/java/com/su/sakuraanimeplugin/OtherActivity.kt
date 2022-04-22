package com.su.sakuraanimeplugin

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.su.mediabox.plugin.ComponentFactory
import com.su.mediabox.plugin.CustomConst
import com.su.sakuraanimeplugin.databinding.ActivityOtherBinding

class OtherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtherBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.text = """
            插件API版本：${
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            ).metaData?.getInt("media_plugin_api_version", -1)
        }
            数据源地址： ${CustomConst.host}
            开源地址：https://github.com/RyensX/SakuraAnimePlugin
            
            这是一个解析樱花动漫的MediaBox插件示例
            
            免责声明
            ·此软件只提供数据展示，不提供原始数据，和普通浏览器功能类似。
            ·此软件显示的所有内容，其版权均归原作者所有。
            ·此软件仅可用作学习交流，未经授权，禁止用于其他用途，请在下载24小时内删除。
            ·因使用此软件产生的版权问题，软件作者概不负责。
        """.trimIndent()
    }
}