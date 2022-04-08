package com.uqac.pet_retail

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
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
        holder.messageOther.text = item.message
        holder.dateOther.text = item.date
        holder.readOther.isChecked = item.read
        holder.isUser.isChecked = item.isUser

        Log.w("Is User", item.isUser.toString())
        if (item.isUser){
            holder.userMessage.visibility = View.VISIBLE
            holder.otherMessage.visibility = View.GONE
        }else{
            holder.otherMessage.visibility = View.VISIBLE
            holder.userMessage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var message: TextView
        var date: TextView
        var read: CheckBox
        var messageOther: TextView
        var dateOther: TextView
        var readOther: CheckBox
        var isUser: CheckBox
        var uid: TextView
        var otherMessage: RelativeLayout
        var userMessage: RelativeLayout

        init {
            uid = itemView.findViewById(R.id.message_id)
            otherMessage = itemView.findViewById(R.id.message_other_container)
            userMessage = itemView.findViewById(R.id.message_user_container)
            isUser = itemView.findViewById(R.id.is_user)
            message = itemView.findViewById(R.id.message_user)
            date = itemView.findViewById(R.id.message_user_date)
            read = itemView.findViewById(R.id.message_user_read)
            messageOther = itemView.findViewById(R.id.message_other)
            dateOther = itemView.findViewById(R.id.message_other_date)
            readOther = itemView.findViewById(R.id.message_other_read)
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }

    init {
        mData = data
        this.context = context
    }
}