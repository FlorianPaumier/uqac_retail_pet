package com.uqac.pet_retail.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityHomeBinding
import com.uqac.pet_retail.ui.chat.ChatActivity
import com.uqac.pet_retail.ui.login.LoginActivity
import com.uqac.pet_retail.ui.register.RegisterActivity

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val chatButton = binding.chatButton
        chatButton.setOnClickListener(this)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_home)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val logOutButton = findViewById<Button>(R.id.logout_button)
        logOutButton.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.logout_button -> {
                FirebaseAuth.getInstance().signOut()
                gotToSignIn()
            }
            R.id.chat_button ->{
                gotToChat()
            }
        }
    }

    private fun gotToSignIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun gotToChat() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }
}