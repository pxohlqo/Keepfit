package com.cracky_axe.pxohlqo.keepfit.util

import com.cracky_axe.pxohlqo.keepfit.model.FoodEntity
import com.cracky_axe.pxohlqo.keepfit.model.FoodRecordEntity

class TestUtils {
    companion object {
        fun generateDataSet(): List<FoodRecordEntity> {

            return listOf(
                    FoodRecordEntity(
                            foodEntity = generateFood(),
                            quantity = 1
                    ),
                    FoodRecordEntity(
                            foodEntity = generateFood(),
                            quantity = 1
                    )
            )

        }

        fun generateFood(): FoodEntity {
            return FoodEntity(
                    name = "Food 0",
                    unit = "",
                    imageUri = "",
                    thermal = 125)
        }
    }
}