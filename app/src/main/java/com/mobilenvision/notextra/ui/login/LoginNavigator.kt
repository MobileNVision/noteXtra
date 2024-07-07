package com.mobilenvision.notextra.ui.login


interface LoginNavigator {
    fun onUserLoggedIn(userId: String?)
    fun onLoginFailed(s: String)
    fun onLoginClick()
    fun onRegisterClick()
    fun onRegistrationSuccess()
    fun onRegistrationFailed(s: String)
    fun appNameClick()
}