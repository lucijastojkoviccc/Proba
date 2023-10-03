package elfak.mosis.petfinder

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.data.NewPost
import elfak.mosis.petfinder.databinding.FragmentEditBinding
import elfak.mosis.petfinder.model.LocationViewModel
import elfak.mosis.petfinder.model.NewPostViewModel
import java.io.*
import java.util.*


class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
//    private val binding get() = _binding!!
    private val myPetViewModel: NewPostViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val REQUEST_IMAGE_CAPTURE = 1
    private var formCheck:BooleanArray = BooleanArray(6)
    private var pictureSet = false
    lateinit var kliknutID: String
    lateinit var petName:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (myPetViewModel.selected == null)
            (activity as AppCompatActivity).supportActionBar?.title = "Add my pet"

        binding= FragmentEditBinding.inflate(inflater, container, false)

        binding.editmypetFinishedButton.visibility= View.GONE
        binding.editmypetSelectPictureButton.visibility=View.GONE
        kliknutID = arguments!!.getString("newpostId").toString()
        //Log.d("Mata","Paris"+kliknutID)
        Firebase.firestore.collection("pets").document(kliknutID).get().addOnSuccessListener {document ->

            Log.d("Mata",document["type"].toString())

                binding.editmypetTypeEdit.setText(document["type"].toString())
                binding.editmypetBreedEdit.setText(document["breed"]?.toString())
                binding.editmypetColorEdit.setText(document["color"]?.toString())
                binding.editmypetNameEdit.setText(document["name"]?.toString())
                binding.editmypetDescEdit.setText(document["description"]?.toString())

                petName=document["petID"].toString()


            //if (checkInternetConnection())
           // {
                //ovde je potrebno da sakupim postedId i ime ljubimca "pets/${ownerId}${petName}.jpg"
                Firebase.storage.getReference("pets/${petName}.jpg").downloadUrl.addOnSuccessListener { uri->
                    Glide.with(requireContext()).load(uri).into(binding.editmypetPicture)
                    pictureSet=true
                    adjustPadding()
                }
            //}


        }
        return binding.root
    }

//    private fun checkInternetConnection() : Boolean
//    {
//        var manager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        return manager.activeNetworkInfo?.isConnectedOrConnecting == true
//    }

//    private fun loadLocalPetPicture()
//    {
//        try
//        {
//            var putanja = Environment.getExternalStorageDirectory().toString()
//            var fajl = File(putanja, "PetImage")
//            var inputStream = FileInputStream(fajl)
//            binding.editmypetPicture.setImageBitmap(BitmapFactory.decodeStream(inputStream))
//            inputStream.close()
//        }
//        catch (e:Exception)
//        {
//            Log.d("PetFinder", e.message.toString())
//        }
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editmypetSelectPictureButton.setOnClickListener {
            dispatchTakePictureIntent()
            enableEdit()
        }
        binding.editmypetCancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
        //ADD REVIEWS
        binding.addR.setOnClickListener {
            kliknutID = arguments!!.getString("newpostId").toString()
            Firebase.firestore.collection("pets").document(kliknutID).get().addOnSuccessListener { document ->
                   petName = document["petID"].toString()
            }
            val argsAll = Bundle()
            argsAll.putString("newpost2", petName)
            //Log.d("Mataa", petName)
            findNavController().navigate(R.id.action_EditFragment_to_AddReviewFragment, argsAll)
            //view.findNavController().navigate(R.id.action_EditFragment_to_AddReviewFragment)
        }
        //ALL REVIEWS
        binding.allR.setOnClickListener {
            kliknutID = arguments!!.getString("newpostId").toString()
            Firebase.firestore.collection("pets").document(kliknutID).get().addOnSuccessListener { document ->
                petName = document["petID"].toString()
            }
            val argsAll = Bundle()
            argsAll.putString("newpost", petName)
            //Log.d("Mataa", petName)
            findNavController().navigate(R.id.action_EditFragment_to_AllReviewsFragment, argsAll)
            //view.findNavController().navigate(R.id.action_EditFragment_to_AllReviewsFragment)
        }




        binding.editmypetFinishedButton.setOnClickListener{

            var type=binding.editmypetTypeEdit.text.toString()
            var breed=binding.editmypetBreedEdit.text.toString()
            var color=binding.editmypetColorEdit.text.toString()
            var name = binding.editmypetNameEdit.text.toString()
            var description = binding.editmypetDescEdit.text.toString()

            binding.editmypetPicture.isDrawingCacheEnabled = true
            binding.editmypetPicture.buildDrawingCache()
            var bitmap = (binding.editmypetPicture.drawable as BitmapDrawable).bitmap
            val nizBajtova = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, nizBajtova)
            val pic = nizBajtova.toByteArray()

            changeLostPetData(type, breed, color, name, pic, description)
            val navigation: NavigationView = requireActivity().findViewById(R.id.nav_view)
            val headerLayout: View = navigation.getHeaderView(0)
            val image: ImageView = headerLayout.findViewById(R.id.slika)

            Glide.with(requireContext()).load(Uri.fromFile(File(currentPhotoPath))).into(image)

            findNavController().popBackStack()
        }

        binding.editmypetTypeEdit.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                formCheck[1] = p0?.isNotEmpty() ?: false
                enableEdit()
            }
        })


        binding.editmypetBreedEdit.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                formCheck[2] = p0?.isNotEmpty() ?: false
                enableEdit()
            }
        })
        binding.editmypetColorEdit.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                formCheck[3] = p0?.isNotEmpty() ?: false
                enableEdit()
            }
        })
        binding.editmypetNameEdit.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                formCheck[4] = p0?.isNotEmpty() ?: false
                enableEdit()
            }
        })
        binding.editmypetDescEdit.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                formCheck[5] = p0?.isNotEmpty() ?: false
                enableEdit()
            }
        })
        enableEdit()
    }
    private fun changeLostPetData(type:String, breed:String, color:String, name:String, pic:ByteArray, description:String)
    {
        if(Firebase.auth.currentUser?.uid?.isNotEmpty() == true)  // ovde hocu da omogucim da menjam samo pets osobe koja je ulogovana ne bilo kome!!!
        {
            var id = Firebase.auth.currentUser!!.uid
            var newDoc = hashMapOf<String, Any>(
                "type" to type,
                "breed" to breed,
                "color" to color,
                "name" to name,
                "description" to description
            )
            Firebase.firestore
                .collection("pets")
                .document(id)
                .update(newDoc)
//
//            Firebase.storage
//                .getReference("petPic/$id.jpg")
//                .putFile(Uri.fromFile(File(currentPhotoPath)))
        }
    }
    private fun enableEdit()
    {
        if(formCheck.any{ it })
        {
           //binding.button.setBackgroundResource(R.drawable.et_button_shape_green)
            binding.editmypetFinishedButton.isEnabled = true
        }
        else
        {
            //binding.button.setBackgroundResource(R.drawable.button_disabled)
            binding.editmypetFinishedButton.isEnabled = false
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            var file = File(currentPhotoPath)
            Glide.with(requireContext()).load(file).into(binding.editmypetPicture)
            //binding.profileImagePlaceholder.setImageDrawable(null)
            formCheck[0] = true
            pictureSet=true
            adjustPadding()
            enableEdit()
        }
    }

    private fun adjustPadding() {
        if (pictureSet) {
            binding.editmypetPicture.setPadding(0, 0, 0, 0)
        } else {
            binding.editmypetPicture.setPadding(55, 55, 55, 55)
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