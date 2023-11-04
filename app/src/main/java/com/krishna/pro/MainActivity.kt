@file:Suppress("DEPRECATION")

package com.krishna.pro

import Useradapter
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.krishna.pro.databinding.ActivityMainBinding
import com.krishna.pro.model.User

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var users: ArrayList<User> = ArrayList()
    private var useradapter: Useradapter? = null

    private lateinit var databaseReference: DatabaseReference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (auth.currentUser == null) {
            // If the user is not authenticated, navigate to the login activity or perform the required action.
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        useradapter = Useradapter(this, users)
        val layoutManager = GridLayoutManager(this, 2)
        binding.mRec.layoutManager = layoutManager
        binding.mRec.adapter = useradapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                val currentUserId = auth.uid

                for (snapshot1 in snapshot.children) {
                    val user: User = snapshot1.getValue(User::class.java) ?: continue
                    if (currentUserId != user.uid) {
                        users.add(user)
                    }
                }


                useradapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                val errorMessage = "Database error: ${error.message}"
                Log.e("FirebaseError", errorMessage)
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item1 -> {
                // Handle Option 1 click by starting the EditProfileActivity
                logoutUser()
                return true
            }
            R.id.menu_item2 -> {
                // Handle Option 1 click by starting the EditProfileActivity
                val intent=Intent(this,AboutUs::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        if (currentId != null) {
            FirebaseDatabase.getInstance().getReference("presence/$currentId")
                .setValue("online")
        }
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        if (currentId != null) {
            FirebaseDatabase.getInstance().getReference("presence/$currentId")
                .setValue("offline")
        }
    }


    override fun onBackPressed() {

        super.onBackPressed()
        finishAffinity()

    }

}
