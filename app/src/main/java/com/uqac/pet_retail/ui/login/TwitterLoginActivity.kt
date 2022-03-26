package com.uqac.pet_retail.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R
import com.uqac.pet_retail.ui.home.HomeActivity


class TwitterLoginActivity : LoginActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnTwitter: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter_login)

        btnTwitter = findViewById<Button>(R.id.twitter_login_button)
        mAuth = Firebase.auth

        val provider = OAuthProvider.newBuilder("twitter.com")
        provider.addCustomParameter("lang", "fr ")

        val pendingResultTask: Task<AuthResult>? = mAuth.getPendingAuthResult()
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener(
                    OnSuccessListener<AuthResult?> { authResult ->
                        loginSuccess(authResult.user)
                    })
                .addOnFailureListener(
                    OnFailureListener { exception ->
                        failLogin("" + exception.message)
                    })
        } else {
            mAuth
                .startActivityForSignInWithProvider( /* activity= */this, provider.build())
                .addOnSuccessListener(
                    OnSuccessListener<AuthResult?> { authResult ->
                        loginSuccess(authResult.user)
                    })
                .addOnFailureListener(
                    OnFailureListener { exception ->
                        failLogin("" + exception.message)
                    })
        }
    }
}