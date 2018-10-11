package com.cracky_axe.pxohlqo.keyi.ui.home

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity
import com.cracky_axe.pxohlqo.keyi.ui.home.forDayFragment.ForDayRecordFragment
import com.cracky_axe.pxohlqo.keyi.util.FitDateUtils

class HomeFragmentPagerAdapter(fragmentManager: FragmentManager, context: Context, foodRecordForWeek: MutableList<MutableList<FoodRecordEntity>>?, recordFragmentCallBackHandler: ForDayRecordFragment.RecordFragmentCallBackHandler): FragmentStatePagerAdapter(fragmentManager) {
    lateinit var mContext: Context
    var dataSet: MutableList<MutableList<FoodRecordEntity>>?
    var manager: FragmentManager
    var fragTags: MutableList<String> = mutableListOf()
    var callBackHandler: ForDayRecordFragment.RecordFragmentCallBackHandler

    init {
        mContext = context
        dataSet = foodRecordForWeek
        manager = fragmentManager
        callBackHandler = recordFragmentCallBackHandler
    }

    override fun getItem(position: Int): Fragment {
        //return ForDayRecordFragment.newInstance(mContext, dataSet[position])
        return ForDayRecordFragment.newInstance(mContext, dataSet!![position], callBackHandler, position)
    }

    override fun getCount(): Int {
        return 7
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return FitDateUtils.getThisWeekDayList()[position]
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
        //return PagerAdapter.POSITION_NONE
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
    }



}