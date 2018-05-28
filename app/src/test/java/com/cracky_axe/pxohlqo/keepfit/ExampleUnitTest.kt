package com.cracky_axe.pxohlqo.keepfit

import com.cracky_axe.pxohlqo.keepfit.util.FitDateUtils
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    /*@Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }*/

    /*@Test
    fun testDayOfWeek() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val calendar = Calendar.getInstance()
        calendar.time = date

        var simpleDateFormat = SimpleDateFormat("d")
        System.out.println(simpleDateFormat.format(date))
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK))

        var today = calendar.get(Calendar.DAY_OF_MONTH)
        var mon2Today = calendar.get(Calendar.DAY_OF_WEEK) - 1
        var mon = today - mon2Today
        var week: String = ""
        var stringBuilder = StringBuilder()
        repeat(7) {
            stringBuilder.append(mon + it).append(" ")
        }
        System.out.println(stringBuilder)
    }*/

    /*@Test
    fun testRxJava() {
        val observable = Observable.create(ObservableOnSubscribe<Int> {
            println("Observable thread is: ${Thread.currentThread().name}")
            it.onNext(1)
        })

        val consumer = Consumer<Int> {
            println("Observer thread is: ${Thread.currentThread().name}")
        }

        println("test")

        observable.subscribeOn(Schedulers.trampoline()).observeOn(Schedulers.io())
                .subscribe(consumer)
    }*/

    /*@Test
    fun testDateUtils() {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DATE)
        val mon2Today = calendar.get(Calendar.DAY_OF_WEEK) - 2
        val monInMillis = FitDateUtils.getDateTimeRange(today)[0] - mon2Today * FitDateUtils.DAY_TIME_RANGE
        println(monInMillis)
    }*/

    @Test
    fun testDateUtils2() {

        val calendar = Calendar.getInstance()

        var mon2Today = calendar.get(Calendar.DAY_OF_WEEK) - 2
        println(FitDateUtils.getWeekTimeRange())
    }




}
