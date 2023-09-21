package elfak.mosis.petfinder

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.DrawerLocker
import elfak.mosis.petfinder.databinding.FragmentEditProfileBinding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class FragmentEditProfile : Fragment()
{

    private lateinit var binding: FragmentEditProfileBinding
    private val REQUEST_IMAGE_CAPTURE = 1;
    private var CAMERA_REQUEST_CODE = 0
    private var GALLERY_REQUEST_CODE = 0
    private var selectedImageUri: Uri? = null
    private var formCheck:BooleanArray = BooleanArray(4)

    override fun onResume()
    {
        super.onResume()
        val title: TextView = requireActivity().findViewById(R.id.toolbar_title)
        val navigation: NavigationView = requireActivity().findViewById(R.id.nav_view)
        (activity as DrawerLocker?)!!.setDrawerEnabled(true)


        for (i in 0 until navigation.getMenu().size())
            navigation.getMenu().getItem(i).setChecked(false)

        title.text = "Edit Profile"
//        val buttonNotification: ImageView = requireActivity().findViewById(R.id.notification_toolbar)
//        val buttonFriend: ImageView = requireActivity().findViewById(R.id.addFriend_toolbar)

//        buttonFriend.visibility = View.GONE
//        buttonNotification.visibility = View.GONE
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun fillData()
    {
        //get ako nema internera ce da pokupi iz kesa podatke
        var id = Firebase.auth.currentUser!!.uid
        Firebase.firestore.collection("users").document(id).get().addOnSuccessListener {
            binding.editTextEditProfileName.setText(it["name"].toString())
            binding.editTextEditProfileEmail.setText(it["email"]?.toString())
            binding.editTextEditProfileDescription.setText(it["description"]?.toString())
            //binding.brojLajkova.setText(it["numberOfLikes"])
        }

        if (checkInternetConnection())
        {
            Firebase.storage.getReference("profilePics/$id.jpg").downloadUrl.addOnSuccessListener { uri->
                Glide.with(requireContext()).load(uri).into(binding.profileImage)
                binding.imgUser.isVisible = false
            }

        }
    }

    private fun checkInternetConnection() : Boolean
    {
        var manager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    private fun loadLocalProfilePicture()
    {
        try
        {
            var putanja = Environment.getExternalStorageDirectory().toString()
            var fajl = File(putanja, "accImage")
            var inputStream = FileInputStream(fajl)
            binding.profileImage.setImageBitmap(BitmapFactory.decodeStream(inputStream))
            binding.imgUser.isVisible = false
            inputStream.close()
        }
        catch (e:Exception)
        {
            Log.d("PetFinder", e.message.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val btnImage = binding.profileImage
        btnImage.setOnClickListener{
            dispatchTakePictureIntent()
            enableEdit()
        }

        val btnText = binding.textViewTakePicture
        btnText.setOnClickListener{
            dispatchTakePictureIntent()
            enableEdit()
        }

        fillData()

        binding.button.setOnClickListener {
            var name = binding.editTextEditProfileName.text.toString()
            var email = binding.editTextEditProfileEmail.text.toString()
            var desc = binding.editTextEditProfileDescription.text.toString()


            binding.profileImage.isDrawingCacheEnabled = true
            binding.profileImage.buildDrawingCache()
            var bitmap = (binding.profileImage.drawable as BitmapDrawable).bitmap
            val nizBajtova = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, nizBajtova)
            val pic = nizBajtova.toByteArray()

            changeUserData(name, email, desc, pic)
            val navigation: NavigationView = requireActivity().findViewById(R.id.nav_view)
            val headerLayout: View = navigation.getHeaderView(0)
            val image: ImageView = headerLayout.findViewById(R.id.imgUser)

            Glide.with(requireContext()).load(Uri.fromFile(File(currentPhotoPath))).into(image)

            findNavController().popBackStack()

        }

        binding.editTextEditProfileName.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                formCheck[1] = p0?.isNotEmpty() ?: false
                enableEdit()
            }
        })


        binding.editTextEditProfileEmail.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                formCheck[2] = p0?.isNotEmpty() ?: false
                enableEdit()
            }
        })


        binding.editTextEditProfileDescription.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                formCheck[3] = p0?.isNotEmpty() ?: false
                enableEdit()
            }
        })
        enableEdit()
    }

    private fun changeUserData(name: String,email: String, desc: String, pic: ByteArray)
    {
        if(Firebase.auth.currentUser?.uid?.isNotEmpty() == true)
        {
            var id = Firebase.auth.currentUser!!.uid
            var newDoc = hashMapOf<String, Any>(
                "name" to name,
                "email" to email,
                "description" to desc
            )
            Firebase.firestore
                .collection("users")
                .document(id)
                .update(newDoc)

            Firebase.storage
                .getReference("profilePics/$id.jpg")
                .putFile(Uri.fromFile(File(currentPhotoPath)))
        }
    }

    private fun enableEdit()
    {
        if(formCheck.all { it })
        {
            //binding.button.setBackgroundResource(R.drawable.et_button_shape_green)
            binding.button.isEnabled = true
        }
        else
        {
            //binding.button.setBackgroundResource(R.drawable.button_disabled)
            binding.button.isEnabled = false
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

    @Throws(IOException::class)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var file = File(currentPhotoPath)
            Glide.with(requireContext()).load(file).into(binding.profileImage)
            //binding.profileImagePlaceholder.setImageDrawable(null)
            formCheck[0] = true
            enableEdit()
        }
    }
    private fun verifyStoragePermissions()
    {
        var permisiije = ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permisiije != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity()
                , arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                , 1)
        }
    }


}