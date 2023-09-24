package elfak.mosis.petfinder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.databinding.FragmentFirstBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    private val REQUEST_IMAGE_CAPTURE = 1;

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(requireContext(), "Please allow PetFinder to use your phone camera", Toast.LENGTH_SHORT).show()
            }
        }

override fun onResume() {
    super.onResume()
    val title: TextView = requireActivity().findViewById(R.id.toolbar_title)
    val navigation: NavigationView = requireActivity().findViewById(R.id.nav_view)  //wtf is this for??

    (activity as DrawerLocker?)!!.setDrawerEnabled(true)
    for (i in 0 until navigation.getMenu().size())
        navigation.getMenu().getItem(i).setChecked(false)
    title.text = "PetFinder"

}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun onLostPetClick(view: View) {
        this.findNavController().navigate(R.id.action_FirstFragment_to_AddPetFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnHere=binding.clickHereText
        btnHere.setOnClickListener{
           onLostPetClick(btnHere)
        }

        val fabC=binding.cameraFab
        fabC.setOnClickListener{
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            } else {
                dispatchTakePictureIntent()
            }
        }
    }

    private fun dispatchTakePictureIntent()
{
    try
    {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try
                {
                    createImageFile()
                }
                catch (ex: IOException)
                {
                    Log.d("PetFinder", "Error while trying to launch camera - " + ex.message)
                    Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_SHORT).show()

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
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
    catch (e:Exception)
    {
        Log.d("PetFinder", "Error while trying to launch camera - " + e.message)
        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_SHORT).show()
    }
}
    lateinit var currentPhotoPath: String
    private fun createImageFile(): File
    {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
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

    private fun saveNewPost(lon: String, lat: String, pic:String)
    {
        Firebase.firestore.collection("users").whereEqualTo("UserID", Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {

            for(doc in it)
            {

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            var file = File(currentPhotoPath)
//            Glide.with(requireContext()).load(file).into(binding.profileImage)
//            //binding.profileImagePlaceholder.setImageDrawable(null)
//            formCheck[0] = true
//            enableEdit()
        }
    }

}