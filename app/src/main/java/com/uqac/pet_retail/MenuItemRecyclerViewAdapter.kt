package com.uqac.pet_retail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.MenuItemFragment.MenuItem
import com.uqac.pet_retail.databinding.FragmentMenuItemBinding
import com.uqac.pet_retail.ui.chat.RoomActivity
import com.uqac.pet_retail.ui.home.HomeActivity
import com.uqac.pet_retail.ui.login.LoginActivity
import com.uqac.pet_retail.ui.profil.ProfileActivity


/**
 * [RecyclerView.Adapter] that can display a [MenuItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MenuItemRecyclerViewAdapter(private val values: List<MenuItem>)
    : RecyclerView.Adapter<MenuItemRecyclerViewAdapter.ViewHolder>() {
//    private var mClickListener: ItemClickListener? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(FragmentMenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content

        with(holder.itemView) {
            tag = item
            setOnClickListener { itemView ->
                val menuItem = itemView.tag as MenuItem
                val bundle = Bundle()
                bundle.putString(
                    MenuItemFragment.ARG_ITEM_ID,
                    menuItem.id
                )
                when (menuItem.id) {
                    "1" -> {
                        goToHome()
                    }
                    "2" -> {
                        goToProfile()
                    }
                    "3" -> {
                        goToChat()
                    }
                    "4" -> {
                        logout()
                    }
                }
            }
            /**
             * Context click listener to handle Right click events
             * from mice and trackpad input to provide a more native
             * experience on larger screen devices
             */
            setOnContextClickListener { v ->
                val menuItem = v.tag as MenuItem
                Toast.makeText(
                    v.context,
                    "Context click of item " + menuItem.id,
                    Toast.LENGTH_LONG
                ).show()
                true
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = values.size

    private fun logout() {
        Firebase.auth.signOut()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun goToChat() {
        val intent = Intent(context, RoomActivity::class.java)
        context.startActivity(intent)
    }


    private fun goToProfile(profile_id: Int) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra("profile_id", profile_id.toString())
        context.startActivity(intent)
    }

    private fun goToProfile() {
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
    }

    inner class ViewHolder(binding: FragmentMenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content



        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}