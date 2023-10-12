@file:Suppress("DEPRECATION")
package com.krishna.pro

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import kotlin.Deprecated
import kotlin.Deprecated as KotlinDeprecated


class OTPVerification : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        val otpEditText=findViewById<EditText>(R.id.otp)
        val verifyOTPButton=findViewById<AppCompatButton>(R.id.verify)
        val sendOTPButton=findViewById<AppCompatButton>(R.id.send)

        val phoneNumber = intent.getStringExtra("phoneNumber")

        mAuth = FirebaseAuth.getInstance();

        sendOTPButton.setOnClickListener {

            val phoneNumber = intent.getStringExtra("phoneNumber").toString()
            sendOTP(phoneNumber)
        }

        verifyOTPButton.setOnClickListener {
            val otp = otpEditText.text.toString()
            verifyOTP(otp)
        }
    }


    private fun sendOTP(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Handle verification failure, e.g., invalid phone number
                }

                override fun onCodeSent(
                    newVerificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = newVerificationId
                }
            }
        )
    }

    private fun verifyOTP(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Successfully signed in
                    // You can get the signed-in user with mAuth.currentUser

                    val intent = Intent(this,SetProfile::class.java)
                    startActivity(intent)

                }
                else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // Invalid OTP
                    } else {
                        // Other sign-in failures, handle appropriately
                    }
                }
            }
    }
}

