package elfak.mosis.petfinder


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import elfak.mosis.petfinder.data.NewPost
import elfak.mosis.petfinder.databinding.FragmentMapBinding
import elfak.mosis.petfinder.model.LocationViewModel
import elfak.mosis.petfinder.model.NewPostViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay



class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding

    lateinit var map: MapView
    private var filterNPosts:ArrayList<NewPost> = ArrayList()
    private var nizNPosts:ArrayList<NewPost> = ArrayList()
    private val newPost: NewPostViewModel by activityViewModels()
    private val locationViewModel:LocationViewModel by activityViewModels()
    var radijusKM=1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
 override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var context = activity?.applicationContext
        Configuration.getInstance().load(context,PreferenceManager.getDefaultSharedPreferences(context!!))
        map = requireView().findViewById<MapView>(R.id.map)
        map.setMultiTouchControls(true)


        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        else {

            if(locationViewModel.oneLocation==true)
            {
                map.overlays.clear()
                map.invalidate()
                var tmpPost:NewPost=newPost.selected!!
                tmpPost.latitude=locationViewModel.latitude.value!!
                tmpPost.longitude=locationViewModel.longitude.value!!

                Log.d("Mata",tmpPost.name)
                createMarker(tmpPost)
            }

            else {
                setMyLocationOverlay()
                setOnMapClickOveralay()
                val nizObserver = Observer<ArrayList<NewPost>> { newValue ->
                    nizNPosts = newValue


                    if (newPost.getFilteredPosts().isEmpty() &&
                        newPost.vratiNewPostsDatum().isEmpty() &&
                        newPost.getNPtype().isEmpty()
                    ) {
                        ucitajSveNP(1.0)
                    } else {

                        val myLocationOverlay =
                            map.overlays.find { it is MyLocationNewOverlay } as MyLocationNewOverlay?

                        myLocationOverlay?.runOnFirstFix {
                            ucitajFiltriraneNP(1000.0, myLocationOverlay)
                        }
                    }

                }
                locationViewModel.liveNP.observe(viewLifecycleOwner, nizObserver)
            }

        }

        map.controller.setZoom(10.0)
        val startPoint = GeoPoint(43.32472 ,21.90333)
        map.controller.setCenter(startPoint)

            var dugmeSearch=view.findViewById<Button>(R.id.buttonSearch)
            dugmeSearch.setOnClickListener()
            {
                var radijus=view.findViewById<EditText>(R.id.editTextText2).text.toString()

                if(radijus.isNotEmpty()) {
                    radijusKM = radijus.toDouble()
                    ucitajSveNP(radijusKM)
                }
                else
                {
                    Toast.makeText(context,"Set radius!",Toast.LENGTH_SHORT).show()
                }
            }

            var dugmeAdd = view.findViewById<Button>(R.id.buttonDodaj)
            dugmeAdd.setOnClickListener()
            {
                val myLocationOverlay =
                    map.overlays.find { it is MyLocationNewOverlay } as MyLocationNewOverlay?
                if (myLocationOverlay != null && myLocationOverlay.myLocation != null) {
                    val currentLocation = myLocationOverlay.myLocation
                    val latitude = currentLocation.latitude
                    val longitude = currentLocation.longitude
                    locationViewModel.setLocation(longitude.toString(), latitude.toString())
                    findNavController().navigate(R.id.action_MapFragment_to_AddPetFragment)
                }
            }
            var dugmeFilter = view.findViewById<Button>(R.id.buttonFilter)
            dugmeFilter.setOnClickListener {
                findNavController().navigate(R.id.action_MapFragment_to_FilterFragment)
            }

        }


    private fun ucitajFiltriraneNP(radijusKilometri: Double, myLocationOverlay:MyLocationNewOverlay) {

        if(newPost.getFilteredPosts().isNotEmpty())
        {
            filterNPosts=newPost.getFilteredPosts()
        }
        else if (newPost.vratiNewPostsDatum().isNotEmpty())
        {
            filterNPosts=newPost.vratiNewPostsDatum()
        }
        else
        {
            filterNPosts=newPost.getNPtype()
        }
        if (myLocationOverlay != null && myLocationOverlay.lastFix != null) {

            val currentLocation = myLocationOverlay.lastFix
            val latitude = currentLocation.latitude.toDouble()
            val longitude = currentLocation.longitude.toDouble()
            for(marker in locationViewModel.markeri )
            {
                map.overlays.remove(marker)
            }
            locationViewModel.markeri.clear()
            map.invalidate()
            for (jedanObjekat in filterNPosts) {
                if (jedanObjekat != null) {
                    val npLatitude = jedanObjekat.latitude.toDouble()
                    val npLongitude = jedanObjekat.longitude.toDouble()
                    val udaljenost = calculateDistance(
                        latitude, longitude, npLatitude, npLongitude)
                    if (udaljenost <= radijusKilometri) {
                        createMarker(jedanObjekat)
                    }
                }
            }
            map.invalidate()
        }
    }
    private fun setOnMapClickOveralay() {
        var receive = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                var lon = p?.longitude.toString()
                var lat = p?.latitude.toString()

                var latitudeCurrent = 0.0
                var longitudeCurrent = 0.0
                val myLocationOverlay =
                    map.overlays.find { it is MyLocationNewOverlay } as MyLocationNewOverlay?
                if (myLocationOverlay != null && myLocationOverlay.myLocation != null) {
                    val currentLocation = myLocationOverlay.myLocation
                    latitudeCurrent = currentLocation.latitude
                    longitudeCurrent = currentLocation.longitude
                }
                var startPoint = GeoPoint(latitudeCurrent, longitudeCurrent)
                var endPoint = GeoPoint(p!!.latitude, p!!.longitude)
                if (startPoint.distanceToAsDouble(endPoint) < 200) {
                    locationViewModel.setLocation(lon, lat)
                    findNavController().popBackStack()

                    return true
                }
                else
                {
                    Toast.makeText(context,"Destination out of reach",Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }
        var overlayEvents = MapEventsOverlay(receive)
        map.overlays.add(overlayEvents)
    }

    private fun setMyLocationOverlay() {
        var myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activity), map)
        myLocationOverlay.enableMyLocation()
        map.overlays.add(myLocationOverlay)
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setMyLocationOverlay()
            setOnMapClickOveralay()
        }
    }
    fun ucitajSveNP(radijus:Double) {

        val myLocationOverlay =
            map.overlays.find { it is MyLocationNewOverlay } as MyLocationNewOverlay?

        myLocationOverlay?.runOnFirstFix {
            ucitajNPURadijusu(radijus, myLocationOverlay)
        }

    }
    fun createMarker(np: NewPost) {

        if(map != null) {
            val marker = Marker(map)
            map.overlays?.add(marker)
            marker.position =
                GeoPoint(np.latitude.toDouble(), np.longitude.toDouble())
            marker.title = np.toString()
            Log.d("Mata","oce" )

            locationViewModel.markeri.add(marker)
            marker.setOnMarkerClickListener { marker, mapView ->
                val args = Bundle()
                args.putString("newpostId", np.ID)
                Log.d("Mata", np.ID)
                findNavController().navigate(R.id.action_MapFragment_to_EditFragment, args)

                true
            }
        }
    }

    fun ucitajNPURadijusu(radijusKilometri: Double, myLocationOverlay: MyLocationNewOverlay) {



        if (myLocationOverlay != null && myLocationOverlay.lastFix != null) {

            val currentLocation = myLocationOverlay.myLocation
            val latitude = currentLocation.latitude
            val longitude = currentLocation.longitude

            for(marker in locationViewModel.markeri )
            {
                map.overlays.remove(marker)
            }
            locationViewModel.markeri.clear()


            for (jedanObjekat in nizNPosts) {


                if (jedanObjekat.latitude!= null&& jedanObjekat.longitude!=null) {
                    val npLatitude = jedanObjekat.latitude.toDouble()
                    val npLongitude = jedanObjekat.longitude.toDouble()


                    val udaljenost = calculateDistance(
                        latitude, longitude, npLatitude, npLongitude
                    )

                    if (udaljenost <= radijusKilometri) {
                        createMarker(jedanObjekat)
                    }
                }
                else
                {
                    continue
                }
            }
            map.invalidate()
        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth's radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }
    override fun onDestroyView() {
        newPost.getFilteredPosts().clear()
        newPost.vratiNewPostsDatum().clear()

        locationViewModel.oneLocation=false
        locationViewModel.markeri.clear()
        super.onDestroyView()
    }
    override fun onResume()
    {
        super.onResume()
        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        map.onResume()
    }
    override fun onPause()
    {
        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        super.onPause()
        map.onPause()
    }
//private fun startLocationTracking() {
//        val locationManager =
//            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val locationListener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//                val latitude = location.latitude
//                val longitude = location.longitude
//                startMarker.position = GeoPoint(latitude, longitude)
//            }
//
//            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//            override fun onProviderEnabled(provider: String) {}
//            override fun onProviderDisabled(provider: String) {}
//        }
//
//        locationManager.requestLocationUpdates(
//            LocationManager.GPS_PROVIDER,
//            1000,
//            10f,
//            locationListener
//        )
//    }

}