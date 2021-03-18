package se.ju.student.hitech.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        val userRepository = UserRepository()
    }

    fun getUserID(): String {
        return auth.currentUser?.uid.toString()
    }

    fun checkIfLoggedIn(): Boolean {
        if (auth.currentUser != null) {
            return true
        }
        return false
    }

    fun userLogin(email: String, password: String, callback: (String) -> Unit) {
        var result: String
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            result = when {
                isEmailVerified() -> "successful"
                else -> "emailNotVerified"
            }
            callback(result)

        }.addOnFailureListener { error ->
            val e = error.toString()
            Log.d("User login error", e)
            result = when {
                e.contains("no user record") -> "emailNotFound"
                e.contains("password is invalid") -> "invalidPassword"
                else -> "internalError"
            }

            callback(result)
        }
    }

    private fun isEmailVerified(): Boolean {
        val user = auth.currentUser
        return if (user?.isEmailVerified == true) {
            true
        } else {
            user?.sendEmailVerification()
            auth.signOut()
            false
        }
    }

    private fun reloadUser() {
        auth.currentUser?.reload()
    }

    fun userLogout() {
        auth.signOut()
    }

    fun createUser(
        email: String, password: String, name: String, role: String,
        callback: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { registeredUser ->

                val user = hashMapOf(
                    "name" to name,
                    "role" to role
                )

                db.collection("users").document(registeredUser.user?.uid.toString())
                    .set(user)
                    .addOnSuccessListener {
                        callback("successful")
                    }.addOnFailureListener { error ->
                        Log.d("Insert user into database error", error.toString())
                        callback("internalError")
                    }

            }.addOnFailureListener { error ->
                Log.d("Create user error", error.toString())
                callback("internalError")

            }
    }

    fun getCurrentUserInfo(
        callbackOnSuccessful: (User, String) -> Unit,
        callbackOnFailure: (String) -> Unit
    ) {
        reloadUser()
        db.collection("users").document(getUserID())
            .get()
            .addOnSuccessListener { result ->
                val user = result.toObject(User::class.java)
                if (user != null) {
                    callbackOnSuccessful(user, auth.currentUser?.email.toString())
                } else {
                    callbackOnFailure("notFound")
                }
            }.addOnFailureListener { error ->
                Log.d("Get user info database error", error.toString())
                callbackOnFailure("internalError")
            }
    }

    fun updateCurrentUserInfo(
        newEmail: String,
        newName: String,
        newRole: String,
        callback: (String) -> Unit
    ) {
        db.collection("users").document(getUserID())
            .update("name", newName, "role", newRole)
            .addOnSuccessListener {
                auth.currentUser!!.updateEmail(newEmail)
                    .addOnSuccessListener {
                        callback("successful")
                    }.addOnFailureListener { error ->
                        Log.d("Update user email error", error.toString())
                        callback("internalError")
                    }
            }.addOnFailureListener { error ->
                Log.d("Update user info database error", error.toString())
                callback("internalError")
            }
    }

    fun updateCurrentUserPassword(newPassword: String, callback: (String) -> Unit) {
        auth.currentUser!!.updatePassword(newPassword)
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener { error ->
                Log.d("Update user email error", error.toString())
                callback("internalError")
            }
    }

    fun sendPasswordReset(email: String, callback: (String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            callback("successful")
        }.addOnFailureListener { error ->
            Log.d("Send password reset error", error.toString())
            callback("internalError")
        }
    }

    fun deleteCurrentUser(callback: (String) -> Unit) {
        val userID = getUserID()

        auth.currentUser!!.delete()
            .addOnSuccessListener {
                db.collection("users").document(userID)
                    .delete()
                    .addOnSuccessListener {
                        callback("successful")
                    }.addOnFailureListener { error ->
                        Log.d("Delete user info database error", error.toString())
                        callback("internalError")
                    }
            }.addOnFailureListener { error ->
                Log.d("Delete user error", error.toString())
                callback("internalError")
            }
    }
}