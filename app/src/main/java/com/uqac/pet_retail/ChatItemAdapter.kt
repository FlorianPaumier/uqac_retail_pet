package com.uqac.pet_retail

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uqac.pet_retail.ui.chat.ChatActivity
import com.uqac.pet_retail.ui.chat.FirebaseChatModel
import com.uqac.pet_retail.ui.chat.Model
import com.uqac.pet_retail.ui.chat.RoomActivity

class ChatItemAdapter(context: Context, data: ArrayList<FirebaseChatModel>) :
    RecyclerView.Adapter<ChatItemAdapter.ViewHolder>() {
    private val mData: List<FirebaseChatModel>
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.list_chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.uid.text = item.uid
        holder.message.text = item.message
        holder.date.text = item.date
        holder.read.isChecked = item.read
        holder.isUser.isChecked = item.isUser
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var message: TextView
        var uid: TextView = itemView.findViewById(R.id.message_id)
        var date: TextView
        var read: CheckBox
        var isUser: CheckBox = itemView.findViewById(R.id.is_user)

        override fun onClick(view: View) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("id", uid.text.toString())
            context.startActivity(intent)
        }

        init {
            if (isUser.isChecked){
                itemView.findViewById<LinearLayout>(R.id.message_user_container).visibility = View.VISIBLE
                message = itemView.findViewById(R.id.message_user)
                date = itemView.findViewById(R.id.message_user_date)
                read = itemView.findViewById(R.id.message_user_read)
            }else{
                itemView.findViewById<LinearLayout>(R.id.message_other_container).visibility = View.VISIBLE
                message = itemView.findViewById(R.id.message_other)
                date = itemView.findViewById(R.id.message_other_date)
                read = itemView.findViewById(R.id.message_other_read)
            }
            itemView.setOnClickListener(this)
        }
    }

    init {
        mData = data
        this.context = context
    }
}