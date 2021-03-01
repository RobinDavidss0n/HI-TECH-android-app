package se.ju.student.hitech

import com.google.firebase.auth.FirebaseAuth

class UserRepository {

    public suspend fun checkIfLoggedIn(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            return true
        }
        return false
    }

    public suspend fun userLogin(email: String, password: String, callback: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        var result: String
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { signIn ->
            result = if (signIn.isSuccessful) {
                if (isEmailVerified(auth)) {
                    "successful"
                } else {
                    "emailNotVerified"
                }
            } else {
                "failed"
            }
            callback(result)
        }
    }

    private fun isEmailVerified(auth: FirebaseAuth): Boolean {
        val user = auth.currentUser
        return if (user?.isEmailVerified == true) {
            true
        } else {
            user?.sendEmailVerification()
            auth.signOut()
            false
        }
    }

    public suspend fun userLogout() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
    }
}