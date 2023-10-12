package com.krishna.pro

import MessagesAdapter
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.TotpMultiFactorAssertion
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.krishna.pro.databinding.ActivityChatBinding
import com.krishna.pro.model.Message
import java.util.Calendar
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private var binding: ActivityChatBinding? = null
    private var adapter: MessagesAdapter? = null
    private var messages: ArrayList<Message>? = null
    private var senderRoom: String? = null
    private var receiverRoom: String? = null

    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var dialog: ProgressDialog? = null
    private var senderUid: String? = null
    private var receiverUid: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbar)
        database = FirebaseDatabase.getInstance()

        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this@ChatActivity)
        dialog?.setMessage("Uploading image...")
        dialog?.setCancelable(false)
        messages = ArrayList()

        val name = intent.getStringExtra("name")
        val profile = intent.getStringExtra("image")
        binding?.name?.text = name

        binding?.profile01?.let {
            Glide.with(this@ChatActivity)
                .load(profile)
                .placeholder(R.drawable.placeholder)
                .into(it)
        }



        binding?.imageView?.setOnClickListener { finish() }
        receiverUid = intent.getStringExtra("uid")

        senderUid = FirebaseAuth.getInstance().uid

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        if (database == null) {
            Toast.makeText(this, "DATABASE", Toast.LENGTH_SHORT).show()
        } else {

            database?.reference?.child("presence")?.child(receiverUid!!)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val status = snapshot.getValue(String::class.java)
                            if (status == "offline") {
                                binding?.status?.visibility = View.GONE
                            } else {
                                binding?.status?.text = status
                                binding?.status?.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })



            adapter = MessagesAdapter(this@ChatActivity, messages, senderRoom!!, receiverRoom!!)
            binding?.recyclerView?.layoutManager = LinearLayoutManager(this@ChatActivity)
            binding?.recyclerView?.adapter = adapter

            database?.reference?.child("chats")
                ?.child(senderRoom!!)
                ?.child("messages")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messages?.clear()
                        for (snapshot1 in snapshot.children) {
                            val message: Message? = snapshot1.getValue(Message::class.java)
                            message?.messageId = snapshot1.key
                            message?.let { messages?.add(it) }
                        }
                        adapter?.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })




            binding?.sendBtn?.setOnClickListener {
                val messageTxt: String = binding?.messageBox?.text.toString()
                val date = Date()
                val message = Message(messageTxt, senderUid, date.time)
                binding?.messageBox?.setText("")
                val randomKey = database?.reference?.push()?.key

                val lastMsgObj = HashMap<String, Any>()
                lastMsgObj["lastMsg"] = message.message!!
                lastMsgObj["lastMsgTime"] = date.time



                    database?.reference?.child("chats")?.child(senderRoom!!)
                        ?.updateChildren(lastMsgObj)
                    database?.reference?.child("chats")?.child(receiverRoom!!)
                        ?.updateChildren(lastMsgObj)
                    randomKey?.let { it ->
                        database?.reference?.child("chats")?.child(senderRoom!!)
                            ?.child("messages")?.child(it)
                            ?.setValue(message)?.addOnSuccessListener {
                                database?.reference?.child("chats")?.child(receiverRoom!!)
                                    ?.child("messages")?.child(randomKey)
                                    ?.setValue(message)?.addOnSuccessListener {

                                    }
                            }
                    }
                    // You don't need any navigation code here to return to MainActivity
                }

            binding?.attachment?.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, 25)
            }

            val handler = Handler()
            binding?.messageBox?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    database?.reference?.child("Presence")
                        ?.child(senderUid!!)
                        ?.setValue("typing...")
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(userStoppedTyping, 1000)
                }

                var userStoppedTyping = Runnable {
                    database?.reference?.child("Presence")
                        ?.child(senderUid!!)
                        ?.setValue("Online")
                }
            })

            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25) {
            if (data != null) {
                val selectedImage = data.data
                val calendar = Calendar.getInstance()
                val ref = storage?.reference?.child("chats")
                    ?.child(calendar.timeInMillis.toString() + "")
                dialog?.show()
                ref?.putFile(selectedImage!!)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            ref.downloadUrl.addOnSuccessListener { uri ->
                                val filePath = uri.toString()
                                val messageTxt: String = binding?.messageBox?.text.toString()
                                val date = Date()
                                val message = Message(messageTxt, senderUid, date.time)
                                message.message = "photo"
                                message.imageUrl = filePath
                                binding?.messageBox?.setText("")
                                val randomKey = database?.reference?.push()?.key
                                val lastMsgObj = HashMap<String, Any>()
                                lastMsgObj["lastMsg"] = message.message!!
                                lastMsgObj["lastMsgTime"] = date.time
                                database?.reference?.child("chats")
                                    ?.updateChildren(lastMsgObj)
                                database?.reference?.child("chats")
                                    ?.child(receiverRoom!!)
                                    ?.updateChildren(lastMsgObj)
                                randomKey?.let {
                                    database?.reference?.child("chats")
                                        ?.child(senderRoom!!)
                                        ?.child("messages")?.child(it)
                                        ?.setValue(message)?.addOnSuccessListener {
                                            database?.reference?.child("chats")
                                                ?.child(receiverRoom!!)
                                                ?.child("messages")?.child(it.toString())
                                                ?.setValue(message)?.addOnSuccessListener { }
                                        }
                                }
                                dialog?.dismiss()
                            }
                        } else {
                            dialog?.dismiss()
                            // Handle the error
                            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database?.reference?.child("Presence")
            ?.child(currentId!!)
            ?.setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database?.reference?.child("Presence")
            ?.child(currentId!!)
            ?.setValue("Offline")
    }
}
