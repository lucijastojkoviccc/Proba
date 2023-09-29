package elfak.mosis.petfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.data.User
import elfak.mosis.petfinder.databinding.FragmentSecondBinding
import elfak.mosis.petfinder.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {


    var sviKorisnici:ArrayList<User> = ArrayList()
    lateinit var listaKorisnika: ListView
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usersRef = Firebase.firestore.collection("users")

        usersRef.addSnapshotListener { snapshot, e ->

            if (snapshot != null) {
                val users = snapshot.toObjects(User::class.java)
                sortUsers(users)
            }
        }

    }

    private fun sortUsers(users: List<User>) {
        val sortedUsers = users.sortedByDescending { it.Points }
        if(view!=null)
        {
            val listaKorisnika: ListView = view!!.findViewById(R.id.listViewKorisnici)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, sortedUsers.map { it.prikaziKoirniske() })
            listaKorisnika.adapter = adapter
        }
        else {}

    }


}
