package se.ju.student.hitech

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class ShopRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var shopItems: List<ShopItem> = ArrayList()

    fun loadShopImages(): List<ShopItem> {
        db.collection("images").get().addOnSuccessListener { result ->
            shopItems = result.toObjects(ShopItem::class.java)

        }.addOnFailureListener {
            Log.d(TAG, "Error getting images: ", it)
        }
        return shopItems
    }

}