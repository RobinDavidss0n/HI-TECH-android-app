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
import se.ju.student.hitech.user.UserRepository.Companion.userRepository

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

            emailInputLayout?.error = getString(R.string.emailEmpty)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout?.error = getString(R.string.invalidEmail)
            return false
        }

        if (password.isEmpty()) {
            passwordInputLayout?.error = getString(R.string.passwordEmpty)
            return false
        }

        if (password.length < 5) {
            passwordInputLayout?.error = getString(R.string.passwordNotLongEnough)
            return false

        }

        if (rePassword != password) {
            rePasswordInputLayout?.error = getString(R.string.passwordNotMatch)
            return false
        }

        if (name.isEmpty()) {
            nameInputLayout?.error = getString(R.string.nameEmpty)
            return false
        }

        if (role.isEmpty()) {
            roleInputLayout?.error = getString(R.string.roleEmpty)
            return false
        }
        return true
    }

    private fun createUser(email: String, password: String, name: String, role: String) {
        userRepository.createUser(email, password, name, role) { result ->
            progressBar?.visibility = View.GONE
            when (result) {

                "successful" -> {
                    (context as MainActivity).makeToast(getString(R.string.createUserSuccessful))
                    userRepository.userLogout()
                    //redirect
                }
                "internalError" -> (context as MainActivity).makeToast(getString(R.string.internalError))
            }
        }
    }
}
