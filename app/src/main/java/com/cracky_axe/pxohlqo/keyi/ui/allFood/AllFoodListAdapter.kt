package com.cracky_axe.pxohlqo.keyi.ui.allFood

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cracky_axe.pxohlqo.keyi.R
import com.cracky_axe.pxohlqo.keyi.model.FoodEntity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_all_food.view.*
import org.jetbrains.anko.AnkoLogger

class AllFoodListAdapter(val context: Context,
                         var dataSet: MutableList<FoodEntity>,
                         val itemInteractionHandler: AllFoodItemInteractionHandler
): RecyclerView.Adapter<AllFoodListAdapter.AllFoodListViewHolder>(), AnkoLogger {

    interface AllFoodItemInteractionHandler {
        fun onClick(view: View, position: Int)
        fun onLongClick(view: View, position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllFoodListViewHolder {
        return AllFoodListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_all_food, parent, false))
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: AllFoodListViewHolder, position: Int) {
        holder.bindAllFood(dataSet[position])
    }

    fun updateDataSet(newData: MutableList<FoodEntity>) {
        dataSet = newData
        notifyDataSetChanged()
    }

    inner class AllFoodListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindAllFood(data: FoodEntity) {

            Picasso.get().load(data.imageUri).fit().centerCrop().into(itemView.item_all_food_imageView)
            itemView.item_all_food_foodName_textView.text = data.name
            itemView.item_all_food_foodThermal_textView.text = "${data.thermal} kcal."
            itemView.item_all_food_rootLayout.setOnLongClickListener{
                itemInteractionHandler.onLongClick(it, adapterPosition)
                true
            }
        }
    }
}