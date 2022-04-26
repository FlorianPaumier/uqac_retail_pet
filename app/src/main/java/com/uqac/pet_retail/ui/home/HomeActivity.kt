package com.uqac.pet_retail.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.uqac.pet_retail.HomeCardItemAdapter
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityHomeBinding
import com.uqac.pet_retail.ui.profil.AnnonceModel
import com.uqac.pet_retail.ui.profil.ProfileActivity


class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var lastVisible: DocumentSnapshot
    private lateinit var binding: ActivityHomeBinding
    var data: ArrayList<AnnonceModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Home"

        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        val fragment: Fragment? = manager.findFragmentById(R.id.menu_fragment)
        if (fragment != null)
            ft.hide(fragment)
        ft.commit()

        val bdd = Firebase.firestore

        val rv = findViewById<RecyclerView>(R.id.card_home_container)
        val ref = Firebase.storage("gs://pet-retail.appspot.com").reference
        val annonceAdapter = HomeCardItemAdapter(this, data, ref)
        rv.adapter = annonceAdapter
        rv.setHasFixedSize(true);
        val llm = LinearLayoutManager(this)
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        val query = bdd.collection("annonce");
        val snapshot = query.get();
        var count: Int = 0

        snapshot.addOnSuccessListener {
            it ->
            if(it.size() > 10){
                count = 10
            } else {
                count = it.size()
            }
        };

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && data.size - 4 < count) {
                    bdd.collection("annonce")
                        .startAfter(lastVisible)
                        .limit(4).get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val annonce = document.toObject<AnnonceModel>()
                                annonce.uid = document.id
                                data.add(annonce)
                            }
                            rv.getAdapter()?.notifyDataSetChanged()
                        }
                }
            }
        })

        bdd.collection("annonce").limit(4).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val annonce = document.toObject<AnnonceModel>()
                    annonce.uid = document.id
                    data.add(annonce)
                }
                Log.w("Size", data.size.toString())
                lastVisible = documents.documents[documents.size() - 1]
                rv.getAdapter()?.notifyDataSetChanged()
            }
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
                if (item.isChecked) {
                    item.setIcon(R.drawable.ic_baseline_menu_24)
                    item.isChecked = false
                    home.visibility = View.VISIBLE
                    if (fragment != null)
                        ft.hide(fragment)
                } else {
                    item.setIcon(R.drawable.ic_baseline_close_24)
                    item.isChecked = true
                    ft.addToBackStack(null)
                    if (fragment != null)
                        ft.show(fragment)
                    home.visibility = View.INVISIBLE
                }
                ft.commit()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.card_home_container -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
