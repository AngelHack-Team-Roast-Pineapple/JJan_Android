package com.shimhg02.jjan.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.enablex.jjan.R
import com.shimhg02.jjan.adapter.HorizontalViewAdapter.MessageViewHolder
import com.shimhg02.jjan.model.HorizontalViewModel
import enx_rtc_android.Controller.EnxPlayerView

class HorizontalViewAdapter(private var streamArrayList: List<HorizontalViewModel>, private val context: Context, var mHieght: Int, var mWidth: Int, var mSharedPrersentation: Boolean) : RecyclerView.Adapter<MessageViewHolder>() {
    override fun getItemCount(): Int {
        return if (streamArrayList.size > 0) {
            streamArrayList.size
        } else 0
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val enxStream = streamArrayList[position].enxStream
        val isAudioOnly = streamArrayList[position].enxStream?.isAudioOnlyStream
        val messageViewHolder = holder
        try {
            if (enxStream == null) {
                return
            }
            val mScreen = enxStream.hasScreen()
            val enxPlayerView = streamArrayList[position].enxPlayerView
            enxPlayerView?.id = Integer.valueOf(enxStream.id)
            if (mScreen) {
                enxPlayerView?.setScalingType(EnxPlayerView.ScalingType.SCALE_ASPECT_FIT) //SCALE_ASPECT_BALANCED for full view other waise FIT is actual size
                enxPlayerView?.tag = "screen"
            } else {
                enxPlayerView?.setScalingType(EnxPlayerView.ScalingType.SCALE_ASPECT_BALANCED)
                enxPlayerView?.tag = "not_screen"
            }
            val name = enxStream.name
            val count = messageViewHolder.relativeLayout.childCount
            if (count > 0) {
                var textView: TextView? = null
                var surfaceview: View? = null
                for (i in 0 until count) {
                    surfaceview = messageViewHolder.relativeLayout.getChildAt(i)
                    if (surfaceview is EnxPlayerView) {
                        textView = if (i == count - 1) {
                            messageViewHolder.relativeLayout.getChildAt(i - 1) as TextView
                        } else {
                            messageViewHolder.relativeLayout.getChildAt(i + 1) as TextView
                        }
                        break
                    } else {
                        surfaceview = null
                        textView = messageViewHolder.relativeLayout.getChildAt(i) as TextView
                    }
                }
                if (surfaceview != null) {
                    val sv = surfaceview as EnxPlayerView
                    messageViewHolder.relativeLayout.removeView(sv)
                    messageViewHolder.relativeLayout.removeView(textView)
                } else {
                    messageViewHolder.relativeLayout.removeView(textView)
                }
            }
            if (mSharedPrersentation) {
                if (position == 0) {
                    messageViewHolder.relativeLayout.setBackgroundColor(context.resources.getColor(R.color.colorAccent))
                } else {
                    messageViewHolder.relativeLayout.setBackgroundColor(context.resources.getColor(R.color.white))
                }
            } else {
                messageViewHolder.relativeLayout.setBackgroundColor(context.resources.getColor(R.color.white))
            }
            messageViewHolder.relativeLayout.addView(enxPlayerView)
            if (enxStream.media != null) {
                enxStream.attachRenderer(enxPlayerView)
            } else {
                Toast.makeText(context, name + "null", Toast.LENGTH_SHORT).show()
            }
            if (isAudioOnly!!) {
                messageViewHolder.audioOnlyText.visibility = View.GONE
            } else {
                messageViewHolder.audioOnlyText.visibility = View.VISIBLE
            }
            val tv = TextView(context)
            tv.setTextColor(context.resources.getColor(R.color.green))
            val tvlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tvlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            tvlp.addRule(RelativeLayout.CENTER_HORIZONTAL)
            tv.layoutParams = tvlp
            if (name != null) {
                tv.text = name
            }
            messageViewHolder.relativeLayout.addView(tv)
        } catch (e: Exception) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    inner class MessageViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var relativeLayout: RelativeLayout
        var audioOnlyText: TextView

        init {
            relativeLayout = view.findViewById<View>(R.id.imageView) as RelativeLayout
            audioOnlyText = view.findViewById<View>(R.id.audioonlyAdapterText) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.horizontalviewitem, parent, false)
        return MessageViewHolder(itemView)
    }

    fun setItems(list: List<HorizontalViewModel>) {
        streamArrayList = list
    }

}