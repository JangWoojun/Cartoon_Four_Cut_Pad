package com.woojun.cartoon_four_cut_pad.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.woojun.cartoon_four_cut_pad.data.Frame
import com.woojun.cartoon_four_cut_pad.databinding.PhotoFrameLayoutBinding
import com.woojun.cartoon_four_cut_pad.util.Utils.dpToPx
import jp.wasabeef.glide.transformations.BlurTransformation

class FrameViewPagerAdapter(private val frameItemList: List<Frame>) : RecyclerView.Adapter<FrameViewPagerAdapter.ViewPagerHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val binding = PhotoFrameLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewPagerHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(frameItemList[position])
    }

    override fun getItemCount(): Int {
        return frameItemList.size
    }

    class ViewPagerHolder(private val binding: PhotoFrameLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(frameItem: Frame) {
            binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginStart = binding.root.context.dpToPx(24f).toInt()
                marginEnd = binding.root.context.dpToPx(24f).toInt()
            }

            Glide.with(binding.root.context)
                .load(frameItem.images[0])
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.image1)
            Glide.with(binding.root.context)
                .load(frameItem.images[1])
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.image2)

            Glide.with(binding.root.context)
                .load(frameItem.images[0])
                .transform(MultiTransformation(CenterCrop(), BlurTransformation(15, 3)))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.image3)
            Glide.with(binding.root.context)
                .load(frameItem.images[1])
                .transform(MultiTransformation(CenterCrop(), BlurTransformation(15, 3)))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.image4)

            binding.filterText.text = frameItem.frameResponse.name

            val frameResponse = frameItem.frameResponse

            Glide.with(binding.root.context)
                .load(frameResponse.top)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.topImage)
            Glide.with(binding.root.context)
                .load(frameResponse.bottom)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.bottomImage)
            Glide.with(binding.root.context)
                .load(frameResponse.background)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.backgroundImage)
        }
    }

}
