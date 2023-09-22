package elfak.mosis.petfinder

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import elfak.mosis.petfinder.databinding.FragmentFirstBinding


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private var CAMERA_REQUEST_CODE = 0
    private var GALLERY_REQUEST_CODE = 0
    private val REQUEST_IMAGE_CAPTURE=1
    private lateinit var cameraLauncher:ActivityResultLauncher<Intent>
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    fun onLostPetClick(view: View) {
        this.findNavController().navigate(R.id.action_FirstFragment_to_EditFragment)
    }
     override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.cameraFab.setOnClickListener {
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cameraFab.setOnClickListener{
            openCamera();
        }
    }


    ///////////////////////////////       KAMERA
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE)
        } else {
            // Do something if permission is denied
        }
    }
    private fun openCamera() {
        val cameraPermission = android.Manifest.permission.CAMERA
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            cameraPermission
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCameraPermission) {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE)
        }
    }

    ////////////////////////////////
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_my_places_list->{
                this.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                true
            }
            R.id.action_new_place->{
                this.findNavController().navigate(R.id.action_FirstFragment_to_EditFragment)
                true
            }
            else->super.onOptionsItemSelected(item)

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

}