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
import com.google.firebase.auth.FirebaseAuth

class AdminLoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_admin_login, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.onCreate(savedInstanceState)


        auth = FirebaseAuth.getInstance()

        val email = view?.findViewById<TextInputEditText>(R.id.admin_login_emailTextInputEditText)
        val password = view?.findViewById<TextInputEditText>(R.id.admin_login_passwordTextInputEditText)
        val loginButton = view?.findViewById<Button>(R.id.admin_login_loginButton)
        val logoutButton = view?.findViewById<Button>(R.id.logout)

        loginButton?.setOnClickListener {
            userLogin(email?.text.toString().trim(), password?.text.toString().trim())
        }

        logoutButton?.setOnClickListener {
            userLogout()
        }


        val register = view?.findViewById<TextView>(R.id.admin_login_register)

        register?.setOnClickListener {
            (context as MainActivity).changeToFragment(MainActivity.TAG_REGISTER_USER)
        }


    }

    private fun userLogout() {
        auth.signOut()
        (context as MainActivity).makeToast("User logged out!")
        //redirect
    }


    private fun userLogin(email: String, password: String) {
        val emailInputLayout = view?.findViewById<TextInputLayout>(R.id.admin_login_emailTextInputLayout)
        val passwordInputLayout = view?.findViewById<TextInputLayout>(R.id.admin_login_passwordTextInputLayout)
        emailInputLayout?.error = ""
        passwordInputLayout?.error = ""

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

        if (auth.currentUser != null) {
            (context as MainActivity).makeToast("Already logged in.")
            return
        }

        val progressBar = view?.findViewById<ProgressBar>(R.id.admin_login_progressBar)
        progressBar?.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { signIn ->

            if (signIn.isSuccessful) {

                val user = auth.currentUser

                if (user != null) {
                    if (user.isEmailVerified)
                    //redirect
                        (context as MainActivity).makeToast("Login successful!")
                    else {
                        user.sendEmailVerification()
                        auth.signOut()
                        (context as MainActivity).makeToast("Check your email to verify your account.")
                    }
                }else{
                    (context as MainActivity).makeToast("Something went wrong, please try again.")
                }
                progressBar?.visibility = View.GONE

            } else {
                (context as MainActivity).makeToast("Failed to login, please check your credentials.")
                progressBar?.visibility = View.GONE

            }
        }
    }
}