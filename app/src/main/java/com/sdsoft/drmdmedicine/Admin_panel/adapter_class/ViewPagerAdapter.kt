package com.sdsoft.drmdmedicine.Admin_panel.adapter_class

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.sdsoft.drmdmedicine.R
import java.util.Objects

class ViewPagerAdapter(var context: Context): PagerAdapter() {

    var imageList=ArrayList<String>()
    override fun getCount(): Int {
        return  imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {


        val itemView: View =
            LayoutInflater.from(container.context).inflate(R.layout.image_silder_list, container, false)


        val imageView: ImageView = itemView.findViewById<View>(R.id.imgSliderView) as ImageView

        Glide.with(context).load(imageList[position])
            .placeholder(R.drawable.ic_image).into(imageView)

        Log.e("TAG", "image slider: " + imageList[position])



        Objects.requireNonNull(container).addView(itemView)


        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as RelativeLayout)
    }

    fun updateList(imageList: ArrayList<String>) {
        this.imageList=imageList
        notifyDataSetChanged()

    }
}