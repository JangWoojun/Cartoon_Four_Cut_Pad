package com.woojun.cartoon_four_cut_pad.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.woojun.cartoon_four_cut_pad.R
import com.woojun.cartoon_four_cut_pad.databinding.ActivityPhotoBinding

class PhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}