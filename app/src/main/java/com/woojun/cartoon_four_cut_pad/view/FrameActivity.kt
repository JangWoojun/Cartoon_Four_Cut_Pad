package com.woojun.cartoon_four_cut_pad.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.woojun.cartoon_four_cut_pad.BuildConfig
import com.woojun.cartoon_four_cut_pad.R
import com.woojun.cartoon_four_cut_pad.adapter.FrameViewPagerAdapter
import com.woojun.cartoon_four_cut_pad.data.Frame
import com.woojun.cartoon_four_cut_pad.data.FrameResponse
import com.woojun.cartoon_four_cut_pad.database.BitmapData.getImage1
import com.woojun.cartoon_four_cut_pad.database.BitmapData.getImage2
import com.woojun.cartoon_four_cut_pad.databinding.ActivityFrameBinding
import com.woojun.cartoon_four_cut_pad.network.RetrofitAPI
import com.woojun.cartoon_four_cut_pad.network.RetrofitClient
import com.woojun.cartoon_four_cut_pad.util.Dialog.createLoadingDialog
import com.woojun.cartoon_four_cut_pad.util.OnSingleClickListener
import com.woojun.cartoon_four_cut_pad.util.Utils.dpToPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class FrameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFrameBinding
    private var frameIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrameBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")!!

        CoroutineScope(Dispatchers.Main).launch {
            val list = generateFrameItemList(name)
            val isBitmapNotNull = (getImage1() != null && getImage2() != null)

            if (isBitmapNotNull) {
                val adapter = FrameViewPagerAdapter(list)
                binding.viewPager.apply {
                    this.adapter = adapter
                    offscreenPageLimit = 1

                    val pageMargin = this@FrameActivity.dpToPx(8f)
                    val offset = this@FrameActivity.dpToPx(16f)
                    setPageTransformer { page, position ->
                        val pageOffset = position * -(2 * offset + pageMargin)
                        if (ViewCompat.getLayoutDirection(binding.viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                            page.translationX = -pageOffset
                        } else {
                            page.translationX = pageOffset
                        }
                    }
                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            frameIndex = position
                        }
                    })
                }

                binding.selectButton.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        val (loadingDialog, setDialogText) = createLoadingDialog(this@FrameActivity)
                        loadingDialog.show()
                        setDialogText("세팅 중")

                        val intent = Intent(this@FrameActivity, DownloadActivity::class.java)
                        uploadAiImages(name, loadingDialog, setDialogText) { uploadResponse ->
                            intent.putStringArrayListExtra("fileName", arrayListOf(uploadResponse[0], uploadResponse[1]))
                            intent.putExtra("frameName", list[frameIndex].frameResponse.name)
                            loadingDialog.dismiss()
                            startActivity(intent)
                        }

                    }
                })

            }

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
    }

    private fun createFrameItem(name: String, response: FrameResponse): Frame {
        return Frame(
            name,
            response,
            mutableListOf(
                getImage1()!!,
                getImage2()!!
            )
        )
    }

    private suspend fun getFrame(): List<FrameResponse>? {
        val (loadingDialog, setDialogText) = createLoadingDialog(this)
        loadingDialog.show()
        setDialogText("프레임 로딩 중")

        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getFrame()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        setDialogText("프레임 로딩 완료")
                        loadingDialog.dismiss()
                    }
                    response.body()
                } else {
                    withContext(Dispatchers.Main) {
                        setDialogText("프레임 로딩 실패")
                        loadingDialog.dismiss()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@FrameActivity, "다른 프레임을 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                setDialogText("네트워크 오류 발생")
                loadingDialog.dismiss()
            }
            null
        }
    }

    private suspend fun generateFrameItemList(name: String): List<Frame> {
        return withContext(Dispatchers.Main) {
            val list = mutableListOf(createFrameItem(
                name,
                FrameResponse("", "", "", "카툰네컷 기본 프레임 - 화이트")
            ))
            getFrame()?.forEach {
                list.add(createFrameItem(name, it))
            }

            list
        }
    }

    private fun uploadAiImages(type: String, loadingDialog: android.app.Dialog, setDialogText: ((String) -> Unit), callback: (List<String>) -> Unit) {
        setDialogText("이미지 업로드 중")

        val images = listOf(getImage1()!!, getImage2()!!)
        val imageParts = createPartsFromBitmaps(images, System.currentTimeMillis().toString())

        val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
        val call: Call<List<String>> = retrofitAPI.putImage(BuildConfig.apiKey, type, imageParts)

        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                if (response.isSuccessful) {
                    setDialogText("업로드 완료")
                    callback(response.body()!!)
                } else {
                    setDialogText("업로드 실패")
                    loadingDialog.dismiss()
                    Toast.makeText(this@FrameActivity, "A.I 필터는 사용은\n하루 최대 10번만 가능합니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                setDialogText("변환 실패")
                loadingDialog.dismiss()
                Toast.makeText(this@FrameActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createPartsFromBitmaps(files: List<Bitmap>, name: String): List<MultipartBody.Part> {
        val parts = mutableListOf<MultipartBody.Part>()

        for ((index, bitmap) in files.withIndex()) {
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val imageData = bos.toByteArray()

            val requestFile = RequestBody.create(MediaType.parse("image/*"), imageData)

            val part = MultipartBody.Part.createFormData("images", "$name-$index.jpg", requestFile)
            parts.add(part)
        }

        return parts
    }
}