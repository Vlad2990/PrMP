package com.example.app.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.domain.entities.HistoryItem

class HistoryAdapter(
    private val items: List<HistoryItem>,
    private val onItemClick: (HistoryItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view
        val expression: TextView = view.findViewById(R.id.tvExpression)
        val result: TextView = view.findViewById(R.id.tvResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.expression.text = item.expression
        holder.result.text = "= ${item.result}"

        holder.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = items.size
}