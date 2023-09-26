package elfak.mosis.petfinder

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.data.NewPost
import elfak.mosis.petfinder.data.PetListItem
import elfak.mosis.petfinder.databinding.FragmentSecondBinding
import elfak.mosis.petfinder.model.LocationViewModel
import elfak.mosis.petfinder.model.NewPostViewModel
import java.util.*
import kotlin.collections.ArrayList

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private val myPetViewModel: NewPostViewModel by activityViewModels()
    private  val myLoc:LocationViewModel by activityViewModels()
    private lateinit var myPetsList: ListView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonMyPosts: RadioButton
    private lateinit var radioButtonPublic: RadioButton
    private lateinit var textViewTitle: TextView
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<PetListItem>
    lateinit var imageId:Array<Int>
    lateinit var heading: Array<String>
    val calendar = Calendar.getInstance()
    val customDate = calendar.time

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPetsList = view.findViewById(R.id.my_places_list)
        radioGroup = view.findViewById(R.id.radio_group)
        radioButtonMyPosts = view.findViewById(R.id.radio_my_posts)
        radioButtonPublic = view.findViewById(R.id.radio_public)

        updateViewForRadioButton()

        myPetViewModel.NewPosts.add(NewPost("", "", "", "", "", "", "", "", "","",false, ArrayList()))

        myPetsList.setOnItemClickListener { parent, view, position, id ->
            var myPet = myPetsList.adapter.getItem(position) as NewPost
            Log.d("Mata",myPet.ID)
            myLoc.setOneLocation(myPet.longitude, myPet.latitude)
            myPetViewModel.selected=myPet
            view.findNavController().navigate(R.id.action_SecondFragment_to_MapFragment)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            updateViewForRadioButton()
        }

    }

    private fun updateViewForRadioButton() {
        if (radioButtonMyPosts.isChecked) {

            myPetsList.adapter=null

            val currentUserUid = Firebase.auth.currentUser?.uid
            if (currentUserUid != null) {
                Firebase.firestore.collection("pets")
                    .whereEqualTo("postedID", currentUserUid)
                    .get()
                    .addOnSuccessListener { documents ->
                        val petList = ArrayList<NewPost>()
                        for (document in documents) {
                            val pet = document.toObject(NewPost::class.java)
                            pet.ID = document.id
                            petList.add(pet)

                        }
                        if (petList.isEmpty()) {
                            Toast.makeText(requireContext(), "My List is empty", Toast.LENGTH_SHORT).show()
                        } else {
                            myPetsList.adapter = ArrayAdapter<NewPost>(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                petList
                            )
                        }
                    }
                           }
        } else if (radioButtonPublic.isChecked) {
            myPetsList.adapter=null

            Firebase.firestore.collection("pets")
                .get()
                .addOnSuccessListener { documents ->
                    val petList = ArrayList<NewPost>()
                    for (document in documents) {
                        val pet = document.toObject(NewPost::class.java)
                        Log.d("Mata", "document.id")
                        pet.ID = document.id
                        petList.add(pet)
                    }
                    if (petList.isEmpty()) {
                        Toast.makeText(requireContext(), "The List is empty", Toast.LENGTH_SHORT).show()
                    } else {
                        myPetsList.adapter = ArrayAdapter<NewPost>(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            petList
                        )
                    }
                }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_new_place->{
                this.findNavController().navigate(R.id.action_SecondFragment_to_EditFragment)
                true
            }
            else->super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item=menu.findItem(R.id.action_my_places_list)
        item.isVisible=false;
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }
}