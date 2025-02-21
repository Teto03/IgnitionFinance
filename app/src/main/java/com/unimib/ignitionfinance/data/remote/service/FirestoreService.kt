package com.unimib.ignitionfinance.data.remote.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.unimib.ignitionfinance.data.remote.service.utils.FirestoreErrors
import com.unimib.ignitionfinance.data.remote.service.utils.FirestoreServiceException
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getDocument(collectionPath: String, documentId: String): Map<String, Any>? {
        return try {
            val documentSnapshot = firestore.collection(collectionPath).document(documentId).get().await()
            documentSnapshot.data
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.GETTING_DOCUMENT, true), e)
        } catch (e: Exception) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.GETTING_DOCUMENT, false), e)
        }
    }

    suspend fun getCollection(collectionPath: String): QuerySnapshot = try {
        firestore.collection(collectionPath).get().await()
    } catch (e: FirebaseFirestoreException) {
        throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.GETTING_COLLECTION, true), e)
    } catch (e: Exception) {
        throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.GETTING_COLLECTION, false), e)
    }

    suspend fun addDocument(
        collectionPath: String,
        data: Map<String, Any>,
        documentId: String? = null
    ): String? {
        return try {
            val collectionReference = firestore.collection(collectionPath)
            val documentReference = if (documentId != null) {
                collectionReference.document(documentId).set(data).await()
                collectionReference.document(documentId)
            } else {
                collectionReference.add(data).await()
            }
            documentReference.id
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.ADDING_DOCUMENT, true), e)
        } catch (e: Exception) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.ADDING_DOCUMENT, false), e)
        }
    }

    suspend fun updateDocument(collectionPath: String, data: Map<String, Any>, documentId: String) {
        try {
            firestore.collection(collectionPath).document(documentId).update(data).await()
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.UPDATING_DOCUMENT, true), e)
        } catch (e: Exception) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.UPDATING_DOCUMENT, false), e)
        }
    }

    suspend fun deleteDocument(collectionPath: String, documentId: String) {
        try {
            firestore.collection(collectionPath).document(documentId).delete().await()
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.DELETING_DOCUMENT, true), e)
        } catch (e: Exception) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.DELETING_DOCUMENT, false), e)
        }
    }

    suspend fun deleteAllDocuments(collectionPath: String) {
        try {
            val documents = firestore.collection(collectionPath).get().await()
            for (document in documents) {
                document.reference.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.DELETING_ALL_DOCUMENTS, true), e)
        } catch (e: Exception) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.DELETING_ALL_DOCUMENTS, false), e)
        }
    }

    suspend fun documentExists(collectionPath: String, userId: String): Boolean {
        return try {
            val documentSnapshot = firestore.collection(collectionPath).document(userId).get().await()
            documentSnapshot.exists()
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.CHECKING_USER_EXISTS, true), e)
        } catch (e: Exception) {
            throw FirestoreServiceException(FirestoreErrors.getErrorMessage(FirestoreErrors.CHECKING_USER_EXISTS, false), e)
        }
    }

}
