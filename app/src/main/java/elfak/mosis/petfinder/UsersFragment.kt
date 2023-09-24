package elfak.mosis.petfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import elfak.mosis.petfinder.data.User

class UsersFragment : Fragment(), UserListAdapter.Pomoc {


    private lateinit var mDrawer: DrawerLayout
    lateinit var recycler: RecyclerView
    val sharedViewModel: SharedViewHome by activityViewModels()


    private lateinit var usersList: ListView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonRanked: RadioButton

    private val users: MutableList<User> = mutableListOf()       // Treba da ih sakupim iz storage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)

        usersList = view.findViewById(R.id.users_list)
        radioGroup = view.findViewById(R.id.radio_group)
        radioButtonRanked = view.findViewById(R.id.radio_ranked)

        radioButtonRanked.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateTitle("Ranked Users")
                // Fetch ranked users from Firebase and update `users` list
                // Then update the adapter
            } else {
                updateTitle("Users")
                // Fetch all users from Firebase and update `users` list
                // Then update the adapter
            }
        }

        // Fetch initial set of users from Firebase and update `users` list
        // Then update the adapter

        return view
    }
       override fun onResume()
    {
//        super.onResume()
//        val title: TextView = requireActivity().findViewById(R.id.toolbar_title)
//        title.text = "Teammates"
//        (activity as DrawerLocker?)!!.setDrawerEnabled(true)
//        val buttonNotification: ImageView = requireActivity().findViewById(R.id.notification_toolbar)
//        val buttonFriend: ImageView = requireActivity().findViewById(R.id.addFriend_toolbar)
//        val navigation: NavigationView = requireActivity().findViewById(R.id.nav_view)
//        for (i in 0 until navigation.getMenu().size())
//            navigation.getMenu().getItem(i).setChecked(false)
//        navigation.menu.findItem(R.id.nav_teammates).setChecked(true)
//
//        buttonFriend.visibility = View.VISIBLE
//        buttonNotification.visibility = View.GONE
    }

//    private fun showAllUsers() {
//        val adapter = UserListAdapter(requireContext(), R.layout.list_item_user, users)
//        usersList.adapter = adapter
//    }

//    private fun showRankedUsers() {
//        val rankedUsers = users.sortedByDescending { it.likes }  //sakupim im nekako likes....
//        val adapter = UserListAdapter(requireContext(), R.layout.list_item_user, rankedUsers)
//        usersList.adapter = adapter
//    }

    private fun updateTitle(title: String) {
        activity?.title = title
    }

    // Replace this function with your actual user data generation logic
    private fun generateRandomUsers(): List<User> {
        val userList = mutableListOf<User>()
        for (i in 1..10) {
            //userList.add(User("User$i", i * 100))
        }
        return userList
    }

    override fun pomeraj(position: User) {
        TODO("Not yet implemented")
    }
}
