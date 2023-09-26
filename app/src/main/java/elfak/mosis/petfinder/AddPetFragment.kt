package elfak.mosis.petfinder

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.databinding.FragmentAddPetBinding
import elfak.mosis.petfinder.model.LocationViewModel
import elfak.mosis.petfinder.model.NewPostViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddPetFragment : Fragment() {

    private lateinit var binding: FragmentAddPetBinding
    private val myPetViewModel: NewPostViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private var formCheck: BooleanArray = BooleanArray(6)
    private var pictureSet = false

    private val GALLERY_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose a method to get a picture:")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> openGallery()
                    2 -> dialog.dismiss()
                }
            }
            builder.show()
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
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        binding.editmypetCancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editmypetFinishedButton.setOnClickListener {
            val postedID = Firebase.auth.currentUser!!.uid
            val type = binding.editmypetTypeEdit.text.toString()
            val breed = binding.editmypetBreedEdit.text.toString()
            val color = binding.editmypetColorEdit.text.toString()
            val name = binding.editmypetNameEdit.text.toString()
            val description = binding.editmypetDescEdit.text.toString()
            val lost = binding.lostBox.isChecked
            val longitude = binding.editmypetLongitudeEdit.text.toString()
            val latitude = binding.editmypetLatitudeEdit.text.toString()

            if (type.isNotEmpty() && breed.isNotEmpty() && color.isNotEmpty() && name.isNotEmpty()) {
                addLostPet(
                    postedID,
                    type,
                    breed,
                    color,
                    name,
                    description,
                    longitude,
                    latitude,
                    lost
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

    private lateinit var currentPhotoPath: String

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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e("Error", ex.toString())
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
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
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val file = File(currentPhotoPath)
            val uri = Uri.fromFile(file)
            Glide.with(requireContext()).load(uri).into(binding.editmypetPicture)
            pictureSet = true
            adjustPadding()
        }
    }

    private fun addLostPet(
        postedID: String,
        type: String,
        breed: String,
        color: String,
        name: String,
        description: String,
        longitude: String,
        latitude: String,
        lost: Boolean
    ) {
        val newPet = hashMapOf(
            "postedID" to postedID,
            "type" to type,
            "breed" to breed,
            "color" to color,
            "name" to name,
            "description" to description,
            "longitude" to longitude,
            "latitude" to latitude,
            "lost" to lost
        )

        //povecavanje broja poena trenutno ulogovanom korisniku (on je dodao post)
        var id=Firebase.auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(id)
        docRef.update("points", FieldValue.increment(10))
            .addOnSuccessListener {
                Log.d("probam", "Points updated successfully. ")
            }

        Firebase.firestore
            .collection("pets")
            .add(newPet)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    requireContext(),
                    "Pet added successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                val storageRef = Firebase.storage.reference
                val file = Uri.fromFile(File(currentPhotoPath))
                val imageRef = storageRef.child("pets/${id}${name}.jpg")

                val uploadTask = imageRef.putFile(file)

                uploadTask.addOnSuccessListener {

                    // Now, you can retrieve the download URL if needed
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        documentReference.update("picture", imageUrl)
                    }
                }
            }
    }

    private fun adjustPadding() {
        if (pictureSet) {
            binding.editmypetPicture.setPadding(0, 0, 0, 0)
        } else {
            binding.editmypetPicture.setPadding(55, 55, 55, 55)
        }
    }
}