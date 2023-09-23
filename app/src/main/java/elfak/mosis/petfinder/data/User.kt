package elfak.mosis.petfinder.data

data class User(var ID:String,
                var Name: String,
                var Email: String,
                var Desription: String,
                var Points: Long,
                var Pets: ArrayList<String>
               )
{
    constructor(ID:String): this(ID, "", "", "", 1, ArrayList())  //0
}
