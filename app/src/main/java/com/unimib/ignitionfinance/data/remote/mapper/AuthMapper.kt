package com.unimib.ignitionfinance.data.remote.mapper

import com.google.firebase.auth.FirebaseUser
import com.unimib.ignitionfinance.data.remote.model.user.AuthData

object AuthMapper {
    fun mapToUserData(firebaseUser: FirebaseUser): AuthData {

        return AuthData(
            id = firebaseUser.uid,
            email = firebaseUser.email.orEmpty(),
            displayName = firebaseUser.displayName.orEmpty()
        )
    }
}