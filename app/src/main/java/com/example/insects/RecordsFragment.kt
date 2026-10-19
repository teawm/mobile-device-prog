package com.example.insects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecordsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordsAdapter
    private lateinit var repository: GameRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = GameRepository(requireContext())
        recyclerView = view.findViewById(R.id.recyclerViewRecords)
        adapter = RecordsAdapter(
            onDeleteClick = { record ->
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.deleteSession(record.sessionId)
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repository.getRecords().collectLatest { records ->
                adapter.submitList(records)
            }
        }
    }
}