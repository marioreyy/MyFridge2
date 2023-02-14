package com.example.myfridge.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfridge.R
import com.example.myfridge.model.Product


class ProductViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val productName = view.findViewById<TextView>(R.id.tvProductName)
    val productImage = view.findViewById<ImageView>(R.id.ivProduct)

    fun render(product: Product) {
        productName.text = product.productName
    }

}