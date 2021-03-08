package se.ju.student.hitech

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
    lateinit var emailInput: TextInputEditText
    lateinit var nameInput: TextInputEditText
    lateinit var roleInput: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_user_page, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.onCreate(savedInstanceState)

        emailInput = view?.findViewById<TextInputEditText>(R.id.user_page_emailTextInputEditText)!!
        nameInput = view?.findViewById<TextInputEditText>(R.id.user_page_nameTextInputEditText)!!
        roleInput = view?.findViewById<TextInputEditText>(R.id.user_page_roleTextInputEditText)!!
        progressBar = view?.findViewById<ProgressBar>(R.id.user_page_progressBar)!!
        val logoutButton = view?.findViewById<Button>(R.id.user_page_logoutButton)
        val updateButton = view?.findViewById<Button>(R.id.user_page_updateButton)
        val resetPassword = view?.findViewById<TextView>(R.id.user_page_resetPasswordText)
        val deleteAccount = view?.findViewById<TextView>(R.id.user_page_deleteAccountText)

        setUserInfoIntoInputFields()

        updateButton?.setOnClickListener {
            if (verifyUserInputs(
                    emailInput.text.toString().trim(),
                    nameInput.text.toString().trim(),
                    roleInput.text.toString().trim()
                )
            ) {
                progressBar.visibility = View.VISIBLE
                updateUser(
                    emailInput.text.toString().trim(),
                    nameInput.text.toString().trim(),
                    roleInput.text.toString().trim()
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
        deleteAccount?.setOnClickListener{
            deleteAccount()
        }
    }

    private fun setUserInfoIntoInputFields(){
        progressBar.visibility = View.VISIBLE
        UserRepository().getCurrentUserInfo({user, email ->
            progressBar.visibility = View.GONE
            emailInput.setText(email)
            nameInput.setText(user.name)
            roleInput.setText(user.role)
        }, {
            progressBar.visibility = View.GONE
            emailInput.setText("Error, could not access user data.")
            nameInput.setText("Error, could not access user data.")
            roleInput.setText("Error, could not access user data.")
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

        AlertDialog.Builder(context as MainActivity)
            .setTitle("Update user information")
            .setMessage("Do you want to update your user information?")
            .setPositiveButton(
                "Yes"

            ) { _, _ ->
                val userRepository = UserRepository()

                userRepository.updateCurrentUserInfo(email, name, role) { result ->

                    progressBar.visibility = View.GONE

                    when (result) {
                        "successful" -> {
                            (context as MainActivity).makeToast("User was updated successfully!")
                            setUserInfoIntoInputFields()
                        }
                        "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")

                    }
                }
            }
            .setNegativeButton(
                "NO"
            ) { _, _ ->
                progressBar.visibility = View.GONE
                //Don't delete
            }
            .show()



    }

    private fun resetPassword(){

        AlertDialog.Builder(context as MainActivity)
            .setTitle("Reset password")
            .setMessage("Do you want a reset link to your sent to your email?")
            .setPositiveButton(
                "Yes"

            ) { _, _ ->
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
            .setNegativeButton(
                "NO"
            ) { _, _ ->
                progressBar.visibility = View.GONE
                //Don't delete
            }
            .show()
    }

    private fun deleteAccount(){
        AlertDialog.Builder(context as MainActivity)
            .setTitle("Delete account")
            .setMessage("Do you really want to delete your account? You can not undo this.")
            .setPositiveButton(
                "Yes"

            ) { _, _ ->

                UserRepository().deleteCurrentUser {result ->
                    progressBar.visibility = View.GONE
                    when (result) {
                        "successful" -> (context as MainActivity).makeToast("Account deleted.")
                        "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                    }

                }

            }
            .setNegativeButton(
                "NO"
            ) { _, _ ->
                progressBar.visibility = View.GONE
                //Don't delete
            }
            .show()
    }

}