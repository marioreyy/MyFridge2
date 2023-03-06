package com.example.myfridge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.saveButton
import kotlinx.android.synthetic.main.activity_image_details.*

class ProductDetails : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_details)

        val bundle = intent.extras
        val productId = bundle?.getString("productId")
        getInitial(productId ?: "")
        setup(productId ?: "")


    }

    private fun setup(productId:String){



        saveButton.setOnClickListener{
            if(productId.isNotEmpty()){
                db.collection("products").document(productId).set(
                    hashMapOf("name" to productNameEditText.text.toString(),
                        "price" to productPriceEditText.text.toString(),
                        "url" to urlEditText.text.toString())
                )
                onBackPressed()
            }
            }



    }

    private fun getInitial(productId: String){

        val imageview: ImageView = findViewById(R.id.imageView2)

        db.collection("products").document(productId).get().addOnSuccessListener {
            productNameEditText.setText(it.get("name") as String?)
            productPriceEditText.setText(it.get("price") as String?)
            urlEditText.setText(it.get("url") as String?)
            Glide.with(applicationContext).load(it.get("url")).into(imageview)
        }





    }
}