package com.example.bankregistration.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankregistration.model.PanDetails
import com.example.bankregistration.model.PanUseCaseRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: PanUseCaseRepository) : ViewModel() {

    val verificationResult = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    fun verifyPan(panNumber: String, date: String, month: String, year: String) {
        viewModelScope.launch {
            val panDetails = PanDetails(panNumber, date, month, year)
            val validationResult = repository.verifyPanDetails(panDetails)
            verificationResult.value = validationResult.first
            errorMessage.value = validationResult.second
        }
    }
}