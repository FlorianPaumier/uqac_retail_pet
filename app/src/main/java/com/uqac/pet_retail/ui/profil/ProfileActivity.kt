package com.uqac.pet_retail.ui.profil

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.uqac.pet_retail.R


class ProfileActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        val dogname = findViewById<View>(R.id.dog_name) as TextView
        val dogage = findViewById<View>(R.id.dog_age) as TextView
        val info = findViewById<View>(R.id.info) as TextView
        val email = findViewById<View>(R.id.email) as TextView
        val addressname = findViewById<View>(R.id.address_name) as TextView
        val address = findViewById<View>(R.id.address) as TextView
        var profileid = intent.getStringExtra("profile_id")
        println(profileid)
        when(profileid){
            "1" -> {
                dogname.text = "Arlo"
                dogage.text = "20 Kg"
                email.text = "michel.tremblay@gmail.com"
                info.text = "+1-581-447-3011"
                addressname.text = "Residence YC"
                address.text = "256 Rue Begin,\nG7H 4M5, Chicoutimi"
            }
            "2" -> {
                dogname.text = "Lucy"
                dogage.text = "15 Kg"
                email.text = "leclerc.delapierre@gmail.com"
                info.text = "+1-581-281-4821"
                addressname.text = ""
                address.text = "241 Rue Morin,\nG7H 4X8, Chicoutimi"
            }
            "3" -> {
                dogname.text = "Charlie"
                dogage.text = "18 Kg"
                email.text = "edourad.smith@gmail.com"
                info.text = "+1-486-548-1254"
                addressname.text = ""
                address.text = "1287 Boulevard du Saguenay Est,\nG7H 1G7, Chicoutimi"
            }
            "4" -> {
                dogname.text = "Molly"
                dogage.text = "35 Kg"
                email.text = "edith.roy@gmail.com"
                info.text = "+1-218-118-3482"
                addressname.text = ""
                address.text = "2675 Boulevard du Royaume,\n QC G7S 5B8,\nJonquiere"
            }
        }
    }
}