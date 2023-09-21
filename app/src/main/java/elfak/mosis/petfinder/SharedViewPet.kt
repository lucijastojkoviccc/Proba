package elfak.mosis.petfinder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewPet : ViewModel()
{
    // NESTO OD OVOGA DA BUDE KAO INFO O USERU
    //var vlasnik: MutableList<User> = mutableListOf()
    //var selectedUser: MutableLiveData<User> = MutableLiveData()


    var name: MutableLiveData<String> = MutableLiveData()
    var longitude: MutableLiveData<Double> = MutableLiveData()
    var latitude: MutableLiveData<Double> = MutableLiveData()
    var petID:MutableLiveData<String> = MutableLiveData()
    var distance: MutableLiveData<Double> = MutableLiveData(0.0)
    var selectedImage: MutableLiveData<String> = MutableLiveData()
}