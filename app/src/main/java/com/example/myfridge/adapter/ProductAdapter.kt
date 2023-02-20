package com.example.myfridge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfridge.ProductProvider.Companion.products
import com.example.myfridge.R
import com.example.myfridge.model.Product

class ProductAdapter(private val productsList:List<Product>,
                     private val onClickListener:(Product) -> Unit,
                    private val onClickDelete:(Int) -> Unit
): RecyclerView.Adapter<ProductViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(layoutInflater.inflate(R.layout.item_product, parent, false))

    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = productsList[position]
        holder.render(item, onClickListener, onClickDelete)
    }

    override fun getItemCount(): Int = products.size


}