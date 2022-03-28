package com.uqac.pet_retail.ui.chat

class FirebaseChatModel {
    var uid: String = ""
    var date: String = ""
    var author: String = ""
    var message: String = ""
    var read: Boolean = false
    var isUser: Boolean = false

    constructor() {}
    constructor(uid: String, date: String, author: String, message: String, read: Boolean) {
        this.uid = uid
        this.date = date
        this.author = author
        this.message = message
        this.read = read
    }
}