package se.ju.student.hitech.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R
import se.ju.student.hitech.UserRepository
import se.ju.student.hitech.chat.ChatRepository
import se.ju.student.hitech.handlers.*

class TestFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_function_tester, container, false)

    @SuppressLint("HardwareIds")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        super.onCreate(savedInstanceState)

        val testThatShit = view?.findViewById<Button>(R.id.test)
        var msg = 1
        val chatID = "aSluaQPdcSkBvDNm2her"
        val chatRepository = ChatRepository()
        val userRepository = UserRepository()
        val androidID = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        /*
        chatRepository.createNewChat(androidID, "study") { result ->
            when (result) {
                "successful" -> {
                    (context as MainActivity).makeToast("Chat created.")
                }
                "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
            }
        }*/


        testThatShit?.setOnClickListener {


            /*
            chatRepository.loadAllActiveChatsAndUpdateIfChanged() { result ->

                when (result) {
                    "loaded" -> {

                        //all data laddat, säkert att sätta in det i viewn

                    }

                    "internalError" -> {
                        //meddela användaren om att något gick fel med att hämta/uppdatera datan
                    }
                }
            }

            chatRepository.removeCurrentSpecificChatMessagesLoader()

            chatRepository.loadAllMessagesFromSpecificChatAndUpdateIfChanged(chatID) { result ->

                when (result) {
                    "loaded" -> {

                        //all data laddat, säkert att sätta in det i viewn

                    }

                    "internalError" -> {
                        //meddela användaren om att något gick fel med att hämta/uppdatera datan
                    }
                }
            }

            chatRepository.setNewMessagesListener(chatID) { resultString, dataMap ->

                when (resultString) {
                    "newChat" -> {
                        Log.d("Msg", "New msg")
                        Log.d("Msg", dataMap.toString())
                        (context as MainActivity).makeToast("New msg.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong.")
                }

            }


            chatRepository.addMessage(
                msg.toString(),
                UserRepository().checkIfLoggedIn(), chatID
            ) { result ->
                when (result) {
                    "successful" -> {
                        msg++
                        (context as MainActivity).makeToast("Sent msg.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }



            chatRepository.setNewMessagesListener(chatID) { resultString, message ->

                when (resultString) {
                    "newChat" -> {
                       //skicka notis
                    }
                    "internalError" -> {
                        //Meddela användare om fel
                    }
                }

            }


            chatRepository.setNewChatListener() { resultString, chat ->

                when (resultString) {
                    "newChat" -> {
                        Log.d("Chat", "New chat")
                        Log.d("Chat", dataMap.toString())
                        (context as MainActivity).makeToast("New Chat.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong.")
                }

            }

            Log.d(
                "timestamp",
                TimeHandler().getLocalZoneTimestamp().time.toString().convertTimeToTimestamp()
                    .toString()
            )
            Log.d(
                "time",
                TimeHandler().getLocalZoneTimestamp().time.toString()
                    .convertTimeToStringTimeFormat()!!
            )
            Log.d(
                "date",
                TimeHandler().getLocalZoneTimestamp().time.toString()
                    .convertTimeToStringDateFormat()!!
            )
            Log.d(
                "hourMin",
                TimeHandler().getLocalZoneTimestamp().time.toString()
                    .convertTimeToStringHourMinutesFormat()!!
            )



            chatRepository.deactivateChat(chatID) { result ->
                when (result) {
                    "successful" -> {
                        userRepository.removeChatFromUser(chatID) { result ->
                            when (result) {
                                "successful" -> {
                                    (context as MainActivity).makeToast("Deactivated chat.")
                                }
                                "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                            }

                        }

                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }



            userRepository.removeChatFromUser(chatID) { result ->
                when (result) {
                    "successful" -> {
                        (context as MainActivity).makeToast("Removed chat from user.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }


            chatRepository.addAdminToChat(UserRepository().getUserID(), chatID) { result ->
                when (result) {
                    "successful" -> {

                        UserRepository().addChatToUser(chatID) {
                            when (result) {
                                "successful" -> {
                                    (context as MainActivity).makeToast("Added admin.")
                                }
                                "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                            }

                        }
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }

            chatRepository.removeAdminFromChat("6NjnPNNQKtoYHVrAsuhS") { result ->
                when (result) {
                    "successful" -> {
                        (context as MainActivity).makeToast("Removed admin.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }



            chatRepository.getChatWithAndroidID(androidID) { resultString, data ->

                when (resultString) {

                    "successful" -> {
                        (context as MainActivity).makeToast("Got chat")
                        Log.d("Chat", data["case"].toString())
                    }

                    "notFound" -> {
                        (context as MainActivity).makeToast("Not found.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }

             */


        }

    }
}