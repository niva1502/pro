package com.krishna.pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {



        var passwordshowing = false

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val name=findViewById<EditText>(R.id.name)
        val email=findViewById<EditText>(R.id.email)
        val  mobile=findViewById<EditText>(R.id.mobile)
        val password=findViewById<EditText>(R.id.password1)
        val confirmpassword=findViewById<EditText>(R.id.confirmpassword)
        val signup=findViewById<AppCompatButton>(R.id.signup1)
        val signin=findViewById<TextView>(R.id.signin1)
        val mAuth = FirebaseAuth.getInstance()
        val eye = findViewById<ImageView>(R.id.eye2)



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

        signup.setOnClickListener {

            val userName = name.text.toString()
            val em=email.text.toString()
            val mob=mobile.text.toString()
            val pwd = password.text.toString()
            val cnfPwd = confirmpassword.text.toString()

            if (pwd != cnfPwd) {
                Toast.makeText(this@Register, "Please check both having the same password..", Toast.LENGTH_SHORT).show()
            }
            else if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(em) || TextUtils.isEmpty(mob) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(cnfPwd)) {
                Toast.makeText(this@Register, "Please enter your credentials..", Toast.LENGTH_SHORT).show()
            }
            else {
                mAuth.createUserWithEmailAndPassword(em, pwd)
                    .addOnCompleteListener(this@Register, OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                           // Toast.makeText(this@Register, "User Registered..", Toast.LENGTH_SHORT).show()
                           val getMobileText = mobile.text.toString()
                           // val getEmailText = email.text.toString()

                            // Assuming `phone` is the phone number you want to pass
                            val intent = Intent(this, OTPVerification::class.java)
                            intent.putExtra("phoneNumber", getMobileText)
                            startActivity(intent)

                        //    val intent = Intent(this,SetProfile::class.java)
                          //  intent.putExtra("mobile", getMobileText)
                          //  intent.putExtra("email", getEmailText)
                          //  startActivity(intent)

                        } else {
                            val error = task.exception?.message
                            Log.e("FirebaseAuth", "Registration failed: $error")
                            Toast.makeText(this@Register, "Registration failed: $error", Toast.LENGTH_SHORT).show()
                            //Toast.makeText(this@Register, "Fail to register user..", Toast.LENGTH_SHORT).show()
                        }
                    })
            }



        }

        signin.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}


