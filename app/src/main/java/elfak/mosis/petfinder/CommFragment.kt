package elfak.mosis.petfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.model.NewPostViewModel

class CommFragment : Fragment() {
    private val NewPostViewModel: NewPostViewModel by activityViewModels()
    private lateinit var  IDNP:String;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        postaviVrednosti();


        return inflater.inflate(R.layout.fragment_comm, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dugmeAzuriraj= view?.findViewById<Button>(R.id.buttonAzuriraj);

        dugmeAzuriraj!!.setOnClickListener()
        {
            azurirajAtribute();
        }
    }
    private fun postaviVrednosti() {
        val IDNP= NewPostViewModel.returnID();
        val objekatReference= Firebase.firestore.collection("pets").get()

//        objekatReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val newPost = snapshot.getValue(NewPost::class.java)
//                    val ime = newPost?.picture
//                    //val trenutnaOcena= frizerskiSalon?
//
//
//                    if (ime != null  ) {
//
//                        requireView().findViewById<TextView>(R.id.ime).text=ime;
//
//
//                    }
//                }
//            }
//
//
//        })

    }

    private fun azurirajAtribute() {

        val opisPolje=requireView().findViewById<EditText>(R.id.editDescription);
        val nazivFSpolje= requireView().findViewById<TextView>(R.id.ime)

        val opis= opisPolje.text.toString();

        val nazivFS= nazivFSpolje.text.toString();

        //pravimo novu referencu
        val currentUserID = Firebase.auth.currentUser!!.uid
        val objektiReference = Firebase.firestore.collection("users")
        val userReference = Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid).get()
        //userReference.child("br")


        if(opis.isNotEmpty() )
        {
            val mapiranjeAtributa = hashMapOf(
                "opis" to opis,
                "nazivFrizerskogSalona" to nazivFS

            )

//            objektiReference.setValue(mapiranjeAtributa).addOnSuccessListener {
//                Toast.makeText(context, "Uspesno dodato u recenzijama ", Toast.LENGTH_SHORT).show()
//                userReference.child("brojBodova").get().addOnSuccessListener {
////                    var brBodova = it.value.toString().toInt()
////                    brBodova += 5
////                    userReference.child("brojBodova").setValue(brBodova)
//
//                }
//                //findNavController().navigate(R.id.action_detailFragment_to_SecondFragment);
//
//            }.addOnFailureListener()
//            {
//                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
//            }
        }



    }

    // da moze da se seta po fragmentima

//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        val item = menu.findItem(R.id.signinFragment)
//        item.isVisible = false
//
//        val item1=menu.findItem(R.id.HomeFragment)
//        item1.isVisible=false;
//
//        val item2 = menu.findItem(R.id.signUpFragment)
//        item2.isVisible = false
//
//        val item3 = menu.findItem(R.id.editFragment)
//        item3.isVisible = false
//
//        val item4= menu.findItem(R.id.filtrirajFragment);
//        item4.isVisible=false;
//
//    }
}