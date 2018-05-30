package com.synd.kotlin.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.synd.kotlin.model.AndroidVersion
import io.objectbox.example.kotlin.R

class AndroidVersionAdapter(private val androidVersionList: List<AndroidVersion>, private val listener: AdapterItemClickListener)
    : RecyclerView.Adapter<AndroidVersionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_android_version, parent, false)
        return ViewHolder(view, listener)
    }

    override fun getItemCount() = androidVersionList.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(androidVersionList[position])
    }

    class ViewHolder(val view: View, val listener: AdapterItemClickListener) : RecyclerView.ViewHolder(view) {

        fun bind(androidVersion: AndroidVersion) {
            val tvName = view.findViewById<TextView>(R.id.tv_name)
            val tvVersion = view.findViewById<TextView>(R.id.tv_version)
            val tvApiLevel = view.findViewById<TextView>(R.id.tv_api_level)
            tvName.text = androidVersion.name
            tvVersion.text = androidVersion.ver
            tvApiLevel.text = androidVersion.api

            view.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    listener?.onItemClick(androidVersion)
                }
            })
        }
    }
}