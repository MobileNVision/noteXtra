package com.mobilenvision.notextra.ui.login


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.User
import com.mobilenvision.notextra.ui.base.BaseViewModel

class LoginViewModel(dataManager: DataManager) : BaseViewModel<LoginNavigator>(dataManager) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun checkCredentials(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    if(dataManager.getUserData().first?.isEmpty() == true){
                    val userRef =
                        FirebaseFirestore.getInstance().collection("users").document(userId!!)
                    userRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val firstName = document.getString("firstName")
                                val lastName = document.getString("lastName")
                                val email = document.getString("email")
                                val profileImageUrl = document.getString("profileImageUrl")
                                var mUser = User(
                                    userId,
                                    firstName!!,
                                    lastName,
                                    email,
                                    password,
                                    profileImageUrl
                                )
                                addUserToDatabase(mUser)
                            }
                        }

                        }
                    else{
                        navigator?.onUserLoggedIn(userId)
                    }
                }
            else {
                    navigator?.onLoginFailed(task.exception?.message ?: "Login failed")
                }
            }
    }

    private fun addUserToDatabase(mUser: User) {
        dataManager.insertUser(mUser, object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                navigator?.onUserLoggedIn(mUser.id)
            }

            override fun onError(error: Throwable) {
                error.message?.let { navigator?.onLoginFailed(it) }
            }
        })
    }

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigator?.onRegistrationSuccess()
                } else {
                    navigator?.onRegistrationFailed(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun onLoginClick(){
        navigator?.onLoginClick()
    }
    fun appNameClick(){
        navigator?.appNameClick()
    }

    fun onRegisterClick(){
        navigator?.onRegisterClick()
    }

}