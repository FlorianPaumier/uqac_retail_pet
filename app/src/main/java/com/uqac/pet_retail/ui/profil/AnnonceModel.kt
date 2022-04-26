package com.uqac.pet_retail.ui.profil

import java.util.HashMap

class AnnonceModel {
    fun toMap(): MutableMap<String, Any> {
        return hashMapOf(
            "uid" to this.uid,
            "race" to this.race,
            "name" to this.name,
            "type" to this.type,
            "morningTime" to this.morningTime,
            "afternoonTime" to this.afternoonTime,
            "nightTimethis" to this.nightTime,
            "weekTimethis" to this.weekTime,
            "weekendTime" to this.weekendTime,
            "description" to this.description,
            "user" to this.user,
            "picture" to this.picture,
            "address" to this.address
        )
    }

    lateinit var address: HashMap<String, String>
    var uid: String = ""
    var name: String = ""
    var type: String = ""
    var race: String = ""
    var morningTime: Boolean = false
    var afternoonTime: Boolean = false
    var nightTime: Boolean = false
    var weekTime: Boolean = false
    var weekendTime: Boolean = false
    var description: String = ""
    var user: String = ""
    var picture: ArrayList<String?> = ArrayList()

    constructor() {}

    constructor(
        uid: String,
        name: String,
        type: String,
        race: String,
        morningTime: Boolean,
        afternoonTime: Boolean,
        nightTime: Boolean,
        weekendTime: Boolean,
        weekTime: Boolean,
        description: String,
        user: String,
        picture: ArrayList<String?>,
        address: HashMap<String, String>
    ) {
        this.uid = uid
        this.race = race
        this.name = name
        this.type = type
        this.morningTime = morningTime
        this.afternoonTime = afternoonTime
        this.nightTime = nightTime
        this.weekTime = weekTime
        this.weekendTime = weekendTime
        this.description = description
        this.user = user
        this.picture = picture
        this.address = address
    }

}