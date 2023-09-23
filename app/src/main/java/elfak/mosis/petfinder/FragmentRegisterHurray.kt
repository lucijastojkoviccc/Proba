package elfak.mosis.petfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.R
import elfak.mosis.petfinder.databinding.FragmentRegisterHurrayBinding


class FragmentRegisterHurray : Fragment()
{

    private lateinit var binding: FragmentRegisterHurrayBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        binding = FragmentRegisterHurrayBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonGoBack.setOnClickListener {
            var user = Firebase.auth.currentUser
            if(user==null)
                findNavController().navigate(R.id.frRegHurray_to_frLogin)
            else
                findNavController().navigate(R.id.frRegHurray_to_FirstFragment)
        }
    }

}