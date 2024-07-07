package com.mobilenvision.notextra.ui.profile

interface ProfileNavigator {
    fun onSaveClick()
    fun onFailure(message: String?)
    fun onImageClick()
    fun onSaveSuccess()
}