package com.uqac.pet_retail

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uqac.pet_retail.ui.chat.ChatActivity
import com.uqac.pet_retail.ui.chat.Model
import com.uqac.pet_retail.ui.chat.RoomActivity

class RoomItemAdapter(context: Context, data: ArrayList<Model>) :
    RecyclerView.Adapter<RoomItemAdapter.ViewHolder>() {
    private val mData: List<Model>
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.list_room_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.id.text = item.id
        holder.name.text = item.name
        holder.lastMessage.text = item.lastMessage
        holder.lastMessageDate.text = item.lastMessageDate
        holder.name.isSelected = true
        Glide.with(context)
            .load(item.image)
            .into(holder.icon)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var name: TextView
        var id: TextView
        var lastMessage: TextView
        var lastMessageDate: TextView
        var icon: ImageView

        override fun onClick(view: View) {
            val intent = Intent(context, ChatActivity::class.java)
            Log.w("ID", id.text.toString())
            intent.putExtra("id", id.text.toString())
            context.startActivity(intent)
        }

        init {
            id = itemView.findViewById(R.id.room_id)
            name = itemView.findViewById(R.id.profile_name)
            lastMessage = itemView.findViewById(R.id.last_message)
            lastMessageDate = itemView.findViewById(R.id.last_message_date)
            icon = itemView.findViewById(R.id.profile_picture)
            itemView.setOnClickListener(this)
        }
    }

    init {
        mData = data
        this.context = context
    }
}