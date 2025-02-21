package com.unimib.ignitionfinance.data.repository.interfaces

import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    suspend fun getDocumentById(collectionPath: String, documentId: String): Flow<Result<Map<String, Any>?>>
    suspend fun getAllDocuments(collectionPath: String): Flow<Result<QuerySnapshot?>>
    suspend fun addDocument(collectionPath: String, data: Map<String, Any>, documentId: String? = null): Flow<Result<String?>>
    suspend fun updateDocument(collectionPath: String, data: Map<String, Any>, documentId: String): Flow<Result<Unit>>
    suspend fun deleteDocument(collectionPath: String, documentId: String): Flow<Result<Unit>>
    suspend fun deleteAllDocuments(collectionPath: String): Flow<Result<Unit>>
    suspend fun documentExists(collectionPath: String, userId: String): Flow<Result<Boolean>>
}
