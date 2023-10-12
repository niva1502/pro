package com.krishna.pro

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.krishna.pro.databinding.ActivitySetProfileBinding
import com.krishna.pro.model.User
import java.util.*

class SetProfile : AppCompatActivity() {

    private var binding: ActivitySetProfileBinding? = null
    private var selectedImage: Uri? = null
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if profile setup is already completed
        if (SharedPreferencesManager.isProfileCompleted(this)) {
            // Profile setup is completed, navigate to the next activity
            val intent = Intent(this@SetProfile, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivitySetProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Initialize Firebase Instances
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize a Progress Dialog
        val dialog = ProgressDialog(this@SetProfile)
        dialog.setTitle("Updating Profile...")
        dialog.setCancelable(false)

        // Hide the Action Bar
        supportActionBar?.hide()

        binding!!.imageView.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }

        binding!!.continueBtn.setOnClickListener {
            val name: String = binding!!.nameBox.text.toString().trim()
            if (name.isEmpty()) {
                binding!!.nameBox.error = "Please type a name"
            } else {
                dialog.show()
                saveUserData(name, dialog)
            }
        }
    }

    private fun saveUserData(name: String, dialog: ProgressDialog) {
        val uid = auth!!.uid
        val phone = auth!!.currentUser!!.phoneNumber
        val user = User(uid, name, phone, "No Image")

        // If an image is selected, upload it to Firebase Storage
        if (selectedImage != null) {
            val reference = storage!!.reference.child("Profile").child(uid!!)
            reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reference.downloadUrl.addOnCompleteListener { uri ->
                        val imageUrl = uri.toString()
                        user.profileImage = imageUrl
                        saveUserToDatabase(user, dialog)
                    }
                }
            }
        } else {
            // No image selected, save user data without an image
            saveUserToDatabase(user, dialog)
        }
    }

    private fun saveUserToDatabase(user: User, dialog: ProgressDialog) {
        database!!.reference
            .child("users")
            .child(user.uid!!)
            .setValue(user)
            .addOnSuccessListener {
                dialog.dismiss()
                // Mark profile setup as completed
                SharedPreferencesManager.setProfileCompleted(this)
                val intent = Intent(this@SetProfile, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == 45) {
                if (data != null && data.data != null) {
                    val uri = data.data
                    binding!!.imageView.setImageURI(uri)
                    selectedImage = uri
                }
            }
        }
    }
}
