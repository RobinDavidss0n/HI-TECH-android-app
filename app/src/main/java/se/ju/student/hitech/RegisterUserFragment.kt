package se.ju.student.hitech

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterUserFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_register_user, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val email = view?.findViewById<TextInputEditText>(R.id.register_user_emailTextInputEditText)
        val password =
            view?.findViewById<TextInputEditText>(R.id.register_user_passwordTextInputEditText)
        val rePassword =
            view?.findViewById<TextInputEditText>(R.id.register_user_repeatPasswordTextInputEditText)
        val name = view?.findViewById<TextInputEditText>(R.id.register_user_nameTextInputEditText)
        val role = view?.findViewById<TextInputEditText>(R.id.register_user_roleTextInputEditText)
        val registerButton = view?.findViewById<Button>(R.id.register_user_registerButton)


        registerButton?.setOnClickListener {
            registerUser(
                email?.text.toString().trim(),
                password?.text.toString().trim(),
                rePassword?.text.toString().trim(),
                name?.text.toString().trim(),
                role?.text.toString().trim()
            )

        }

    }

    private fun registerUser(
        email: String,
        password: String,
        rePassword: String,
        name: String,
        role: String
    ) {
        val emailInputLayout =
            view?.findViewById<TextInputLayout>(R.id.register_user_emailTextInputLayout)
        val passwordInputLayout =
            view?.findViewById<TextInputLayout>(R.id.register_user_passwordTextInputLayout)
        val rePasswordInputLayout =
            view?.findViewById<TextInputLayout>(R.id.register_user_repeatPasswordTextInputLayout)
        val nameInputLayout =
            view?.findViewById<TextInputLayout>(R.id.register_user_nameTextInputLayout)
        val roleInputLayout =
            view?.findViewById<TextInputLayout>(R.id.register_user_roleTextInputLayout)
        emailInputLayout?.error = ""
        passwordInputLayout?.error = ""
        rePasswordInputLayout?.error = ""
        nameInputLayout?.error = ""
        roleInputLayout?.error = ""

        if (email.isEmpty()) {

            emailInputLayout?.error = "Email is empty"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout?.error = "Not valid mail"
            return
        }

        if (password.isEmpty()) {
            passwordInputLayout?.error = "Password is empty"
            return

        }

        if (password.length < 5) {
            passwordInputLayout?.error = "Password need to be at least 6 character long"
            return

        }

        if (rePassword != password) {
            rePasswordInputLayout?.error = "Passwords dose not match"
            return

        }

        if (name.isEmpty()) {
            nameInputLayout?.error = "Name is empty"
            return

        }

        if (role.isEmpty()) {
            roleInputLayout?.error = "Role is empty"
            return

        }


        val progressBar = view?.findViewById<ProgressBar>(R.id.register_user_progressBar)
        progressBar?.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { createUser ->
            if (createUser.isSuccessful) {

                auth.currentUser?.let { currentUser ->
                    val user = hashMapOf(
                        "name" to name,
                        "role" to role
                    )

                    db.collection("users").document(currentUser.uid.toString())
                        .set(user)
                        .addOnCompleteListener { addedUserToDatabase ->

                            if (addedUserToDatabase.isSuccessful) {
                                (context as MainActivity).makeToast("User was registered!")
                                progressBar?.visibility = View.GONE

                                //redirect user to profile

                            } else {
                                (context as MainActivity).makeToast("Failed to register.")
                                progressBar?.visibility = View.GONE

                            }
                        }
                }
            } else {
                progressBar?.visibility = View.GONE
                (context as MainActivity).makeToast("Failed to connect to Firebase.")
            }

        }

    }
}
