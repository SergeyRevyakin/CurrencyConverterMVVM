package ru.serg.currencyconverter.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.InternalCoroutinesApi
import ru.serg.currencyconverter.R
import ru.serg.currencyconverter.room.Operation
import ru.serg.currencyconverter.view.MainActivity

@InternalCoroutinesApi
class HistoryFragment(private val operationList: LiveData<List<Operation>>) :
    BottomSheetDialogFragment() {

    companion object {
        const val DELETE_REQUEST_CODE = 112
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)

        operationList.observe(this, {
            recyclerView.adapter = HistoryAdapter(it)
        })

        delete_history.setOnClickListener {
            onDeleteClick()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun onDeleteClick() {
//        targetFragment?.onActivityResult(DELETE_REQUEST_CODE, Activity.RESULT_OK, Intent())
        val mainActivity = activity as MainActivity
        mainActivity.deleteHistory()
        dismiss()
    }
}