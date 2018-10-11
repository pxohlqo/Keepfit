package com.cracky_axe.pxohlqo.keyi.ui.addRecord

import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cracky_axe.pxohlqo.keyi.App
import com.cracky_axe.pxohlqo.keyi.R
import com.cracky_axe.pxohlqo.keyi.model.FoodEntity
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity
import com.cracky_axe.pxohlqo.keyi.ui.addFood.AddFoodActivity
import com.cracky_axe.pxohlqo.keyi.util.FitDateUtils
import io.objectbox.BoxStore
import kotlinx.android.synthetic.main.activity_add_record.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class AddRecordActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var store: BoxStore
    lateinit var selectedFood: FoodEntity
    lateinit var foods: MutableList<FoodEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO food menu long click to update a foodItem
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)
        supportActionBar!!.title = getText(R.string.addRecord_actionbar_title)

        store = (application as App).boxStore

        foods = store.boxFor(FoodEntity::class.java).all
        val foodNames = mutableListOf<String>()
        foods.forEach { foodNames.add(it.name) }

        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, foodNames)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        add_record_spinner.adapter = arrayAdapter
        add_record_spinner.onItemSelectedListener = this

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedFood = foods[position]
    }

    fun toAddFoodActivity(view: View) {
        startActivity<AddFoodActivity>()
    }

    fun saveRecord(view: View) {
        val foodRecordEntity = FoodRecordEntity(
                foodEntity = selectedFood,
                quantity = add_record_quantity_editText.text.toString().toInt())
        store.boxFor(FoodRecordEntity::class.java).put(foodRecordEntity)
        toast("${foodRecordEntity.food.target.name} eat at ${FitDateUtils.utcTime2ReadableTime(foodRecordEntity.time)}")
        NavUtils.navigateUpFromSameTask(this)

    }
}
