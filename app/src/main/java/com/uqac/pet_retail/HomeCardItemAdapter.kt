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
import com.uqac.pet_retail.data.model.CardHome
import com.uqac.pet_retail.ui.chat.ChatActivity
import com.uqac.pet_retail.ui.chat.Model
import com.uqac.pet_retail.ui.chat.RoomActivity
import com.uqac.pet_retail.ui.profil.AnnonceActivity

class HomeCardItemAdapter(context: Context, data: ArrayList<CardHome>) :
    RecyclerView.Adapter<HomeCardItemAdapter.ViewHolder>() {
    private val mData: List<CardHome>
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.list_room_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.uuidAnnonce.text = item.uuidAnnonce
        holder.dog.text = item.dog
        holder.user.text = item.user
        Glide.with(context)
            .load(item.image)
            .into(holder.icon)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var user: TextView
        var uuidAnnonce: TextView
        var dog: TextView
        var icon: ImageView

        override fun onClick(view: View) {
            val intent = Intent(context, AnnonceActivity::class.java)
            Log.w("ID", uuidAnnonce.text.toString())
            intent.putExtra("id", uuidAnnonce.text.toString())
            context.startActivity(intent)
        }

        init {
            uuidAnnonce = itemView.findViewById(R.id.room_id)
            dog = itemView.findViewById(R.id.profile_name)
            user = itemView.findViewById(R.id.last_message)
            icon = itemView.findViewById(R.id.profile_picture)
            itemView.setOnClickListener(this)
        }
    }

    init {
        mData = data
        this.context = context
    }
}