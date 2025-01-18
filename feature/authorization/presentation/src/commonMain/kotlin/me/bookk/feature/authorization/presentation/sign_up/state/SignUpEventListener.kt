package me.bookk.feature.authorization.presentation.sign_up.state

interface SignUpEventListener {
    fun onFirstNameTextChanged(text: String)
    fun onLastNameTextChanged(text: String)
    fun onEmailTextChanged(text: String)
    fun onConfirmButtonClick()
}