package com.example.translatorapp.presentation.screen.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

class GoogleSignInHandler(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    private val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                .setServerClientId(
                    context.getString(com.example.translatorapp.R.string.default_web_client_id)
                ).setFilterByAuthorizedAccounts(false)
                .build()
        ).build()

    suspend fun requestIdToken(): String? {
        return try {
            val result = credentialManager.getCredential(request = request, context = context)

            val credentials = result.credential

            if (credentials is CustomCredential && credentials.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                GoogleIdTokenCredential.createFrom(credentials.data).idToken
            } else {
                null
            }
        } catch (exc: NoCredentialException) {
            Log.d("GoogleSignIn", "Caught NoCredentialException")
            null
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Unexpected error", e)
            null
        }
    }
}