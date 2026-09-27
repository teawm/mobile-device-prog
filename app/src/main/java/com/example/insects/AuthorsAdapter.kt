package com.example.insects

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class AuthorsAdapter(
    private val context: Context,
    private val authorsList: List<Author>
) : BaseAdapter() {

    override fun getCount(): Int = authorsList.size

    override fun getItem(position: Int): Any = authorsList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            // Если view не переиспользуется, создаем новый
            view = LayoutInflater.from(context).inflate(R.layout.item_author, parent, false)
            viewHolder = ViewHolder(
                imageView = view.findViewById(R.id.imageViewAuthorPhoto),
                textView = view.findViewById(R.id.textViewAuthorName)
            )
            view.tag = viewHolder
        } else {
            // Переиспользуем существующий view для производительности
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // Заполняем данными текущий элемент
        val author = authorsList[position]
        viewHolder.imageView.setImageResource(author.photoResId)
        viewHolder.textView.text = author.name

        return view
    }

    // Вспомогательный класс для кэширования ссылок на элементы
    private class ViewHolder(
        val imageView: ImageView,
        val textView: TextView
    )
}