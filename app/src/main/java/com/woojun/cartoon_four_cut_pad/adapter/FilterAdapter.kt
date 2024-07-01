package com.woojun.cartoon_four_cut_pad.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.woojun.cartoon_four_cut_pad.data.Filter
import com.woojun.cartoon_four_cut_pad.databinding.FilterSheetItemBinding
import com.woojun.cartoon_four_cut_pad.util.FilterItemClickListener

class FilterAdapter(private val filterList: MutableList<Filter>, private val index: Int, private val filterItemClickListener: FilterItemClickListener): RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = FilterSheetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterViewHolder(binding).also { handler->
            binding.root.setOnClickListener {
                filterItemClickListener.onClick(filterList[handler.position], index)
            }
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filterList[position])
    }

    class FilterViewHolder(private val binding: FilterSheetItemBinding):
        ViewHolder(binding.root) {
        fun bind(filter: Filter) {
            if (binding.root.context != null) {
                Glide.with(binding.root.context)
                    .load(filter.image)
                    .into(binding.imageView)

                binding.name.text = filter.name
            }
        }

    }
}