package elfak.mosis.petfinder
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import elfak.mosis.petfinder.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddPetFragment : Fragment() {

    // Initialize Firestore and Storage
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var pictureSet = false

    // Define the image file path
    private var currentPhotoPath: String? = null

    // Define UI elements
    private lateinit var typeEditText: EditText
    private lateinit var breedEditText: EditText
    private lateinit var colorEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var latitudeEditText: EditText
    private lateinit var lostCheckBox: CheckBox
    private lateinit var pictureImageView: ImageView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showPictureDialog()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val dugme = view?.findViewById<Button>(R.id.editmypet_cancel_button)
        dugme?.setOnClickListener {
            findNavController().popBackStack()
        }
        fillData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_pet, container, false)


        // Initialize UI elements
        typeEditText = view.findViewById(R.id.editmypet_type_edit)
        breedEditText = view.findViewById(R.id.editmypet_breed_edit)
        colorEditText = view.findViewById(R.id.editmypet_color_edit)
        nameEditText = view.findViewById(R.id.editmypet_name_edit)
        descriptionEditText = view.findViewById(R.id.editmypet_desc_edit)
        longitudeEditText = view.findViewById(R.id.editmypet_longitude_edit)
        latitudeEditText = view.findViewById(R.id.editmypet_latitude_edit)
        lostCheckBox = view.findViewById(R.id.lostBox)
        pictureImageView = view.findViewById(R.id.editmypet_picture)

        val selectPictureButton: Button = view.findViewById(R.id.editmypet_select_picture_button)
        val finishedButton: Button = view.findViewById(R.id.editmypet_finished_button)

        // Request permission when the button is clicked
        selectPictureButton.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        finishedButton.setOnClickListener {
            uploadPetData()
        }

        return view
    }
    private fun fillData() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude
            //val lat = view?.findViewById<EditText>(R.id.editmypet_latitude_edit)
            latitudeEditText.setText(latitude.toString())
            //val lon = view?.findViewById<EditText>(R.id.editmypet_longitude_edit)
            longitudeEditText.setText(longitude.toString())

        } else {
                   Log.e("FillData", "Location is null")
        }
    }
    private fun adjustPadding() {
        val picSet=view?.findViewById<ImageView>(R.id.editmypet_picture)
        if (pictureSet) {

            picSet?.setPadding(0, 0, 0, 0)
        } else {
            picSet?.setPadding(55, 55, 55, 55)
        }
    }
    private fun showPictureDialog() {
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
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {

                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun createImageFile(): File {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {

            currentPhotoPath = absolutePath
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            pictureSet=true
            adjustPadding()
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {

                    currentPhotoPath?.let { path ->

                        pictureImageView.setImageURI(Uri.parse(path))
                    }
                }
                REQUEST_IMAGE_PICK -> {

                    val selectedImageUri: Uri? = data?.data
                    pictureImageView.setImageURI(selectedImageUri)
                }
            }
            adjustPadding()
        }
    }

    private fun uploadPetData() {
        // Get user input
        val type = typeEditText.text.toString()
        val breed = breedEditText.text.toString()
        val color = colorEditText.text.toString()
        val name = nameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val longitude = longitudeEditText.text.toString()
        val latitude = latitudeEditText.text.toString()
        val lost = lostCheckBox.isChecked
        val postedID=Firebase.auth.currentUser!!.uid
        val date=System.currentTimeMillis()
        val petID = UUID.randomUUID().toString()


        val imageRef: StorageReference = storageRef.child("pets/$petID.jpg")

        // Get the image URI
        val pictureImageView = pictureImageView
        pictureImageView.isDrawingCacheEnabled = true
        pictureImageView.buildDrawingCache()
        val bitmap = (pictureImageView.drawable).toBitmap()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Upload the image
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            // Image uploaded successfully, now get the download URL
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Create a new pet object
                val newPet = hashMapOf(
                    "postedID" to postedID, // Assuming you have a postedID variable
                    "type" to type,
                    "breed" to breed,
                    "color" to color,
                    "name" to name,
                    "description" to description,
                    "longitude" to longitude,
                    "latitude" to latitude,
                    "lost" to lost,
                    "date" to date,
                    "imageUrl" to uri.toString(),
                    "petID" to petID   // da mogu lepo da sakupim sliku preko njega
                )


                db.collection("pets")
                    .document(petID)
                    .set(newPet)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Pet added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding pet", e)
                        Toast.makeText(
                            requireContext(),
                            "Failed to add pet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }.addOnFailureListener { e ->
            // Handle unsuccessful uploads
            Log.w(TAG, "Error uploading image", e)
            Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val TAG = "AddPetFragment"
    }
}
