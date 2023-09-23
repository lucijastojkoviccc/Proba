package elfak.mosis.petfinder

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Tasks
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.petfinder.data.MyPet
import elfak.mosis.petfinder.data.User
import elfak.mosis.petfinder.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLocker
{

    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    private lateinit var mDrawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private val shareViewModel : SharedViewHome by viewModels()

    fun loadData(id: String) {
        var name: String = ""
        var email: String = ""
        var description: String = ""
        var points: Int = 0
        var pets: ArrayList<String> = ArrayList()


        var pribavljanjePodataka = Firebase.firestore.collection("users").document(id).get()

        pribavljanjePodataka.addOnSuccessListener {
            name = (it["name"].toString())
            email = (it["email"].toString())
            description = (it["description"].toString())
            points=(it["points"] as Int)
            var lostPets = it["pets"] as ArrayList<String>
            if(!lostPets.isEmpty())
                for (pet in lostPets) {
                    pets.add(pet)
                }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        mDrawer = findViewById(R.id.drawer_layout)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        toggle = ActionBarDrawerToggle(
            this,
            mDrawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        mDrawer.addDrawerListener(toggle)

        var storage = Firebase.storage
        var db = Firebase.firestore
        var dbb = Firebase.database
        var user = Firebase.auth.currentUser
        if(user!=null)
        {
            var userID = Firebase.auth.currentUser!!.uid
            loadData(userID)
        }


        toggle.syncState()
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_posts)
        }

        mDrawer.closeDrawer(GravityCompat.START)

        val headerLayout: View = navigationView.getHeaderView(0)
        val image: ImageView = headerLayout.findViewById(R.id.slika)
    }


    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START))
            mDrawer.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()

    }

override fun onNavigationItemSelected(item: MenuItem): Boolean
{
    when(item.itemId)
    {
        R.id.nav_posts ->
        {
            when(navController.currentDestination?.id)
            {

                R.id.fragmentEditProfile->
                {
                    navController.navigate(R.id.action_fragmentEditProfile_to_SecondFragment)
                }
                R.id.FirstFragment->{
                    navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
                }
                R.id.EditFragment->{
                    navController.navigate(R.id.action_EditFragment_to_SecondFragment)
                }
                R.id.UsersFragment->{
                    navController.navigate((R.id.action_UsersFragment_to_SecondFragment))
                }
                R.id.MapFragment->{
                    navController.navigate(R.id.action_MapFragment_to_SecondFragment)
                }

            }
        }
        R.id.nav_users->
        {
            when(navController.currentDestination?.id)
            {
                R.id.FirstFragment->{
                    navController.navigate(R.id.action_FirstFragment_to_UsersFragment)
                }
                R.id.fragmentEditProfile->
                {
                    navController.navigate(R.id.action_fragmentEditProfile_to_UsersFragment)
                }
                R.id.EditFragment->{
                    navController.navigate(R.id.action_EditFragment_to_UsersFragment)
                }
                R.id.SecondFragment->{
                    navController.navigate(R.id.action_SecondFragment_to_UsersFragment)
                }
                R.id.MapFragment->{
                    navController.navigate(R.id.action_MapFragment_to_UsersFragment)
                }
            }
        }
        R.id.nav_map ->
        {
            when(navController.currentDestination?.id)
            {
                R.id.FirstFragment->{
                    navController.navigate(R.id.action_FirstFragment_to_MapFragment)
                }
                R.id.fragmentEditProfile->
                {
                    navController.navigate(R.id.action_fragmentEditProfile_to_MapFragment)
                }
                R.id.EditFragment->{
                    navController.navigate(R.id.action_EditFragment_to_MapFragment)
                }
                R.id.UsersFragment->{
                    navController.navigate((R.id.action_UsersFragment_to_MapFragment))
                }
                R.id.SecondFragment->{
                    navController.navigate(R.id.action_SecondFragment_to_MapFragment)
                }
            }
        }
        R.id.nav_info->
        {
            val i: Intent = Intent(this, About::class.java)
            startActivity(i)
        }

        R.id.nav_myProfile->
        {
            when(navController.currentDestination?.id)
            {
                R.id.FirstFragment->{
                    navController.navigate(R.id.action_FirstFragment_to_fragmentEditProfile)
                }
                R.id.SecondFragment->{
                    navController.navigate(R.id.action_SecondFragment_to_fragmentEditProfile)
                }
                R.id.MapFragment->{
                    navController.navigate(R.id.action_MapFragment_to_fragmentEditProfile)
                }
            }
        }

             R.id.nav_logout ->

        {
            val navigation: NavigationView = findViewById(R.id.nav_view)
            for (i in 0 until navigation.getMenu().size())
                navigation.getMenu().getItem(i).setChecked(false)

            var youSureDialog = AlertDialog.Builder(this)
            youSureDialog
                .setMessage(R.string.logout_question)
                .setPositiveButton("Yes") { p0, p1 ->
                    if (p1 == DialogInterface.BUTTON_POSITIVE)
                    {
                        Firebase.auth.signOut()
                        //pogasiSVE()
                        var i = Intent(this, ActivityLogin::class.java)
                        startActivity(i)
                    }
                }
                .setNegativeButton("No") {p0, p1 -> }
                .show()
        }

    }
    mDrawer.closeDrawer(GravityCompat.START)
    return true
}


//    private fun pogasiSVE()
//    {
//        val intent = Intent(this, ServiceNotificationSpamFirestore::class.java)
//        stopService(intent)
//        val intent2 = Intent(this, ServicePushNotification::class.java)
//        stopService(intent2)
//        var i = Intent(this, ServiceSendLocation::class.java)
//        stopService(i)
//
//    }

    override fun setDrawerEnabled(enabled: Boolean)
    {
        val lockMode = if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        mDrawer.setDrawerLockMode(lockMode)
        toggle.setDrawerIndicatorEnabled(enabled);


    }

}