package com.cracky_axe.pxohlqo.keepfit.ui.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cracky_axe.pxohlqo.keepfit.R
import com.cracky_axe.pxohlqo.keepfit.model.FoodRecordEntity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_forday_foodrecord.view.*

class ForDayRecordAdapter(val dataSet: List<FoodRecordEntity>, val longClickListener: ForDayRecordAdapter.OnItemLongClickListener): RecyclerView.Adapter<ForDayRecordAdapter.ForDayRecordViewHolder>() {

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForDayRecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forday_foodrecord, parent, false)
        return ForDayRecordViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
        //return 5
    }

    override fun onBindViewHolder(holder: ForDayRecordViewHolder, position: Int) {
        holder.bindFoodRecord(dataSet[position])
    }

    inner class ForDayRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindFoodRecord(foodRecordEntity: FoodRecordEntity) {

            Picasso.get().load(foodRecordEntity.food.target.imageUri).centerCrop().fit().into(itemView.item_foodRecord_imageView)
            itemView.item_foodRecord_name.text = foodRecordEntity.food.target.name
            itemView.item_foodRecord_quantity.text = "${foodRecordEntity.quantity} x"
            itemView.item_foorRecord_thermal.text = "${foodRecordEntity.food.target.thermal} kcal."
            itemView.item_foodRecord_rootLayout.setOnLongClickListener {
                longClickListener.onItemLongClick(it, adapterPosition)
                false
            }
        }
    }


}