package elfak.mosis.petfinder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.databinding.FragmentAddPetBinding
import elfak.mosis.petfinder.databinding.FragmentAddReviewBinding
import elfak.mosis.petfinder.databinding.FragmentAllReviewsBinding


class AddReviewFragment : Fragment() {

    private lateinit var binding: FragmentAddReviewBinding
    lateinit var stiglo:String
    lateinit var usrEm:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonAdd.isEnabled = true
        binding.buttonAdd.setOnClickListener {

            stiglo=arguments!!.getString("newpost2").toString()
            val userID = Firebase.auth.currentUser!!.uid
            val postID = stiglo
            val comment = binding.addText.text.toString()
            val grade= binding.numberPicker.text.toString()


            if (comment.isNotEmpty() && grade.isNotEmpty()) {
                addReview(
                    userID,
                    postID,
                    comment,
                    grade
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill in all the fields.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

//    private fun addReview(userID: String,postID: String, comment: String, grade: String) {
//        var id = Firebase.auth.currentUser!!.uid
//        val db = FirebaseFirestore.getInstance()
//        val docRef = db.collection("users").document(id)
//
//        val newRev = hashMapOf(
//            "userID" to userID,
//            "userEmail" to usrEm,
//            "postID" to postID,
//            "comment" to comment,
//            "grade" to grade
//        )
//
//
//        docRef.update("points", FieldValue.increment(5))
//            .addOnSuccessListener {
//                Log.d("probam", "Points updated successfully. ")
//            }
//        Firebase.firestore
//            .collection("reviews")
//            .add(newRev)
//            .addOnSuccessListener { documentReference ->
//                Toast.makeText(
//                    requireContext(),
//                    "Review added successfully!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }
private fun addReview(userID: String, postID: String, comment: String, grade: String) {
    val id = Firebase.auth.currentUser!!.uid
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users").document(id)

    // Retrieve current user's email
    docRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val usrEm = document.getString("email")
                if (usrEm != null) {
                    val newRev = hashMapOf(
                        "userID" to userID,
                        "userEmail" to usrEm,
                        "postID" to postID,
                        "comment" to comment,
                        "grade" to grade
                    )

                    docRef.update("points", FieldValue.increment(5))
                        .addOnSuccessListener {
                            Log.d("probam", "Points updated successfully. ")
                        }

                    Firebase.firestore
                        .collection("reviews")
                        .add(newRev)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(
                                requireContext(),
                                "Review added successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }
        .addOnFailureListener { exception ->
            // Handle errors here
            Log.w("Lulu", "Error getting document", exception)
        }
}

}