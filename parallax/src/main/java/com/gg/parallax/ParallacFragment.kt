package com.gg.parallax

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.LayoutInflaterCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.R.attr.name
import android.R.attr.name
import android.view.ViewParent
import android.os.Build
import android.support.v4.view.ViewCompat
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.content.res.TypedArray
import android.util.Log


/**
 *  Create by GG on 2018/12/28
 *  mail is gg.jin.yu@gmail.com
 */
class ParallacFragment : Fragment(), LayoutInflater.Factory2 {


    companion object {
        const val LAYOUT_ID_KEY = "LAYOUT_ID_KEY"

        fun newInstance(bundle: Bundle) = ParallacFragment().apply {
            arguments = bundle
        }
    }

    private lateinit var mCompatViewInflater: CompatViewInflater

    private val mParallaxAttrs =
        intArrayOf(
            R.attr.translationXIn,
            R.attr.translationXOut,
            R.attr.translationYIn,
            R.attr.translationYOut,
            R.attr.alpha,
            R.attr.scaleXIn,
            R.attr.scaleXOut,
            R.attr.scaleYIn,
            R.attr.scaleYOut
        )

    private val mParallaxViews: ArrayList<View> by lazy { arrayListOf<View>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val layoutId = arguments!!.getInt(LAYOUT_ID_KEY)

        val inflaterClone = inflater.cloneInContext(activity)

        //手动构建view ，监听view 构造过程， 获取属性
        LayoutInflaterCompat.setFactory2(inflaterClone, this)

        return inflaterClone.inflate(layoutId, container, false)
    }


    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return onCreateView(null, name, context, attrs)
    }

    override fun onCreateView(parent: View?, name: String?, context: Context?, attrs: AttributeSet?): View? {
        // View都会来这里,创建View
        // 拦截到View的创建  获取View之后要去解析
        // 1. 创建View
        // If the Factory didn't handle it, let our createView() method try

        val view = createView(parent, name, context, attrs)

        // 2.1 一个activity的布局肯定对应多个这样的 SkinView
        if (view != null) {
            // Log.e("TAG", "我来创建View");
            // 解析所有的我们自己关注属性
            analysisAttrs(view, context, attrs)
        }
        return view

    }

    /**
     * 过滤属性
     */
    private fun analysisAttrs(view: View, context: Context?, attrs: AttributeSet?) {
        val array = context!!.obtainStyledAttributes(attrs, mParallaxAttrs)
        if (array != null && array.indexCount != 0) {
            /* float xIn = array.getFloat(0,0f);
            float xOut = array.getFloat(1,0f);
            float yIn = array.getFloat(2,0f);
            float yOut = array.getFloat(3,0f);*/
            val n = array.indexCount
            val tag = ParallaxTag()
            for (i in 0 until n) {
                val attr = array.getIndex(i)
                when (attr) {
                    0 -> tag.translationXIn = array.getFloat(attr, 0f)
                    1 -> tag.translationXOut = array.getFloat(attr, 0f)
                    2 -> tag.translationYIn = array.getFloat(attr, 0f)
                    3 -> tag.translationYOut = array.getFloat(attr, 0f)
                    4 -> tag.alpha = array.getFloat(attr, -1f)
                    5 -> tag.scaleXIn = array.getFloat(attr, -1f)
                    6 -> tag.scaleXOut = array.getFloat(attr, -1f)
                    7 -> tag.scaleYIn = array.getFloat(attr, -1f)
                    8 -> tag.scaleYOut = array.getFloat(attr, -1f)
                }
            }

            Log.w("tag", tag.toString())
            // 自定义属性怎么存? 还要一一绑定  在View上面设置一个tag
            view.setTag(R.id.parallax_tag, tag)
            //Log.e("TAG",tag.toString());
            mParallaxViews.add(view)
        }
        array!!.recycle()
    }


    /**
     * 构造view
     */
    private fun createView(parent: View?, name: String?, context: Context?, attrs: AttributeSet?): View? {
        val isPre21 = Build.VERSION.SDK_INT < 21

        if (!this::mCompatViewInflater.isInitialized) {
            mCompatViewInflater = CompatViewInflater()
        }

        // We only want the View to inherit it's context if we're running pre-v21
        val inheritContext = (isPre21 && shouldInheritContext(parent as ViewParent))

        return mCompatViewInflater.createView(
            parent, name, context!!, attrs!!, inheritContext,
            isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
            true /* Read read app:theme as a fallback at all times for legacy reasons */
        )
    }

    private fun shouldInheritContext(parent: ViewParent?): Boolean {
        if (parent == null) {
            // The initial parent is null so just return false
            return false
        }
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true
            } else if (parent !is View || ViewCompat.isAttachedToWindow(parent as View)) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false
            }
//            parent = parent.getParent()
        }
    }

    open fun getParallaxViews(): ArrayList<View> = mParallaxViews

}