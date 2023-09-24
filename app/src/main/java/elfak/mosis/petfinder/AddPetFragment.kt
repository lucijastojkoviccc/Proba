package elfak.mosis.petfinder

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.databinding.FragmentAddPetBinding
import elfak.mosis.petfinder.model.LocationViewModel
import elfak.mosis.petfinder.model.NewPostViewModel

class AddPetFragment : Fragment() {

    private lateinit var binding: FragmentAddPetBinding
    private val myPetViewModel: NewPostViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private var formCheck: BooleanArray = BooleanArray(6)
    private var pictureSet = false

    private val GALLERY_REQUEST_CODE = 1

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillData()
        binding.editmypetFinishedButton.isEnabled = true

        binding.editmypetSelectPictureButton.setOnClickListener {
            loadLocalPetPicture()
        }

        binding.editmypetCancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editmypetFinishedButton.setOnClickListener {
            val type = binding.editmypetTypeEdit.text.toString()
            val breed = binding.editmypetBreedEdit.text.toString()
            val color = binding.editmypetColorEdit.text.toString()
            val name = binding.editmypetNameEdit.text.toString()
            val description = binding.editmypetDescEdit.text.toString()
            val longitude = binding.editmypetLongitudeEdit.text.toString()
            val latitude = binding.editmypetLatitudeEdit.text.toString()

            if (type.isNotEmpty() && breed.isNotEmpty() && color.isNotEmpty() && name.isNotEmpty()) {
                addLostPet(type, breed, color, name, description, longitude, latitude, currentPhotoPath)
            } else {
                Toast.makeText(requireContext(), "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    lateinit var currentPhotoPath: String
    private fun fillData() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude
            binding.editmypetLatitudeEdit.setText(latitude.toString())
            binding.editmypetLongitudeEdit.setText(longitude.toString())
        } else {
            // Handle the case when location is null (e.g., GPS is disabled)
            Log.e("FillData", "Location is null")
        }
    }



    private fun loadLocalPetPicture() {
        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            if (selectedImage != null) {
                Glide.with(requireContext()).load(selectedImage).into(binding.editmypetPicture)
                pictureSet = true
                adjustPadding()
            }
        }
    }

    private fun addLostPet(
        type: String,
        breed: String,
        color: String,
        name: String,
        description: String,
        longitude: String,
        latitude: String,
        picUri: String
    ) {
        val id = Firebase.auth.currentUser!!.uid
        val newPet = hashMapOf(
            "type" to type,
            "breed" to breed,
            "color" to color,
            "name" to name,
            "description" to description,
            "longitude" to longitude,
            "latitude" to latitude,
            "ownerID" to id
        )

        Firebase.firestore
            .collection("pets")
            .add(newPet)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    requireContext(),
                    "Pet added successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error adding pet: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // Now, you should upload the image using Firebase storage.
        // You can use `picUri` to get the image path.
    }

    private fun adjustPadding() {
        if (pictureSet) {
            binding.editmypetPicture.setPadding(0, 0, 0, 0)
        } else {
            binding.editmypetPicture.setPadding(55, 55, 55, 55)
        }
    }
}
