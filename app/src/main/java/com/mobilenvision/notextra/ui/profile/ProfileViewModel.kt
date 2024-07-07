package com.mobilenvision.notextra.ui.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.User
import com.mobilenvision.notextra.ui.base.BaseViewModel

class ProfileViewModel (dataManager: DataManager) : BaseViewModel<ProfileNavigator>(dataManager) {
    private val firstName = MutableLiveData<String>()
    private val lastName = MutableLiveData<String>()
    private val email = MutableLiveData<String>()
    private val profileImageUrl = MutableLiveData<String>()

    fun fetchUserProfile() {
        isBaseLoading.set(true)
        val userId = dataManager.getUserData().third
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        firstName.value = document.getString("firstName")
                        lastName.value = document.getString("lastName")
                        email.value = document.getString("email")
                        profileImageUrl.value = document.getString("profileImageUrl")
                        isBaseLoading.set(false)
                    }
                }
                .addOnFailureListener { e ->
                    isBaseLoading.set(false)
                    navigator?.onFailure(e.message)
                }
        }
    }
    fun getFirstName(): MutableLiveData<String> {
        return firstName
    }
    fun getLastName(): MutableLiveData<String> {
        return lastName
    }
    fun getEmail(): MutableLiveData<String> {
        return email
    }
    fun getProfileImageUrl(): MutableLiveData<String> {
        return profileImageUrl
    }
    fun updateUser(firstName: String, lastName: String, email: String, profileImageUri: Uri?) {
        val id = dataManager.getUserData().third.toString()
        if (profileImageUri != null) {
            uploadProfileImage(id, firstName, lastName, email, profileImageUri)
        } else {
            saveUserToFirestore(id, firstName, lastName, email, profileImageUrl.value ?: "")
        }
    }

    private fun uploadProfileImage(userId: String, firstName: String, lastName: String, email: String, imageUri: Uri) {
        isBaseLoading.set(true)
        val ref = FirebaseStorage.getInstance().reference.child("profile_images/$userId")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                isBaseLoading.set(false)
                ref.downloadUrl.addOnSuccessListener { uri ->
                    isBaseLoading.set(false)
                    saveUserToFirestore(userId, firstName, lastName, email, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                isBaseLoading.set(false)
                navigator?.onFailure(e.message ?: "Failed to upload image")
            }
    }

    private fun saveUserToFirestore(userId: String, firstName: String, lastName: String, email: String, profileImageUrl: String) {
        isBaseLoading.set(true)
        val user = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "profileImageUrl" to profileImageUrl
        )
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                isBaseLoading.set(false)
                navigator?.onSaveSuccess()
            }
            .addOnFailureListener { e ->
                isBaseLoading.set(false)
                navigator?.onFailure(e.message ?: "Failed to save user")
            }
    }
    fun getUserForUpdate(firstName: String, lastName: String, profileImageUri: Uri?) {
        isBaseLoading.set(true)
        val userId = dataManager.getUserData().third.toString()
        dataManager.loadUserById(userId, object : DbCallback<User> {
            override fun onSuccess(result: User) {
                isBaseLoading.set(false)
                var user = result
                user.firstName = firstName
                user.lastName = lastName
                user.profileImage = profileImageUri.toString()
                updateUserToDatabase(user)
            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
                isBaseLoading.set(false)
            }
        })
    }
    private fun updateUserToDatabase(user: User) {
        isBaseLoading.set(true)
        dataManager.updateUser(user, object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                user.mail?.let {
                    updateUser(user.firstName ?: "", user.lastName ?: "", it,  Uri.parse(user.profileImage))
                    isBaseLoading.set(false)
                }
            }

            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
                isBaseLoading.set(false)
            }
        })
    }
    fun onImageClick(){
        navigator?.onImageClick()
    }
    fun onSaveClick(){
        navigator?.onSaveClick()
    }
}