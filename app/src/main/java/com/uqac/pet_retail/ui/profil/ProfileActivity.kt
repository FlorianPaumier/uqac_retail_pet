package com.uqac.pet_retail.ui.profil

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Tag
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R


class ProfileActivity : AppCompatActivity() {

    private lateinit var bdd: CollectionReference
    private lateinit var profile: DocumentReference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
//        val dogname = findViewById<View>(R.id.dog_name) as TextView
//        val dogage = findViewById<View>(R.id.dog_age) as TextView
        val info = findViewById<View>(R.id.info) as TextView
        val email = findViewById<View>(R.id.email) as TextView
//        val addressname = findViewById<View>(R.id.address_name) as TextView
        val address = findViewById<View>(R.id.address) as TextView
//        var profileid = intent.getStringExtra("profile_id")
//        println(profileid)

        val user = FirebaseAuth.getInstance().currentUser
        bdd = Firebase.firestore.collection("profile")

        findViewById<Button>(R.id.new_annonce).setOnClickListener {
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
                        profile = document.reference
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }


//        when (profileid) {
//            "1" -> {
//                dogname.text = "Arlo"
//                dogage.text = "20 Kg"
//                email.text = "michel.tremblay@gmail.com"
//                info.text = "+1-581-447-3011"
//                addressname.text = "Residence YC"
//                address.text = "256 Rue Begin,\nG7H 4M5, Chicoutimi"
//            }
//            "2" -> {
//                dogname.text = "Lucy"
//                dogage.text = "15 Kg"
//                email.text = "leclerc.delapierre@gmail.com"
//                info.text = "+1-581-281-4821"
//                addressname.text = ""
//                address.text = "241 Rue Morin,\nG7H 4X8, Chicoutimi"
//            }
//            "3" -> {
//                dogname.text = "Charlie"
//                dogage.text = "18 Kg"
//                email.text = "edourad.smith@gmail.com"
//                info.text = "+1-486-548-1254"
//                addressname.text = ""
//                address.text = "1287 Boulevard du Saguenay Est,\nG7H 1G7, Chicoutimi"
//            }
//            "4" -> {
//                dogname.text = "Molly"
//                dogage.text = "35 Kg"
//                email.text = "edith.roy@gmail.com"
//                info.text = "+1-218-118-3482"
//                addressname.text = ""
//                address.text = "2675 Boulevard du Royaume,\n QC G7S 5B8,\nJonquiere"
//            }
//        }
    }
}