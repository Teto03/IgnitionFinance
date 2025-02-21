package com.unimib.ignitionfinance.data.remote.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.unimib.ignitionfinance.data.remote.service.utils.AuthErrors
import com.unimib.ignitionfinance.data.remote.service.utils.AuthServiceException
import kotlinx.coroutines.tasks.await

class AuthService {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthServiceException(AuthErrors.INVALID_CREDENTIALS, e)
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthServiceException(AuthErrors.NO_ACCOUNT_FOUND, e)
        } catch (e: FirebaseAuthException) {
            throw AuthServiceException(AuthErrors.SIGN_IN_FAILED, e)
        } catch (e: Exception) {
            throw AuthServiceException(AuthErrors.GENERIC_ERROR, e)
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: FirebaseAuthUserCollisionException) {
            throw AuthServiceException(AuthErrors.EMAIL_ALREADY_IN_USE, e)
        } catch (e: FirebaseAuthException) {
            throw AuthServiceException(AuthErrors.CREATE_USER_FAILED, e)
        } catch (e: Exception) {
            throw AuthServiceException(AuthErrors.GENERIC_ERROR, e)
        }
    }

    suspend fun resetPassword(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            throw AuthServiceException(AuthErrors.RESET_PASSWORD_FAILED, e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}