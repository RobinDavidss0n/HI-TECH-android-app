package se.ju.student.hitech.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R

class VerifyNewUser : Fragment() {
    lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_verify_new_user, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.onCreate(savedInstanceState)
        userRepository = UserRepository()

        val verifyNewUser = view?.findViewById<TextView>(R.id.verification_user1)
        val verifyNewUserButton = view?.findViewById<Button>(R.id.verification_user_button1)
        val denyNewUserButton = view?.findViewById<ImageButton>(R.id.deny_user_button1)
        val goBackButton = view?.findViewById<Button>(R.id.verify_user_goBackButton)

        var userID = ""


        userRepository.getNotVerifiedUser() { result, user, id ->
            when (result) {
                "successful" -> {
                    val username1 = user.name
                    if (username1 != null) {
                        verifyNewUser?.text = username1
                        userID = id
                    }

                }
                "internalError" -> {
                    (context as MainActivity).makeToast(getString(R.string.internalError))
                }
            }
        }


        verifyNewUserButton?.setOnClickListener {
            if (userID != ""){
                verifyUser(userID)
            }
        }

        denyNewUserButton?.setOnClickListener {
            if (userID != ""){
                denyUser(userID)
            }
        }


        goBackButton?.setOnClickListener {
            (context as MainActivity).changeToFragment(MainActivity.TAG_USER_PAGE)
        }

    }

    private fun verifyUser(userID: String){
        userRepository.verifyUser(userID){ result ->
            when (result) {
                "successful" -> {
                    (context as MainActivity).reloadFragment(MainActivity.TAG_FRAGMENT_VERIFY_NEW_USER)
                    (context as MainActivity).makeToast(getString(R.string.user_verified))
                }
                "internalError" -> {
                    (context as MainActivity).makeToast(getString(R.string.internalError))
                }
            }
        }
    }

    private fun denyUser(userID: String){
        userRepository.denyUser(userID){ result ->
            when (result) {
                "successful" -> {
                    (context as MainActivity).reloadFragment(MainActivity.TAG_FRAGMENT_VERIFY_NEW_USER)
                    (context as MainActivity).makeToast(getString(R.string.user_denied))
                }
                "internalError" -> {
                    (context as MainActivity).makeToast(getString(R.string.internalError))
                }
            }
        }
    }


}