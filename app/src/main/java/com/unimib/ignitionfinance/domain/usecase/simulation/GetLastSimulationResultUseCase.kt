package com.unimib.ignitionfinance.domain.usecase.simulation

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.text.isNotEmpty

class GetLastSimulationResultUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>
) {
    fun execute(): Flow<Result<Pair<List<SimulationResult>, Double?>>> = flow {
        val currentUserResult = authRepository.getCurrentUser().first()
        val authData = currentUserResult.getOrNull()
            ?: throw IllegalStateException("Unable to retrieve authenticated user")

        val userId = authData.id.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("User ID is missing")

        val localUserResult = localDatabaseRepository.getById(userId).first()
        val localUser = localUserResult.getOrNull()
            ?: throw IllegalStateException("User not found in local database for ID: $userId")

        val simulationOutcome = localUser.simulationOutcome
            ?: throw IllegalStateException("No simulation outcome found")

        emit(Result.success(simulationOutcome))
    }.catch { e ->
        when (e) {
            is CancellationException -> throw e
            else -> emit(Result.failure(e))
        }
    }
}