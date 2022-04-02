package com.uqac.pet_retail.data.model

class CardHome {
    var image: String = ""
    var user: String = ""
    var dog: String = ""
    var uuidAnnonce: String = ""

    constructor() {}
    constructor(image: String, user: String, dog: String, uuidAnnonce: String) {
        this.image = image
        this.user = user
        this.dog = dog
        this.uuidAnnonce = uuidAnnonce
    }
}