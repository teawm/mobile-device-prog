package com.example.insects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class AuthorsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listViewAuthors)

        // Создаем список авторов
        val authorsList = listOf(
            Author("Звонков Иван Владимирович", R.drawable.zodiac_leo),
            Author("Малышев Никита Александрович", R.drawable.not_zodiac_just_me)
        )

        // Устанавливаем адаптер
        val adapter = AuthorsAdapter(requireContext(), authorsList)
        listView.adapter = adapter
    }
}