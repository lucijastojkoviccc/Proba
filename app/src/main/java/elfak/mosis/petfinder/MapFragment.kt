package elfak.mosis.petfinder


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import elfak.mosis.petfinder.data.NewPost
import elfak.mosis.petfinder.databinding.FragmentMapBinding
import elfak.mosis.petfinder.model.LocationViewModel
import elfak.mosis.petfinder.model.NewPostViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay



class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    lateinit var map: MapView
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var mapController: IMapController
    private lateinit var startMarker: Marker
    private val sharedViewModel: SharedViewHome by activityViewModels()
    private var filterNPosts:ArrayList<NewPost> = ArrayList()
    private var nizNPosts:ArrayList<NewPost> = ArrayList()
    private val newPost: NewPostViewModel by activityViewModels()
    private val locationViewModel:LocationViewModel by activityViewModels()
    var radijusKM=1.0

 private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //startLocationTracking()
            } else {
                Toast.makeText(requireContext(), "Please allow GPS to track your location", Toast.LENGTH_SHORT).show()
            }
        }
 override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var context = activity?.applicationContext;
        Configuration.getInstance().load(context,PreferenceManager.getDefaultSharedPreferences(context!!))
        map = binding.osmMapView

        map.setMultiTouchControls(true)
        mapController = map.controller
        map.controller.setZoom(8.5)
        map.controller.setCenter(GeoPoint(43.32472, 21.90333))
        startMarker = Marker(map)
        val d =
            ResourcesCompat.getDrawable(resources, org.osmdroid.library.R.drawable.person, null)
        startMarker.icon = d //ContextCompat.getDrawable(requireContext(),R.drawable.baseline_location_on_24)
        startMarker.infoWindow = null
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(startMarker);

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        else {
            //startLocationTracking()
            setMyLocationOverlay()
            //setOnMapClickOveralay();
            val nizObserver= Observer<ArrayList<NewPost>>{
                    newValue->
                nizNPosts=newValue;
                if(newPost.getFilteredPosts().isEmpty()&&
                    newPost.vratiNewPostsDatum().isEmpty() )
                {
                    ucitajSveFrizerskeSalone(100.0)
                }
                else
                {
                    val myLocationOverlay =
                        map.overlays.find { it is MyLocationNewOverlay } as MyLocationNewOverlay?
                    myLocationOverlay?.runOnFirstFix {
                        ucitajFiltriraneSalone(100.0,myLocationOverlay);
                    }
                }
            }
            locationViewModel.liveNizSalona.observe(viewLifecycleOwner, nizObserver);
        }
        var dugmeSearch=view.findViewById<Button>(R.id.buttonSearch);
        dugmeSearch.setOnClickListener()
        {
            var radijus=view.findViewById<EditText>(R.id.editTextText2).text.toString();

            if(radijus.isNotEmpty()) {
                radijusKM = radijus.toDouble();
                ucitajSveFrizerskeSalone(radijusKM);
            }
            else
            {
                Toast.makeText(context,"Niste uneli vrednost za radius!",Toast.LENGTH_SHORT).show();
            }
        }

//        var dugmePlus = view.findViewById<Button>(R.id.buttonDodaj);
//        dugmePlus.setOnClickListener()
//        {
//            val myLocationOverlay =
//                map.overlays.find { it is MyLocationNewOverlay } as MyLocationNewOverlay?
//            if (myLocationOverlay != null && myLocationOverlay.myLocation != null) {
//                val currentLocation = myLocationOverlay.myLocation
//                val latitude = currentLocation.latitude
//                val longitude = currentLocation.longitude
//                locationViewModel.setLocation(longitude.toString(), latitude.toString());
//                //findNavController().navigate(R.id.action_mapFragment_to_editFragment2);
//            }
//        }
    }
    private fun ucitajFiltriraneSalone(radijusKilometri: Double,myLocationOverlay:MyLocationNewOverlay) {

        if(newPost.getFilteredPosts().isNotEmpty())
        {
            filterNPosts=newPost.getFilteredPosts();
        }
        else
        {
            filterNPosts=newPost.vratiNewPostsDatum();
        }
        if (myLocationOverlay != null && myLocationOverlay.lastFix != null) {

            val currentLocation = myLocationOverlay.lastFix
            val latitude = currentLocation.latitude.toDouble()
            val longitude = currentLocation.longitude.toDouble()
            for(marker in locationViewModel.markeri )
            {
                map.overlays.remove(marker);
            }
            locationViewModel.markeri.clear();
            map.invalidate();
            for (jedanObjekat in filterNPosts) {
                if (jedanObjekat != null) {
                    val salonLatitude = jedanObjekat.latitude.toDouble()
                    val salonLongitude = jedanObjekat.longitude.toDouble()
                    val udaljenost = calculateDistance(
                        latitude, longitude, salonLatitude, salonLongitude)
                    if (udaljenost <= radijusKilometri) {
                        createMarker(jedanObjekat)
                    }
                }
            }
            map.invalidate()
        }
    }
