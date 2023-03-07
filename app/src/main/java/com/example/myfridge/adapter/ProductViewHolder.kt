package com.example.myfridge.adapter

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfridge.databinding.ItemProductBinding
import com.example.myfridge.model.Product


class ProductViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val binding = ItemProductBinding.bind(view)



    fun render(
        productModel: Product,
        onClickListener: (Product) -> Unit,
        onClickDelete: (Int) -> Unit
    ) {
        binding.tvProductName.text = productModel.productName
        binding.btnDelete.setOnClickListener{onClickDelete(adapterPosition)}

                Glide.with(binding.ivProduct.context).load(productModel.image).into(binding.ivProduct)


        itemView.setOnClickListener{
            onClickListener(productModel)
        }
    }

}