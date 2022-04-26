package com.uqac.pet_retail.ui.profil

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.uqac.pet_retail.R
import com.uqac.pet_retail.ui.base.BaseActivity

class EditProfileActivity : BaseActivity(), View.OnClickListener {
    private var imageUri: Uri? = null
    private var uploadUri: String = ""
    private lateinit var profile: ProfileModel
    private lateinit var ref: StorageReference
    private lateinit var bdd: CollectionReference
    private var hasThumbnail: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val user = FirebaseAuth.getInstance().currentUser!!
        ref = Firebase.storage("gs://pet-retail.appspot.com").reference
        bdd = Firebase.firestore.collection("profile")

        val thumbnail = findViewById<ImageView>(R.id.thumbnail)
        val firstname = findViewById<EditText>(R.id.user_firstname)
        val lastname = findViewById<EditText>(R.id.user_lastname)
        val address = findViewById<EditText>(R.id.address_address)
        val city = findViewById<EditText>(R.id.address_city)
        val region = findViewById<EditText>(R.id.addres_region)
        val country = findViewById<EditText>(R.id.address_country)
        val postalCode = findViewById<EditText>(R.id.address_postalCode)
        val phone = findViewById<EditText>(R.id.user_phone)
        val description = findViewById<EditText>(R.id.user_description)
        findViewById<Button>(R.id.update_profile).setOnClickListener(this)
        findViewById<Button>(R.id.btn_thumbnail).setOnClickListener(this)
        findViewById<ImageView>(R.id.delete_thumbnail).setOnClickListener(this)


        bdd.whereEqualTo("user", user.uid).get().addOnSuccessListener { it ->
            profile = it.documents.first().toObject<ProfileModel>()!!
            profile.id = it.documents.first().id
            firstname.setText(profile.firstname)
            lastname.setText(profile.lastname)
            phone.setText(profile.phone)
            description.setText(profile.description)
            address.setText(profile.address["address"])
            city.setText(profile.address["city"])
            region.setText(profile.address["region"])
            country.setText(profile.address["country"])
            postalCode.setText(profile.address["postalCode"])

            Log.w("thumbnail", profile.thumbnail.toString())
            if (profile.thumbnail.isNotEmpty()) {
                val download = ref.child(profile.thumbnail)
                download.downloadUrl.addOnSuccessListener { it ->
                    Glide.with(this)
                        .load(it)
                        .into(thumbnail)
                    hasThumbnail = true
                    imageUri = it
                }
            }else{
                Glide.with(this)
                    .load(R.drawable.ic_use)
                    .into(thumbnail)
            }

        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.update_profile -> validForm()
            R.id.btn_thumbnail -> OpenGalery()
            R.id.delete_thumbnail -> {
                profile.thumbnail = ""
            }
        }
    }

    private fun validForm() {
        profile.firstname = findViewById<EditText>(R.id.user_firstname).text.toString()
        profile.lastname = findViewById<EditText>(R.id.user_lastname).text.toString()
        profile.phone = findViewById<EditText>(R.id.user_phone).text.toString()
        profile.description = findViewById<EditText>(R.id.user_description).text.toString()
        profile.address["address"] = findViewById<EditText>(R.id.address_address).text.toString()
        profile.address["city"] = findViewById<EditText>(R.id.address_city).text.toString()
        profile.address["region"] = findViewById<EditText>(R.id.addres_region).text.toString()
        profile.address["country"] = findViewById<EditText>(R.id.address_country).text.toString()
        profile.address["postalCade"] =
            findViewById<EditText>(R.id.address_postalCode).text.toString()

        if (imageUri == null){
            deletePicture()
        }else if (imageUri !== null && !hasThumbnail){
            val riversRef = ref.child("images/profile").child("${imageUri!!.lastPathSegment}")
            val uploadTask = riversRef.putFile(imageUri!!)
            profile.thumbnail = "images/profile/${imageUri!!.lastPathSegment}"

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener {
                Log.w("File", it.toString())
                Toast.makeText(this, "Erreur Upload", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
            }
        }

        if (profile.user.isNotEmpty()){
            bdd.document(profile.id)
                .update(profile.toMap()).addOnSuccessListener {
                        it ->
                    Toast.makeText(
                        this,
                        "Annonce Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding Udpating", e)
                    Toast.makeText(this, "Error adding Annonce" + e.message, Toast.LENGTH_SHORT).show()
                }
        }else{
            bdd.add(profile)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "Annonce added with ID: ${documentReference.id}")
                    Toast.makeText(
                        this,
                        "Annonce added with ID: ${documentReference.id}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding Annonce", e)
                    Toast.makeText(this, "Error adding Annonce" + e.message, Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun OpenGalery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        Log.w(ContentValues.TAG, "Click")
        // setting type to select to be image
        intent.type = "image/*"

        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (data?.getClipData() != null) {
                imageUri = data.clipData?.getItemAt(0)!!.uri
                Log.w("Uri", imageUri.toString())
                Glide.with(this).clear(findViewById<ImageView>(R.id.thumbnail))
                Glide.with(this).load(imageUri).into(findViewById(R.id.thumbnail))
                findViewById<ImageView>(R.id.delete_thumbnail).visibility = View.VISIBLE
            }
        }
    }

    public fun deletePicture() {
        profile.thumbnail = ""
        ref.child(imageUri!!.lastPathSegment!!).delete()
        uploadUri = ""
        Glide.with(this).clear(findViewById<ImageView>(R.id.thumbnail))
        bdd.document(profile.user).update("thumbnail", "")
    }
}