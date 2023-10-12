import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krishna.pro.ChatActivity
import com.krishna.pro.MainActivity
import com.krishna.pro.R
import com.krishna.pro.databinding.ItemProfileBinding
import com.krishna.pro.model.User

class Useradapter(private val context: MainActivity, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<Useradapter.UserViewHolder>() {

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

        // Load the user's profile image using Glide
        Glide.with(context)
            .load(user.profileImage)
            .placeholder(R.drawable.avtar)
            .into(holder.binding.profile)

        holder.binding.profile.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", user.name)
            intent.putExtra("uid", user.uid) // Pass the user's UID as an extra
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = userList.size
}
