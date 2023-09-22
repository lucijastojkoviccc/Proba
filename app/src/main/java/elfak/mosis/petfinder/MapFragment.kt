package elfak.mosis.petfinder


import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private val myPetViewModel: MyPetViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    lateinit var map: MapView
    private lateinit var binding: FragmentMapBinding
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var mapController: IMapController
    private lateinit var startMarker: Marker
    private val sharedViewModel: SharedViewHome by activityViewModels()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var context = activity?.applicationContext;
        Configuration.getInstance().load(context,PreferenceManager.getDefaultSharedPreferences(context!!))
        map = binding.osmMapView

        binding.buttonConfirmMapPosition.visibility = View.GONE


        binding.buttonConfirmMapPosition.setOnClickListener {
            //podaci se setuju u onclick eventu
            findNavController().popBackStack()
        }


//        if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
//        else
//        {
//            setupMap()
//        }

//        map.controller.setZoom(15.0)
//        val startPoint = GeoPoint(43.3209, 21.8958)
//        map.controller.setCenter(startPoint)

        map.setMultiTouchControls(true)
        mapController = map.controller
        map.controller.setZoom(8.5)
        map.controller.setCenter(GeoPoint(44.0333, 20.8))
        startMarker = Marker(map)
        startMarker.icon = ContextCompat.getDrawable(requireContext(),R.drawable.baseline_location_on_24)
        startMarker.infoWindow = null
        if(sharedViewModel.longitude.value != null && sharedViewModel.latitude.value != null) {
            startMarker.position =GeoPoint(sharedViewModel.latitude.value!!,
                sharedViewModel.longitude.value!!)
        }
        else
        {
            startMarker.position = GeoPoint(44.0333, 20.8)
        }
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(startMarker);
        setOnMapClickOverlay()

    }

//    override fun onResume()
//    {
//        super.onResume()
//        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar)
//        toolbar.visibility = View.GONE
//        mapa.onResume()
//    }
//    private fun setMyLocationOverlay()
//    {
//        var myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activity), map)
//        myLocationOverlay.enableMyLocation()
//        map.overlays.add(myLocationOverlay)
//    }
//    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
//    { isGranted : Boolean ->
//        if (isGranted)
//        {
//            setMyLocationOverlay()
//            setOnMapClickOverlay()
//        }
//    }
//    private fun setupMap()
//    {
//        var startPoint = GeoPoint(43.3289,21.8958)
//        map.controller.setZoom(15.0)
//        if (locationViewModel.setLocation)
//            setOnMapClickOverlay()
//        else
//        {
//            if (myPetViewModel.selected != null)
//                startPoint = GeoPoint(myPetViewModel.selected!!.latitude.toDouble(),myPetViewModel.selected!!.longitude.toDouble())
//            else
//                setMyLocationOverlay()
//
//        }
//
//        map.controller.animateTo(startPoint)
//    }
private fun setOnMapClickOverlay()
{
    var receive = object : MapEventsReceiver
    {
        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean
        {
            binding.buttonConfirmMapPosition.visibility = View.VISIBLE
            map.overlays.remove(startMarker)
            startMarker.position = GeoPoint(p!!.latitude, p!!.longitude)
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(startMarker);
            sharedViewModel.latitude.value = p?.latitude
            sharedViewModel.longitude.value = p?.longitude
            return true
        }

        override fun longPressHelper(p: GeoPoint?): Boolean { return false }
    }
    var overlayEvents = MapEventsOverlay(receive)
    map.overlays.add(overlayEvents)
}

//    override fun onOptionsItemSelected(item: MenuItem): Boolean
//    {
//        return when (item.itemId)
//        {
//            R.id.action_new_place ->
//            {
//                this.findNavController().navigate(R.id.action_MapFragment_to_EditFragment)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

//    override fun onPrepareOptionsMenu(menu: Menu)
//    {
//        super.onPrepareOptionsMenu(menu)
//        var item=menu.findItem(R.id.my_places_list)
//        item.isVisible=false;
//        item=menu.findItem(R.id.action_show_map)
//        item.isVisible=false;
//
////        menu.findItem(R.id.action_my_places_list).isVisible = false
////        menu.findItem(R.id.action_show_map).isVisible = false
//    }
//
//    override fun onResume()
//    {
//        super.onResume()
//        map.onResume()
//    }
//
//    override fun onPause()
//    {
//        super.onPause()
//        map.onPause()
//    }


}