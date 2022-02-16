package com.su.sakuraanimeplugin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.su.sakuraanimeplugin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pluginInfo.text = "这是樱花动漫MediaBox插件的主界面"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("关于")?.apply {
            intent = Intent(this@MainActivity, OtherActivity::class.java)
        }
        return true
    }
}