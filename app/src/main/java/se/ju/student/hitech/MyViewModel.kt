package se.ju.student.hitech

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel: ViewModel() {

    val news = MutableLiveData<List<Novelty>?>()

    init {

    }
}