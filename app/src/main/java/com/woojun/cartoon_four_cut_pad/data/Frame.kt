package com.woojun.cartoon_four_cut_pad.data

import android.graphics.Bitmap

data class Frame (
    val name: String,
    val frameResponse: FrameResponse,
    val images: MutableList<Bitmap>,
)