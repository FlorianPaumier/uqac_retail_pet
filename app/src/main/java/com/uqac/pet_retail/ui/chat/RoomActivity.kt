package com.uqac.pet_retail.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R
import com.uqac.pet_retail.RoomItemAdapter
import com.uqac.pet_retail.databinding.ActivityRoomBinding
import com.uqac.pet_retail.ui.profil.AnnonceModel
import org.json.JSONArray
import org.json.JSONObject


class RoomActivity : AppCompatActivity() {

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

        database = Firebase.database.reference

        bdd = Firebase.firestore
        auth = FirebaseAuth.getInstance().currentUser!!

        rv = findViewById<RecyclerView>(R.id.room_container)
        val roomAdapter = RoomItemAdapter(this, data)
        rv.adapter = roomAdapter

        bdd.collection("rooms").document(auth.uid)
            .get().addOnSuccessListener {
            result ->
            val rooms = result.toObject<UserRoomsModel>()!!
            Log.w("Rooms", rooms.toString())
            for (room in rooms.rooms){
                database.child("rooms").orderByKey().equalTo(room)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            findViewById<View>(R.id.animationView).visibility = View.GONE

                            generateItems(dataSnapshot)

                            Log.e("Data", "onDataChange: " + data.size)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            println("The read failed: " + databaseError.code)
                        }
                    })
            }
        }

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
                            roomModel.name = dataModel.user1Name
                        }
                        dataModel.user2 != auth.uid -> {
                            roomModel.name = dataModel.user2Name
                        }
                    }

                    if (dataModel.messages.isNotEmpty()) {
                        val lastMessage = dataModel.messages.values.first()
                        roomModel.lastMessage =
                            lastMessage.get("message").toString()
                        roomModel.lastMessageDate =
                            lastMessage.get("date").toString()
                    } else {
                        roomModel.lastMessage = "No message"
                        roomModel.lastMessageDate = "None"
                    }
                    data.add(roomModel)
                }
            }
            rv.getAdapter()?.notifyDataSetChanged()
        }
    }
}