package com.uqac.pet_retail.ui.chat

class FirebaseRoomModel {
    var user1Name: String = ""
    var user2Name: String = ""
    var uid: String = ""
    var user1: String = ""
    var user2: String = ""
    var messages: HashMap<String, HashMap<String, Any>> = HashMap()

    constructor() {}
    constructor(uid: String, user1: String, user2: String, messages: HashMap<String, HashMap<String, Any>>) {
        this.uid = uid
        this.user1 = user1
        this.user2 = user2
        this.messages = messages
    }
}