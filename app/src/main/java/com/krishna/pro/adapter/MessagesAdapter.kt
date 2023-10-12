import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.krishna.pro.ChatActivity
import com.krishna.pro.R
import com.krishna.pro.databinding.SendMsgBinding
import com.krishna.pro.model.Message

class MessagesAdapter(
    var context: ChatActivity,
    messages: ArrayList<Message>?,
    senderRoom: String,
    receiverRoom: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages: ArrayList<Message> = ArrayList()
    private val ITEM_SENT = 1
    private val ITEM_RECEIVE = 2
    private val senderRoom: String
    private val receiverRoom: String

    init {
        if (messages != null) {
            this.messages = messages
        }
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(context).inflate(R.layout.send_msg, parent, false)
            SentMsgHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.recieve_msg, parent, false)
            ReceiveMsgHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (messages.isNotEmpty() && position < messages.size) {
            val message = messages[position]
            return if (FirebaseAuth.getInstance().uid == message.senderId) {
                ITEM_SENT
            } else {
                ITEM_RECEIVE
            }
        }
        return ITEM_SENT // Default to sent messages
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMsgHolder) {
            val viewHolder = holder
            if (message.message == "photo") {
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.message.visibility = View.GONE
                viewHolder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.image)
            } else {
                viewHolder.binding.message.text = message.message
            }
            viewHolder.itemView.setOnLongClickListener {
                // Handle the long click event
                // ...
                false // Return true if the long click was handled
            }
        } else if (holder is ReceiveMsgHolder) {
            val viewHolder = holder
            if (message.message == "photo") {
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.message.visibility = View.GONE
                viewHolder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.image)
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class SentMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SendMsgBinding = SendMsgBinding.bind(itemView)
    }

    inner class ReceiveMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SendMsgBinding = SendMsgBinding.bind(itemView)
    }
}