//    private fun setOnMapClickOveralay() {
//        var receive = object : MapEventsReceiver {
//            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
//                var lon = p?.longitude.toString();
//                var lat = p?.latitude.toString();
//
//                var latitudeCurrent = 0.0;
//                var longitudeCurrent = 0.0;
//                val myLocationOverlay =
//                    map.overlays.find { it is MyLocationNewOverlay } as MyLocationNewOverlay?
//                if (myLocationOverlay != null && myLocationOverlay.myLocation != null) {
//                    val currentLocation = myLocationOverlay.myLocation
//                    latitudeCurrent = currentLocation.latitude
//                    longitudeCurrent = currentLocation.longitude
//                }
//                var startPoint = GeoPoint(latitudeCurrent, longitudeCurrent);
//                var endPoint = GeoPoint(p!!.latitude, p!!.longitude);
//                if (startPoint.distanceToAsDouble(endPoint) < 200) {
//                    locationViewModel.setLocation(lon, lat)
//                    findNavController().popBackStack();
//                    //findNavController().navigate(R.id.action_mapFragment_to_editFragment2);
//                    return true
//                }
//                else
//                {
//                    Toast.makeText(context,"Ne mozete dodati objekat ",Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            }
//            override fun longPressHelper(p: GeoPoint?): Boolean {
//                return false;
//            }
//        }
//        var overlayEvents = MapEventsOverlay(receive);
//        map.overlays.add(overlayEvents);
//    }

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
            //setOnMapClickOveralay()
        }
    }
    fun ucitajSveFrizerskeSalone(radijus:Double) {

        val myLocationOverlay =
            map.overlays.find { it is MyLocationNewOverlay } as MyLocationNewOverlay?

        myLocationOverlay?.runOnFirstFix {
            ucitajFrizerskeSaloneURadijusu(radijus, myLocationOverlay);
        }

    }
    fun createMarker(frizerskiSalon: NewPost) {
        if(map != null) {
            val marker = Marker(map)
            map.overlays?.add(marker)
            marker.position =
                GeoPoint(frizerskiSalon.latitude.toDouble(), frizerskiSalon.longitude.toDouble())
            marker.title = frizerskiSalon.toString()

            locationViewModel.markeri.add(marker);
        }
    }

    fun ucitajFrizerskeSaloneURadijusu(radijusKilometri: Double, myLocationOverlay: MyLocationNewOverlay) {



        if (myLocationOverlay != null && myLocationOverlay.lastFix != null) {

            val currentLocation = myLocationOverlay.myLocation
            val latitude = currentLocation.latitude.toDouble();
            val longitude = currentLocation.longitude.toDouble()

            for(marker in locationViewModel.markeri )
            {
                map.overlays.remove(marker);
            }
            locationViewModel.markeri.clear();


            for (jedanObjekat in nizNPosts) {

                if (jedanObjekat != null) {
                    val salonLatitude = jedanObjekat.latitude.toDouble()
                    val salonLongitude = jedanObjekat.longitude.toDouble()


                    val udaljenost = calculateDistance(
                        latitude, longitude, salonLatitude, salonLongitude
                    )

                    if (udaljenost <= radijusKilometri) {
                        createMarker(jedanObjekat)
                    }
                }
            }
            map.invalidate()
        }
    }
    // Funkcija za računanje udaljenosti između tačaka koristeći Haversine formulu
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
        newPost.getFilteredPosts().clear();
        newPost.vratiNewPostsDatum().clear();


        locationViewModel.markeri.clear();
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