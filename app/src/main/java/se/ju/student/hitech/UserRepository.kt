package se.ju.student.hitech

import android.util.Log
import com.google.common.base.Strings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()



    fun getUserID(): String {
        return auth.currentUser?.uid.toString()
    }



    fun checkIfLoggedIn(): Boolean {
        if (auth.currentUser != null) {
            return true
        }
        return false
    }

    fun getUsername(adminID: String, callback: (String, String) -> Unit) {
        db.collection("users").document(getUserID())
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
                    "role" to role,
                    "chats" to arrayListOf<Strings>()
                )

                db.collection("users").document(registeredUser.user?.uid.toString())
                    .set(user)
                    .addOnSuccessListener {
                        callback("successful")
                    }.addOnFailureListener{ error ->
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
    ){

        db.collection("users").document(getUserID())
            .get()
            .addOnSuccessListener { result ->
                val user = result.toObject(User::class.java)
                if (user != null){
                callbackOnSuccessful(user, auth.currentUser?.email.toString())
                }else{
                    callbackOnFailure("notFound")
                }
            }.addOnFailureListener{ error ->
                Log.w("Get user info database error", error)
                callbackOnFailure("internalError")

            }
    }



    fun updateCurrentUserInfo(
        newEmail: String,
        newName: String,
        newRole: String,
        callback: (String) -> Unit
    ){

        db.collection("users").document(getUserID())
            .update("name", newName, "role", newRole)
            .addOnSuccessListener {
                auth.currentUser!!.updateEmail(newEmail)
                    .addOnSuccessListener {
                        callback("successful")
                    }.addOnFailureListener{ error ->
                        Log.w("Update user email error", error)
                        callback("internalError")
                    }
            }.addOnFailureListener{ error ->
                Log.w("Update user info database error", error)
                callback("internalError")

            }
    }

    fun updateCurrentUserPassword(newPassword: String, callback: (String) -> Unit){
        auth.currentUser!!.updatePassword(newPassword)
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener{ error ->
                Log.w("Update user email error", error)
                callback("internalError")
            }
    }

    fun sendPasswordReset(email: String, callback: (String) -> Unit){
        auth.sendPasswordResetEmail(email).
        addOnSuccessListener {
            callback("successful")
        }.addOnFailureListener{ error ->
            Log.w("Send password reset error", error)
            callback("internalError")
        }

    }

    fun deleteCurrentUser(callback: (String) -> Unit){
        val userID = getUserID()

        auth.currentUser!!.delete()
            .addOnSuccessListener {
                db.collection("users").document(userID)
                    .delete()
                    .addOnSuccessListener {
                        callback("successful")
                    }.addOnFailureListener{ error ->
                        Log.w("Delete user info database error", error)
                        callback("internalError")
                    }
            }.addOnFailureListener{ error ->
                Log.w("Delete user error", error)
                callback("internalError")
            }
    }
    fun addChatToUser(chatID: String, callback: (String) -> Unit){

        db.collection("users").document(getUserID())
            .update("chats", FieldValue.arrayUnion(chatID))
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener{ error ->
                Log.w("Add chat to user database error", error)
                callback("internalError")

            }
    }

    fun removeChatFromUser(chatID: String, callback: (String) -> Unit){

        db.collection("users").document(getUserID())
            .update("chats", FieldValue.arrayRemove(chatID))
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener{ error ->
                Log.w("Add chat to user database error", error)
                callback("internalError")

            }
    }


}