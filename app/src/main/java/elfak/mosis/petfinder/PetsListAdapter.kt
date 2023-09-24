package elfak.mosis.petfinder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.data.NewPost

class PetsListAdapter(val ct: Context, /*val pets: MutableList<MyPet>?, */val listener: Pomoc, val pets: ArrayList<NewPost>): RecyclerView.Adapter<PetsListAdapter.ViewHolder>()
{

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val ceoView: ConstraintLayout = itemView.findViewById(R.id.my_places_list)
        val titleImage: ShapeableImageView =itemView.findViewById(R.id.title_image)
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)
    }

    interface Pomoc{
        fun pomeraj(position: NewPost)
    }

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView=LayoutInflater.from(ct).inflate(R.layout.list_item_post,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pets?.count()?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // val currentItem = pets[position]

        holder.tvHeading.text= "Lost "+ pets?.get(position)?.type+", Breed: "+pets?.get(position)?.breed
        var petID=pets?.get(position)?.ID
        Firebase.storage.getReference("petPic/$petID.jpg").downloadUrl.addOnSuccessListener { uri ->
            Glide.with(ct).load(uri).into(holder.titleImage)
        }

        holder.ceoView.setOnClickListener{
           pets?.get(position)?.let { it1 -> listener.pomeraj(it1) }   //da mi ispadne EditFragment i da mi se popuni sa podacima od izabranog ljubimca
        }
    }

}
