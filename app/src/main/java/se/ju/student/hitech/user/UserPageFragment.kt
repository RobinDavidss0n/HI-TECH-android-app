package se.ju.student.hitech.user

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R
import se.ju.student.hitech.user.UserRepository.Companion.userRepository

class UserPageFragment : Fragment() {

    lateinit var progressBar: ProgressBar
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

        userRepository.getCurrentUserInfo({ _, email ->
            // HI TECH Board admin account that can't be deleted or updated by mistake
            if (email == "hitechstyrelsen@gmail.com") {
                deleteAccount?.visibility = GONE
                updateButton?.visibility = GONE
            }
        }, {
            (context as MainActivity).makeToast(getString(R.string.errorAccessData))
        })

        updateButton?.setOnClickListener {
            if (verifyUserInputs(
                    emailInput.text.toString().trim(),
                    nameInput.text.toString().trim(),
                    roleInput.text.toString().trim()
                )
            ) {
                progressBar.visibility = VISIBLE
                updateUser(
                    emailInput.text.toString().trim(),
                    nameInput.text.toString().trim(),
                    roleInput.text.toString().trim()
                )
            }
        }

        logoutButton?.setOnClickListener {
            userRepository.userLogout()
            (context as MainActivity).makeToast(getString(R.string.userLoggedOut))
            (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_ADMIN_LOGIN)
        }

        resetPassword?.setOnClickListener {
            resetPassword()
        }
        deleteAccount?.setOnClickListener {
            deleteAccount()
        }
    }

    private fun setUserInfoIntoInputFields() {
        progressBar.visibility = VISIBLE
        userRepository.getCurrentUserInfo({ user, email ->
            progressBar.visibility = GONE
            emailInput.setText(email)
            nameInput.setText(user.name)
            roleInput.setText(user.role)
        }, {
            progressBar.visibility = GONE
            emailInput.setText(getString(R.string.errorAccessData))
            nameInput.setText(getString(R.string.errorAccessData))
            roleInput.setText(getString(R.string.errorAccessData))
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

            emailInputLayout?.error = getString(R.string.emailEmpty)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout?.error = getString(R.string.invalidEmail)
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

    private fun updateUser(email: String, name: String, role: String) {

        AlertDialog.Builder(context as MainActivity)
            .setTitle(getString(R.string.UpdateUserInformation))
            .setMessage(getString(R.string.updateUserConfirmation))
            .setPositiveButton(
                getString(R.string.yes)

            ) { _, _ ->
                userRepository.updateCurrentUserInfo(email, name, role) { result ->

                    progressBar.visibility = GONE

                    when (result) {
                        "successful" -> {
                            (context as MainActivity).makeToast(getString(R.string.updateUserSuccessful))
                            setUserInfoIntoInputFields()
                        }
                        "internalError" -> (context as MainActivity).makeToast(getString(R.string.internalError))
                    }
                }
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { _, _ ->
                progressBar.visibility = GONE
                //Don't delete
            }
            .show()
    }

    private fun resetPassword() {
        userRepository.getCurrentUserInfo({ _, email ->
            AlertDialog.Builder(context as MainActivity)
                .setTitle(getString(R.string.user_page_resetPassword))
                .setMessage(getString(R.string.resetPasswordConfirmation) + " $email?")
                .setPositiveButton(
                    getString(R.string.yes)

                ) { _, _ ->
                    userRepository.sendPasswordReset(email) { result ->
                        Log.d("email", email)
                        progressBar.visibility = GONE
                        when (result) {
                            "successful" -> (context as MainActivity).makeToast(getString(R.string.resetConfirmed) + " $email!")
                            "internalError" -> (context as MainActivity).makeToast(getString(R.string.internalError))
                        }
                    }
                }
                .setNegativeButton(
                    getString(R.string.no)
                ) { _, _ ->
                    progressBar.visibility = GONE
                    //Don't delete
                }
                .show()
        }, {
            (context as MainActivity).makeToast(getString(R.string.internalError))
        })
    }

    private fun deleteAccount() {
        AlertDialog.Builder(context as MainActivity)
            .setTitle(getString(R.string.deleteAccount))
            .setMessage(getString(R.string.deleteAccountConfirmation))
            .setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->

                userRepository.deleteCurrentUser { result ->
                    progressBar.visibility = GONE
                    when (result) {
                        "successful" -> (context as MainActivity).makeToast(getString(R.string.accountDeleted))
                        "internalError" -> (context as MainActivity).makeToast(getString(R.string.internalError))
                    }
                }
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { _, _ ->
                progressBar.visibility = GONE
                //Don't delete
            }
            .show()
    }
}