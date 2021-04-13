package com.firstprojects.locationsaverwithkotlin.activities

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.firstprojects.locationsaverwithkotlin.model.Places
import com.firstprojects.locationsaverwithkotlin.adapter.PlacesAdapter
import com.firstprojects.locationsaverwithkotlin.R

class MainActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinlater = MenuInflater(this)
        menuinlater.inflate(R.menu.fromactivitytomaps,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.fromActivityToMaps) {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("isItNew",true)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    private lateinit var listView : ListView
    private lateinit var customAdapter : PlacesAdapter
    private lateinit var arrayAdapter : ArrayAdapter<String>
    private lateinit var arrayListNames : ArrayList<String>
    private lateinit var arrayListIdNumbers : ArrayList<Int>
    //database
    private lateinit var sqlite : SQLiteDatabase

    //places modelling
    private lateinit var placesArray : ArrayList<Places>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onStart() {
        super.onStart()
        //initilization
        listView = findViewById(R.id.listView)
        placesArray = arrayListOf()
        customAdapter = PlacesAdapter(this@MainActivity,placesArray)
        // arrayListNames = arrayListOf()
        //  arrayListIdNumbers = arrayListOf()
        // arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayListNames)
        sqlite = openOrCreateDatabase("Locations",MODE_PRIVATE,null)
        sqlite.execSQL("CREATE TABLE IF NOT EXISTS placenames(id INTEGER PRIMARY KEY,latitude DOUBLE,longitude DOUBLE,name VANCHAR)")
        getData()
        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                intent.putExtra("isItNew",false)
                intent.putExtra("model",placesArray[position])
                startActivity(intent)

            }

        })

    }

    private fun getData() {
        val queryRow = sqlite.rawQuery("SELECT * FROM placenames",null)
        val idIndex = queryRow.getColumnIndex("id")
        val nameIndex = queryRow.getColumnIndex("name")
        val latitudeIndex = queryRow.getColumnIndex("latitude")
        val longitudeIndex = queryRow.getColumnIndex("longitude")
        while (queryRow.moveToNext()) {

            //   arrayListIdNumbers.add(queryRow.getInt(idIndex))
                // arrayListNames.add(queryRow.getString(nameIndex))

            val places = Places(queryRow.getString(nameIndex),queryRow.getDouble(latitudeIndex),queryRow.getDouble(longitudeIndex))
            placesArray.add(places)
            println(placesArray[0].name)
        }
        queryRow.close()
        //listView.adapter = arrayAdapter
        listView.adapter = customAdapter
    }
}