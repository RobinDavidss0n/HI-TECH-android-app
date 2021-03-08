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

        progressBar = view?.findViewById<ProgressBar>(R.id.admin_login_progressBar)!!

        loginButton?.setOnClickListener {
            UserRepository().userLogout()
            if (verifyLoginInputs(
                    emailInput?.text.toString().trim(),
                    passwordInput?.text.toString()
                )
            ) {
                progressBar.visibility = View.VISIBLE
                userLogin(emailInput?.text.toString().trim(), passwordInput?.text.toString())
            }

        }

        val register = view?.findViewById<TextView>(R.id.admin_login_register)

        register?.setOnClickListener {
            (context as MainActivity).changeToFragment(MainActivity.TAG_REGISTER_USER)
        }

        val forgotPassword = view?.findViewById<TextView>(R.id.admin_login_forgotPassword)

        forgotPassword?.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            resetPassword(emailInput?.text.toString().trim())

        }


    }

    private fun resetPassword(email: String){
        val userRepository = UserRepository()
        if (email.isEmpty()){
            progressBar.visibility = View.GONE
            (context as MainActivity).makeToast("Write your email in the email field and press on 'Forgot password?' again.")
        }else{
            userRepository.sendPasswordReset(email) { result ->
                progressBar.visibility = View.GONE
                when (result) {
                    "successful" -> (context as MainActivity).makeToast("Check your email!")
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }

            }

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

                    "successful" ->{
                        (context as MainActivity).makeToast("Login successful!")
                        (context as MainActivity).changeToFragment(MainActivity.TAG_USER_PAGE)
                    }
                    "invalidEmail" -> (context as MainActivity).makeToast("No user is tied to this email, pleas use a correct email.")
                    "invalidPassword" -> (context as MainActivity).makeToast("Wrong password, try again.")
                    "emailNotVerified" -> (context as MainActivity).makeToast("Check your email to verify your account.")
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")

                }
            }


        }
    }
}