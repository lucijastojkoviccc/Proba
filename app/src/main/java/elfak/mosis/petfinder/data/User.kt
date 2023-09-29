package elfak.mosis.petfinder.data

data class User(var ID:String,
                var Name: String,
                var Email: String,
                var Phone: String,
                var Desription: String,
                var Points: Long,
                var Pets: ArrayList<String>
               )
{
    constructor(ID:String): this(ID, "", "", "", "",1, ArrayList())
    constructor(): this("", "", "", "", "",1, ArrayList())

    override fun toString(): String {
        val email=this.Email
        val points=this.Points
        return email+" "+points
    }
    fun prikaziKoirniske():String
    {
        return " Name: $Name Username: $Email Points: $Points"
    }
}
