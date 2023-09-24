package elfak.mosis.petfinder.data

import java.util.Date
data class NewPost(var ID:String, var postedID: String, var type: String, var breed: String, var color:String, var name:String, var picture:String,var longitude: String, var latitude: String, var lost: Boolean, var date:Date)
{
    override fun toString(): String
    {
        val m = if (this.lost) "Lost" else "Found"
        val type=this.type
        val breed=this.breed
        val name = this.name
        return m+" Type: "+ type +", Breed: "+ breed+  ", Name: "+ name
    }
}

