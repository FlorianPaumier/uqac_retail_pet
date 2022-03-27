package com.uqac.pet_retail.ui.chat

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityRoomBinding


class RoomActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var users: java.util.HashMap<String, String>
    private lateinit var auth: FirebaseUser
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityRoomBinding
    private lateinit var bdd: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button = findViewById<Button>(R.id.button_new_room)
        button?.setOnClickListener(this)
        database = Firebase.database.reference

        bdd = Firebase.firestore
        auth = FirebaseAuth.getInstance().currentUser!!

        val emails: ArrayList<String> = ArrayList<String>()
        users = HashMap<String, String>()

        bdd.collection("profile")
            .whereNotEqualTo("user", auth.uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.w(ContentValues.TAG, "ID : " + document.id)
                    users.put(document.get("email").toString(),document.get("user").toString())
                    emails.add(document.get("email").toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.select_dialog_item, emails
        )

        val textView = findViewById<AutoCompleteTextView>(R.id.user_research)
        textView?.setAdapter(adapter)

        val rooms = database.child("rooms").get()

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.button_new_room -> {
                val email = findViewById<AutoCompleteTextView>(R.id.user_research).text.toString()

                val room = hashMapOf(
                    "user1" to users.get(email),
                    "user2" to auth.uid
                )
                val key = database.database.reference.child("rooms").push().key
                database.child("rooms/$key").setValue(room)

                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("id", key);
                startActivity(intent)
            }
        }
    }
}