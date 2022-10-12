package nl.hva.task01.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.task01.model.Profile
import nl.hva.task01.repository.ProfileRepository

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "FIRESTORE"
    private val profileRepository: ProfileRepository = ProfileRepository()

    val profile: LiveData<Profile> = profileRepository.profile

    val createSuccess: LiveData<Boolean> = profileRepository.createSuccess

    private val _errorText: MutableLiveData<String> = MutableLiveData()
    val errorText: LiveData<String>
        get() = _errorText

    fun getProfile() {
        viewModelScope.launch {
            try {
                profileRepository.getProfile()
            } catch (ex: ProfileRepository.ProfileRetrievalError) {
                val errorMsg = "Something went wrong while retrieving profile"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun createProfile(firstName: String, lastName: String, description: String, imageUri: String) {
        if (inputIsNotBlank(firstName, lastName, description, imageUri)) {
            // persist data to firestore
            val profile = Profile(firstName, lastName, description, imageUri)

            viewModelScope.launch {
                try {
                    profileRepository.createProfile(profile)
                } catch (ex: ProfileRepository.ProfileSaveError) {
                    val errorMsg = "Something went wrong while saving the profile"
                    Log.e(TAG, ex.message ?: errorMsg)
                    _errorText.value = errorMsg
                }
            }
        }
    }

    private fun inputIsNotBlank(firstName: String, lastName: String, description: String, imageUri: String): Boolean {
        return when {
            firstName.isBlank() -> {
                _errorText.value = "Please fill in a first name"
                false
            }
            lastName.isBlank() -> {
                _errorText.value = "Please fill in a last name"
                false
            }
            description.isBlank() -> {
                _errorText.value = "Please fill in a description"
                false
            }
            imageUri.isBlank() -> {
                _errorText.value = "Please add a profile image"
                false
            }
            else -> true
        }
    }
}