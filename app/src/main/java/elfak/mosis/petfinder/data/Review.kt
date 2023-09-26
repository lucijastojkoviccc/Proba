package elfak.mosis.petfinder.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Review(var ID:String, var userID:String, var postID:String, var comment:String, var grade:String) {


    constructor(ID: String) : this(ID, "", "", "", "")
    constructor() : this("", "", "", "","")

    override fun toString(): String {

        lateinit var a: String
        Firebase.firestore.collection("users").document(this.userID).get().addOnSuccessListener { document ->

            a=document["name"].toString()
        }
        return "User: "+" Comment: "+this.comment+" Grade: "+grade

    }
}
