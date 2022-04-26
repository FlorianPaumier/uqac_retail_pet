package com.uqac.pet_retail.ui.base

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R


public open class BaseActivity : AppCompatActivity() {
    protected var txtHeading: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding = ActivityHomeBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        val card = findViewById<CardView>(R.id.card_home_container)

        supportActionBar?.title = "Home"

        val auth = Firebase.auth
        val user = auth.currentUser

        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        val fragment: Fragment? = manager.findFragmentById(R.id.menu_fragment)
        if(fragment != null)
            ft.hide(fragment)
        ft.commit()

        Log.w(ContentValues.TAG, ""+user?.uid)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu -> {
                val manager: FragmentManager = supportFragmentManager
                val ft: FragmentTransaction = manager.beginTransaction()
                val fragment: Fragment? = manager.findFragmentById(R.id.menu_fragment)
                val home = findViewById<ScrollView>(R.id.homeView)
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                if(item.isChecked) {
                    item.setIcon(R.drawable.ic_baseline_menu_24)
                    item.isChecked = false
                    home.visibility = View.VISIBLE
                    if(fragment != null)
                        ft.hide(fragment)
                }
                else {
                    item.setIcon(R.drawable.ic_baseline_close_24)
                    item.isChecked = true
                    ft.addToBackStack(null)
                    if(fragment != null)
                        ft.show(fragment)
                    home.visibility = View.INVISIBLE
                }
                ft.commit()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
    protected fun setHeading(resId: Int) {
//        if (tv_title == null) txtHeading = findViewById(R.id.tv_title)
//        if (tv_title != null) txtHeading!!.setText(resId)
    }
}