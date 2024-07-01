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
import com.woojun.cartoon_four_cut_pad.network.RetrofitAPI
import com.woojun.cartoon_four_cut_pad.network.RetrofitClient
import com.woojun.cartoon_four_cut_pad.util.Dialog.createLoadingDialog
import com.woojun.cartoon_four_cut_pad.util.FilterItemClickListener
import com.woojun.cartoon_four_cut_pad.util.OnSingleClickListener
import jp.wasabeef.glide.transformations.BlurTransformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilterActivity : AppCompatActivity(), FilterItemClickListener {
    private lateinit var binding: ActivityFilterBinding
    private val filterList = mutableListOf<String?>(null, null)
    private var name = ""

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

            getFilter { filterItemList ->
                binding.imageFrame1.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        val bottomSheet = FilterBottomSheetDialog(filterItemList.toMutableList(), 0, this@FilterActivity)
                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                    }
                })

                binding.imageFrame2.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        val bottomSheet = FilterBottomSheetDialog(filterItemList.toMutableList(), 1, this@FilterActivity)
                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                    }
                })

                binding.selectButton.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        if (filterList.filterNotNull().size == 2) {
                            startActivity(
                                Intent(this@FilterActivity, FrameActivity::class.java).apply {
                                    this.putExtra("name", name)
                                }
                            )
                        } else {
                            Toast.makeText(this@FilterActivity, "모든 사진의 필터를 선택해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }

        }

    }

    override fun onClick(item: Filter, index: Int) {
        filterList[index] = item.name
        if (name == "") name = item.name
        else name+="/${item.name}"
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

    private fun getFilter(callback: (List<Filter>) -> Unit) {
        val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
        val call: Call<List<Filter>> = retrofitAPI.getFilter()

        val (loadingDialog, setDialogText) = createLoadingDialog(this)
        loadingDialog.show()
        setDialogText("필터 로딩 중")

        call.enqueue(object : Callback<List<Filter>> {
            override fun onResponse(
                call: Call<List<Filter>>,
                response: Response<List<Filter>>
            ) {
                if (response.isSuccessful) {
                    setDialogText("필터 로딩 완료")
                    loadingDialog.dismiss()
                    callback(response.body()!!)
                } else {
                    setDialogText("필터 로딩 실패")
                    loadingDialog.dismiss()
                    Toast.makeText(this@FilterActivity, "A.I 필터를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                    callback(listOf())
                }
            }

            override fun onFailure(call: Call<List<Filter>>, t: Throwable) {
                setDialogText("필터 로딩 실패")
                loadingDialog.dismiss()
                Toast.makeText(this@FilterActivity, "A.I 필터를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                callback(listOf())
            }
        })
    }
}