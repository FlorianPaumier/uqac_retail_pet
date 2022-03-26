package com.uqac.pet_retail.ui.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityLoginBinding
import com.uqac.pet_retail.ui.home.HomeActivity
import com.uqac.pet_retail.ui.register.RegisterActivity
import java.lang.Exception


const val EXTRA_MESSAGE = "com.example.pet_retail.MESSAGE"

open class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private var mAuth: FirebaseAuth? = null
    private var fbLoginButton: LoginButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.email
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val signUp = binding.signUpButton
        val googleSignInButton = binding.googleLoginBtn
        val twitterSignInButton = binding.twitterLoginBtn

        mAuth = FirebaseAuth.getInstance()

        //region default login
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login?.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username?.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }

            Log.w(TAG, "Success :" + (loginResult.success != null))
            if (loginResult.success != null) {
                Log.w(TAG, "Sign In")
                signIn()
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }
        }

        login?.setOnClickListener {
            loginViewModel.login(username.text.toString(), password.text.toString())
        }

        signUp?.setOnClickListener(this)

        val facebookSignInButton = findViewById<Button>(R.id.facebook_login_button)
        facebookSignInButton?.setOnClickListener(this)
        twitterSignInButton?.setOnClickListener(this)
        googleSignInButton?.setOnClickListener(this)
        //endregion

    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth!!.currentUser

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account !== null && currentUser !== null) {
            loginSuccess(currentUser)
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.google_login_btn -> {
                val signInIntent = Intent(this, GoogleLoginActivity::class.java)
                startActivity(signInIntent)
            }
            R.id.sign_up_button -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            R.id.facebook_login_button -> {
                val intent = Intent(this, FacebookLoginActivity::class.java)
                startActivity(intent)
            }
            R.id.twitter_login_btn -> {
                val intent = Intent(this, TwitterLoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun signIn() {
        val email = findViewById<EditText>(R.id.email).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                Log.w(TAG, task.isSuccessful.toString())
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.w(TAG, "signInWithEmail:success")
                    val user = mAuth!!.currentUser
                    this.loginSuccess(user)
                } else {
                    this.failLogin(""+task.exception?.message)
                }
            }
    }

    fun loginSuccess(user: FirebaseUser?) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "Bienvenue : " + user?.displayName)
        }
        startActivity(intent)
    }

    fun failLogin(exception: String) {
        // If sign in fails, display a message to the user.
        Log.w(TAG, "signInWithEmail:failure$exception")
        Toast.makeText(
            this, "Authentication failed.",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}