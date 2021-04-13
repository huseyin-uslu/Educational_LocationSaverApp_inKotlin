package com.firstprojects.locationsaverwithkotlin.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.firstprojects.locationsaverwithkotlin.R
import com.firstprojects.locationsaverwithkotlin.model.Places


class PlacesAdapter (private val context : Activity,private val placeArrayList : ArrayList<Places> ) : ArrayAdapter<Places>(context,
    R.layout.custom_listview,placeArrayList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater =  context.layoutInflater
        val customView = layoutInflater.inflate(R.layout.custom_listview,null,true)
        val textView : TextView = customView.findViewById(R.id.custom_listView_TextView)
        textView.text = placeArrayList.get(position).name

        return customView
    }
}