package com.cracky_axe.pxohlqo.keyi.ui.allRecord

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cracky_axe.pxohlqo.keyi.R
import com.cracky_axe.pxohlqo.keyi.model.FoodRecordEntity
import com.cracky_axe.pxohlqo.keyi.util.FitDateUtils
import com.squareup.picasso.Picasso
import com.wang.avi.AVLoadingIndicatorView
import kotlinx.android.synthetic.main.item_all_record.view.*
import kotlinx.android.synthetic.main.item_all_record_footer.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.*

private const val VIEW_TYPE_RECORD = 200
private const val VIEW_TYPE_FOOTER = 201
const val BOTTOM_LOADING = 301
const val BOTTOM_NO_MORE = 300
const val BOTTOM_LOAD_ERROR = 302
class AllRecordListAdapter(val context: Context, val dataSet: MutableList<FoodRecordEntity>, val interactionHandler: AllRecordsItemInteractionHandler): RecyclerView.Adapter<AllRecordListAdapter.AllRecordListViewHolder>(), AnkoLogger {

    var bottomState = 0
    lateinit var bottomTextView: TextView
    lateinit var bottomAVLoadingIndicatorView: AVLoadingIndicatorView

    interface AllRecordsItemInteractionHandler {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRecordListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {

            VIEW_TYPE_FOOTER -> {
                val itemView = inflater.inflate(R.layout.item_all_record_footer, parent, false)
                return AllRecordListViewHolder(itemView)
            }

            else -> {
                val itemView = inflater.inflate(R.layout.item_all_record, parent, false)
                return AllRecordListViewHolder(itemView)
            }
        }

    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    override fun onBindViewHolder(holder: AllRecordListViewHolder, position: Int) {
        //info { "${position}: ${holder.itemViewType}" }
        when (holder.itemViewType) {
            VIEW_TYPE_RECORD -> {
                holder.bindAllRecord(dataSet[position])
            }

            else -> {
                holder.bindFooter()
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            VIEW_TYPE_FOOTER
        } else {
            VIEW_TYPE_RECORD
        }
    }

    inner class AllRecordListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindAllRecord(data: FoodRecordEntity) {
            Picasso.get().load(data.food.target.imageUri).fit().centerCrop().into(itemView.item_all_record_imageView)
            itemView.item_all_record_name.text = data.food.target.name
            itemView.item_all_record_date.text = Date(data.time).toString()
            itemView.item_all_record_date.text = FitDateUtils.easyTimeMillis2String(data.time)
            itemView.item_all_record_rootLayout.setOnLongClickListener{
                interactionHandler.onItemLongClick(it, adapterPosition)
                true
            }
        }

        fun bindFooter() {
            bottomTextView = itemView.item_footer_textView
            bottomAVLoadingIndicatorView = itemView.item_footer_loadingIndicator
        }
    }

    fun noMore() {
        bottomState = BOTTOM_NO_MORE
        bottomTextView.text = context.resources.getString(R.string.all_record_footer_noMore)
        bottomAVLoadingIndicatorView.smoothToHide()
    }
}