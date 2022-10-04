package com.example.pusanrestaurant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pusanrestaurant.databinding.ItemRecyclerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class CustomAdapter:RecyclerView.Adapter<Holder>() {
    lateinit var listData :MutableList<Restaurant>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val memo = listData[position]
        holder.setMemo(memo)
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
class Holder(private val binding: ItemRecyclerBinding):RecyclerView.ViewHolder(binding.root){
    fun setMemo(restaurant: Restaurant){
        CoroutineScope(Dispatchers.Main).launch {
            binding.Title.text = restaurant.title
            binding.Address.text = restaurant.address

            val bitmap = withContext(Dispatchers.IO){
                val url = URL(restaurant.img)
                val stream = url.openStream()
                BitmapFactory.decodeStream(stream)
            }
            binding.restImg.setImageBitmap(bitmap)
        }
    }
}

