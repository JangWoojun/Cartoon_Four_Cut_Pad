package com.woojun.cartoon_four_cut_pad.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.woojun.cartoon_four_cut_pad.databinding.ActivityMainBinding
import com.woojun.cartoon_four_cut_pad.util.OnSingleClickListener
import com.woojun.cartoon_four_cut_pad.util.VisibilityControlListener

class MainActivity : AppCompatActivity(), VisibilityControlListener {
    private lateinit var binding: ActivityMainBinding

    private var backPressedTime: Long = 0
    private val backPressInterval: Long = 2000

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

    override fun onBackPressed() {
        if (backPressedTime + backPressInterval > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    override fun onVisibilityChange(visible: Boolean) {
        if (!visible) {
            binding.defaultImage.visibility = View.GONE
            binding.photoRecyclerView.visibility = View.VISIBLE
        } else {
            binding.defaultImage.visibility = View.VISIBLE
            binding.photoRecyclerView.visibility = View.GONE
        }
    }
}