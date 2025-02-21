package com.unimib.ignitionfinance.data.local.utils

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.remote.model.UserData
import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.data.remote.model.user.Settings
import com.unimib.ignitionfinance.data.remote.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.remote.model.user.settings.Intervals
import com.unimib.ignitionfinance.data.remote.model.user.settings.Withdrawals

object UserMapper {
    fun mapUserToUserData(user: User): UserData {
        return UserData(
            name = user.name,
            surname = user.surname,
            authData = user.authData,
            settings = user.settings,
            cash = user.cash,
            productList = user.productList,
            firstAdded = user.firstAdded,
            dataset = user.dataset,
            simulationOutcome = user.simulationOutcome
        )
    }

    fun mapUserDataToUser(userData: UserData?): User {
        return User(
            id = userData?.authData?.id.orEmpty(),
            name = userData?.name.orEmpty(),
            surname = userData?.surname.orEmpty(),
            authData = userData?.authData ?: AuthData(id = "", email = "", displayName = ""),
            settings = userData?.settings ?: Settings(
                withdrawals = Withdrawals("", ""),
                inflationModel = "",
                expenses = Expenses("", "", ""),
                intervals = Intervals("", "", ""),
                numberOfSimulations = ""
            ),
            cash = userData?.cash ?: "0",
            productList = userData?.productList ?: emptyList(),
            firstAdded = userData?.firstAdded == true,
            dataset = userData?.dataset ?: emptyList(),
            simulationOutcome = userData?.simulationOutcome
        )
    }
}
