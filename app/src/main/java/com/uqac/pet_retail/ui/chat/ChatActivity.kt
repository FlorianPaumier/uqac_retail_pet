package com.uqac.pet_retail.ui.chat

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.ChatItemAdapter
import com.uqac.pet_retail.R
import com.uqac.pet_retail.RoomItemAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseUser
    private lateinit var id: String
    private lateinit var name: String
    private lateinit var image: String
    private lateinit var database: DatabaseReference
    private lateinit var bdd: FirebaseFirestore
    private lateinit var rv: RecyclerView
    var data: ArrayList<FirebaseChatModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val extras = intent.extras
        if (extras != null) {
            id = extras.getString("id").toString()
            //The key argument here must match that used in the other activity
        }

        if (id.isEmpty() || id.isBlank()) {
            val intent = Intent(this, RoomActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.return_rom).setOnClickListener {
            val intent = Intent(this, RoomActivity::class.java)
            startActivity(intent)
        }


        bdd = Firebase.firestore
        auth = FirebaseAuth.getInstance().currentUser!!
        database = Firebase.database.reference

        findViewById<ImageView?>(R.id.sentButton).setOnClickListener {
            findViewById<ImageView>(R.id.sentButton).visibility = View.GONE
            findViewById<LottieAnimationView>(R.id.send_message_waiting).visibility = View.VISIBLE

            val key = database.child("rooms").child(id).child("messages").push().key

            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formatted = current.format(formatter)


            Log.w("Date", formatted)
            val message = hashMapOf(
                "uid" to key,
                "message" to findViewById<EditText>(R.id.messageBox).text.toString(),
                "date" to formatted,
                "author" to auth.uid,
                "read" to false
            )

            database.child("rooms").child(id).child("messages/$key")
                .setValue(message).addOnSuccessListener {
                    findViewById<LottieAnimationView>(R.id.send_message_waiting).visibility =
                        View.GONE
                    findViewById<LottieAnimationView>(R.id.send_message_success).addAnimatorUpdateListener { listner ->
                        Log.w("Listner", "Listner vAlue : " + listner.toString())
                    }
                }.addOnCanceledListener {
                    findViewById<LottieAnimationView>(R.id.send_message_waiting).visibility =
                        View.GONE
                    findViewById<LottieAnimationView>(R.id.send_message_error).visibility =
                        View.VISIBLE
                }

            Handler(Looper.getMainLooper()).postDelayed({
                findViewById<LottieAnimationView>(R.id.send_message_error).visibility = View.GONE
                findViewById<LottieAnimationView>(R.id.send_message_success).visibility = View.GONE
                findViewById<ImageView>(R.id.sentButton).visibility = View.VISIBLE
                findViewById<EditText>(R.id.messageBox).text.clear()
                findViewById<EditText>(R.id.messageBox).clearFocus()
            }, 1000)

        }

        rv = findViewById<RecyclerView>(R.id.messages_list)
        val roomAdapter = ChatItemAdapter(this, data)
        rv.adapter = roomAdapter

        database.child("rooms").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val roomModel = Model()

                    Log.e("z_cp_values", "onDataChange: " + dataSnapshot.value)

                    dataSnapshot.getValue(FirebaseRoomModel::class.java)?.let {
                        val dataModel: FirebaseRoomModel = it
                        if (dataModel.user2 == auth.uid || dataModel.user1 == auth.uid) {

                            roomModel.id = dataModel.uid
                            when {
                                dataModel.user1 != auth.uid -> {
                                    findViewById<TextView>(R.id.profile_name).text = dataModel.user1Name
                                }
                                dataModel.user2 != auth.uid -> {
                                    findViewById<TextView>(R.id.profile_name).text = dataModel.user2Name
                                }
                                else -> {}
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                }
            })

        database.child("rooms").child("$id/messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.w("Value", snapshot.value.toString())
                    snapshot.getValue(FirebaseChatModel::class.java)?.let {
                        val dataModel: FirebaseChatModel = it

                        if (dataModel.author == auth.uid) {
                            dataModel.isUser = true
                        } else {
                            dataModel.read = true
                            Log.w("Read", dataModel.read.toString())
                            database.child("rooms/$id/messages/").child(dataModel.uid)
                                .setValue(dataModel)
                        }
                        Log.w("Author", dataModel.isUser.toString())
                        data.add(dataModel)
                        rv.scrollToPosition(data.size - 1)
                    }
                    rv.getAdapter()?.notifyDataSetChanged()
                }

                override fun onChildChanged(
                    dataSnapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                    dataSnapshot.getValue(FirebaseChatModel::class.java)?.let {
                        val dataModel: FirebaseChatModel = it

                        if (dataModel.author == auth.uid) {
                            dataModel.isUser = true
                        }
                        for (i in 0 until data.size) {
                            if (data[i].uid == dataSnapshot.key) {
                                data.set(i, dataModel)
                                break;
                            }
                        }
                        rv.scrollToPosition(data.size - 1)
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                }
            })

        rv.getAdapter()?.notifyDataSetChanged()
    }

    override fun onStart() {
        Log.w("Step", "Just Start")
        super.onStart()
        for (item in data) {
            Log.w("Item", item.uid)
            if (!item.read) {
                item.read = true
                database.child("rooms/$id/messages/" + item.uid).setValue(item)
            }
        }
    }

    override fun onResume() {
        Log.w("Step", "Just Resume")
        super.onResume()
        for (item in data) {
            Log.w("Item", item.uid)
            if (!item.read) {
                item.read = true
                database.child("rooms/$id/messages/" + item.uid).setValue(item)
            }
        }
    }

    private fun generateItems(dataSnapshot: DataSnapshot) {
        for (snapshot in dataSnapshot.children) {
            snapshot.getValue(FirebaseChatModel::class.java)?.let {
                val dataModel: FirebaseChatModel = it

                if (dataModel.author == auth.uid) {
                    dataModel.isUser = true
                }
                data.add(dataModel)
            }
            rv.getAdapter()?.notifyDataSetChanged()
        }
    }
}