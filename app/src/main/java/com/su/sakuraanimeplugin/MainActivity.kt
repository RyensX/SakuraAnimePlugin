package com.su.sakuraanimeplugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.su.sakuraanimeplugin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pluginInfo.text = "樱花动漫MediaBox插件"
    }
}