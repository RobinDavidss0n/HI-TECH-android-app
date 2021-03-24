package se.ju.student.hitech.shop

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class ShopRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var shopItems = mutableListOf<ShopItem>()

    fun loadShopImages(
        images: MutableLiveData<List<ShopItem>>,
        callback: (List<ShopItem>, MutableLiveData<List<ShopItem>>) -> Unit
    ) {

        db.collection("images").get().addOnSuccessListener { result ->
            shopItems = result.toObjects(ShopItem::class.java)
            callback(shopItems, images)

        }.addOnFailureListener {
            Log.d(TAG, "Error getting documents: ", it)
        }
    }
}