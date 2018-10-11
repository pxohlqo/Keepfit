package com.cracky_axe.pxohlqo.keyi.ui.addFood

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.cracky_axe.pxohlqo.keyi.App
import com.cracky_axe.pxohlqo.keyi.R
import com.cracky_axe.pxohlqo.keyi.model.FoodEntity
import com.cracky_axe.pxohlqo.keyi.util.NewPicassoEngine
import com.squareup.picasso.Picasso
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_add_food.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

private const val REQUEST_CODE_CHOSEN_IMAGE = 2020

@RuntimePermissions
class AddFoodActivity : AppCompatActivity(), AnkoLogger {

    lateinit var foodBox: Box<FoodEntity>

    var imageUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        supportActionBar!!.title = getText(R.string.addFood_actionbar_title)
        add_food_imageView.setOnClickListener { toImageSelectActivityWithPermissionCheck() }

        foodBox = (application as App).boxStore.boxFor(FoodEntity::class.java)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun toImageSelectActivity() {

        Matisse.from(this)
                .choose(MimeType.allOf())
                .maxSelectable(1)
                .imageEngine(NewPicassoEngine())
                .forResult(REQUEST_CODE_CHOSEN_IMAGE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOSEN_IMAGE && resultCode == Activity.RESULT_OK) {
            info { "Uri: " + Matisse.obtainResult(data) + "\n" }
            imageUri = Matisse.obtainResult(data)[0].toString()
            Picasso.get().load(imageUri).into(add_food_imageView)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    fun addFoodItem(view: View) {
        val food = FoodEntity(
                name = add_food_foodName_editText.text.toString(),
                imageUri = imageUri,
                thermal = add_food_foodThermal_editText.text.toString().toInt()
        )

        foodBox.put(food)
        toast("Food ${food.name} added.")
        NavUtils.navigateUpFromSameTask(this)
    }
}
