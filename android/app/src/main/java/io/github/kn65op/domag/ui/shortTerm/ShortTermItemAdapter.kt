package io.github.kn65op.domag.ui.shortTerm

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.database.AppDatabase
import io.github.kn65op.domag.database.entities.Item
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ShortTermItemAdapter(
    lifecycleOwner: LifecycleOwner,
    val items: LiveData<List<Item>>?,
    val db: AppDatabase?,
    val activity: Activity?
) :
    RecyclerView.Adapter<ShortTermItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.short_term_item_row_name)
        val amount: TextView = view.findViewById(R.id.short_term_item_row_amount)
        val unit: TextView = view.findViewById(R.id.short_term_item_row_unit)
        val date: TextView = view.findViewById(R.id.short_term_item_row_date)
    }

    init {
        items?.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        val size = items?.value?.size ?: 0
        Log.i(LOG_TAG, "Found $size items")
        return size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.short_term_item_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        items?.value?.get(position)?.let { item ->
            Log.i(LOG_TAG, "Bind $position")
            GlobalScope.launch {
                Log.i(LOG_TAG, "Start routine")
                val categoryDao = db?.categoryDao()
                val depotDao = db?.depotDao()
                val name = if (item.description.isNullOrEmpty())
                    "${categoryDao?.getCategoryName(item.categoryId)} in ${depotDao?.getDepotName(
                        item.depotId
                    )}"
                else
                    "${item.description} (${categoryDao?.getCategoryName(item.categoryId)}) ${activity?.applicationContext?.getString(R.string.inside)} ${depotDao?.getDepotName(
                        item.depotId
                    )}"
                val unit = categoryDao?.getCategoryUnit(item.categoryId)
                activity?.runOnUiThread {
                    holder.amount.text = item.amount.toString()
                    holder.name.text = name
                    holder.unit.text = unit
                    holder.date.text = item.bestBefore?.format(timeFormatter)
                }
            }
            holder.itemView.setOnClickListener {
                Log.i(LOG_TAG, "Navigate to item ${item.uid}")
                val id = item.uid ?: 0
                val action = ShortTermFragmentDirections.actionNavShortTermToFragmentEditItem(id)
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    companion object {
        private val timeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        private const val LOG_TAG = "ShortTermItemAdapter"
    }
}