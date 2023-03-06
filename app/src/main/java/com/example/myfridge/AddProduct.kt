package com.example.myfridge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.saveButton
import kotlinx.android.synthetic.main.activity_add_product.*

class AddProduct : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setup()

    }
    fun isValidInput(editText: EditText): Boolean {
        return editText.text?.toString()?.isNotBlank() == true
    }

    private fun setup(){
        var productId: Int? = null
        db.collection("products").get().addOnCompleteListener {
            productId = it.result.size()+1
        }
        urlEditText.addTextChangedListener{
            Glide.with(this)
                .load(urlEditText.text.toString())
                .into(imageView2)
        }
        saveButton.setOnClickListener {
            if (isValidInput(productNameEditText) && isValidInput(productPriceEditText) && isValidInput(
                    urlEditText
                )
            ) {
                if (productId != null) {
                    db.collection("products").document(productId.toString()).set(
                        hashMapOf(
                            "name" to productNameEditText.text.toString(),
                            "price" to productPriceEditText.text.toString(),
                            "url" to urlEditText.text.toString(),
                            "userId" to mAuth.currentUser?.email.toString()
                        )
                    )


                    finish()
                }
            } else {
                // Mostrar un mensaje de error indicando que se deben completar todos los campos
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = "Todos los campos son obligatorios"
            }

            }

        }

    }

