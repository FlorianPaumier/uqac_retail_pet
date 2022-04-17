package com.uqac.pet_retail.ui.chat

class UserRoomsModel {
    lateinit var rooms: ArrayList<String>

    constructor(){}
    constructor(rooms: ArrayList<String>){
        this.rooms = rooms
    }
}