package elfak.mosis.petfinder


import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import elfak.mosis.petfinder.databinding.FragmentMapBinding
import elfak.mosis.petfinder.model.LocationViewModel
import elfak.mosis.petfinder.model.MyPetViewModel
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
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var mapController: IMapController
    private lateinit var startMarker: Marker
    private val sharedViewModel: SharedViewHome by activityViewModels()

 private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startLocationTracking()
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
        //binding.button.visibility = View.GONE
//        binding.button.setOnClickListener {
//            findNavController().popBackStack()
//        }

        map.setMultiTouchControls(true)
        mapController = map.controller
        map.controller.setZoom(8.5)
        map.controller.setCenter(GeoPoint(44.0333, 20.8))
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
        } else {
            startLocationTracking()
        }

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
private fun startLocationTracking() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude

                // Update your UI with the new location
                // For example, you can update 'startMarker' position here
                startMarker.position = GeoPoint(latitude, longitude)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            10f,
            locationListener
        )
    }

}