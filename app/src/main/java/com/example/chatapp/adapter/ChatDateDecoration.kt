package com.example.chatapp.adapter

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.chatapp.R
import kotlin.math.max

class ChatDateDecoration (
    private val headerOffset: Int,
    private val sectionCallback: SectionCallback ):ItemDecoration() {

    private lateinit var headerView: View
    private lateinit var header: TextView

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)
        if (sectionCallback.isSection(pos)){
            outRect.top = headerOffset
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (!this::headerView.isInitialized) {
            headerView = inflateHeaderView(parent)
            header = headerView.findViewById(R.id.dateHeaderTextView)
            fixLayoutSize(headerView, parent)
        }

        var previousHeader: CharSequence = ""
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            val title = sectionCallback.getSectionHeader(position)

            if (!title.isNullOrBlank() && (previousHeader != title || sectionCallback.isSection(position))) {
                header.text = title
                drawHeader(c, child, headerView)
                previousHeader = title
            }
        }
    }

    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        val centerY = max(0, child.top - headerView.height / 2).toFloat()
        c.save()
        c.translate(0f, centerY)
        headerView.draw(c)
        c.restore()
    }

    private fun inflateHeaderView(parent: RecyclerView):View{
        return LayoutInflater.from(parent.context).inflate(R.layout.date_header_layout,parent,false)
    }

    private fun fixLayoutSize(view: View,parent: ViewGroup){
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            parent.width,
            View.MeasureSpec.EXACTLY
        )
        val heightSpec = View.MeasureSpec.makeMeasureSpec(
            parent.height,
            View.MeasureSpec.UNSPECIFIED
        )
        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
    interface SectionCallback {
        fun isSection(position: Int): Boolean
        fun getSectionHeader(position: Int): CharSequence?
    }
}