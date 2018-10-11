package com.cracky_axe.pxohlqo.keyi.util

import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity_
import io.objectbox.Box

class ObjectBoxUtils {
    companion object {

        fun addFoodRecord(box: Box<FoodRecordEntity>, foodRecord: FoodRecordEntity) {

        }

        fun queryFoodRecordForDay(dayOfMonth: Int, foodRecordBox: Box<FoodRecordEntity>): List<FoodRecordEntity> {
            val builder = foodRecordBox.query()
            builder.between(FoodRecordEntity_.time, FitDateUtils.getDateTimeRange(dayOfMonth)[0], FitDateUtils.getDateTimeRange(dayOfMonth)[1])
            return builder.build().find()
        }

    }
}