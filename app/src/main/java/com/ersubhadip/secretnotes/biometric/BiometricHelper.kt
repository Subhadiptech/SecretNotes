package com.ersubhadip.secretnotes.biometric

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

object BiometricHelper {
    private fun createBiometricPrompt(
        activity: AppCompatActivity,
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit
    ): BiometricPrompt {

        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                processSuccess(result)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }
        }
        return BiometricPrompt(activity, executor, callback)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder()
            .apply {
                setTitle("Secret Notes")
                setDescription("Use your fingerprint or face to unlock app and see secret notes")
                setConfirmationRequired(false)
                setNegativeButtonText("Cancel")
            }.build()

    fun showPrompt(activity: AppCompatActivity, onSuccess: () -> Unit) {
        createBiometricPrompt(activity = activity) {
            onSuccess()
        }.authenticate(createPromptInfo())
    }
}