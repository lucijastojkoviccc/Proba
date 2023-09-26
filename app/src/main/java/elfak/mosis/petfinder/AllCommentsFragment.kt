package elfak.mosis.petfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import elfak.mosis.petfinder.data.NewPost
import elfak.mosis.petfinder.model.NewPostViewModel


class AllCommentsFragment : Fragment() {

    private val newPostViewModel: NewPostViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
               return inflater.inflate(R.layout.fragment_all_comments, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //pronadjiIme();

    }
//    private fun pronadjiIme() {
//        val IDFS= newPostViewModel.returnID();
//        val referencaFS= FirebaseDatabase.getInstance().reference.child("objekti").child(IDFS);
//        lateinit var  name:String;
//        referencaFS.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val np = snapshot.getValue(NewPost::class.java)
//                    name = np?.name.toString();
//                    if(name!=null)
//                        ispisiKomentare(name);
//
//                                    }
//            }
//
//        })
//    }

//    private fun ispisiKomentare(name:String) {
//
//
//        val db = FirebaseDatabase.getInstance().reference
//        val query = db.child("recenzije").orderByChild("nazivFrizerskogSalona").equalTo(name);
//
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val reviews: MutableList<Pair<String, String>> = mutableListOf()
//
//                for (objekat in snapshot.children) {
//                    val komentar = objekat.child("opis").getValue(String::class.java)
//                    val ocena = objekat.child("ocena").getValue(String::class.java)
//
//                    if (komentar != null && ocena != null) {
//                        reviews.add(Pair(komentar, ocena))
//                    }
//                }
//
//                // Ovde možete postaviti listu reviews u ListView adapter
//                val listView: ListView = requireView().findViewById(R.id.listViewKomentari)
//                val adapter = ArrayAdapter(
//                    requireContext(),
//                    android.R.layout.simple_list_item_1,
//                    reviews.map { "Komentar: ${it.first}, Ocena: ${it.second}" }
//                )
//                listView.adapter = adapter
//            }
//        })



    }
    ////////////////////////////////////////////////////// za setanje
//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        val item = menu.findItem(R.id.editFragment)
//        item.isVisible = false
//
//        val item1 = menu.findItem(R.id.signinFragment)
//        item1.isVisible = false
//
//        val item2 = menu.findItem(R.id.signUpFragment)
//        item2.isVisible = false
//
//        val item3= menu.findItem(R.id.filtrirajFragment)
//        item3.isVisible=false;
//
//
//    }