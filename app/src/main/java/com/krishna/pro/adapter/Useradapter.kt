import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.krishna.pro.ChatActivity
import com.krishna.pro.MainActivity
import com.krishna.pro.R
import com.krishna.pro.databinding.ItemProfileBinding
import com.krishna.pro.model.User

class Useradapter(private val context: MainActivity, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<Useradapter.UserViewHolder>() {
    private val users: ArrayList<User> = userList


    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemProfileBinding = ItemProfileBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.username.text = user.name

        // Log the URL of the user's profile image
        Log.d("Useradapter", "Profile Image URL: ${user.profileImage}")
        // Load the user's profile image using Glide
        Glide.with(context)
            .load(user.profileImage)
            .placeholder(R.drawable.avtar)
            .into(holder.binding.profile)

        holder.binding.profile.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", user.name)
            intent.putExtra("uid", user.uid) // Pass the user's UID as an extra
            intent.putExtra("PImage",user.profileImage)
            context.startActivity(intent)
        }

        holder.binding.dp.setOnClickListener {
            showDeleteConfirmationDialog(user)
        }

    }

    override fun getItemCount(): Int = userList.size



    private fun showDeleteConfirmationDialog(user: User) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)

        val confirmButton = dialogView.findViewById<Button>(R.id.confirm)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel)

        val alertDialog = dialogBuilder.create()

        confirmButton.setOnClickListener {
            // Delete the user profile
            deleteUserProfile(user)
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun deleteUserProfile(user: User) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("users")
        databaseReference.child(user.uid!!).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Profile deleted successfully
                    // You can also remove the user from the local list if needed
                    users.remove(user)
                    notifyDataSetChanged()
                    Toast.makeText(context, "Profile deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Error occurred while deleting the profile
                    Toast.makeText(context, "Error deleting profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


}
