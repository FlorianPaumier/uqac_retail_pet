package com.uqac.pet_retail.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R
import com.uqac.pet_retail.RoomItemAdapter
import com.uqac.pet_retail.databinding.ActivityRoomBinding
import org.json.JSONArray
import org.json.JSONObject





class RoomActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var rv: RecyclerView
    private lateinit var users: java.util.HashMap<String, String>
    private lateinit var auth: FirebaseUser
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityRoomBinding
    private lateinit var bdd: FirebaseFirestore
    var data: ArrayList<Model> = arrayListOf()

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
                    users.put(document.get("email").toString(), document.get("user").toString())
                    emails.add(document.get("email").toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

        var adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.select_dialog_item, emails
        )

        val textView = findViewById<AutoCompleteTextView>(R.id.user_research)
        textView?.setAdapter(adapter)

        rv = findViewById<RecyclerView>(R.id.room_container)
        val roomAdapter = RoomItemAdapter(this, data)
        rv.adapter = roomAdapter
        database.child("rooms").orderByChild("user1").equalTo(auth.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    findViewById<View>(R.id.animationView).visibility = View.GONE

                    generateItems(dataSnapshot)

                    Log.e("Data", "onDataChange: "+data.size )
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                }
            })

        database.child("rooms").orderByChild("user2").equalTo(auth.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    findViewById<View>(R.id.animationView).visibility = View.GONE

                    generateItems(dataSnapshot)

                    Log.e("Data", "onDataChange: "+data.size )
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                }
            })
    }

    private fun generateItems(dataSnapshot: DataSnapshot) {
        for (snapshot in dataSnapshot.children) {
            val roomModel = Model()

            Log.e("z_cp_values", "onDataChange: " + snapshot.value)

            snapshot.getValue(FirebaseRoomModel::class.java)?.let {
                val dataModel: FirebaseRoomModel = it
                if (dataModel.user2 == auth.uid || dataModel.user1 == auth.uid) {

                    roomModel.id = dataModel.uid
                    when {
                        dataModel.user1 != auth.uid -> {
                            bdd.collection("profile").whereEqualTo("user", dataModel.user1).get()
                                .addOnSuccessListener { documents ->
                                    val profile = documents.first()
                                    roomModel.name = profile.get("name").toString()
                                }
                        }
                        dataModel.user2 != auth.uid -> {
                            bdd.collection("profile").whereEqualTo("user", dataModel.user2).get()
                                .addOnSuccessListener { documents ->
                                    val profile = documents.first()
                                    roomModel.name = profile.get("name").toString()
                                }
                        }
                    }

                    if (dataModel.messages.isNotEmpty()) {
                        val lastMessage = dataModel.messages.values.first()
                        roomModel.lastMessage = lastMessage.get("message").toString()
                        roomModel.lastMessageDate = lastMessage.get("date").toString()
                    }else{
                        roomModel.lastMessage = "No message"
                        roomModel.lastMessageDate = "None"
                    }

                    data.add(roomModel)
                }
            }
            rv.getAdapter()?.notifyDataSetChanged()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.button_new_room -> {
                val email = findViewById<AutoCompleteTextView>(R.id.user_research).text.toString()

                val messages: ArrayList<HashMap<String, Any>> = ArrayList()

                messages.add(
                    hashMapOf(
                        "message" to "Hello",
                        "date" to "2022-01-01 09:00:00",
                        "read" to false,
                        "author" to ""
                    )
                )

                val room = hashMapOf(
                    "uid" to "",
                    "user1" to users.get(email),
                    "user2" to auth.uid,
                    "messages" to messages
                )

                val key = database.database.reference.child("rooms").push().key
                room["uid"] = key

                database.child("rooms/$key").setValue(room)

                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("id", key);
                startActivity(intent)
            }
        }
    }
}