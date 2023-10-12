package com.krishna.pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView // Import TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener as OnCompleteListener1

class Login : AppCompatActivity() {

  private lateinit var mAuth: FirebaseAuth
  lateinit var eye: ImageView

  override fun onCreate(savedInstanceState: Bundle?) {

    var passwordshowing = false



    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    val em: EditText = findViewById(R.id.email1)
    val password: EditText = findViewById(R.id.password)
    val signin: AppCompatButton = findViewById(R.id.signin)
    val signup: TextView = findViewById(R.id.signup) // Change to TextView
    eye = findViewById(R.id.eye)
    mAuth = FirebaseAuth.getInstance()


    // Checking if password is shown or not

    eye.setOnClickListener {
      if (passwordshowing) {
        passwordshowing = false

        password.inputType =
          InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        eye.setImageResource(R.drawable.eyeoff)
      } else {
        passwordshowing = true
        password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        eye.setImageResource(R.drawable.eye)
      }

      // Move cursor to the end of text
      password.setSelection(password.length())
    }

    signin.setOnClickListener {
      val email = em.text.toString()
      val password = password.text.toString()

      if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
        Toast.makeText(this@Login, "Please enter your credentials..", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this@Login, OnCompleteListener1<AuthResult> { task ->
          if (task.isSuccessful) {
           // Toast.makeText(this@Login, "Login Successful..", Toast.LENGTH_SHORT).show()
            val i = Intent(this@Login, SetProfile::class.java)
            startActivity(i)
            finish()
          } else {
            Toast.makeText(this@Login, "Please enter valid user credentials..", Toast.LENGTH_SHORT).show()
          }
        })

    }

    signup.setOnClickListener {
      val intent = Intent(this, Register::class.java)
      startActivity(intent)
    }
  }

  override fun onStart() {
    super.onStart()

    if(mAuth.currentUser!=null){
      val intent=Intent(this,SetProfile::class.java)
      startActivity(intent)
    }
  }
}
