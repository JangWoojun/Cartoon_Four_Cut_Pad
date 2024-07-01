package com.woojun.cartoon_four_cut_pad.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.woojun.cartoon_four_cut_pad.databinding.ActivitySelectModeBinding

class SelectModeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectModeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}