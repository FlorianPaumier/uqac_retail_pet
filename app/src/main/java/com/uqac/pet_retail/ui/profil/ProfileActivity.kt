package com.uqac.pet_retail.ui.profil

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.uqac.pet_retail.R


class ProfileActivity : AppCompatActivity() {

    private lateinit var bdd: CollectionReference
    private lateinit var profile: ProfileModel
    private lateinit var ref: StorageReference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val info = findViewById<View>(R.id.info) as TextView
        val email = findViewById<View>(R.id.email) as TextView
        val firstName = findViewById<View>(R.id.user_firstname) as TextView
        val lastName = findViewById<View>(R.id.user_lastname) as TextView
        val mobile = findViewById<View>(R.id.user_phone) as TextView
        val thumbnail = findViewById<ImageView>(R.id.thumbnail)
        val newAnnonce = findViewById<Button>(R.id.new_annonce)
        val address: HashMap<String, TextView> = hashMapOf(
            "address" to this.findViewById(R.id.profile_address_address),
            "city" to this.findViewById(R.id.profile_address_city),
            "country" to this.findViewById(R.id.profile_address_country),
            "postalCode" to this.findViewById(R.id.profile_address_postalCode),
            "region" to this.findViewById(R.id.profile_address_region)
        )
        val location = findViewById<Button>(R.id.addBbtn)
        val edit = findViewById<Button>(R.id.edit_profile)
        edit.setOnClickListener { it ->
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("id", profile.user)
            startActivity(intent)
        }
        val user = FirebaseAuth.getInstance().currentUser
        bdd = Firebase.firestore.collection("profile")
        ref = Firebase.storage("gs://pet-retail.appspot.com").reference
        var profileUid: String? = null;

        val extras = intent.extras
        if (extras != null) {
            profileUid = extras.getString("profile_uid").toString()
            Log.w("Uid", profileUid.toString())
            Log.w("Uid", user?.uid.toString())
            if (user?.uid !== profileUid) {
                newAnnonce.visibility = View.GONE
                edit.visibility = View.GONE
            } else {
                location.visibility = View.GONE
            }
            //The key argument here must match that used in the other activity
        } else {
            location.visibility = View.GONE
        }

        newAnnonce.setOnClickListener {
            val intent = Intent(this, AnnonceActivity::class.java)
            startActivity(intent)
        }

        bdd.whereEqualTo("user", user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.count() == 0) {
                    val newProfile = hashMapOf(
                        "user" to user?.uid
                    )
                    bdd.add(newProfile)
                } else {
                    for (document in documents) {
                        Log.w(TAG, "ID : " + document.id)
                        profile = document.toObject<ProfileModel>()
                        email.text = profile.email
                        firstName.text = profile.firstname
                        lastName.text = profile.lastname
                        mobile.text = profile.phone
                        info.text = profile.description
                        address["address"]?.text = profile.address["address"]
                        address["city"]?.text = profile.address["city"]
                        address["postalCode"]?.text = profile.address["postalCode"]
                        address["region"]?.text = profile.address["region"]
                        address["country"]?.text = profile.address["country"]

                        if (profile.thumbnail.isNotEmpty()) {
                            val download = ref.child(profile.thumbnail!!)
                            Log.w("Thumbnail", profile.thumbnail)
                            download.downloadUrl.addOnSuccessListener {
                                Glide.with(this).load(it).into(thumbnail)
                            }.addOnFailureListener {
                                Log.w("Download", it.toString())
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        val fragment: Fragment? = manager.findFragmentById(R.id.menu_fragment)
        if (fragment != null)
            ft.hide(fragment)
        ft.commit()
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
}