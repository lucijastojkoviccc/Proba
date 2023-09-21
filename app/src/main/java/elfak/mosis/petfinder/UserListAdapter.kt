package elfak.mosis.petfinder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.data.User

class UserListAdapter (val ct: Context, val users: ArrayList<User>?, val listener: Pomoc ): RecyclerView.Adapter<UserListAdapter.ViewHolder>(){


    interface Pomoc
    {
        fun pomeraj(position: User)
    }
    class ViewHolder(itemView: View, /*listener:Pomoc*/) : RecyclerView.ViewHolder(itemView){
        val titleImage: ShapeableImageView =itemView.findViewById(R.id.title_image)
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)
        val ceoView: ConstraintLayout = itemView.findViewById(R.id.users_list)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.ViewHolder {
        val itemView=
            LayoutInflater.from(ct).inflate(R.layout.list_item_post,parent,false)
        return UserListAdapter.ViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return users?.count() ?: 0
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvHeading.text = users?.get(position)?.Name
                var userID = users?.get(position)?.ID

        Firebase.storage.getReference("profilePics/$userID.jpg").downloadUrl.addOnSuccessListener { uri ->
            Glide.with(ct).load(uri).into(holder.titleImage)
        }

        holder.ceoView.setOnClickListener{
            users?.get(position)?.let { it1 -> listener.pomeraj(it1) }  // hocu da mogu da kliknem bilo gde na tog usera i da me on odvede na EditProfile aka da mi pokaze info o tom nekome
        }
    }
}