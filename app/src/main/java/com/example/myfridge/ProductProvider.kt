package com.example.myfridge

import android.util.Log
import com.example.myfridge.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductProvider {

    companion object{

        val products = populate()
        public fun populate(): MutableList<Product> {

            val db = FirebaseFirestore.getInstance()
            val mAuth = FirebaseAuth.getInstance()
            val products: MutableList<Product> = ArrayList()

            db.collection("products")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(document.get("userId") == mAuth.currentUser?.email){
                            products.add(
                                Product(document.get("name") as String,
                                    document.get("url") as String
                                )
                            )
                        }

                    }

                }
                .addOnFailureListener { exception ->
                    Log.d("Error getting documents: ", exception.toString())
                }

            return products
        }



    }
}