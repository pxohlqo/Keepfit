package com.cracky_axe.pxohlqo.keepfit.ui.home

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.cracky_axe.pxohlqo.keepfit.App
import com.cracky_axe.pxohlqo.keepfit.R
import com.cracky_axe.pxohlqo.keepfit.model.FoodRecordEntity
import com.cracky_axe.pxohlqo.keepfit.model.FoodRecordEntity_
import com.cracky_axe.pxohlqo.keepfit.ui.addRecord.AddRecordActivity
import com.cracky_axe.pxohlqo.keepfit.util.FitDateUtils
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_home.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class HomeActivity : AppCompatActivity(), AnkoLogger, ForDayRecordFragment.RecordFragmentCallBackHandler {

    lateinit var foodRecordForWeek : MutableList<MutableList<FoodRecordEntity>>

    lateinit var store: BoxStore

    lateinit var mFragmentManager: FragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        info { "onCreate()" }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        store = (application as App).boxStore

        foodRecordForWeek = mutableListOf()
        repeat(7) {
            foodRecordForWeek.add(it, mutableListOf<FoodRecordEntity>())
        }

        mFragmentManager = this.supportFragmentManager

        home_viewPager.adapter = HomeFragmentPagerAdapter(mFragmentManager, this, foodRecordForWeek, this)
        home_viewPager.currentItem =FitDateUtils.getTodayOfWeek()

        home_tabLayout.setupWithViewPager(home_viewPager)
    }

    override fun onResume() {
        super.onResume()
        getFoodRecordAsync()
    }

    fun toAddRecordActivity(view: View) {
        startActivity<AddRecordActivity>()
    }


    fun getFoodRecord(): MutableList<MutableList<FoodRecordEntity>> {
        val weekTimeRange = FitDateUtils.getWeekTimeRange()
        var foodRecord = mutableListOf<MutableList<FoodRecordEntity>>()
        var queryRecord = store.boxFor(FoodRecordEntity::class.java).query()
                /*.between(FoodRecordEntity_.time, weekTimeRange[0], weekTimeRange[1])
                .build()*/

        repeat(7) {
            queryRecord.between(FoodRecordEntity_.time, weekTimeRange[0], weekTimeRange[0] + it * FitDateUtils.DAY_TIME_RANGE)

            foodRecord.add(it, queryRecord.build().find())
        }

        return foodRecord
    }

    /**
     * @it day# of the week, from 0 ~ 6
     */
    fun getFoodRecordOnce(it: Int): MutableList<FoodRecordEntity> {
        val weekTimeRange = FitDateUtils.getWeekTimeRange()

        val queryRecord = store.boxFor(FoodRecordEntity::class.java).query()

        queryRecord.between(FoodRecordEntity_.time, weekTimeRange[0] + (it) * FitDateUtils.DAY_TIME_RANGE, weekTimeRange[0] + (it + 1) * FitDateUtils.DAY_TIME_RANGE)
        info { "${weekTimeRange[0]}, ${it}" }
        info { "${weekTimeRange[0] + (it) * FitDateUtils.DAY_TIME_RANGE}, ${weekTimeRange[0] + (it) * FitDateUtils.DAY_TIME_RANGE}" }

        return queryRecord.build().find()
    }

    fun getFoodRecordAsync() {
        info { "start getFoodRecordAsync() at Thread: ${Thread.currentThread().name}" }
        Observable.create<Int> {

            info { "checkpoint 1" }

            //foodRecordForWeek = mutableListOf<MutableList<FoodRecordEntity>>()

            val emitter = it

            emitter.onNext(1)

            repeat(7) {

                if (it > FitDateUtils.getTodayOfWeek()) {
                    foodRecordForWeek[it] = mutableListOf()

                } else {
                    val tempList = getFoodRecordOnce(it)
                    foodRecordForWeek[it] = tempList
                    info { "got ${tempList.size} records." }

                }
            }

            info { "checkpoint 2" }

            emitter.onComplete()

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {

                            when (it) {
                                1 -> {
                                    mFragmentManager.fragments.forEach {
                                        (it as ForDayRecordFragment).showLoading()
                                    }
                                }
                            }

                        },
                        onError = {info { "onError. $it" }},
                        onComplete = {
                            info { "query complected!" }

                            //home_viewPager.adapter = HomeFragmentPagerAdapter(supportFragmentManager, this, foodRecordForWeek)
                            if (home_viewPager.adapter != null) {
                                //info { "adapter is not null." }

                                mFragmentManager.fragments.forEach {
                                    (it as ForDayRecordFragment).apply {
                                        hideLoading()
                                        refreshList()
                                    }
                                }

                                //refreshList() does not work. So I have to call this method below
                                home_viewPager.adapter!!.notifyDataSetChanged()

                            } else {
                                info { "adapter is null." }
                            }
                        }
                )
    }

    override fun removeRecordItem(item: FoodRecordEntity) {
        store.boxFor(FoodRecordEntity::class.java).remove(item)
        toast("record deleted.")
        home_viewPager.adapter!!.notifyDataSetChanged()
    }

    override fun changeRecordItem(item: FoodRecordEntity) {
        toast("Change record item. Not implement yet.")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home_query -> {
                getFoodRecordAsync()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = this.menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }
}
