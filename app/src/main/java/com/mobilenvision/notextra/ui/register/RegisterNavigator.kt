package com.mobilenvision.notextra.ui.register


interface RegisterNavigator {
    fun onRegisterSuccess()
    fun onRegisterFailure(message: String)
    fun onImageUploadFailure(message: String)
    fun onRegisterClick()
    fun onImageClick()
    fun onBackClick()
}
