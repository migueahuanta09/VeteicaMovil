package com.example.veteica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.models.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }

    class ProductViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private var currentProduct: Product? = null

        init {
            itemView.setOnClickListener {
                currentProduct?.let { onItemClick(it) }
            }
        }

        fun bind(product: Product) {
            currentProduct = product
            val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

            tvId.text = product.id.toString()
            tvName.text = product.name
            tvStock.text = product.stock.toString()
            tvPrice.text = format.format(product.price)
        }
    }
}