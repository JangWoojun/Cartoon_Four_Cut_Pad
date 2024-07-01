package com.woojun.cartoon_four_cut_pad.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.woojun.cartoon_four_cut_pad.R
import com.woojun.cartoon_four_cut_pad.data.Filter
import com.woojun.cartoon_four_cut_pad.database.BitmapData.getImage1
import com.woojun.cartoon_four_cut_pad.database.BitmapData.getImage2
import com.woojun.cartoon_four_cut_pad.databinding.ActivityFilterBinding
import com.woojun.cartoon_four_cut_pad.util.FilterItemClickListener
import com.woojun.cartoon_four_cut_pad.util.OnSingleClickListener
import jp.wasabeef.glide.transformations.BlurTransformation

class FilterActivity : AppCompatActivity(), FilterItemClickListener {
    private lateinit var binding: ActivityFilterBinding
    private val filterList = mutableListOf<String?>(null, null)
    private val filterItemList = mutableListOf<Filter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
        setContentView(binding.root)

        val isBitmapNotNull = (getImage1() != null && getImage2() != null)
        if (isBitmapNotNull) {

            setImageFrame(0)
            setImageFrame(1)
            Glide.with(this@FilterActivity)
                .load(getImage1())
                .centerCrop()
                .into(binding.image3)
            Glide.with(this@FilterActivity)
                .load(getImage2())
                .centerCrop()
                .into(binding.image4)

            binding.selectButton.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    if (filterList.filterNotNull().size == 2) {
                        startActivity(Intent(this@FilterActivity, FrameActivity::class.java))
                    } else {
                        Toast.makeText(this@FilterActivity, "모든 사진의 필터를 선택해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            binding.imageFrame1.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    val bottomSheet = FilterBottomSheetDialog(filterItemList, 0, this@FilterActivity)
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }
            })

            binding.imageFrame2.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    val bottomSheet = FilterBottomSheetDialog(filterItemList, 1, this@FilterActivity)
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }
            })
        }

    }

    override fun onClick(item: Filter, index: Int) {
        filterList[index] = item.name
        setImageFrame(index)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
    }

    private fun setImageFrame(index: Int) {
        when (index) {
            0 -> {
                Glide.with(this@FilterActivity)
                    .load(getImage1())
                    .centerCrop()
                    .into(binding.image1)
            }
            1 -> {
                Glide.with(this@FilterActivity)
                    .load(getImage2())
                    .centerCrop()
                    .into(binding.image2)
            }
            2 -> {
                Glide.with(this@FilterActivity)
                    .load(getImage1())
                    .transform(MultiTransformation(CenterCrop(), BlurTransformation(15, 3)))
                    .into(binding.image3)
            }
            3 -> {
                Glide.with(this@FilterActivity)
                    .load(getImage2())
                    .transform(MultiTransformation(CenterCrop(), BlurTransformation(15, 3)))
                    .into(binding.image4)
            }
        }
    }
}