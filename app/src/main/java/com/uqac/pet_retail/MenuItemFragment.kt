package com.uqac.pet_retail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * A fragment representing a list of Items.
 */
class MenuItemFragment : Fragment() {

    private lateinit var menuItems: ArrayList<MenuItem>
    private val columnCount: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuItems = ArrayList()
        menuItems.add(MenuItem("1", "Accueil"))
        menuItems.add(MenuItem("2", "Profil"))
        menuItems.add(MenuItem("3", "Discussions"))
        menuItems.add(MenuItem("4", "Deconnexion"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu_item_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.list)

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MenuItemRecyclerViewAdapter(menuItems)
            }
        }
        return view
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }

    /**
     * A menu item.
     */
    data class MenuItem(val id: String, val content: String) {
        override fun toString(): String = content
    }
}