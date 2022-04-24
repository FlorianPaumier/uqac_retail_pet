package com.uqac.pet_retail.ui.profil

class ProfileModel {
    var user: String = ""
    var email: String = ""
    var firstname: String = ""
    var lastname: String = ""
    var phone: String = ""
    var address: HashMap<String, String> = HashMap()
    var thumbnail: String = ""

    constructor() {}

    constructor(
        user: String,
        email: String,
        firstname: String,
        phone: String,
        lastname: String,
        thumbnail: String,
        address: HashMap<String, String>
    ) {
        this.user = user
        this.email = email
        this.firstname = firstname
        this.lastname = lastname
        this.phone = phone
        this.address = address
        this.thumbnail = thumbnail
    }

    constructor(data: Map<String, Any>) {
        this.user = data.get("user").toString()
        this.email = data.get("email").toString()
        this.firstname = data.get("firstname").toString()
        this.lastname = data.get("lastname").toString()
        this.phone = data.get("phone").toString()
        if (data.get("address") != null) {
            this.address = data.get("address") as HashMap < String, String>
        }
        this.thumbnail = data.get("thumbnail").toString()
    }
}