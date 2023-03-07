package com.example.myfridge

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_home.saveButton
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.ByteArrayOutputStream
import java.util.*

class AddProduct : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()


    private fun selectImage() {
        val options = arrayOf<CharSequence>("Tomar foto", "Seleccionar de galería", "Cancelar")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar foto")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Tomar foto" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, 1)
                }
                options[item] == "Seleccionar de galería" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, 2)
                }
                options[item] == "Cancelar" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }
    private var selectedBitmap: Bitmap? = null // variable para almacenar el bitmap seleccionado

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> { // Si la imagen es de la cámara
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    selectedBitmap = imageBitmap
                    urlEditText.visibility = View.INVISIBLE
                    urlEditText.setText("a")
                    imageView2.setImageBitmap(imageBitmap)

                }
                2 -> { // Si la imagen es de la galería
                    val selectedImage = data?.data
                    imageView2.setImageURI(selectedImage)
                    urlEditText.visibility = View.INVISIBLE
                    urlEditText.setText("")
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setup()

        val btnSelectImage = findViewById<Button>(R.id.btnSelectImage)

        btnSelectImage.setOnClickListener{ selectImage() }

    }
    fun isValidInput(editText: EditText): Boolean {
        return editText.text?.toString()?.isNotBlank() == true
    }

    private fun setup(){
        var productId: Int? = null
        db.collection("products").get().addOnCompleteListener {
            productId = it.result.size()+1
        }
        if(!urlEditText.text.contains("android.graphics")){
            urlEditText.addTextChangedListener{
                Glide.with(this)
                    .load(urlEditText.text.toString())
                    .into(imageView2)
            }
        }
        else if(urlEditText.text.contains("android.graphics")){
            urlEditText.addTextChangedListener{
                val imageBitmap = urlEditText.text as Bitmap
                imageView2.setImageBitmap(imageBitmap)
            }
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

                    uploadBitmapToFirestore(productId)
                    finish()
                }
            } else {
                // Mostrar un mensaje de error indicando que se deben completar todos los campos
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = "Todos los campos son obligatorios"
            }

            }

        }

    private fun uploadBitmapToFirestore(productId: Int?) {
        selectedBitmap?.let { bitmap ->
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
            val uploadTask = storageRef.putBytes(data)
            uploadTask.addOnSuccessListener { taskSnapshot ->

                storageRef.downloadUrl.addOnSuccessListener { uri ->

                    val imageUrl = uri.toString()


                    val productRef = FirebaseFirestore.getInstance().collection("products").document(productId.toString())
                    productRef.update("url", imageUrl).addOnSuccessListener {

                    }.addOnFailureListener { exception ->

                        exception.printStackTrace()
                    }
                }
            }.addOnFailureListener { exception ->

                exception.printStackTrace()
            }
        }
    }

}

