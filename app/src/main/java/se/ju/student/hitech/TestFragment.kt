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

        testThatShit?.setOnClickListener {

            val chatRepository = ChatRepository()
            val userRepository = UserRepository()

            val androidID = Settings.Secure.getString(
                context?.contentResolver,
                Settings.Secure.ANDROID_ID);

            // Sm54rp4sGzMxj3g9rHJsGfnQBAk1

            val chatID = "LeQPZP34Os54X4YkDQnA"

            chatRepository.setChatListener(){activity, chatID ->

                when (activity) {
                    "newChat" -> {
                        Log.d("Chat", "New chat")
                        Log.d("Chat", chatID)
                        (context as MainActivity).makeToast("New Chat.")
                    }
                    "newMessageOrAdminChange" -> {
                        (context as MainActivity).makeToast("New message or admin change.")
                    }
                    "chatClosed" -> {
                        (context as MainActivity).makeToast("Chat closed.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }

            }
/*
            chatRepository.deactivateChat(chatID) { result ->
                when (result) {
                    "successful" -> {
                        userRepository.removeChatFromUser(chatID){ result ->
                            when (result) {
                                "successful" -> {
                                    (context as MainActivity).makeToast("Deactivated chat.")                                }
                                "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                            }

                        }

                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }


            chatRepository.getAllActiveChats({ data ->

                (context as MainActivity).makeToast("Got messages")
                Log.d("Chat", data["case"].toString())
                Log.d("Chat", data["lastUpdated"].toString().convertTimeToTimestamp().toString())

            }, { result ->
                when (result) {
                    "notFound" -> {
                        (context as MainActivity).makeToast("Not found.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            })

            chatRepository.getAllMessagesFromChat(chatID, { data ->

                (context as MainActivity).makeToast("Got messages")
                Log.d("Chat", data["msgText"].toString())
                Log.d("Chat", data["timestamp"].toString().convertTimeToTimestamp().toString())

            }, { result ->
                when (result) {
                    "notFound" -> {
                        (context as MainActivity).makeToast("Not found.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            })


            chatRepository.addMessage(msg.toString(),
                UserRepository().checkIfLoggedIn(), chatID){ result ->
                when (result) {
                    "successful" -> {
                        msg ++
                        (context as MainActivity).makeToast("Sent msg.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }



            userRepository.removeChatFromUser(chatID){ result ->
                when (result) {
                    "successful" -> {
                        (context as MainActivity).makeToast("Removed chat from user.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }


            chatRepository.addAdminToChat(UserRepository().getUserID(), chatID){ result ->
                            when (result) {
                                "successful" -> {

                                    UserRepository().addChatToUser(chatID){
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

            chatRepository.removeAdminFromChat("6NjnPNNQKtoYHVrAsuhS"){ result ->
                when (result) {
                    "successful" -> {
                        (context as MainActivity).makeToast("Removed admin.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }



            chatRepository.getChatWithAndroidID(androidID, { data ->

                    (context as MainActivity).makeToast("Got chat")
                    Log.d("Chat", data["case"].toString())

            }, { result ->
                when (result) {
                    "notFound" -> {
                        (context as MainActivity).makeToast("Not found.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            })

            */


        }

    }
}