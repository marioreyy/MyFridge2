package com.example.myfridge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.saveButton
import kotlinx.android.synthetic.main.activity_image_details.*

class AddProduct : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setup()

    }

    private fun setup(){
        var productId: Int? = null
        db.collection("products").get().addOnCompleteListener {
            productId = it.result.size()+1
        }

        saveButton.setOnClickListener{
            if(productId != null){
                db.collection("products").document(productId.toString()).set(
                    hashMapOf("name" to productNameEditText.text.toString(),
                        "price" to productPriceEditText.text.toString(),
                        "url" to urlEditText.text.toString(),
                        "userId" to mAuth.currentUser?.email.toString())
                )
                val intent = Intent(this, Index::class.java)
                startActivity(intent)
            }
        }
    }
}
