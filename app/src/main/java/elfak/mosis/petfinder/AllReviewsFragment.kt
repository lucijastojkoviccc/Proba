package elfak.mosis.petfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.data.NewPost
import elfak.mosis.petfinder.data.Review
import elfak.mosis.petfinder.databinding.FragmentAllReviewsBinding
import elfak.mosis.petfinder.model.NewPostViewModel


class AllReviewsFragment : Fragment() {

    private var _binding: FragmentAllReviewsBinding? = null
    private val binding get() = _binding!!
    private val newPostViewModel: NewPostViewModel by activityViewModels()
    private lateinit var rList: ListView
    lateinit var stiglo:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllReviewsBinding.inflate(inflater, container, false)

//        stiglo=arguments!!.getString("newpostID").toString()
//        Firebase.firestore.collection("reviews").whereEqualTo("postID", stiglo).get().addOnSuccessListener{ documents ->
//            val reviewList = ArrayList<Review>()
//            for (document in documents) {
//
//                val r = document.toObject(Review::class.java)
//                r.ID=document.id
//                reviewList.add(r)
//            }
//            if (reviewList.isEmpty()) {
//                Toast.makeText(requireContext(), "No reviews", Toast.LENGTH_SHORT).show()
//            } else {
//                rList.adapter = ArrayAdapter<Review>(
//                    requireContext(),
//                    android.R.layout.simple_list_item_1,
//                    reviewList
//                )
//            }
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rList = view.findViewById(R.id.listViewKomentari)
        //updateView()
        stiglo=arguments!!.getString("newpost").toString()
        Firebase.firestore.collection("reviews").whereEqualTo("postID", stiglo).get().addOnSuccessListener{ documents ->
            val reviewList = ArrayList<Review>()
            for (document in documents) {

                val r = document.toObject(Review::class.java)
                r.ID=document.id

                reviewList.add(r)
            }
            if (reviewList.isEmpty()) {
                Toast.makeText(requireContext(), "No reviews", Toast.LENGTH_SHORT).show()
            } else {
                rList.adapter = ArrayAdapter<Review>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    reviewList
                )
            }
        }
    }

//    private fun updateView() {
//        val neUmemDaPosaljem= "null"
//        rList.adapter = null
//        Firebase.firestore.collection("reviews")
//            .whereEqualTo("postID", "5jO7HKquz47IZcbzzSTU")
//            .get()
//            .addOnSuccessListener { documents ->
//                val reviewList = ArrayList<Review>()
//                for (document in documents) {
//
//                    val r = document.toObject(Review::class.java)
//                    r.ID=document.id
//                    reviewList.add(r)
//                }
//                if (reviewList.isEmpty()) {
//                    Toast.makeText(requireContext(), "No reviews", Toast.LENGTH_SHORT).show()
//                } else {
//                    rList.adapter = ArrayAdapter<Review>(
//                        requireContext(),
//                        android.R.layout.simple_list_item_1,
//                        reviewList
//                    )
//                }
//            }
//
//    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}