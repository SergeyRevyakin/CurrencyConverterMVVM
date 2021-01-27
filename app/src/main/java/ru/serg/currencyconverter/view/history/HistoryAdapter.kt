package ru.serg.currencyconverter.view.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_history.view.*
import ru.serg.currencyconverter.R
import ru.serg.currencyconverter.room.Operation

class HistoryAdapter(
    private val operationList: List<Operation>
) : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.bind(operationList[position])
    }

    override fun getItemCount(): Int = operationList.size

    class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(operation: Operation) = with(itemView) {
            txtFromName.text = operation.fromName
            txtToName.text = operation.toName
            txtFromAmount.text = operation.fromAmount.toString()
            txtToAmount.text = operation.toAmount.toString()
        }
    }
}