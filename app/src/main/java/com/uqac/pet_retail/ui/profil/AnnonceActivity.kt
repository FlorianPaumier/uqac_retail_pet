package com.uqac.pet_retail.ui.profil

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityAnnonceBinding
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


class AnnonceActivity : AppCompatActivity(), View.OnClickListener,AdapterView.OnItemSelectedListener {
    private var annonce_uid: String? = null
    private lateinit var annonce: AnnonceModel
    private var uid: String? = null
    private lateinit var profile: ProfileModel
    private lateinit var ref: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private val PICK_IMAGE_MULTIPLE: Int = 1
    private lateinit var binding: ActivityAnnonceBinding
    var pictures: ArrayList<Uri> = ArrayList<Uri>()
    lateinit var imageView: ImageSwitcher
    private var currentIndex = 0
    var items: ArrayList<String> = ArrayList<String>()
    var typeAnimal: String? = ""
    var uploadPicture: ArrayList<Uri> = ArrayList()
    lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annonce)
        binding = ActivityAnnonceBinding.inflate(layoutInflater)

        db = Firebase.firestore
        storage = Firebase.storage("gs://pet-retail.appspot.com")
        ref = storage.reference

        val btnValidation = findViewById<Button>(R.id.valid_annonce)

        imageView = findViewById<ImageSwitcher>(R.id.pet_pictures)
        val btnPictures = findViewById<Button>(R.id.add_pictures)
        val previous = findViewById<Button>(R.id.prev_picture)
        val next = findViewById<Button>(R.id.next_picture)
        val name = findViewById<EditText>(R.id.pet_name)
        val race = findViewById<EditText>(R.id.race_animal)
        val type = findViewById<Spinner>(R.id.type_animal)
        val morning = findViewById<CheckBox>(R.id.pet_time_morning)
        val afternoon = findViewById<CheckBox>(R.id.pet_time_afternoon)
        val night = findViewById<CheckBox>(R.id.pet_time_night)
        val week = findViewById<CheckBox>(R.id.pet_time_week)
        val weekend = findViewById<CheckBox>(R.id.pet_time_weekend)
        val description = findViewById<EditText>(R.id.description)
        findViewById<Button>(R.id.delete_annonce).setOnClickListener(this)

        val extras = intent.extras
        if (extras != null) {
            annonce_uid = extras.getString("annonce_uid").toString()
            //The key argument here must match that used in the other activity
        }

        uid = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("profile").whereEqualTo("user", uid).get()
            .addOnSuccessListener { querySnapshot ->
                Log.w("Count Snap", querySnapshot.size().toString())
                for (document in querySnapshot.documents) {
                    Log.w("Profile", document.data.toString())
                    profile = ProfileModel(document.data!!)

                    if (profile.address.isEmpty()) {
                        Toast.makeText(
                            this,
                            "Vous devez inscrire une address pour créer une annonce",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

        // showing all images in imageswitcher
        // showing all images in imageswitcher
        imageView.setFactory {
            val imageView = ImageView(applicationContext)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            // returning imageview
            imageView
        }

        // Declare in and out animations and load them using AnimationUtils class

        items.add("dog")
        items.add("cat")
        items.add("horse")
        items.add("mouse")

        // Declare in and out animations and load them using AnimationUtils class
        val `in`: Animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out: Animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)

        // set the animation type to ImageSwitcher

        // set the animation type to ImageSwitcher
        imageView.setInAnimation(`in`)
        imageView.setOutAnimation(out)

        next.setOnClickListener {
            imageView.setInAnimation(this, R.anim.from_left);
            imageView.setOutAnimation(this, R.anim.to_right);
            currentIndex++;
            Log.w("Position", currentIndex.toString())
            // condition
            if (currentIndex == pictures.count())
                currentIndex = 0;
            imageView.setImageURI(pictures.get(currentIndex));
        }

        // click here to view previous image

        // click here to view previous image
        previous.setOnClickListener {
            imageView.setInAnimation(this, R.anim.from_right);
            imageView.setOutAnimation(this, R.anim.to_left);
            --currentIndex;
            // condition
            if (currentIndex < 0)
                currentIndex = pictures.count() - 1;
            imageView.setImageURI(pictures.get(currentIndex));
        }

        btnPictures.setOnClickListener(this)
        btnValidation.setOnClickListener(this)

        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items.toList())
        type.adapter = adapter
        type.onItemSelectedListener = this
        type.setSelection(0)


        rv = findViewById<RecyclerView>(R.id.upload_picture)
        val annonceAdapter = AnnonceImageItemAdapter(this, uploadPicture)
        rv.adapter = annonceAdapter
        rv.setHasFixedSize(true);
        val llm = LinearLayoutManager(this)
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(llm);

        if (annonce_uid != null) {
            val docRef = db.collection("annonce").document(annonce_uid!!)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.data != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        annonce = document.toObject<AnnonceModel>()!!
                        name.setText(annonce.name)
                        race.setText(annonce.race)
                        description.setText(annonce.description)
                        morning.isChecked = annonce.morningTime
                        afternoon.isChecked = annonce.afternoonTime
                        night.isChecked = annonce.nightTime
                        week.isChecked = annonce.weekTime
                        weekend.isChecked = annonce.weekendTime
                        type.setSelection(items.indexOf(annonce.type))

                        for (file in annonce.picture){
                            val download = ref.child(file!!)
                            download.downloadUrl.addOnSuccessListener {
                                uploadPicture.add(it)
                                rv.getAdapter()?.notifyDataSetChanged()
                            }.addOnFailureListener {
                                Log.w("Download", it.toString())
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }else{
            annonce = AnnonceModel()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_pictures -> openGalery()
            R.id.valid_annonce -> validForm()
            R.id.delete_picture -> deleteAnnonce()
        }
    }

    private fun deleteAnnonce() {
        for (file in annonce.picture){
            ref.child(file!!).delete()
        }
        db.collection("annonce").document(annonce_uid!!).delete().addOnSuccessListener {
            var intent = Intent(this, ProfileActivity::class.java)
            Toast.makeText(this, "Annonce supprimé", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    private fun validForm() {
        val petName = findViewById<EditText>(R.id.pet_name).text.toString()
        val petTimeMorning = findViewById<CheckBox>(R.id.pet_time_morning).isChecked
        val petTimeAfternoon = findViewById<CheckBox>(R.id.pet_time_afternoon).isChecked
        val petTimeNight = findViewById<CheckBox>(R.id.pet_time_night).isChecked
        val petTimeWeek = findViewById<CheckBox>(R.id.pet_time_week).isChecked
        val petTimeWeekend = findViewById<CheckBox>(R.id.pet_time_weekend).isChecked
        val petRace = findViewById<EditText>(R.id.race_animal).text.toString()
        val petDescription = findViewById<EditText>(R.id.description).text.toString()

        val picturesId = ArrayList<String?>()

        for (file in pictures){
            if (file !== null) {
                val riversRef = ref.child("images/annonces").child("${file.lastPathSegment}")
                val uploadTask = riversRef.putFile(file)
                picturesId.add("images/annonces/${file.lastPathSegment}")

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener {
                    Log.w("File", it.toString())
                    Toast.makeText(this, "Erreur Upload", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
                }
            }
        }


        if (picturesId.count() != pictures.count()) {
            return;
        }

        annonce.name = petName
        annonce.type = typeAnimal!!
        annonce.race = petRace
        annonce.afternoonTime = petTimeAfternoon
        annonce.morningTime = petTimeMorning
        annonce.nightTime = petTimeNight
        annonce.weekendTime = petTimeWeekend
        annonce.weekTime = petTimeWeek
        annonce.description = petDescription
        annonce.user = uid!!
        annonce.picture = picturesId
        annonce.address = profile.address
        annonce.owner = profile.firstname + " " + profile.lastname

        if (annonce_uid != null){
            db.collection("annonce").document(annonce_uid!!)
                .update(annonce.toMap()).addOnSuccessListener {
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
                    Log.w(TAG, "Error adding Udpating", e)
                    Toast.makeText(this, "Error adding Annonce" + e.message, Toast.LENGTH_SHORT).show()
                }
        }else{
           db.collection("annonce").add(annonce)
               .addOnSuccessListener { documentReference ->
                   Log.d(TAG, "Annonce added with ID: ${documentReference.id}")
                   Toast.makeText(
                       this,
                       "Annonce added with ID: ${documentReference.id}",
                       Toast.LENGTH_SHORT
                   ).show()
                   val intent = Intent(this, ProfileActivity::class.java)
                   startActivity(intent)
               }
               .addOnFailureListener { e ->
                   Log.w(TAG, "Error adding Annonce", e)
                   Toast.makeText(this, "Error adding Annonce" + e.message, Toast.LENGTH_SHORT).show()
               }
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

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_MULTIPLE) {

            // if multiple images are selected
            if (data?.getClipData() != null) {
                val count = data.clipData?.itemCount
                for (i in 0..count!! - 1) {
                    var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    pictures.add(imageUri)
                    imageView.setImageURI(imageUri)
                }
            } else if (data?.getData() != null) {
                // if single image is selected
                var imageUri: Uri = data.data!!
                pictures.add(imageUri)
                imageView.setImageURI(imageUri)
            }
        }
    }

    private fun showImage(imgIndex: Int) {
        val imageName: Uri = this.pictures.get(imgIndex)
        val resId = getDrawableResIdByName(imageName.toString())
        if (resId != 0) {
            this.imageView.setImageResource(resId)
        }
    }

    // Find Image ID corresponding to the name of the image (in the drawable folder).
    fun getDrawableResIdByName(resName: String?): Int {
        val pkgName = this.packageName
        // Return 0 if not found.
        val resID = this.resources.getIdentifier(resName, "drawable", pkgName)
        Log.i("MyLog", "Res Name: $resName==> Res ID = $resID")
        return resID
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        typeAnimal = parent.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }


    public fun deletePicture(uri: Uri){
        if(annonce.picture.contains(uri.lastPathSegment)){
            annonce.picture.remove(uri.lastPathSegment)
            ref.child(uri.lastPathSegment!!).delete()
            uploadPicture.remove(uri)
            rv.getAdapter()?.notifyDataSetChanged()
        }
    }
}
