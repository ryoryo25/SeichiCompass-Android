package com.ryoryo.seichicompass.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ryoryo.seichicompass.R

class SeichiInfoAdapter(private val seichiInfoList: MutableList<SeichiInfo>)
    : RecyclerView.Adapter<SeichiInfoAdapter.SeichiInfoViewHolder>() {

    private lateinit var clickListener: ListItemClickListener

    class SeichiInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var infoTitle: TextView = itemView.findViewById(R.id.seichiInfoItemTitle)
    }

    // https://okuzawats.com/blog/functional-interface/
    fun interface ListItemClickListener {
        fun onItemClick(info: SeichiInfo)
    }

    fun setListItemClickListener(clickListener: ListItemClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeichiInfoViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.seichi_info_item, parent, false)

        return SeichiInfoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return seichiInfoList.size
    }

    override fun onBindViewHolder(holder: SeichiInfoViewHolder, position: Int) {
        val info = seichiInfoList[position]
        holder.infoTitle.text = info.title
        holder.itemView.setOnClickListener {
            this.clickListener.onItemClick(info)
        }
    }
}