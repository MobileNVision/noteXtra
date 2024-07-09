package com.mobilenvision.notextra.ui.register

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.ui.base.BaseViewModel

class RegisterViewModel(dataManager: DataManager) : BaseViewModel<RegisterNavigator>(dataManager) {

    fun onRegisterClick(){
        navigator?.onRegisterClick()
    }
    fun onImageClick(){
        navigator?.onImageClick()
    }
    fun onBackClick(){
        navigator?.onBackClick()
    }
    fun registerUser(email: String, password: String, firstName: String, lastName: String, imageUri: Uri?) {
        isBaseLoading.set(true)
        if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() && imageUri != null) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isBaseLoading.set(false)
                        uploadProfileImage(firstName, lastName, email, imageUri)
                    } else {
                        isBaseLoading.set(false)
                        navigator?.onRegisterFailure(task.exception?.message ?: "Registration failed")
                    }
                }
        } else {
            isBaseLoading.set(false)
            navigator?.onRegisterFailure("Lütfen tüm alanları doldurunuz ve fotoğraf seçiniz")
        }
    }

    private fun uploadProfileImage(firstName: String, lastName: String, email: String, imageUri: Uri) {
        isBaseLoading.set(true)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseStorage.getInstance().reference.child("profile_images/$userId")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    isBaseLoading.set(false)
                    saveUserToFirestore(firstName, lastName, email, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                isBaseLoading.set(false)
                navigator?.onImageUploadFailure(e.message ?: "Failed to upload image")
            }
    }

    private fun saveUserToFirestore(firstName: String, lastName: String, email: String, profileImageUrl: String) {
        isBaseLoading.set(true)
        val user = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "profileImageUrl" to profileImageUrl
        )
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                isBaseLoading.set(false)
                navigator?.onRegisterSuccess()
            }
            .addOnFailureListener { e ->
                isBaseLoading.set(false)
                navigator?.onRegisterFailure(e.message ?: "Failed to save user")
            }
    }
}
