package se.ju.student.hitech

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class AdminLoginFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_admin_login, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.onCreate(savedInstanceState)


        mAuth = FirebaseAuth.getInstance()

        val email = view?.findViewById<TextInputEditText>(R.id.admin_login_emailTextInputEditText)
        val password = view?.findViewById<TextInputEditText>(R.id.admin_login_passwordTextInputEditText)
        val loginButton = view?.findViewById<Button>(R.id.admin_login_loginButton)
        //val logoutButton = view?.findViewById<Button>(R.id.)

        loginButton?.setOnClickListener {
            userLogin(email?.text.toString().trim(), password?.text.toString().trim())
        }

        /*logoutButton?.setOnClickListener {
            userLogout()
        }*/


        val register = view?.findViewById<TextView>(R.id.admin_login_register)

        register?.setOnClickListener {
        //fix
        //startActivity(Intent(this, RegisterUser::class.java))
        }


    }

    private fun userLogout() {
        mAuth.signOut()
        (context as MainActivity).makeToast("User logged out!")
        //redirect
    }


    private fun userLogin(email: String, password: String) {
        val emailInputLayout = view?.findViewById<EditText>(R.id.admin_login_emailTextInputLayout)
        val passwordInputLayout = view?.findViewById<EditText>(R.id.admin_login_passwordTextInputLayout)

        if (email.isEmpty()) {
            emailInputLayout?.error = "Email is empty"
            //Toast.makeText(this, "Email is empty", Toast.LENGTH_LONG).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //Toast.makeText(this, "Not valid mail", Toast.LENGTH_LONG).show()
            return
        }

        if (password.isEmpty()) {
            //Toast.makeText(this, "Password is empty", Toast.LENGTH_LONG).show()
            return

        }

        if (mAuth.currentUser != null) {
            //Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show()
            return
        }

        val progressBar = view?.findViewById<ProgressBar>(R.id.admin_login_loginProgressBar)
        progressBar?.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { signIn ->

            if (signIn.isSuccessful) {

                val user = mAuth.currentUser

                if (user != null) {
                    if (user.isEmailVerified)
                    //redirect
                        (context as MainActivity).makeToast("Login successful!")
                    else {
                        user.sendEmailVerification()
                        (context as MainActivity).makeToast("Check your email to verify your account!")
                    }
                }
                progressBar?.visibility = View.GONE

            } else {
                (context as MainActivity).makeToast("Failed to login, please check your credentials.")
                progressBar?.visibility = View.GONE

            }
        }
    }
}