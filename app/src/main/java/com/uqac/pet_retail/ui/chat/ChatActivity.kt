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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.ChatItemAdapter
import com.uqac.pet_retail.R
import com.uqac.pet_retail.RoomItemAdapter
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseUser
    private lateinit var id: String
    private lateinit var database: DatabaseReference
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

        findViewById<Button>(R.id.return_room).setOnClickListener {
            val intent = Intent(this, RoomActivity::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance().currentUser!!
        database = Firebase.database.reference

        findViewById<ImageView?>(R.id.sentButton).setOnClickListener {
            findViewById<ImageView>(R.id.sentButton).visibility = View.GONE
            findViewById<LottieAnimationView>(R.id.send_message_waiting).visibility = View.VISIBLE

            val key = database.child("rooms").child(id).child("messages").push().key

            val message = hashMapOf(
                "uid" to key,
                "message" to findViewById<EditText>(R.id.messageBox).text.toString(),
                "date" to Date().toString(),
                "author" to auth.uid,
                "read" to false
            )

            database.child("rooms").child(id).child("messages/$key")
                .setValue(message).addOnSuccessListener {
                    findViewById<LottieAnimationView>(R.id.send_message_waiting).visibility =
                        View.GONE
                    findViewById<LottieAnimationView>(R.id.send_message_success).addAnimatorUpdateListener { listner ->
                        Log.w("Listner","Listner vAlue : " + listner.toString())
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
            }, 1000)

        }

        rv = findViewById<RecyclerView>(R.id.messages_list)
        val roomAdapter = ChatItemAdapter(this, data)
        rv.adapter = roomAdapter

        database.child("rooms").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.e("Data", "onDataChange: " + data.size)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                }
            })

        database.child("rooms").child("$id/messages").orderByChild("date")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.w("Value", snapshot.value.toString())
                    snapshot.getValue(FirebaseChatModel::class.java)?.let {
                        val dataModel: FirebaseChatModel = it

                        if (dataModel.author == auth.uid) {
                            dataModel.isUser = true
                        }else {
                            dataModel.read = true
                            database.child("rooms/$id/messages/").child(dataModel.uid).setValue(dataModel)
                        }
                        data.add(dataModel)
                    }
                    rv.getAdapter()?.notifyDataSetChanged()

                    Log.e("Data", "onDataChange: " + data.size)
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
                        for (i in 0 until data.size){
                            if (data[i].uid == dataSnapshot.key){
                                data.set(i, dataModel)
                                break;
                            }
                        }
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
        for (item in data){
            Log.w("Item", item.uid)
            if (!item.read){
                item.read = true
                database.child("rooms/$id/messages/"+item.uid).setValue(item)
            }
        }
    }

    override fun onResume() {
        Log.w("Step", "Just Resume")
        super.onResume()
        for (item in data){
            Log.w("Item", item.uid)
            if (!item.read){
                item.read = true
                database.child("rooms/$id/messages/"+item.uid).setValue(item)
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