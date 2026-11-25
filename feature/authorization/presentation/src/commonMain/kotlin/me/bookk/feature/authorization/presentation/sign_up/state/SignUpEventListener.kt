package me.bookk.feature.authorization.presentation.sign_up.state

interface SignUpEventListener {
    fun onNameTextChanged(text: String)
    fun onEmailTextChanged(text: String)
    fun onConfirmButtonClick()
    fun onLearnMoreClick()
}
