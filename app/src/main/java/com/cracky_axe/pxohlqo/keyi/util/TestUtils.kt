package com.cracky_axe.pxohlqo.keyi.util

import com.cracky_axe.pxohlqo.keyi.model.FoodEntity
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity

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
                    imageUri = "",
                    thermal = 125)
        }
    }
}