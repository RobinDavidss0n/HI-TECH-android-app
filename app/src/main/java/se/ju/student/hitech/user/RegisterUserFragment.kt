package se.ju.student.hitech.user

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R

class RegisterUserFragment : Fragment() {

    private val progressBar = view?.findViewById<ProgressBar>(R.id.register_user_progressBar)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_register_user, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.onCreate(savedInstanceState)

        val emailInput =
            view?.findViewById<TextInputEditText>(R.id.register_user_emailTextInputEditText)
        val passwordInput =
            view?.findViewById<TextInputEditText>(R.id.register_user_passwordTextInputEditText)
        val rePassword =
            view?.findViewById<TextInputEditText>(R.id.register_user_repeatPasswordTextInputEditText)
        val name = view?.findViewById<TextInputEditText>(R.id.register_user_nameTextInputEditText)
        val role = view?.findViewById<TextInputEditText>(R.id.register_user_roleTextInputEditText)
        val registerButton = view?.findViewById<Button>(R.id.register_user_registerButton)


        registerButton?.setOnClickListener {
            if (verifyRegisterUserInputs(
                    emailInput?.text.toString().trim(),
                    passwordInput?.text.toString(),
                    rePassword?.text.toString().trim(),
                    name?.text.toString().trim(),
                    role?.text.toString().trim()
                )
            ) {
                progressBar?.visibility = View.VISIBLE
                createUser(
                    emailInput?.text.toString().trim(),
                    passwordInput?.text.toString(),
                    name?.text.toString().trim(),
                    role?.text.toString().trim()
                )
            }

        }

    }

    private fun verifyRegisterUserInputs(
        email: String, password: String, rePassword: String, name: String, role: String
    ): Boolean {

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
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout?.error = "Not valid mail"
            return false
        }

        if (password.isEmpty()) {
            passwordInputLayout?.error = "Password is empty"
            return false

        }

        if (password.length < 5) {
            passwordInputLayout?.error = "Password need to be at least 6 character long"
            return false

        }

        if (rePassword != password) {
            rePasswordInputLayout?.error = "Passwords dose not match"
            return false

        }

        if (name.isEmpty()) {
            nameInputLayout?.error = "Name is empty"
            return false

        }

        if (role.isEmpty()) {
            roleInputLayout?.error = "Role is empty"
            return false

        }
        return true
    }

    private fun createUser(email: String, password: String, name: String, role: String) {

        val userRepository = UserRepository()
        userRepository.createUser(email, password, name, role) { result ->
            progressBar?.visibility = View.GONE
            when (result) {

                "successful" -> {
                    (context as MainActivity).makeToast("User was created successfully!")
                    userRepository.userLogout()
                    //redirect
                }
                "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")

            }
        }

    }
}
