package elfak.mosis.petfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.petfinder.model.NewPostViewModel
import java.util.*


class FilterFragment : Fragment() {

    private val  FilteredNP: NewPostViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        FilteredNP.NewPostsDatum.clear()
        FilteredNP.NewPostsType.clear()
        FilteredNP.FilteredNewPosts.clear()

        return inflater.inflate(R.layout.fragment_filter, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filtriraj();
    }
    private fun filtriraj() {
        val buttonFilter = requireView().findViewById<Button>(R.id.buttonF);
        buttonFilter.setOnClickListener {
            val radioG=requireView().findViewById<RadioGroup>(R.id.radioGroup)
            val selectedId=radioG.checkedRadioButtonId
            when (selectedId) {

                R.id.author->{
                    val email = requireView().findViewById<EditText>(R.id.editTextFilter)
                    val username=email.text.toString()
                    if(username!=null)
                    {
                        val usrID:String=""
                        Firebase.firestore.collection("users").whereEqualTo("email", username).get().addOnCanceledListener {
//                        document->
//
//                            usrID=document["ID"]
                        }
                        Firebase.firestore.collection("pets").whereEqualTo("postedID", usrID).get().addOnSuccessListener {

//                            for(document in documents)
//                            {
//                                FilteredNP.addFilteredPost(document)
//                            }
                        }


                        findNavController().navigate(R.id.action_FilterFragment_to_FilterResFragment)
                    }
                    else
                    {
                        Toast.makeText(context,"Username not inserted",Toast.LENGTH_SHORT).show();
                    }

                }
                R.id.lf->
                {
                    var type= requireView().findViewById<EditText>(R.id.editTextFilter).text.toString()
                    if(type.isNotEmpty())
                    {

                        findNavController().navigate(R.id.action_FilterFragment_to_FilterResFragment)
                    }
                    else
                    {
                        Toast.makeText(context,"Lost/Found",Toast.LENGTH_SHORT).show();
                    }
                }
                R.id.Date->
                {
                    val datePickerOd = requireView().findViewById<DatePicker>(R.id.datePickerOd)
                    val datePickerDo = requireView().findViewById<DatePicker>(R.id.datePickerDo)

                    val godinaOd = datePickerOd.year
                    val mesecOd = datePickerOd.month
                    val danOd = datePickerOd.dayOfMonth

                    val godinaDo = datePickerDo.year
                    val mesecDo = datePickerDo.month
                    val danDo = datePickerDo.dayOfMonth

                    val calendarOd = Calendar.getInstance()
                    calendarOd.set(godinaOd, mesecOd, danOd, 0, 0, 0)
                    val datumOd = calendarOd.timeInMillis

                    val calendarDo = Calendar.getInstance()
                    calendarDo.set(godinaDo, mesecDo, danDo, 23, 59, 59)
                    val datumDo = calendarDo.timeInMillis

                    if(datumOd!=null && datumDo!=null)
                    {

                        findNavController().navigate(R.id.action_FilterFragment_to_FilterResFragment)
                    }
                    else
                    {
                        Toast.makeText(context,"Choose dates!",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }


}