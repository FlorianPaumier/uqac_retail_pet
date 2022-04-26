package com.uqac.pet_retail.ui.profil

class ProfileModel {
    var id: String = ""
    var user: String = ""
    var email: String = ""
    var firstname: String = ""
    var lastname: String = ""
    var phone: String = ""
    var description: String = ""
    var address: HashMap<String, String> = HashMap()
    var thumbnail: String = ""

    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "user" to this.user,
            "email" to this.email,
            "firstname" to this.firstname,
            "lastname" to this.lastname,
            "phone" to this.phone ,
            "address" to this.address ,
            "thumbnail" to this.thumbnail,
            "description" to this.description
        )
    }

    constructor() {}

    constructor(
        user: String,
        email: String,
        firstname: String,
        phone: String,
        lastname: String,
        thumbnail: String,
        description: String,
        address: HashMap<String, String>
    ) {
        this.user = user
        this.email = email
        this.firstname = firstname
        this.lastname = lastname
        this.phone = phone
        this.address = address
        this.thumbnail = thumbnail
        this.description = description
    }

    constructor(data: Map<String, Any>) {
        this.user = data.get("user").toString()
        this.email = data.get("email").toString()
        this.firstname = data.get("firstname").toString()
        this.lastname = data.get("lastname").toString()
        this.phone = data.get("phone").toString()
        this.description = data.get("description").toString()
        if (data.get("address") != null) {
            this.address = data.get("address") as HashMap < String, String>
        }
        this.thumbnail = data.get("thumbnail").toString()
    }
}