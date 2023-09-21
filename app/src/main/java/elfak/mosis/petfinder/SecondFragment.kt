package elfak.mosis.petfinder

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import elfak.mosis.petfinder.data.MyPet
import elfak.mosis.petfinder.data.PetListItem
import elfak.mosis.petfinder.databinding.FragmentSecondBinding
import elfak.mosis.petfinder.model.MyPetViewModel

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    private val myPetViewModel:MyPetViewModel by activityViewModels()
    private lateinit var myPetsList: ListView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonMyPosts: RadioButton
    private lateinit var radioButtonPublic: RadioButton
    private lateinit var textViewTitle: TextView
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<PetListItem>
    lateinit var imageId:Array<Int>
    lateinit var heading: Array<String>
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPetsList: ListView = requireView().findViewById<ListView>(R.id.my_places_list)
        myPetViewModel.myPetsList.add(MyPet("", "", "", "", "", "", "", "", ""))
        myPetsList.adapter = ArrayAdapter<MyPet>(
            view.context,
            android.R.layout.simple_list_item_1,
            myPetViewModel.myPetsList
        )
        myPetsList.setOnItemClickListener { adapterView, view, i, l ->
            var myPet = adapterView?.adapter?.getItem(i) as MyPet
            myPetViewModel.selected = myPet
            view.findNavController().navigate(R.id.action_SecondFragment_to_EditFragment)
        }
        //myPetsList = view.findViewById(R.id.my_places_list) ovo kaze gpt ali gore vec ima
        radioGroup = view.findViewById(R.id.radio_group)
        radioButtonMyPosts = view.findViewById(R.id.radio_my_posts)
        radioButtonPublic = view.findViewById(R.id.radio_public)
        textViewTitle = view.findViewById(R.id.text_view_title)
        updateViewForRadioButton()
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            updateViewForRadioButton()
        }
    }
    private fun updateViewForRadioButton() {
        if (radioButtonMyPosts.isChecked) {
            textViewTitle.text = "My Posts"
            myPetsList.adapter = ArrayAdapter<MyPet>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                myPetViewModel.myPetsList
            )
        } else if (radioButtonPublic.isChecked) {
            textViewTitle.text = "Public"
            // Set up adapter with other people's posts
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
        //setHasOptionsMenu(true)

        // ovaj na snimku stavlja sve staticke stvari a meni treba da mi se to dovlaci iz baze sve

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }
}