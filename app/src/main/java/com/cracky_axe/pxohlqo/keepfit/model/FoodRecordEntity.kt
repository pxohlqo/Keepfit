package com.cracky_axe.pxohlqo.keepfit.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class FoodRecordEntity(
        @Id var id: Long = 0,
        var time: Long = 0
){
    lateinit var food: ToOne<FoodEntity>
    var quantity: Int = 0

    constructor(foodEntity: FoodEntity, quantity: Int) : this(){
        food.target = foodEntity
        this.quantity = quantity
        time = System.currentTimeMillis()
    }


}