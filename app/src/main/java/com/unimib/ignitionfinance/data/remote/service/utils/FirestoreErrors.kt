package com.unimib.ignitionfinance.data.remote.service.utils

object FirestoreErrors {
    private const val FIRESTORE_SPECIFIC_ERROR = "Firestore specific error occurred while "
    private const val GENERAL_ERROR = "General error occurred while "

    const val GETTING_DOCUMENT = "getting document"
    const val GETTING_COLLECTION = "getting collection"
    const val ADDING_DOCUMENT = "adding document"
    const val UPDATING_DOCUMENT = "updating document"
    const val DELETING_DOCUMENT = "deleting document"
    const val DELETING_ALL_DOCUMENTS = "deleting all documents"
    const val CHECKING_USER_EXISTS = "checking if user exists"

    fun getErrorMessage(operationType: String, isSpecificError: Boolean): String {
        return if (isSpecificError) {
            "$FIRESTORE_SPECIFIC_ERROR$operationType"
        } else {
            "$GENERAL_ERROR$operationType"
        }
    }
}

