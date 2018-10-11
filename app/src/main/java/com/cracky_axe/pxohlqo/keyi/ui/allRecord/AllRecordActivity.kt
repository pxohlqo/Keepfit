package com.cracky_axe.pxohlqo.keyi.ui.allRecord

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.cracky_axe.pxohlqo.keyi.App
import com.cracky_axe.pxohlqo.keyi.R
import com.cracky_axe.pxohlqo.keyi.model.FoodEntity
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity
import io.objectbox.Box
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_all_record.*
import org.jetbrains.anko.*

const val LOADING_LIMIT_PER_PAGE: Long = 13
const val LOADING_STATE_HAS_MORE = 300
const val LOADING_STATE_NO_MORE = 301
class AllRecordActivity : AppCompatActivity(), AnkoLogger,
        AllRecordListAdapter.AllRecordsItemInteractionHandler{

    var allRecordsDataSet: MutableList<FoodRecordEntity> = mutableListOf()
    var loadOffset: Long = 0
    lateinit var recordAdapter: AllRecordListAdapter
    lateinit var recordsBox: Box<FoodRecordEntity>
    lateinit var foodsBox: Box<FoodEntity>
    var loadingState = LOADING_STATE_HAS_MORE

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO. Use dialog to update records. ~~
        //TODO. empty page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_record)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.drawer_menu_allRecords)

        recordsBox = (application as App).boxStore.boxFor(FoodRecordEntity::class.java)
        foodsBox = (application as App).boxStore.boxFor(FoodEntity::class.java)

        recordAdapter = AllRecordListAdapter(this, allRecordsDataSet, this)
        all_record_recyclerView.adapter = recordAdapter
        all_record_recyclerView.layoutManager = LinearLayoutManager(this)
        all_record_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if ((recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == recyclerView.adapter.itemCount - 1) {
                    info("Scroll to bottom.")
                    if (loadingState == LOADING_STATE_HAS_MORE) {
                        loadMoreRecordsAsync()
                    }
                }
            }
        })
    }

    fun loadMoreRecordsAsync() {
        val allRecordsQuery = recordsBox.query().build()
        Observable.create<Int> { observableEmitter ->
            info { "Load more." }
            val tempList = allRecordsQuery.find(loadOffset, LOADING_LIMIT_PER_PAGE)
            tempList.forEach { allRecordsDataSet.add(it) }
            info { "${tempList.size} loaded." }
            loadingState = if (tempList.isEmpty()) {
                LOADING_STATE_NO_MORE
            } else {
                LOADING_STATE_HAS_MORE
            }
            observableEmitter.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            info { "onError $it" }
                        },
                        onComplete = {
                            when (loadingState) {
                                LOADING_STATE_NO_MORE -> {
                                    recordAdapter.noMore()
                                }

                                LOADING_STATE_HAS_MORE -> {
                                    loadOffset += LOADING_LIMIT_PER_PAGE
                                }
                            }
                            if (allRecordsDataSet.size == 0) {
                                all_record_recyclerView.visibility = View.INVISIBLE

                            } else {
                                all_record_empty_text.visibility = View.GONE
                            }
                            recordAdapter.notifyDataSetChanged()
                        }
                )


    }

    fun removeRecord(record: FoodRecordEntity) {
        recordsBox.remove(record)
        allRecordsDataSet.remove(record)
        recordAdapter.notifyDataSetChanged()
    }

    fun changeRecord(record: FoodRecordEntity, position: Int) {
        var foods = mutableListOf<String>()
        var quantity: EditText? = null
        var foodsSpinner: Spinner? = null
        foodsBox.all.forEach { foods.add(it.name) }
        alert {
            customView {
                verticalLayout {
                    linearLayout {
                        textView("food: ")
                        foodsSpinner = spinner {
                            adapter = ArrayAdapter<String>(
                                    this@AllRecordActivity,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    foods
                            )
                        }
                    }
                    linearLayout {
                        textView("quantities: ")
                        quantity = editText().apply {
                            hint = "(${record.quantity})"
                            inputType = InputType.TYPE_CLASS_NUMBER
                        }
                    }
                }

                positiveButton("save") {
                    val unmodifiedFood = record.food.target
                    val modifiedFood = foodsBox[foodsSpinner!!.selectedItemPosition.toLong()]
                    val unmodifiedQuantity = record.quantity.toString()
                    val modifiedQuantity = quantity!!.text.toString()
                    var resultFood = unmodifiedFood
                    var resultQuantity = unmodifiedQuantity
                    var isModified = false

                    if (modifiedFood != unmodifiedFood) {
                        resultFood = modifiedFood
                        isModified = true
                    }

                    if (modifiedQuantity != unmodifiedQuantity && modifiedQuantity != "") {
                        resultQuantity = modifiedQuantity
                        isModified = true
                    }

                    if (isModified) {
                        val resultRecord = record.apply {
                            this.food.target = resultFood
                            this.quantity = resultQuantity.toInt()
                        }
                        recordsBox.put(resultRecord)
                        allRecordsDataSet[position] = resultRecord
                        recordAdapter.notifyDataSetChanged()
                    }

                }
            }
        }.show()
    }

    override fun onItemClick(view: View, position: Int) {
        TODO("not implemented")
    }

    override fun onItemLongClick(view: View, position: Int) {
        PopupMenu(this, view).apply {
            inflate(R.menu.item_longclick_menu)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_item_longClick_delete -> {
                        removeRecord(allRecordsDataSet[position])
                        true
                    }

                    R.id.menu_item_longClick_change -> {
                        changeRecord(allRecordsDataSet[position], position)
                        true
                    }

                    else -> false
                }
            }

            show()
        }
    }

}
