package com.firstprojects.locationsaverwithkotlin.model

import java.io.Serializable

class Places (name : String, latitude : Double , longitude : Double):Serializable  {
    var name : String =  name
    private set
    var latitude : Double = latitude
    private set
    var longitude : Double = longitude
    private set

}