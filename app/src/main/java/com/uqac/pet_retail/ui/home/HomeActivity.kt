package com.uqac.pet_retail.ui.home

import android.os.Bundle
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityHomeBinding
import com.uqac.pet_retail.ui.base.BaseActivity


class HomeActivity : BaseActivity()/*, View.OnClickListener*/ {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val card = findViewById<CardView>(R.id.card_home_container)

        // card?.setOnClickListener(this)
        supportActionBar?.title = "Home"

        hideMenu()
    }

    override fun onStart() {
        super.onStart()
        val imageView = findViewById<ImageView>(R.id.card_image)
        //Glide.with(this).load("https://picsum.photos/200").into(imageView);
    }

//    override fun onClick(p0: View?) {
//        when(p0?.id){
//            R.id.card_home_container -> {
//                val intent = Intent(this, ProfileActivity::class.java)
//                startActivity(intent)
//            }
//        }
//    }
}