package com.synd.kotlin.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.synd.kotlin.model.UserModel
import io.objectbox.example.kotlin.R

class UserAdapter(private var userList: List<UserModel>, private val listener: AdapterItemClickListener)
    : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    fun setData(userList: List<UserModel>) {
        this.userList = userList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view, listener)
    }

    override fun getItemCount() = userList.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    class ViewHolder(val view: View, val listener: AdapterItemClickListener) : RecyclerView.ViewHolder(view) {

        fun bind(userModel: UserModel) {
            val tvName = view.findViewById<TextView>(R.id.tv_name)
            val tvAge = view.findViewById<TextView>(R.id.tv_age)
            val tvScores = view.findViewById<TextView>(R.id.tv_scores)
            tvName.text = userModel.name
            tvAge.text = "  -  " + userModel.age?.toString() + " yrs"
            tvScores.text = userModel.scoresToString()

            view.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    listener?.onItemClick(userModel)
                }
            })
        }
    }
}