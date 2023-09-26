package elfak.mosis.petfinder.data

import android.icu.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

data class NewPost(var ID:String, var postedID: String, var type: String, var breed: String, var color:String, var name:String, var picture:String, val description:String,
                   var longitude: String, var latitude: String, var lost: Boolean, var comments: ArrayList<String>, var date:LocalDate=LocalDate.now())
{

    constructor(ID:String):this(ID, "", "", "", "","","","","","",false, ArrayList())
    constructor():this("", "", "", "", "","","","","","",false, ArrayList())
    override fun toString(): String
    {
        val m = if (this.lost) "Lost" else "Found"
        val type=this.type
        val breed=this.breed
        val name = this.name
        return m+" Type: "+ type +", Breed: "+ breed+  ", Name: "+ name//+" Posted by:"+postedID
    }
    fun prikaziPostove():String
    {
        val m = if (this.lost) "Lost" else "Found"
        return " "+m+" Type: "+ type+" Breed: "+breed+" Name: "+name
    }
}

