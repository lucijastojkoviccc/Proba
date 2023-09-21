package elfak.mosis.petfinder


import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import elfak.mosis.petfinder.model.LocationViewModel
import elfak.mosis.petfinder.model.MyPetViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment() {

    private val myPetViewModel: MyPetViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    lateinit var map: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var ctx: Context? = activity?.applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx!!))
        map = requireView().findViewById(R.id.map)
        map.setMultiTouchControls(true)

        if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        else
        {
            setupMap()
        }

//        map.controller.setZoom(15.0)
//        val startPoint = GeoPoint(43.3209, 21.8958)
//        map.controller.setCenter(startPoint)
    }

    private fun setMyLocationOverlay()
    {
        var myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activity), map)
        myLocationOverlay.enableMyLocation()
        map.overlays.add(myLocationOverlay)
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    { isGranted : Boolean ->
        if (isGranted)
        {
            setMyLocationOverlay()
            setOnMapClickOverlay()
        }
    }
    private fun setupMap()
    {
        var startPoint = GeoPoint(43.3289,21.8958)
        map.controller.setZoom(15.0)
        if (locationViewModel.setLocation)
            setOnMapClickOverlay()
        else
        {
            if (myPetViewModel.selected != null)
                startPoint = GeoPoint(myPetViewModel.selected!!.latitude.toDouble(),myPetViewModel.selected!!.longitude.toDouble())
            else
                setMyLocationOverlay()

        }

        map.controller.animateTo(startPoint)
    }
private fun setOnMapClickOverlay()
{
    var receive = object : MapEventsReceiver
    {
        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean
        {
            var lon = p.longitude.toString()
            var lat = p.latitude.toString()
            locationViewModel.setLocation(lon, lat)
            findNavController().popBackStack()
            return true
        }

        override fun longPressHelper(p: GeoPoint?): Boolean { return false }
    }
    var overlayEvents = MapEventsOverlay(receive)
    map.overlays.add(overlayEvents)
}

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.action_new_place ->
            {
                this.findNavController().navigate(R.id.action_MapFragment_to_EditFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu)
    {
        super.onPrepareOptionsMenu(menu)
        var item=menu.findItem(R.id.my_places_list)
        item.isVisible=false;
        item=menu.findItem(R.id.action_show_map)
        item.isVisible=false;

//        menu.findItem(R.id.action_my_places_list).isVisible = false
//        menu.findItem(R.id.action_show_map).isVisible = false
    }

    override fun onResume()
    {
        super.onResume()
        map.onResume()
    }

    override fun onPause()
    {
        super.onPause()
        map.onPause()
    }


}