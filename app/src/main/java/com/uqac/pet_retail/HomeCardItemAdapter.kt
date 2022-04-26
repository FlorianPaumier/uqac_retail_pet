package com.uqac.pet_retail

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import com.uqac.pet_retail.ui.profil.AnnonceActivity
import com.uqac.pet_retail.ui.profil.AnnonceModel

class HomeCardItemAdapter(context: Context, data: ArrayList<AnnonceModel>, ref: StorageReference) :
    RecyclerView.Adapter<HomeCardItemAdapter.ViewHolder>() {
    private val mData: List<AnnonceModel>
    private val ref: StorageReference
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.list_home_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.uuidAnnonce.text = item.uid
        holder.animalNom.text = item.name
        holder.user.text = item.owner

        if (item.picture.size > 0) {
            Log.w("Picture", item.picture.size.toString())
            val download = this.ref.child(item.picture[0]!!)
            download.downloadUrl.addOnSuccessListener {
                Glide.with(context)
                    .load(it)
                    .override(120, 110)
                    .into(holder.icon)
            }
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var user: TextView
        var uuidAnnonce: TextView
        var animalNom: TextView
        var icon: ImageView
        var detail: Button

        override fun onClick(view: View) {
            val intent = Intent(context, AnnonceActivity::class.java)
            intent.putExtra("annonce_uid", uuidAnnonce.text.toString())
            context.startActivity(intent)
        }

        init {
            uuidAnnonce = itemView.findViewById(R.id.uuid_annonce)
            animalNom = itemView.findViewById(R.id.animalNom)
            user = itemView.findViewById(R.id.owner)
            icon = itemView.findViewById(R.id.animalImg)
            detail = itemView.findViewById<Button>(R.id.details)
            detail.setOnClickListener(this)
        }
    }

    init {
        mData = data
        this.context = context
        this.ref = ref
    }
}