package com.uqac.pet_retail.ui.chat

class Model {
    var id: String = ""
    var image: String = ""
    var name: String = ""
    var lastMessage: String = ""
    var lastMessageDate: String = ""

    constructor() {}
    constructor(id: String, image: String, name: String, lastMessage: String, lastMessageDate: String) {
        this.id = image
        this.image = image
        this.name = name
        this.lastMessage = lastMessage
        this.lastMessageDate = lastMessageDate
    }
}