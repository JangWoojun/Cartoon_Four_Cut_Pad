package com.woojun.cartoon_four_cut_pad.database

import android.graphics.Bitmap

object BitmapData {
    private var image1: Bitmap? = null
    private var image2: Bitmap? = null

    fun getImage1(): Bitmap? {
        return image1?.copy(Bitmap.Config.RGBA_F16, true)
    }

    fun setImage1(bitmap: Bitmap) {
        image1 = bitmap
    }

    fun getImage2(): Bitmap? {
        return image2?.copy(Bitmap.Config.RGBA_F16, true)
    }

    fun setImage2(bitmap: Bitmap) {
        image2 = bitmap
    }

}
