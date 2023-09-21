package elfak.mosis.petfinder.model

import androidx.lifecycle.ViewModel
import elfak.mosis.petfinder.data.MyPet

class MyPetViewModel: ViewModel() {
    var myPetsList: ArrayList<MyPet> = ArrayList<MyPet>()
    fun addPet(pet:MyPet){
        myPetsList.add(pet)
    }
    var selected: MyPet?=null
}