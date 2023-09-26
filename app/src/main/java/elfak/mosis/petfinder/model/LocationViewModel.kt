package elfak.mosis.petfinder.model

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import elfak.mosis.petfinder.data.NewPost

import org.osmdroid.views.overlay.Marker


class LocationViewModel: ViewModel() {


    var markeri:ArrayList<Marker> = ArrayList();
    private var nizNewPosts:ArrayList<NewPost> = ArrayList()

    private var _nizNewPosts=MutableLiveData<ArrayList<NewPost>>()
    val liveNP:LiveData<ArrayList<NewPost>>
        get()=_nizNewPosts;

    private val _longitude = MutableLiveData<String>("")
    val longitude: LiveData<String>
        get() = _longitude

    private val _latitude = MutableLiveData<String>("")
    val latitude: LiveData<String>
        get() = _latitude

    var setLocation:Boolean = false
    var oneLocation:Boolean=false

    fun setLocation(lon: String, lat: String)
    {
        _longitude.value = lon
        _latitude.value = lat
        setLocation = true
    }
    fun setOneLocation(lon: String, lat: String)
    {
        _longitude.value = lon
        _latitude.value = lat
        setLocation = true
        oneLocation=true
    }
    fun getLocation(): LocationData {
        return LocationData(_longitude.value ?: "", _latitude.value ?: "")

    }
    fun setFrizerskiSaloni(saloni:ArrayList<NewPost>)
    {
        nizNewPosts=saloni;
    }

    fun getfrizerskiSaloni() : ArrayList<NewPost>
    {
        return nizNewPosts;
    }

    fun ucitajNewPostsURadijusu(  ) {

        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("pets")


        collectionRef.get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val nizNewPosts = ArrayList<NewPost>()

                    for (document in result) {
                        val newPost = document.toObject(NewPost::class.java)
                        nizNewPosts.add(newPost)
                    }

                    _nizNewPosts.postValue(nizNewPosts)
                }
            }
    }

    init {
        ucitajNewPostsURadijusu();
    }
}