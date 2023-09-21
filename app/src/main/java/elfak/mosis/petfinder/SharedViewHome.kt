package elfak.mosis.petfinder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import elfak.mosis.petfinder.data.MyPet
import elfak.mosis.petfinder.data.User

class SharedViewHome : ViewModel()
{
    var korisnik: MutableLiveData<User> = MutableLiveData()
    var korisnici: MutableList<User> = mutableListOf()
    var longitude: MutableLiveData<Double> = MutableLiveData()
    var latitude: MutableLiveData<Double> = MutableLiveData()
    var dataChanger: MutableLiveData<Boolean> = MutableLiveData(false)
    var fullUcitavanje: MutableLiveData<Boolean> = MutableLiveData(false)   //Da li su ucitani svi podaci o korisnicima
    var ljubimci: MutableList<MyPet> = mutableListOf()
}