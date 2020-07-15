package com.shimhg02.jjan.utilities

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class OnDragTouchListener @JvmOverloads constructor(view: View, parent: View? = view.parent as View, onDragActionListener: OnDragActionListener? = null) : OnTouchListener {
    private var mView: View? = null
    private var mParent: View? = null
    private var isDragging = false
    private var isInitialized = false
    private var width = 0
    private var xWhenAttached = 0f
    private var maxLeft = 0f
    private var maxRight = 0f
    private var dX = 0f
    private var height = 0
    private var yWhenAttached = 0f
    private var maxTop = 0f
    private var maxBottom = 0f
    private var dY = 0f
    private var mOnDragActionListener: OnDragActionListener? = null

    constructor(view: View, onDragActionListener: OnDragActionListener?) : this(view, view.parent as View, onDragActionListener) {}

    fun setOnDragActionListener(onDragActionListener: OnDragActionListener?) {
        mOnDragActionListener = onDragActionListener
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (isDragging) {
            val bounds = FloatArray(4)
            // LEFT
            bounds[0] = event.rawX + dX
            if (bounds[0] < maxLeft) {
                bounds[0] = maxLeft
            }
            // RIGHT
            bounds[2] = bounds[0] + width
            if (bounds[2] > maxRight) {
                bounds[2] = maxRight
                bounds[0] = bounds[2] - width
            }
            // TOP
            bounds[1] = event.rawY + dY
            if (bounds[1] < maxTop) {
                bounds[1] = maxTop
            }
            // BOTTOM
            bounds[3] = bounds[1] + height
            if (bounds[3] > maxBottom) {
                bounds[3] = maxBottom
                bounds[1] = bounds[3] - height
            }
            when (event.action) {
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> onDragFinish()
                MotionEvent.ACTION_MOVE -> mView!!.animate().x(bounds[0]).y(bounds[1]).setDuration(0).start()
            }
            return true
        } else {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDragging = true
                    if (!isInitialized) {
                        updateBounds()
                    }
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    if (mOnDragActionListener != null) {
                        mOnDragActionListener!!.onDragStart(mView)
                    }
                    return true
                }
            }
        }
        return false
    }

    fun initListener(view: View?, parent: View?) {
        mView = view
        mParent = parent
        isDragging = false
        isInitialized = false
    }

    fun updateBounds() {
        updateViewBounds()
        updateParentBounds()
        isInitialized = true
    }

    fun updateViewBounds() {
        width = mView!!.width
        xWhenAttached = mView!!.x
        dX = 0f
        height = mView!!.height
        yWhenAttached = mView!!.y
        dY = 0f
    }

    fun updateParentBounds() {
        maxLeft = 0f
        maxRight = maxLeft + mParent!!.width
        maxTop = 0f
        maxBottom = maxTop + mParent!!.height
    }

    private fun onDragFinish() {
        if (mOnDragActionListener != null) {
            mOnDragActionListener!!.onDragEnd(mView)
        }
        dX = 0f
        dY = 0f
        isDragging = false
    }

    interface OnDragActionListener {
        fun onDragStart(view: View?)
        fun onDragEnd(view: View?)
    }

    init {
        initListener(view, parent)
        setOnDragActionListener(onDragActionListener)
    }
}