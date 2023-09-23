package elfak.mosis.petfinder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.data.MyPet
import elfak.mosis.petfinder.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment()
{
    private lateinit var binding: FragmentRegisterBinding

    private var nameEntered = false
    private var emailEntered = false
    private var passEntered = false



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    private fun login(email:String, pass:String)
//    {
//        Firebase.auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
//            when(it.isSuccessful)
//            {
//                true ->{
//                    var userID = Firebase.auth.currentUser!!.uid
//                    loadData(userID)
//                    gotoMainActivity()}
//                else -> Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
    fun loadData(id: String) {
        var name: String = ""
        var email: String = ""
        var description: String = ""
        //var points: Int = 0
        var pets: ArrayList<MyPet> = ArrayList()


        var pribavljanjePodataka = Firebase.firestore.collection("users").document(id).get()

        pribavljanjePodataka.addOnSuccessListener {
            name = (it["name"].toString())
            email = (it["email"].toString())
            description = (it["description"].toString())
            //points=(it["points"].)
            var lostPets = it["pets"] as ArrayList<MyPet>
            for (pet in lostPets) {
                pets.add(pet)
            }
        }
    }
    private fun gotoMainActivity()
    {
        var i = Intent(context, MainActivity::class.java)
        startActivity(i)
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
          binding.registerRegisterLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.registerRegisterButton.setOnClickListener {

            var name = binding.editTextRegisterName.text.toString()
            var email = binding.editTextRegisterEmail.text.toString()
            var pass = binding.editTextRegisterPassword.text.toString()
            register(name, email, pass, points = 0)
            ////////////////////
            //login(email, pass)
            ////////////////////
        }

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

        enableRegister()
    }

    private fun register(name: String, email: String, pass: String, points:Int) //, numberOfLikes:Int
    {
        Firebase.auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if(it.isSuccessful)
            {
                var korisnik = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "description" to null,
                    "points" to points,
                    "pets" to arrayListOf<String>()  //niz stringova koji su zapravo ID MyPet
                    )

                if(Firebase.auth.currentUser?.uid?.isNotEmpty() == true)
                {
                    Firebase.firestore
                        .collection("users")
                        .document(Firebase.auth.currentUser!!.uid)
                        .set(korisnik)
                    //////////////////
                    var userID = Firebase.auth.currentUser!!.uid
                    loadData(userID)
                    gotoMainActivity()
                    ////////////////////

                }

                else
                    Firebase.firestore
                        .collection("users")
                        .add(korisnik)

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

}