package com.cracky_axe.pxohlqo.keepfit.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class FoodEntity(
        @Id var Id: Long = 0,
        var name: String,
        var unit: String,
        var imageUri: String,
        var thermal: Int //kcal. as unit.
) {
}