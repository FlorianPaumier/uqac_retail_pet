package com.uqac.pet_retail.ui.profil

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uqac.pet_retail.R

class AnnonceImageItemAdapter(context: Context, uploadPicture: ArrayList<Uri>) :
    RecyclerView.Adapter<AnnonceImageItemAdapter.ViewHolder>() {
    private val mData: List<Uri>
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.annonce_image_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        Log.w("Uri", item.toString())
        Glide.with(context).load(item).into(holder.image)
        holder.pos.text = position.toString()

    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var image: ImageView = itemView.findViewById<ImageView>(R.id.annonce_card_picture)
        var pos = itemView.findViewById<TextView>(R.id.position)
        var delete = itemView.findViewById<ImageButton>(R.id.delete_picture).setOnClickListener(this)

        override fun onClick(p0: View?) {
            when (p0?.id) {
                R.id.delete_picture -> {
                    if (context is AnnonceActivity) {
                        (context as AnnonceActivity).deletePicture(mData[Integer.parseInt(pos.text.toString())])
                    }
                }
            }
        }
    }

    init {
        mData = uploadPicture
        this.context = context
    }
}
