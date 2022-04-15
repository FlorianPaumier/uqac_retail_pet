package com.uqac.pet_retail.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityHomeBinding
import com.uqac.pet_retail.ui.chat.RoomActivity
import com.uqac.pet_retail.ui.login.LoginActivity
import com.uqac.pet_retail.ui.profil.AnnonceActivity
import com.uqac.pet_retail.ui.profil.ProfileActivity

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val card = findViewById<CardView>(R.id.card_home_container)

        card?.setOnClickListener(this)
        supportActionBar?.title = "Home";

        val auth = Firebase.auth
        val user = auth.currentUser

        Log.w(TAG, ""+user?.uid)
        //setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_home)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
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

    private fun goToAnnonce() {
        val intent = Intent(this, AnnonceActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.chat -> {
                goToChat()
                true
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                logout()
                true
            }
            R.id.annonce -> {
                goToAnnonce()
                true
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

    private fun logout() {
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun goToChat() {
        val intent = Intent(this, RoomActivity::class.java)
        startActivity(intent)
    }


    private fun goToProfile(profile_id: Int) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("profile_id", profile_id.toString())
        startActivity(intent)
    }

    private fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
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