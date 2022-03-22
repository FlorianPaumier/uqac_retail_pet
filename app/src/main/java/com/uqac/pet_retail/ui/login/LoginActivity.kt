package com.uqac.pet_retail.ui.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.nfc.Tag
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
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.uqac.pet_retail.R
import com.uqac.pet_retail.databinding.ActivityLoginBinding
import com.uqac.pet_retail.ui.home.HomeActivity
import com.uqac.pet_retail.ui.register.RegisterActivity


const val EXTRA_MESSAGE = "com.example.pet_retail.MESSAGE"

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val RC_SIGN_IN: Int = 9001
    private lateinit var googleSignInClient: GoogleSignInClient
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

        val provider = OAuthProvider.newBuilder("23719345")
        // clientId S1cwX0RmNm5LRlJ4dmt0LVFQamI6MTpjaQ
        // clientSecret KZBya5Jn6uDBNmZYWi129kWXGHmLAhZres3bWkN3E1NmGW2-va

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

        //region Login Google
        Log.w(TAG, getString(R.string.google_id_secret))
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_id_secret))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //endregion

        // Initialize Facebook Login button

        //region Fb Login
        val callbackManager = CallbackManager.Factory.create()

        val fbLoginButton = findViewById<LoginButton>(R.id.fb_login_button)
        fbLoginButton.setPermissions("email", "public_profile")
        fbLoginButton.registerCallback(callbackManager, object : FacebookCallback<com.facebook.login.LoginResult> {
            override fun onSuccess(result: com.facebook.login.LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })

        //endregion
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth!!.currentUser

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account !== null && currentUser !== null) {
            loginSucces(currentUser)
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.google_login_btn -> signInGoogle()
            R.id.sign_up_button -> gotToSignUp()
            R.id.facebook_login_button -> fbLoginButton?.performClick()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        println("requestCode : $requestCode")
        println("resultCode : $resultCode")
        Log.d("Info","data : ${data?.dataString}")

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Log.w(TAG, "" + e.message)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth?.currentUser
                    loginSucces(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    failLogin(task)
                }
            }
    }

    private fun gotToSignUp() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
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
                    this.loginSucces(user)
                } else {
                    this.failLogin(task)
                }
            }
    }

    private fun loginSucces(user: FirebaseUser?) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "Bienvenue : " + user?.displayName)
        }
        startActivity(intent)
    }

    private fun failLogin(task: Task<AuthResult>) {
        // If sign in fails, display a message to the user.
        Log.w(TAG, "signInWithEmail:failure", task.exception)
        Toast.makeText(
            this, "Authentication failed.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth?.currentUser
                    val intent = Intent(this, HomeActivity::class.java).apply {
                        putExtra(EXTRA_MESSAGE, "Bienvenue : " + user?.displayName)
                    }
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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