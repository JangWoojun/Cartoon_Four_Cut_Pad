package com.woojun.cartoon_four_cut_pad.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.print.PrintHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.woojun.cartoon_four_cut_pad.R
import com.woojun.cartoon_four_cut_pad.data.PostPrintItem
import com.woojun.cartoon_four_cut_pad.data.Print
import com.woojun.cartoon_four_cut_pad.databinding.ActivityDownloadBinding
import com.woojun.cartoon_four_cut_pad.network.RetrofitAPI
import com.woojun.cartoon_four_cut_pad.network.RetrofitClient
import com.woojun.cartoon_four_cut_pad.util.Dialog.createLoadingDialog
import com.woojun.cartoon_four_cut_pad.util.OnSingleClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep

class DownloadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDownloadBinding
    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fileName = intent.getStringArrayListExtra("fileName")
        val frameName = intent.getStringExtra("frameName")!!

        val (loadingDialog, setDialogText) = createLoadingDialog(this)
        loadingDialog.show()
        setDialogText("사진 제작 중")

        if (fileName != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val frameImage = postPrint(PostPrintItem(frameName, fileName))
                withContext(Dispatchers.Main) {
                    frameImage.let {
                        Glide.with(this@DownloadActivity)
                            .load(frameImage!!.preview)
                            .centerCrop()
                            .into(binding.imageView)
                        loadImageFromUrl(frameImage.result)
                    }
                    loadingDialog.dismiss()
                }
            }
        } else {
            Toast.makeText(this@DownloadActivity, "이미지를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
        }

        binding.selectButton.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val intent = Intent(this@DownloadActivity, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.anim_fade_out, R.anim.vertical_exit)
            }
        })

        binding.printButton.setOnClickListener {
            if (this::imageBitmap.isInitialized) doPhotoPrint(imageBitmap)
            else Toast.makeText(this@DownloadActivity, "다시 시도해주세요", Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadImageFromUrl(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageBitmap = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private suspend fun postPrint(postPrintItem: PostPrintItem): Print? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.postPrint(postPrintItem)
                if (response.isSuccessful) {
                    Log.d("확인", "잘 옴")
                    response.body()
                } else {
                    Log.d("확인", "에러났음")
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@DownloadActivity, "다른 프레임을 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    private fun doPhotoPrint(bitmap: Bitmap) {
        this.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FILL
            }.also { printHelper ->
                printHelper.printBitmap("세명컴퓨터고등학교 - 카툰네컷", bitmap)
            }
        }
    }

}