package com.example.myfridge.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfridge.R
import com.example.myfridge.databinding.ItemProductBinding
import com.example.myfridge.model.Product


class ProductViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val binding = ItemProductBinding.bind(view)



    fun render(productModel: Product, onClickListener:(Product) -> Unit) {
        binding.tvProductName.text = productModel.productName
        Glide.with(binding.ivProduct.context).load(productModel.image).into(binding.ivProduct)
        itemView.setOnClickListener{
            onClickListener(productModel)
        }
    }

}