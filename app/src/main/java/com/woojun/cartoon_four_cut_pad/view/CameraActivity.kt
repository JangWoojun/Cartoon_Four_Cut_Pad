package com.woojun.cartoon_four_cut_pad.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Flash
import com.woojun.cartoon_four_cut_pad.R
import com.woojun.cartoon_four_cut_pad.database.BitmapData.setImage1
import com.woojun.cartoon_four_cut_pad.database.BitmapData.setImage2
import com.woojun.cartoon_four_cut_pad.databinding.ActivityCameraBinding
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File


class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var isFlash = false
    private var imageCount = 0

    companion object {
        const val CAMERA_PERMISSION_CODE = 999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
        setContentView(binding.root)

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Toast.makeText(this, "카메라 권한이 없어 사용하실 수 없습니다", Toast.LENGTH_SHORT).show()
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                        requestCameraPermission()
                    } else {
                        getPermissionMove()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
    }

    private fun getPermissionMove() {
        Toast.makeText(this, "카메라 권한을 설정에서 허용해주세요", Toast.LENGTH_SHORT).show()

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private val uCropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        overridePendingTransition(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.let { UCrop.getOutput(it) }
            if (uri != null) {
                when(imageCount) {
                    1 -> setImage1(getBitmap(uri))
                    2 -> {
                        setImage2(getBitmap(uri))
                        startActivity(Intent(this@CameraActivity, FilterActivity::class.java))
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "에러", Toast.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "에러", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        binding.cameraView.setLifecycleOwner(this)
        binding.cameraView.addCameraListener(object : CameraListener() {

            override fun onPictureTaken(result: PictureResult) {
                result.toBitmap { bitmap ->
                    if (bitmap != null) {
                        imageCount++
                        binding.countText.text = "${imageCount}/2"
                        binding.cameraView.post {
                            val option = UCrop.Options().apply {
                                val pinkColor = ContextCompat.getColor(this@CameraActivity, R.color.pink)
                                val whiteColor = ContextCompat.getColor(this@CameraActivity, R.color.white)

                                setToolbarColor(pinkColor)
                                setStatusBarColor(pinkColor)

                                setToolbarTitle("편집하기")
                                setRootViewBackgroundColor(whiteColor)
                            }
                            val uCropIntent = UCrop.of(getImageUri(bitmap), Uri.fromFile(File(cacheDir, "${System.currentTimeMillis()}.png")))
                                .withAspectRatio(500f, 500f)
                                .withMaxResultSize(500, 500)
                                .withOptions(option)
                                .getIntent(this@CameraActivity)

                            uCropLauncher.launch(uCropIntent)
                            overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
                        }
                    }
                }
                binding.captureButton.isEnabled = true
            }
            override fun onPictureShutter() {
            }
        })

        binding.switchButton.setOnClickListener {
            binding.cameraView.toggleFacing()
        }

        binding.captureButton.setOnClickListener {
            it.isEnabled = false
            binding.pulseCountDown.start {
                binding.cameraView.takePictureSnapshot()
            }
        }

        binding.flashButton.setOnClickListener {
            isFlash = !isFlash
            if (isFlash) {
                binding.flashButton.setImageResource(R.drawable.flash_on_icon)
                binding.cameraView.flash = Flash.TORCH
            } else {
                binding.flashButton.setImageResource(R.drawable.flash_off_icon)
                binding.cameraView.flash = Flash.OFF
            }
        }
    }

    private fun getBitmap(imageUri: Uri): Bitmap {
        return ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                contentResolver,
                imageUri
            )
        )
    }

    private fun getImageUri(image: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(this.contentResolver, image, "image", null)
        return Uri.parse(path)
    }
}