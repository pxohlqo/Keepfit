package com.cracky_axe.pxohlqo.keyi.ui.allFood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.cracky_axe.pxohlqo.keyi.App
import com.cracky_axe.pxohlqo.keyi.R
import com.cracky_axe.pxohlqo.keyi.model.FoodEntity
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity_
import com.cracky_axe.pxohlqo.keyi.util.NewPicassoEngine
import com.squareup.picasso.Picasso
import com.wang.avi.AVLoadingIndicatorView
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import io.objectbox.Box
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_all_food.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

private const val REQUEST_CODE_CHOSEN_IMAGE = 2020

class AllFoodActivity : AppCompatActivity(), AllFoodListAdapter.AllFoodItemInteractionHandler, PopupMenu.OnMenuItemClickListener, AnkoLogger {


    lateinit var foodBox: Box<FoodEntity>
    lateinit var recordBox: Box<FoodRecordEntity>
    lateinit var listAdapter: AllFoodListAdapter
    lateinit var allFoodList: MutableList<FoodEntity>
    lateinit var recyclerView: RecyclerView
    lateinit var loadingIndicator: AVLoadingIndicatorView

    var selectedItemIndex = 0
    var modifiedImageUri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO. empty page

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_food)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.drawer_menu_allFoods)

        foodBox = (application as App).boxStore.boxFor(FoodEntity::class.java)
        recordBox = (application as App).boxStore.boxFor(FoodRecordEntity::class.java)
        allFoodList = mutableListOf()

        listAdapter = AllFoodListAdapter(this, allFoodList, this)
        recyclerView = all_food_recyclerView
        recyclerView.apply {
            adapter = listAdapter
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        }
        loadingIndicator = all_food_loadingIndicator

        getAllFoodAsync()

    }

    private fun getAllFoodAsync() {
        val foodQuery = foodBox.query().build()
        Observable.create<Int> {

            it.onNext(0)
            allFoodList = foodQuery.find()

            it.onComplete()

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            info { "get food error. $it" }
                        },
                        onNext = {
                            when (it) {
                                0 -> showLoading()
                            }
                        },
                        onComplete = {
                            listAdapter.updateDataSet(allFoodList)
                            listAdapter.notifyDataSetChanged()
                            hideLoading()

                            if (allFoodList.size == 0) {
                                recyclerView.visibility = View.INVISIBLE

                            } else {
                                all_food_empty_text.visibility = View.GONE
                            }
                        }
                )
    }


    private fun showLoading() {
        recyclerView.visibility = View.INVISIBLE
        loadingIndicator.smoothToShow()
    }

    private fun hideLoading() {
        loadingIndicator.smoothToHide()
        recyclerView.visibility = View.VISIBLE
    }

    override fun onClick(view: View, position: Int) {
        //TODO("not implemented")
    }

    override fun onLongClick(view: View, position: Int) {

        showLongClickMenu(view, position)

    }

    fun showLongClickMenu(view: View, position: Int) {
        selectedItemIndex = position
        val menu = PopupMenu(this, view)
        menu.setOnMenuItemClickListener(this)
        menu.menuInflater.inflate(R.menu.item_longclick_menu, menu.menu)
        menu.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_longClick_change -> {

                alert(allFoodList[selectedItemIndex].name) {
                    var name: EditText? = null
                    var thermal: EditText? = null
                    var image: ImageView?
                    customView {
                        val rootLayout = verticalLayout {
                            image = imageView {
                                onClick {
                                    Matisse.from(this@AllFoodActivity)
                                            .choose(MimeType.allOf())
                                            .maxSelectable(1)
                                            .imageEngine(NewPicassoEngine())
                                            .forResult(REQUEST_CODE_CHOSEN_IMAGE)
                                }
                            }.lparams(width = 80, height = 80)
                            Picasso.get().load(allFoodList[selectedItemIndex].imageUri).fit().centerCrop().into(image)
                            name = editText().apply { hint = "(${allFoodList[selectedItemIndex].name})" }
                            thermal = editText()
                                    .apply {
                                        hint = "(${allFoodList[selectedItemIndex].thermal})"
                                        inputType = InputType.TYPE_CLASS_NUMBER
                                    }
                        }

                        positiveButton("save") {
                            val unmodifiedName = allFoodList[selectedItemIndex].name
                            val unmodifiedThermal = allFoodList[selectedItemIndex].thermal.toString()
                            val modifiedName = name!!.text.toString()
                            val modifiedThermal = thermal!!.text.toString()
                            var resultName = unmodifiedName
                            var resultThermal = unmodifiedThermal
                            var isModified = false

                            if (modifiedName != unmodifiedName && modifiedName != "") {
                                info { "name is changed." }
                                resultName = modifiedName
                                isModified = true
                            }
                            if (modifiedThermal != unmodifiedThermal && modifiedThermal != "") {
                                info { "thermal is changed." }
                                resultThermal = modifiedThermal
                                isModified = true
                            }
                            if (modifiedImageUri != "") {
                                isModified = true
                            }

                            if (isModified) {
                                foodBox.put(allFoodList[selectedItemIndex].apply {
                                    this.name = resultName
                                    this.thermal = resultThermal.toInt()
                                    this.imageUri = modifiedImageUri
                                })
                                getAllFoodAsync()
                            }
                        }
                        negativeButton("cancel") {
                            //toast("no")

                        }


                    }

                }.show()
                true
            }
            R.id.menu_item_longClick_delete -> {
                var foodUsageList = mutableListOf<FoodRecordEntity>()
                val recordQuery = recordBox.query().equal(FoodRecordEntity_.foodId, allFoodList[selectedItemIndex].Id).build()
                Observable.create<Int> {
                    foodUsageList = recordQuery.find()
                    it.onComplete()
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onComplete = {
                                    if (foodUsageList.size > 0) {
                                        alert("Find usage: ${foodUsageList.size}, continue?") {

                                            positiveButton("yes") {
                                                foodBox.remove(allFoodList[selectedItemIndex])
                                                recordBox.remove(foodUsageList)
                                                getAllFoodAsync()
                                            }
                                            negativeButton("no") {

                                            }
                                        }.show()

                                    } else {
                                        foodBox.remove(allFoodList[selectedItemIndex])
                                    }
                                }
                        )
                true
            }
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOSEN_IMAGE && resultCode == Activity.RESULT_OK) {
            modifiedImageUri = Matisse.obtainResult(data)[0].toString()
        }
    }
}
