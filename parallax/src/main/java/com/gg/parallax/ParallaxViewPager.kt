package com.gg.parallax

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log


/**
 *  Create by GG on 2018/12/28
 *  mail is gg.jin.yu@gmail.com
 */
class ParallaxViewPager : ViewPager {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val mFragments: ArrayList<Fragment> by lazy { arrayListOf<Fragment>() }


    fun setLayout(fragmentManager: FragmentManager?, layoutIds: IntArray) {
        mFragments.clear()
        layoutIds.forEach {
            mFragments.add(ParallacFragment.newInstance(Bundle().apply {
                putInt(ParallacFragment.LAYOUT_ID_KEY, it)
            }))
        }

        adapter = ParallaxAdapter(fragmentManager, mFragments)

        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // 滚动  position 当前位置    positionOffset 0-1     positionOffsetPixels 0-屏幕的宽度px
//                Log.e(
//                    "TAG",
//                    "position->$position positionOffset->$positionOffset positionOffsetPixels->$positionOffsetPixels"
//                )

                // 获取左out 右 in
                val outFragment: ParallacFragment = mFragments[position] as ParallacFragment
                var parallaxViews = outFragment.getParallaxViews()
                for (parallaxView in parallaxViews) {
                    val (_, translationXOut, _, translationYOut, alpha, _, scaleXOut) = parallaxView.getTag(R.id.parallax_tag) as ParallaxTag
                    // 为什么这样写 ？
                    parallaxView.translationX = -positionOffsetPixels * translationXOut
                    parallaxView.translationY = -positionOffsetPixels * translationYOut

                    if (scaleXOut != -1f)
                        parallaxView.scaleX = -positionOffset * scaleXOut

                    if (alpha != -1F)
                        parallaxView.alpha = positionOffset * alpha
                }

                try {
                    val inFragment: ParallacFragment = mFragments[position + 1] as ParallacFragment
                    parallaxViews = inFragment.getParallaxViews()
                    for (parallaxView in parallaxViews) {
                        val (translationXIn, _, translationYIn, _, alpha, scaleXIn, _) = parallaxView.getTag(R.id.parallax_tag) as ParallaxTag
                        parallaxView.translationX = (measuredWidth - positionOffsetPixels) * translationXIn
                        parallaxView.translationY = (measuredWidth - positionOffsetPixels) * translationYIn

                        if (scaleXIn != -1F)
                            parallaxView.scaleX = positionOffset * scaleXIn
                        if (alpha != -1F) {
                            parallaxView.alpha = positionOffset * (1 - alpha)
                        }
                    }
                } catch (e: Exception) {
                }


            }

            override fun onPageSelected(p0: Int) {
            }

        })

    }

    private class ParallaxAdapter(fragmentManager: FragmentManager?, val fragments: ArrayList<Fragment>) :
        FragmentPagerAdapter(fragmentManager) {
        override fun getCount(): Int = fragments.size

        override fun getItem(position: Int): Fragment = fragments[position]

    }
}