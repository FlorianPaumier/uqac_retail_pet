package com.uqac.pet_retail.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityHomeBinding
import com.uqac.pet_retail.ui.profil.ProfileActivity


class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val card = findViewById<CardView>(R.id.card_home_container)

        card?.setOnClickListener(this)
        supportActionBar?.title = "Home"

        val auth = Firebase.auth
        val user = auth.currentUser

        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        val fragment: Fragment? = manager.findFragmentById(R.id.menu_fragment)
        if(fragment != null)
            ft.hide(fragment)
        ft.commit()



        Log.w(TAG, ""+user?.uid)
    }

    override fun onStart() {
        super.onStart()
        val imageView = findViewById<ImageView>(R.id.card_image)
        //Glide.with(this).load("https://picsum.photos/200").into(imageView);
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

    fun getProfile(v: View?) {
        when(v?.id){
            /*R.id.profile_1 -> {
                goToProfile(1)
            }
            R.id.profile_2 -> {
                goToProfile(2)
            }
            R.id.profile_3 -> {
                goToProfile(3)
            }
            R.id.profile_4 -> {
                goToProfile(4)
            }*/
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.card_home_container -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }
}