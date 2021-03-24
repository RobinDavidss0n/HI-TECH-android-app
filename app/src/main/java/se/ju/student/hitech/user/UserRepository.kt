package se.ju.student.hitech.user

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import se.ju.student.hitech.R

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        var userRepository = UserRepository()
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

    fun loginWithGoogle(data: Intent?, context: Context, callback: (String) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (task.isSuccessful) {
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("sigInWithGoogle", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!, context, callback)
            } catch (e: ApiException) {
                Log.w("Google login failed", e)
                callback("internalError")
            }
        } else {
            callback("internalError")
            Log.w("Google login failed", task.exception)
        }
    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        context: Context,
        callback: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val docRef = db.collection("users").document(currentUser.uid)
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val verified = document.data?.get("verified")
                                    if (verified == true) {
                                        callback("successful")
                                    } else {
                                        userLogout()
                                        callback("needsVerification")
                                    }
                                } else {
                                    val user = hashMapOf(
                                        "name" to currentUser.displayName,
                                        "role" to context.getString(R.string.account_made_with_google),
                                        "verified" to false
                                    )
                                    db.collection("users").document(currentUser.uid)
                                        .set(user)
                                        .addOnSuccessListener {
                                            userLogout()
                                            callback("needsVerification")
                                        }.addOnFailureListener { error ->
                                            userLogout()
                                            Log.w("Insert user into database error", error)
                                            callback("internalError")
                                        }
                                }
                            }
                            .addOnFailureListener { error ->
                                userLogout()
                                Log.w("Insert user into database error", error)
                                callback("internalError")
                            }
                    } else {
                        userLogout()
                        Log.w("Insert user into database error", "No user was found")
                        callback("internalError")
                    }
                } else {
                    userLogout()
                    callback("internalError")
                    Log.w("Google login failed", task.exception)
                }
            }
    }

    fun getNotVerifiedUser(callback: (String, User, String) -> Unit) {
        db.collection("users")
            .whereEqualTo("verified", false)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot!!.documents.forEach { doc ->
                    val user = doc.toObject(User::class.java)
                    if (user != null) {
                        callback("successful", user, doc.id)
                    }
                }
            }.addOnFailureListener { error ->
                Log.w("Get not verified users database error", error)
                callback("internalError", User(), "")
            }
    }

    fun verifyUser(
        userID: String, callback: (String) -> Unit
    ) {

        db.collection("users").document(userID)
            .update("verified", true)
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener { error ->
                Log.w("verify user database error", error)
                callback("internalError")
            }
    }


    fun getUsername(adminID: String, callback: (String, String) -> Unit) {
        db.collection("users").document(adminID)
            .get()
            .addOnSuccessListener { docSnap ->
                val username = docSnap.get("name").toString()
                callback("successful", username)

            }.addOnFailureListener { error ->
                Log.w("Get user info database error", error)
                callback("internalError", "")
            }
    }

    fun userLogin(email: String, password: String, callback: (String) -> Unit) {
        var result: String
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                when {
                    isEmailVerified() -> callback("successful")
                    else -> callback("emailNotVerified")
                }


            }.addOnFailureListener { error ->
                val e = error.toString()
                Log.d("User login error", e)
                result = when {
                    e.contains("no user record") -> "invalidEmail"
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
                        Log.w("Insert user into database error", error)
                        callback("internalError")
                    }

            }.addOnFailureListener { error ->
                Log.w("Create user error", error)
                callback("internalError")
            }
    }

    fun getCurrentUserInfo(
        callbackOnSuccessful: (User, String) -> Unit,
        callbackOnFailure: (String) -> Unit
    ) {

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
                Log.w("Get user info database error", error)
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
                        Log.w("Update user email error", error)
                        callback("internalError")
                    }
            }.addOnFailureListener { error ->
                Log.w("Update user info database error", error)
                callback("internalError")
            }
    }

    fun sendPasswordReset(email: String, callback: (String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            callback("successful")
        }.addOnFailureListener { error ->
            Log.w("Send password reset error", error)
            callback("internalError")
        }

    }

    fun deleteCurrentUser(callback: (String) -> Unit) {

        auth.currentUser!!.delete()
            .addOnSuccessListener {
                db.collection("users").document(getUserID())
                    .delete()
                    .addOnSuccessListener {
                        callback("successful")
                    }.addOnFailureListener { error ->
                        Log.w("Delete user info database error", error)
                        callback("internalError")
                    }
            }.addOnFailureListener { error ->
                Log.w("Delete user error", error)
                callback("internalError")
            }
    }

    fun denyUser(userID: String, callback: (String) -> Unit) {

        db.collection("users").document(userID)
            .delete()
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener { error ->
                Log.w("Deny user database error", error)
                callback("internalError")
            }
    }

    fun removeChatFromUser(chatID: String, callback: (String) -> Unit) {

        db.collection("users").document(getUserID())
            .update("chats", FieldValue.arrayRemove(chatID))
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener { error ->
                Log.w("Add chat to user database error", error)
                callback("internalError")
            }
    }
}