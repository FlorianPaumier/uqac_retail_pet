package com.uqac.pet_retail.ui.profil

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityAnnonceBinding


class AnnonceActivity : AppCompatActivity(), View.OnClickListener {
    private val PICK_IMAGE_MULTIPLE: Int = 1
    private val AUTOCOMPLETE_REQUEST_CODE = 10
    private lateinit var binding: ActivityAnnonceBinding
    var pictures: ArrayList<Uri?> = ArrayList<Uri?>()
    var imageView: ImageSwitcher? = null
    var position: Int = 0
    private lateinit var petAddress: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annonce)
        binding = ActivityAnnonceBinding.inflate(layoutInflater)

        val btnValidation = findViewById<Button>(R.id.valid_annonce)

        val imageView = findViewById<ImageSwitcher>(R.id.pet_pictures)
        val btnPictures = findViewById<Button>(R.id.add_pictures)
        val previous = binding.prevPicture
        val next = binding.nextPicture

        // showing all images in imageswitcher

        // showing all images in imageswitcher
        imageView.setFactory(ViewSwitcher.ViewFactory {
            // Create a new ImageView and set it's properties
            val imageViewItem = ImageView(applicationContext)
            // set Scale type of ImageView to Fit Center
            imageViewItem.scaleType = ImageView.ScaleType.FIT_CENTER
            // set the Height And Width of ImageView To FIll PARENT
            imageViewItem.layoutParams =
                FrameLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT
                )
            imageViewItem
        })

        // Declare in and out animations and load them using AnimationUtils class

        // Declare in and out animations and load them using AnimationUtils class
        val `in`: Animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out: Animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)

        // set the animation type to ImageSwitcher

        // set the animation type to ImageSwitcher
        imageView.setInAnimation(`in`)
        imageView.setOutAnimation(out)

        next.setOnClickListener {
            if (position < pictures.size - 1) {
                // increase the position by 1
                position++
                imageView.setImageURI(pictures[position])
            } else {
                Toast.makeText(
                    this@AnnonceActivity,
                    "Last Image Already Shown",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // click here to view previous image

        // click here to view previous image
        previous.setOnClickListener {
            if (position > 0) {
                // decrease the position by 1
                position--
                imageView.setImageURI(pictures[position])
            }
        }

        btnPictures.setOnClickListener(this)
        btnValidation.setOnClickListener(this)

        val dropdown = findViewById<Spinner>(R.id.pet_type)
        val items = arrayOf("dog", "cat", "horse", "mouse")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter

        // Initialize the AutocompleteSupportFragment.
//        val autocompleteFragment =
//            supportFragmentManager.findFragmentById(R.id.pet_address)
//                    as AutocompleteSupportFragment
//
//        // Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
//
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: ${place.name}, ${place.id}")
//            }
//
//            override fun onError(status: Status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: $status")
//            }
//        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_pictures -> openGalery()
            R.id.valid_annonce -> validForm()
        }
    }

    private fun validForm() {
        val petName = binding.petName.text.toString()
        val petTimeMorning = binding.petTimeMorning.isChecked
        val petTimeAfternoon = binding.petTimeAfternoon.isChecked
        val petTimeNight = binding.petTimeNight.isChecked
        val petDescription = binding.petDescription.text.toString()

        val db = Firebase.firestore

        val user = FirebaseAuth.getInstance().currentUser

        val annonce = hashMapOf(
            "name" to petName,
            "address" to petAddress,
            "morning_time" to petTimeMorning,
            "afternoon_time" to petTimeAfternoon,
            "night_time" to petTimeNight,
            "description" to petDescription,
            "user" to user?.uid,
            "picture" to pictures
        )

        db.collection("annonce")
            .add(annonce)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Annonce added with ID: ${documentReference.id}")
                Toast.makeText(this, "Annonce added with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding Annonce", e)
                Toast.makeText(this, "Error adding Annonce" + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun openGalery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        Log.w(TAG, "Click")
        // setting type to select to be image
        intent.type = "image/*"

        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGE_MULTIPLE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_MULTIPLE){

            // if multiple images are selected
            if (data?.getClipData() != null) {
                val count = data.clipData?.itemCount
                for (i in 0..count!! - 1) {
                    var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    pictures.add(imageUri)
                    imageView?.setImageURI(imageUri)
                }
            } else if (data?.getData() != null) {
                // if single image is selected
                var imageUri: Uri = data.data!!
                pictures.add(imageUri)
                imageView?.setImageURI(imageUri)
            }
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        petAddress = Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place: ${petAddress.name}, ${petAddress.id}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
    }

}