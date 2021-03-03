package se.ju.student.hitech

import android.util.Log
import android.view.View
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.sign

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    public fun getUserID(): String {
        return auth.currentUser?.uid.toString()
    }

    public fun checkIfLoggedIn(): Boolean {
        if (auth.currentUser != null) {
            return true
        }
        return false
    }

    public fun userLogin(email: String, password: String, callback: (String) -> Unit) {
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

    public fun userLogout() {
        auth.signOut()
    }

    public fun createUser(
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
                    }.addOnFailureListener{ error ->
                        Log.d("Insert user into database error", error.toString())
                        callback("internalError")

                    }

            }.addOnFailureListener { error ->
                Log.d("Create user error", error.toString())
                callback("internalError")

            }

    }
}