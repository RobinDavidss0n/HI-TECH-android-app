package se.ju.student.hitech

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class UserPageFragment : Fragment() {

    lateinit var  progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_user_page, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.onCreate(savedInstanceState)

        val emailInput =
            view?.findViewById<TextInputEditText>(R.id.user_page_emailTextInputEditText)
        val nameInput = view?.findViewById<TextInputEditText>(R.id.user_page_nameTextInputEditText)
        val roleInput = view?.findViewById<TextInputEditText>(R.id.user_page_roleTextInputEditText)
        val logoutButton = view?.findViewById<Button>(R.id.user_page_logoutButton)
        val updateButton = view?.findViewById<Button>(R.id.user_page_updateButton)
        val resetPassword = view?.findViewById<TextView>(R.id.user_page_resetPasswordText)

        progressBar = view?.findViewById<ProgressBar>(R.id.user_page_progressBar)!!

        updateButton?.setOnClickListener {
            if (verifyUserInputs(
                    emailInput?.text.toString().trim(),
                    nameInput?.text.toString().trim(),
                    roleInput?.text.toString().trim()
                )
            ) {
                progressBar.visibility = View.VISIBLE
                updateUser(
                    emailInput?.text.toString().trim(),
                    nameInput?.text.toString().trim(),
                    roleInput?.text.toString().trim()
                )
            }

        }

        logoutButton?.setOnClickListener{
            UserRepository().userLogout()
            (context as MainActivity).makeToast("User logged out!")
            (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_ADMIN_LOGIN)
        }

        resetPassword?.setOnClickListener{
            resetPassword()
        }
    }

    fun setUserInfoInInputfields(emailInput: TextInputEditText, nameInput: TextInputEditText, roleInput: TextInputEditText){
        val userRepository = UserRepository()
        userRepository.getCurrentUserInfo({userDoc, email ->
        }, {
            (context as MainActivity).makeToast("Something went wrong, could not load user information.")
        })
    }

    private fun verifyUserInputs(
        email: String, name: String, role: String
    ): Boolean {

        val emailInputLayout =
            view?.findViewById<TextInputLayout>(R.id.user_page_emailTextInputLayout)
        val nameInputLayout =
            view?.findViewById<TextInputLayout>(R.id.user_page_nameTextInputLayout)
        val roleInputLayout =
            view?.findViewById<TextInputLayout>(R.id.user_page_roleTextInputLayout)
        emailInputLayout?.error = ""
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

    private fun updateUser(email: String, name: String, role: String) {

        val userRepository = UserRepository()

        userRepository.updateCurrentUserInfo(email, name, role) { result ->

            progressBar.visibility = View.GONE

            when (result) {
                "successful" -> {
                    (context as MainActivity).makeToast("User was updated successfully!")
                    (context as MainActivity).changeToFragment(MainActivity.TAG_USER_PAGE)
                }
                "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")

            }
        }
    }

    private fun resetPassword(){
        val userRepository = UserRepository()

        userRepository.getCurrentUserInfo({_, email ->
            userRepository.sendPasswordReset(email) { result ->
                progressBar.visibility = View.GONE
                when (result) {
                    "successful" -> (context as MainActivity).makeToast("Check your email!")
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }

            }
        }, {
            (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
        })


    }
}