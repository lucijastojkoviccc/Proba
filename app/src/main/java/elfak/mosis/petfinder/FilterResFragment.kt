package elfak.mosis.petfinder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import elfak.mosis.petfinder.data.NewPost
import elfak.mosis.petfinder.model.NewPostViewModel


class FilterResFragment : Fragment() {

    private val xFilteredNP: NewPostViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_filter_res, container, false)

        var dugme = view.findViewById<Button>(R.id.buttonFF)
        dugme.setOnClickListener {
            findNavController().navigate(R.id.action_FilterResFragment_to_MapFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView: ListView = view.findViewById(R.id.listviewFF)
        val filteredNP = xFilteredNP.getFilteredPosts()
        val npType = xFilteredNP.getNPtype()
        val npDate = xFilteredNP.vratiNewPostsDatum()


        lateinit var adapter: ArrayAdapter<NewPost>



        Log.d("Luka", filteredNP.size.toString())

        if (filteredNP.isNotEmpty()) {
                  adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                      filteredNP

            )
            listView.adapter = adapter
        }

        if (npType.isNotEmpty()) {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                npType
            )
            listView.adapter = adapter
        }

        if (npDate.isNotEmpty()) {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                npDate
            )
            listView.adapter = adapter
        }

//        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
//            override fun onItemClick(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                var kliknutObjekat: NewPost = parent?.adapter?.getItem(position) as NewPost
//            }
//        })
    }


}

