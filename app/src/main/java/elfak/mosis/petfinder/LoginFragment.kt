package elfak.mosis.petfinder

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.MainActivity

import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.data.MyPet
import elfak.mosis.petfinder.databinding.FragmentLoginBinding


class LoginFragment : Fragment()
{
    private lateinit var binding: FragmentLoginBinding
    private var emailEntered = false
    private var passEntered = false
    private lateinit var Auth: FirebaseAuth
    //private lateinit var firestore: FirebaseFirestore
    private var authStateListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { p0 ->
            if (p0.currentUser != null && p0.currentUser!!.isEmailVerified )
                gotoMainActivity()
        }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        Auth = Firebase.auth
        var firestore = Firebase.firestore
        var user = Firebase.auth.currentUser
        var db = Firebase.database

        ////////////////////////////////// BOZE POMOZI

        var store = Firebase.storage
        var storageRef = store.reference

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        val regButton = binding.registerButton
        regButton.setOnClickListener {
            findNavController().navigate(R.id.frLogin_to_frReg)
        }

        binding.loginButton.setOnClickListener {
            var email = binding.editTextTextEmailAddress.text.toString()
            var pass = binding.editTextTextPassword.text.toString()
            login(email, pass)
        }

        binding.editTextTextEmailAddress.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                emailEntered = android.util.Patterns.EMAIL_ADDRESS.matcher(p0).matches()
                enableLogin()
            }
        })

        binding.editTextTextPassword.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                passEntered = p0?.isNotEmpty() ?: false
                enableLogin()
            }
        })

             enableLogin()
    }
    fun loadData(id: String) {
        var name: String = ""
        var email: String = ""
        var description: String = ""
        var numberOfLikes: Int = 0
        var pets: ArrayList<MyPet> = ArrayList()


        var pribavljanjePodataka = Firebase.firestore.collection("users").document(id).get()

        pribavljanjePodataka.addOnSuccessListener {
            name = (it["name"].toString())
            email = (it["email"].toString())
            description = (it["description"].toString())
            //numberOfLikes=(it["numberOfLikes"].)
            var lostPets = it["pets"] as ArrayList<MyPet>
            for (pet in lostPets) {
                pets.add(pet)
            }
        }
    }

    private fun login(email:String, pass:String)
    {
        Firebase.auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
            when(it.isSuccessful)
            {
                true ->
                {
                    if (Firebase.auth.currentUser?.isEmailVerified == false)
                        Toast.makeText(context, R.string.message_not_verified, Toast.LENGTH_SHORT).show()
                    else
                    {
                        var userID = Firebase.auth.currentUser!!.uid
                        loadData(userID)

                        //gotoMainActivity()
                    }

                }
                else -> Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun enableLogin()
    {
        if (emailEntered && passEntered)
        {
            binding.loginButton.setBackgroundResource(R.id.guideline_register_registerButton)
            binding.loginButton.isEnabled = true
        }
        else
        {
           //binding.loginButton.setBackgroundResource(R.drawable.button_disabled)
            binding.loginButton.isEnabled = false
        }
    }

    private fun gotoMainActivity()
    {
        var i = Intent(context, MainActivity::class.java)
        startActivity(i)
    }

    override fun onStart()
    {
        super.onStart()
        Auth.addAuthStateListener(authStateListener)
    }
}