package elfak.mosis.petfinder

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.databinding.FragmentRegisterBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment()
{
    private lateinit var binding: FragmentRegisterBinding

    private var nameEntered = false
    private var emailEntered = false
    private var passEntered = false
    private val REQUEST_IMAGE_CAPTURE = 1;
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    lateinit var emailZaSliku:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
          binding.registerRegisterLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.pic.setOnClickListener{
            dispatchTakePictureIntent()
        }
        binding.registerRegisterButton.setOnClickListener {

            var name = binding.editTextRegisterName.text.toString()
            var email = binding.editTextRegisterEmail.text.toString()
            var pass = binding.editTextRegisterPassword.text.toString()
            var phone=binding.editTextRegisterPhone.text.toString()
                       register(name, email, pass, 1,phone)

        }

        binding.editTextRegisterName.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                nameEntered = p0?.isNotEmpty() ?: false
                enableRegister()
            }
        })
        binding.editTextRegisterEmail.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                emailEntered = android.util.Patterns.EMAIL_ADDRESS.matcher(p0).matches()
                enableRegister()
            }
        })

        binding.editTextRegisterPassword.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                passEntered = p0?.isNotEmpty() == true && binding.editTextRegisterConfirmPassword.text.toString() == p0.toString() && p0.length >= 8
                enableRegister()
            }
        })

        binding.editTextRegisterConfirmPassword.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                passEntered = p0?.isNotEmpty() == true && binding.editTextRegisterPassword.text.toString() == p0.toString() && p0.length >= 8
                enableRegister()
            }
        })
        binding.editTextRegisterPhone.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                nameEntered = p0?.isNotEmpty() ?: false
                enableRegister()
            }
        })


        enableRegister()
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

    private fun register(name: String, email: String, pass: String, points:Long, phone:String)
    {
        Firebase.auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if(it.isSuccessful)
            {

                var korisnik = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "points" to Int,
                    "phone" to phone,
                    "pets" to arrayListOf<String>())


                if(Firebase.auth.currentUser?.uid?.isNotEmpty() == true)
                {
                    korisnik["points"]=1
                        Firebase.firestore
                        .collection("users").document(Firebase.auth.currentUser!!.uid)
                        .set(korisnik)
                    val bitmapDrawable = binding.pic.drawable as? BitmapDrawable
                    val pic: ByteArray? = bitmapDrawable?.bitmap?.let { bitmap ->
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                        byteArrayOutputStream.toByteArray()
                    }
                    Firebase.storage.getReference("users/${Firebase.auth.currentUser!!.uid}.jpg").putBytes(pic!!).addOnSuccessListener {
                        Log.d("Mata", "ete gu")
                    }
                 }
                else {
                         Firebase.firestore
                        .collection("users")
                        .add(korisnik)
                }
            }
        }
    }

    private fun enableRegister()
    {
        if(nameEntered && emailEntered && passEntered)
        {
//            binding.registerRegisterButton.setBackgroundResource(R.id.register_registerButton)
//            binding.registerRegisterButton.isEnabled = true
            var reg = binding.registerRegisterButton
            reg.isEnabled=true
        }
        else
        {
            //binding.registerRegisterButton.setBackgroundResource(R.drawable.button_disabled)
            binding.registerRegisterButton.isEnabled = false
        }
    }

    private fun uploadImageToFirebaseStorage() {
        val file = Uri.fromFile(File(currentPhotoPath))
        val imageRef = storageRef.child("users/${file.lastPathSegment}")

        val uploadTask = imageRef.putFile(file)



        uploadTask.addOnSuccessListener {
            //Toast.makeText(requireContext(), "Image uploaded to Firebase Storage", Toast.LENGTH_SHORT).show()

            // Now, you can retrieve the download URL if needed
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                // Do something with the imageUrl (e.g., save it to Firestore)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Error uploading image: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //uploadImageToFirebaseStorage()
            if (File(currentPhotoPath).exists()) {
                var file = File(currentPhotoPath)

                Glide.with(requireContext()).load(file).into(binding.pic)
                //uploadImageToFirebaseStorage()
            } else {
                // Handle the case where no photo was taken
                Toast.makeText(requireContext(), "No photo taken", Toast.LENGTH_SHORT).show()
            }
        }
    }

}