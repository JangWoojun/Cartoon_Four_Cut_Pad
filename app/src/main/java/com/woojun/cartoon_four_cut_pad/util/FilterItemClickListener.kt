package com.woojun.cartoon_four_cut_pad.util

import com.woojun.cartoon_four_cut_pad.data.Filter

interface FilterItemClickListener {
    fun onClick(item: Filter, index: Int)
}
