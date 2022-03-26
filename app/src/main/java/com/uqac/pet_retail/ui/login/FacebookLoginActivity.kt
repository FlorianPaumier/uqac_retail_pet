package com.uqac.pet_retail.ui.login

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R
import java.util.*


/**
 * Demonstrate Firebase Authentication using a Facebook access token.
 */
class FacebookLoginActivity : Activity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook_login)
        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]


        // [START initialize_fblogin]
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        buttonFacebookLogin = findViewById(R.id.fb_login_button)

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<com.facebook.login.LoginResult> {
            override fun onSuccess(result: com.facebook.login.LoginResult) {
                Log.d(ContentValues.TAG, "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Log.d(ContentValues.TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(ContentValues.TAG, "facebook:onError", error)
            }
        })
        // [END initialize_fblogin]
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
    }

    public override fun onRestart() {
        super.onRestart()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    // [END on_start_check_user]

    // [START on_activity_result]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
    // [END on_activity_result]

    // [START auth_with_facebook]
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(ContentValues.TAG, "---------------------")
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Log.d(ContentValues.TAG, "---------------------")
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    // [END auth_with_facebook]

    private fun updateUI(user: FirebaseUser?) {
        Log.d(ContentValues.TAG, "---------------------")
        Log.d(ContentValues.TAG, "---------------------")
        Log.d(ContentValues.TAG, "---------------------")
        Log.d(ContentValues.TAG, "---------------------")
        Log.d(ContentValues.TAG, "---------------------")
        Log.d(ContentValues.TAG, "---------------------")
        Log.d(ContentValues.TAG, "---------------------")
    }

    companion object {
        private const val TAG = "FacebookLogin"
    }
}

