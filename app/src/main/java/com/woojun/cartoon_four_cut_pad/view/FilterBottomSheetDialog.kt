package com.woojun.cartoon_four_cut_pad.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woojun.cartoon_four_cut_pad.R
import com.woojun.cartoon_four_cut_pad.adapter.FilterAdapter
import com.woojun.cartoon_four_cut_pad.data.Filter
import com.woojun.cartoon_four_cut_pad.util.FilterItemClickListener

class FilterBottomSheetDialog(private val itemList: MutableList<Filter>, private val index: Int, private val filterItemClickListener: FilterItemClickListener) : BottomSheetDialogFragment()
{
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.filter_sheet_layout, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        val filterList = view?.findViewById<RecyclerView>(R.id.filter_list)

        filterList?.adapter = FilterAdapter(itemList, index, filterItemClickListener)
        filterList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }
}