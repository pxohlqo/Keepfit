package com.cracky_axe.pxohlqo.keyi.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.cracky_axe.pxohlqo.keyi.App
import com.cracky_axe.pxohlqo.keyi.R
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity_
import com.cracky_axe.pxohlqo.keyi.ui.about.AboutActivity
import com.cracky_axe.pxohlqo.keyi.ui.addRecord.AddRecordActivity
import com.cracky_axe.pxohlqo.keyi.ui.allFood.AllFoodActivity
import com.cracky_axe.pxohlqo.keyi.ui.allRecord.AllRecordActivity
import com.cracky_axe.pxohlqo.keyi.ui.home.forDayFragment.ForDayRecordFragment
import com.cracky_axe.pxohlqo.keyi.util.FitDateUtils
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class HomeActivity : AppCompatActivity(), AnkoLogger, ForDayRecordFragment.RecordFragmentCallBackHandler {

    lateinit var foodRecordForWeek : MutableList<MutableList<FoodRecordEntity>>

    lateinit var store: BoxStore

    lateinit var mFragmentManager: FragmentManager

    lateinit var drawerToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO polish drawer
        info { "onCreate()" }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(home_toolbar)
        supportActionBar!!.title = getText(R.string.home_actionBar_title)
        store = (application as App).boxStore

        foodRecordForWeek = mutableListOf()
        repeat(7) {
            foodRecordForWeek.add(it, mutableListOf<FoodRecordEntity>())
        }

        mFragmentManager = this.supportFragmentManager

        home_viewPager.adapter = HomeFragmentPagerAdapter(mFragmentManager, this, foodRecordForWeek, this)
        home_viewPager.currentItem =FitDateUtils.getTodayOfWeek()

        home_tabLayout.setupWithViewPager(home_viewPager)

        nav_view.setNavigationItemSelectedListener { item: MenuItem ->
            item.isChecked = true
            //drawerLayout_home.closeDrawers()
            when (item.itemId) {
                R.id.nav_allFoods -> startActivity<AllFoodActivity>()
                R.id.nav_allRecords -> startActivity<AllRecordActivity>()
                R.id.nav_aboutInfo -> startActivity<AboutActivity>()
            }

            true
        }

        drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout_home, home_toolbar, R.string.drawer_open_description, R.string.drawer_closed_description) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu()
            }
        }

        drawerLayout_home.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        drawerToggle.isDrawerIndicatorEnabled = true

        info { "fragments number: ${mFragmentManager.fragments.size} at onCreate()" }
        info { "HomeActivity created." }
    }

    override fun onResume() {
        super.onResume()
        getFoodRecordAsync()
        info { "fragments number: ${mFragmentManager.fragments.size} at onResume()" }
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
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

        return queryRecord.build().find()
    }

    /**
     * @return a list of which fragment data has changed
     */
    fun getFoodRecordAsync(): MutableList<Int> {
        var dataChangedFragmentIndexes = mutableListOf<Int>()

        Observable.create<Int> { observableEmitter ->
            //show loading indicator first
            observableEmitter.onNext(1)

            repeat(7) {

                if (it > FitDateUtils.getTodayOfWeek()) {
                    foodRecordForWeek[it] = mutableListOf()

                } else {
                    val tempList = getFoodRecordOnce(it)
                    foodRecordForWeek[it] = tempList
                    //info { "got ${tempList.size} records." }

                    if (tempList.size > 0) {
                        dataChangedFragmentIndexes.add(it)
                    }

                }
            }

            observableEmitter.onComplete()

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { it ->

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
                            //info { "query complected!" }

                            //home_viewPager.adapter = HomeFragmentPagerAdapter(supportFragmentManager, this, foodRecordForWeek)
                            if (home_viewPager.adapter != null) {
                                //info { "adapter is not null." }

                                mFragmentManager.fragments.forEach {
                                    (it as ForDayRecordFragment).apply {
                                        hideLoading()
                                    }
                                }

                            } else {
                                //info { "adapter is null." }
                            }
                        }
                )

        return dataChangedFragmentIndexes
    }

    override fun removeRecordItem(dataIndex: Int) {
        store.boxFor(FoodRecordEntity::class.java).remove(foodRecordForWeek[home_viewPager.currentItem][dataIndex])
        foodRecordForWeek[home_viewPager.currentItem].removeAt(dataIndex)
        toast("record deleted.")
        info { "${foodRecordForWeek[home_viewPager.currentItem].size}" }
        home_viewPager.adapter!!.notifyDataSetChanged()
    }

    override fun changeRecordItem(dataIndex: Int) {
        toast("Change record item. Not implement yet.")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        when (item.itemId) {
            R.id.menu_home_refresh -> {
                val changedList = getFoodRecordAsync()
                notifyFragmentsDataSetChanged(changedList)

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

    fun notifyFragmentsDataSetChanged(dataChangedFragmentIndex: MutableList<Int> = mutableListOf(0, 1, 2, 3, 4, 5, 6)) {
        dataChangedFragmentIndex.forEach {
            (mFragmentManager.fragments[it] as ForDayRecordFragment).refreshList(foodRecordForWeek[it])
        }
    }
}
