package com.example.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.ui.modelview.CalculatorViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HistoryBottomSheet : BottomSheetDialogFragment() {
    private val viewModel: CalculatorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvHistory)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.history.observe(viewLifecycleOwner) { historyList ->
            recyclerView.adapter = HistoryAdapter(historyList.reversed()) { selectedItem ->
                viewModel.onHistoryItemClicked(selectedItem)
                dismiss()
            }
        }
    }
}