 package com.firstprojects.locationsaverwithkotlin.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.firstprojects.locationsaverwithkotlin.model.Places
import com.firstprojects.locationsaverwithkotlin.R
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var checktheplaceyourehere = 0
    private lateinit var selectedLocationName: String
    private lateinit var selectedLocationMark: LatLng
    private lateinit var placemodel : Places

    //database
    private lateinit var sqlite: SQLiteDatabase

    //intent data
    private var checkBoolean = true
    private lateinit var serializableModels : Places


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        sqlite = openOrCreateDatabase("Locations", MODE_PRIVATE, null)

        //intent
        val getIntent = intent
        checkBoolean = getIntent.getBooleanExtra("isItNew", true)

    }


    override fun onMapReady(googleMap: GoogleMap) {


        if (checkBoolean) {
            mMap = googleMap
            mMap.setOnMapLongClickListener(myClickListener)

            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            locationListener = object : LocationListener {

                override fun onLocationChanged(location: Location) {
                    mMap.clear()
                    val latlng = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(latlng).title("you're here."))
                    if (checktheplaceyourehere == 3) {
                        mMap.addMarker(MarkerOptions().title(selectedLocationName).position(selectedLocationMark))
                    }
                    if (checktheplaceyourehere == 0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 20f))
                        checktheplaceyourehere = 1
                    }
                    if (checktheplaceyourehere == 2) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocationMark, 20f))
                        checktheplaceyourehere = 3
                    }
                }
            }
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                )
                val locationLastKnown =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (locationLastKnown != null) {
                    val latLng = LatLng(locationLastKnown.latitude, locationLastKnown.longitude)
                    mMap.addMarker(MarkerOptions().title("you were here before").position(latLng))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                }
            }

        } else {
            //it shows saved data
            mMap = googleMap
            mMap.clear()
          /*  try {
                val queryRow = sqlite.rawQuery("SELECT * FROM placenames WHERE id = ?", arrayOf(idForDatabase.toString()))
                val latitudeIndex = queryRow.getColumnIndex("latitude")
                val longitudeIndex = queryRow.getColumnIndex("longitude")
                val nameIndex = queryRow.getColumnIndex("name")
                while (queryRow.moveToNext()) {
                    val latlngIwant = LatLng(queryRow.getDouble(latitudeIndex), queryRow.getDouble(longitudeIndex))
                    val placeName = queryRow.getString(nameIndex)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngIwant, 20f))
                    mMap.addMarker(MarkerOptions().position(latlngIwant).title(placeName))
                }
                queryRow.close()

            } catch (e: Exception) {
                e.printStackTrace()
                println(e.localizedMessage)
            } */
            val getIntent = intent
            placemodel = getIntent.getSerializableExtra("model") as Places
            val latlngOldPlace = LatLng(serializableModels.latitude,serializableModels.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngOldPlace,20f))
            mMap.addMarker(MarkerOptions().title(placemodel.name).position(latlngOldPlace))
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION && grantResults.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                val locationLastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (locationLastKnown != null) {
                    val latLng = LatLng(locationLastKnown.latitude, locationLastKnown.longitude)
                    mMap.addMarker(MarkerOptions().title("you were here before").position(latLng))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                }
            }
        }
    }

    val myClickListener = object : GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng?) {
            try {
                if (p0 != null) {
                    selectedLocationMark = p0
                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                    val geoList = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)

                    if (geoList.isNotEmpty() && geoList[0].thoroughfare != null) {
                        selectedLocationName = geoList[0].thoroughfare
                    }
                    if (geoList.isNotEmpty() && geoList[0].subThoroughfare != null) {
                        selectedLocationName = selectedLocationName + " " + geoList[0].subThoroughfare
                    }
                    if (geoList.isNotEmpty() && geoList[0].countryName != null) {
                        selectedLocationName = selectedLocationName + " " + geoList[0].countryName
                    }
                    if (geoList.isNotEmpty() && selectedLocationName.isEmpty()) {
                        selectedLocationName = "Unknown Location"
                    }
                    checktheplaceyourehere = 2

                    val alert = AlertDialog.Builder(this@MapsActivity)
                    alert.setCancelable(false)
                    alert.setTitle("Are you sure?")
                    alert.setMessage("Do you wanna save the location? = $selectedLocationName")
                    alert.setPositiveButton("yes I'm", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {

                            sqlite.execSQL("CREATE TABLE IF NOT EXISTS placenames(id INTEGER PRIMARY KEY,latitude DOUBLE,longitude DOUBLE,name VANCHAR)")
                            try {

                                val sqliteStatement = sqlite.compileStatement("INSERT INTO placenames(latitude,longitude,name) VALUES(?,?,?)")
                                sqliteStatement.bindDouble(1, p0.latitude)
                                sqliteStatement.bindDouble(2, p0.longitude)
                                sqliteStatement.bindString(3, selectedLocationName)
                                sqliteStatement.execute()

                                //intent
                                val intent = Intent(this@MapsActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()

                            } catch (e: Exception) {
                                e.printStackTrace()
                                println(e.localizedMessage)
                            }

                        }

                    })
                    alert.setNegativeButton("not I'm not", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {

                        }

                        //it doen't need to do anything in the situation

                    })
                    alert.show()
                } else {
                    Toast.makeText(this@MapsActivity, "the location can not be got!", Toast.LENGTH_SHORT).show()
                }

        }catch (e: Exception)
        {
            e.printStackTrace()
            println(e.localizedMessage)
            if(e.localizedMessage.toString().matches("grpc failed".toRegex())) {
                selectedLocationName = "UNKNOWN ADDRESS"
            }
        }
    }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
