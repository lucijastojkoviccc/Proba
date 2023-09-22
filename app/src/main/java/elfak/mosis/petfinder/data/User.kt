package elfak.mosis.petfinder.data

data class User(var ID:String,
                var Name: String,
                var Email: String,
                var Desription: String,
//                var Points: Int,
                var Pets: ArrayList<MyPet>
               )
{
    constructor(ID:String): this(ID, "", "", "", ArrayList())  //0
}
