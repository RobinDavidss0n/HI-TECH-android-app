package se.ju.student.hitech

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminLoginFragment : Fragment() {

    lateinit var  progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_admin_login, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.onCreate(savedInstanceState)

        val emailInput =
            view?.findViewById<TextInputEditText>(R.id.admin_login_emailTextInputEditText)
        val passwordInput =
            view?.findViewById<TextInputEditText>(R.id.admin_login_passwordTextInputEditText)
        val loginButton = view?.findViewById<Button>(R.id.admin_login_loginButton)
        val logoutButton = view?.findViewById<Button>(R.id.logout)
        progressBar = view?.findViewById<ProgressBar>(R.id.admin_login_progressBar)!!

        loginButton?.setOnClickListener {
            if (verifyLoginInputs(
                    emailInput?.text.toString().trim(),
                    passwordInput?.text.toString()
                )
            ) {
                progressBar.visibility = View.VISIBLE
                userLogin(emailInput?.text.toString().trim(), passwordInput?.text.toString())
            }

        }

        logoutButton?.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            userLogout()


        }

        val register = view?.findViewById<TextView>(R.id.admin_login_register)

        register?.setOnClickListener {
            (context as MainActivity).changeToFragment(MainActivity.TAG_REGISTER_USER)
        }


    }

    private fun verifyLoginInputs(email: String, password: String): Boolean {
        val emailInputLayout =
            view?.findViewById<TextInputLayout>(R.id.admin_login_emailTextInputLayout)
        val passwordInputLayout =
            view?.findViewById<TextInputLayout>(R.id.admin_login_passwordTextInputLayout)
        emailInputLayout?.error = ""
        passwordInputLayout?.error = ""

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
        return true
    }

    private fun userLogin(email: String, password: String) {

        val userRepository = UserRepository()

        if (userRepository.checkIfLoggedIn()) {

            (context as MainActivity).makeToast("Already logged in.")
            progressBar.visibility = View.GONE


        } else {

            userRepository.userLogin(email, password) { result ->
                progressBar.visibility = View.GONE
                when (result) {

                    "successful" -> (context as MainActivity).makeToast("Login successful!")
                    //redirect
                    "invalidEmail" -> (context as MainActivity).makeToast("No user is tied to this email, pleas use a correct email.")
                    "invalidPassword" -> (context as MainActivity).makeToast("Wrong password, try again.")
                    "emailNotVerified" -> (context as MainActivity).makeToast("Check your email to verify your account.")
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")

                }
            }


        }
    }

    private fun userLogout() {

        val userRepository = UserRepository()
        val progressBar = view?.findViewById<ProgressBar>(R.id.admin_login_progressBar)

        userRepository.userLogout()


        progressBar?.visibility = View.GONE
        (context as MainActivity).makeToast("User logged out!")
        //redirect

    }
}