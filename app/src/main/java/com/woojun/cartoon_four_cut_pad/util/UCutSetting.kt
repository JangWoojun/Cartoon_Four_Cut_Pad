package com.woojun.cartoon_four_cut_pad.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.core.content.ContextCompat
import com.yalantis.ucrop.UCrop
import java.io.File

object UCutSetting {

    private val destinationFileName = "${System.currentTimeMillis()}.png"
    private val option = UCrop.Options().apply {
        val pinkColor = Color.parseColor("#FF4689")
        val whiteColor = Color.parseColor("#FFFFFF")

        setToolbarColor(pinkColor)
        setStatusBarColor(pinkColor)

        setToolbarTitle("편집하기")
        setRootViewBackgroundColor(whiteColor)
    }

    fun getUCropIntent(context: Context, uri: Uri, cacheDir: File): Intent {
        return UCrop.of(uri, Uri.fromFile(File(cacheDir, destinationFileName)))
            .withAspectRatio(500f, 500f)
            .withMaxResultSize(500, 500)
            .withOptions(option)
            .getIntent(context)
    }
}