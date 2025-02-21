package com.unimib.ignitionfinance.data.repository.implementation

import com.google.firebase.firestore.QuerySnapshot
import com.unimib.ignitionfinance.data.remote.service.FirestoreService
import com.unimib.ignitionfinance.data.remote.service.utils.FirestoreServiceException
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
) : FirestoreRepository {

    override suspend fun getDocumentById(collectionPath: String, documentId: String): Flow<Result<Map<String, Any>?>> = flow {
        try {
            val document = firestoreService.getDocument(collectionPath, documentId)
            emit(Result.success(document))
        } catch (e: FirestoreServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(Exception("An unexpected error occurred", e)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllDocuments(collectionPath: String): Flow<Result<QuerySnapshot?>> = flow {
        try {
            val documents = firestoreService.getCollection(collectionPath)
            emit(Result.success(documents))
        } catch (e: FirestoreServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(Exception("An unexpected error occurred", e)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addDocument(
        collectionPath: String,
        data: Map<String, Any>,
        documentId: String?
    ): Flow<Result<String?>> = flow {
        try {
            val documentIdResult = firestoreService.addDocument(collectionPath, data, documentId)
            emit(Result.success(documentIdResult))
        } catch (e: FirestoreServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(Exception("An unexpected error occurred", e)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateDocument(collectionPath: String, data: Map<String, Any>, documentId: String): Flow<Result<Unit>> = flow {
        try {
            firestoreService.updateDocument(collectionPath, data, documentId)
            emit(Result.success(Unit))
        } catch (e: FirestoreServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(Exception("An unexpected error occurred", e)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteDocument(collectionPath: String, documentId: String): Flow<Result<Unit>> = flow {
        try {
            firestoreService.deleteDocument(collectionPath, documentId)
            emit(Result.success(Unit))
        } catch (e: FirestoreServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(Exception("An unexpected error occurred", e)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteAllDocuments(collectionPath: String): Flow<Result<Unit>> = flow {
        try {
            firestoreService.deleteAllDocuments(collectionPath)
            emit(Result.success(Unit))
        } catch (e: FirestoreServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(Exception("An unexpected error occurred", e)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun documentExists(collectionPath: String, userId: String): Flow<Result<Boolean>> = flow {
        try {
            val exists = firestoreService.documentExists(collectionPath, userId)
            emit(Result.success(exists))
        } catch (e: FirestoreServiceException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(Exception("An unexpected error occurred", e)))
        }
    }.flowOn(Dispatchers.IO)
}