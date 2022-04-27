package com.uqac.pet_retail.ui.register

import android.R.attr.password
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uqac.pet_retail.R
import com.uqac.pet_retail.data.model.LoggedInUser
import com.uqac.pet_retail.databinding.ActivityLoginBinding
import com.uqac.pet_retail.databinding.ActivityRegisterBinding
import com.uqac.pet_retail.ui.home.HomeActivity
import com.uqac.pet_retail.ui.login.EXTRA_MESSAGE
import com.uqac.pet_retail.ui.login.LoginActivity
import com.uqac.pet_retail.ui.login.LoginViewModel
import com.uqac.pet_retail.ui.login.LoginViewModelFactory


class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.registerEmail
        val password = binding.registerPassword
        val passwordConfirmation = binding.registerPasswordConfirmation

        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance();

        val buttonValidation = findViewById<Button>(R.id.sign_up_button)
        val buttonSignIn = findViewById<TextView>(R.id.sign_in_button)
        buttonValidation.setOnClickListener(this)
        buttonSignIn.setOnClickListener(this)
        database = Firebase.database.reference

        //region registration model
        val registerViewModel = ViewModelProvider(this, RegisterViewModelFactory())[RegisterViewModel::class.java]

        registerViewModel.loginFormState.observe(this@RegisterActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            buttonValidation?.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
            if (loginState.passwordConfirmationError != null) {
                passwordConfirmation.error = getString(loginState.passwordConfirmationError)
            }
        })
        //endregion

        findViewById<ImageView>(R.id.btn_register_back).setOnClickListener {
            it ->
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser

        if(currentUser !== null) {
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, "Bienvenue : " + currentUser.displayName)
            }
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String){
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth!!.currentUser

                    val intent = Intent(this, HomeActivity::class.java).apply {
                        putExtra(EXTRA_MESSAGE, "Bienvenue : " + user?.displayName)
                    }
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.sign_up_button -> signUp()
            R.id.sign_in_button -> goToSignIn()
        }
    }

    private fun goToSignIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun signUp() {
        val email = findViewById<EditText>(R.id.register_email).text.toString()
        val password = findViewById<EditText>(R.id.register_password).text.toString()
        val confirmation = findViewById<EditText>(R.id.register_password_confirmation).text.toString()

        Log.w(TAG, email)
        Log.w(TAG, password)
        Log.w(TAG, confirmation)

        if ((email.isNotEmpty() || password.isNotEmpty() || confirmation.isNotEmpty()) && email !== "" && password == confirmation){
            createAccount(email, password)
        }else{
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show()
        }
    }

    fun writeNewUser(userId: String, name: String, email: String) {
        val user = LoggedInUser(userId, name, email)

        database.child("users").child(userId).setValue(user)
    }
}