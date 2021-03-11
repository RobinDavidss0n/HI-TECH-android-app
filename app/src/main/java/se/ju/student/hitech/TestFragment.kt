package se.ju.student.hitech

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import se.ju.student.hitech.chat.ChatRepository

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
        val chatID = "LeQPZP34Os54X4YkDQnA"
        val chatRepository = ChatRepository()
        val userRepository = UserRepository()
        val androidID = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        testThatShit?.setOnClickListener {




            // Sm54rp4sGzMxj3g9rHJsGfnQBAk1

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



            chatRepository.setNewChatListener() { resultString, dataMap ->

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
                TimeHandler().getLocalZoneTimestamp().time.toString().convertTimeToTimestamp().toString()
            )
            Log.d(
                "time",
                TimeHandler().getLocalZoneTimestamp().time.toString().convertTimeToStringTimeFormat()!!
            )
            Log.d(
                "date",
                TimeHandler().getLocalZoneTimestamp().time.toString().convertTimeToStringDateFormat()!!
            )
            Log.d(
                "hourMin",
                TimeHandler().getLocalZoneTimestamp().time.toString().convertTimeToStringHourMinutesFormat()!!
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


            chatRepository.getAllActiveChats() {resultString, data ->

                (context as MainActivity).makeToast("Got messages")


                when (resultString) {
                    "successful" -> {
                        Log.d("Chat", data["case"].toString())
                        Log.d("Chat", data["lastUpdated"].toString().convertTimeToTimestamp().toString())
                    }
                    "notFound" -> {
                        (context as MainActivity).makeToast("Not found.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }

            chatRepository.getAllMessagesFromChat(chatID) {resultString, data ->

                when (resultString) {
                    "successful" -> {
                        (context as MainActivity).makeToast("Got messages")
                        Log.d("Chat", data["msgText"].toString())
                        Log.d("Chat", data["timestamp"].toString().convertTimeToTimestamp().toString())
                    }
                    "notFound" -> {
                        (context as MainActivity).makeToast("Not found.")
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




        }

    }
}