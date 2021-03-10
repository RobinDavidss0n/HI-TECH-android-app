package se.ju.student.hitech

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import se.ju.student.hitech.Constants.Companion.CONTENT_TYPE
import se.ju.student.hitech.Constants.Companion.SERVER_KEY

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification : PushNotification
    ): Response<ResponseBody>
}