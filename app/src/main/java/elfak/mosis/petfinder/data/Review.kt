package elfak.mosis.petfinder.data

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Review(var ID:String, var userID:String, var userEmail:String, var postID:String, var comment:String, var grade:String) {


    constructor(ID: String) : this(ID, "","", "", "", "")
    constructor() : this("", "","", "", "","")


    lateinit var poruka:String


    override fun toString(): String {

            return "User: "+userEmail+ " Comment: "+this.comment+" Grade: "+grade


    }
}
