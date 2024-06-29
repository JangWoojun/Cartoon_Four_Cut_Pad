package com.woojun.cartoon_four_cut_pad

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.woojun.cartoon_four_cut_pad.databinding.ActivityMainBinding
import com.woojun.cartoon_four_cut_pad.util.OnSingleClickListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.takePhotoButton.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                startActivity(Intent(this@MainActivity, SelectModeActivity::class.java))
            }
        })


    }
}