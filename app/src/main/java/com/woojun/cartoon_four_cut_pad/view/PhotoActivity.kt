package com.woojun.cartoon_four_cut_pad.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.woojun.cartoon_four_cut_pad.R
import com.woojun.cartoon_four_cut_pad.database.BitmapData
import com.woojun.cartoon_four_cut_pad.databinding.ActivityPhotoBinding
import com.woojun.cartoon_four_cut_pad.util.OnSingleClickListener
import com.yalantis.ucrop.UCrop
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoBinding
    private var selectFrame: Int = 1
    private val imageList: MutableList<Pair<Int, Uri>?> = mutableListOf(null, null)

    private var pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val destinationFileName = "${System.currentTimeMillis()}.png"
            val option = UCrop.Options().apply {
                val pinkColor = ContextCompat.getColor(this@PhotoActivity, R.color.pink)
                val whiteColor = ContextCompat.getColor(this@PhotoActivity, R.color.white)

                setToolbarColor(pinkColor)
                setStatusBarColor(pinkColor)

                setToolbarTitle("편집하기")
                setRootViewBackgroundColor(whiteColor)
            }

            val uCropIntent = UCrop.of(uri, Uri.fromFile(File(cacheDir, destinationFileName)))
                .withAspectRatio(500f, 500f)
                .withMaxResultSize(500, 500)
                .withOptions(option)
                .getIntent(this@PhotoActivity)

            uCropLauncher.launch(uCropIntent)
            overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
        }
    }


    private val uCropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        overridePendingTransition(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.let { UCrop.getOutput(it) }
            if (uri != null) {
                when (selectFrame) {
                    1 -> {
                        Glide.with(this@PhotoActivity)
                            .load(uri)
                            .centerCrop()
                            .into(binding.image1)
                        Glide.with(this@PhotoActivity)
                            .load(uri)
                            .transform(MultiTransformation(CenterCrop(), BlurTransformation(15, 3)))
                            .into(binding.image3)
                        imageList[0] = (Pair(selectFrame, uri))
                    }
                    2 -> {
                        Glide.with(this@PhotoActivity)
                            .load(uri)
                            .centerCrop()
                            .into(binding.image2)
                        Glide.with(this@PhotoActivity)
                            .load(uri)
                            .transform(MultiTransformation(CenterCrop(), BlurTransformation(15, 3)))
                            .into(binding.image4)
                        imageList[1] = (Pair(selectFrame, uri))
                    }
                }
            } else {
                Toast.makeText(this, "에러", Toast.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "에러", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
        setContentView(binding.root)

        binding.imageFrame1.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                selectFrame = 1
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        })

        binding.imageFrame2.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                selectFrame = 2
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        })

        binding.selectButton.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (imageList.filterNotNull().size == 2) {
                    CoroutineScope(Dispatchers.IO).launch {
                        imageList.forEach {
                            val bitmap = getBitmap(it!!.second)
                            when (it.first) {
                                1 -> BitmapData.setImage1(bitmap)
                                2 -> BitmapData.setImage2(bitmap)
                            }
                        }
                        withContext(Dispatchers.Main) {
                            overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
                            startActivity(Intent(this@PhotoActivity, FilterActivity::class.java))
                        }
                    }
                } else {
                    Toast.makeText(this@PhotoActivity, "모든 사진을 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.backButton.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                finish()
                overridePendingTransition(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
    }

    private fun getBitmap(imageUri: Uri): Bitmap {
        return ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                contentResolver,
                imageUri
            )
        )
    }
}