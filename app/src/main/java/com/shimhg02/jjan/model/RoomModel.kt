package com.shimhg02.jjan.model

class RoomModel {
    /**
     * result : 0
     * room : {"name":"Test Dev Room","owner_ref":"fadaADADAAee","settings":{"description":"Testing","mode":"lecture","scheduled":false,"adhoc":false,"participants":"10","billing_code":1234,"auto_recording":false,"active_talker":true,"quality":"HD"},"data":{"name":"Mayank"},"created":"2018-11-28T09:26:17.837Z","room_id":"5bfe5f39e9f8e16af7dd5b77"}
     */
    var result = 0
    var room: RoomData? = null

    class RoomData {
        /**
         * name : Test Dev Room
         * owner_ref : fadaADADAAee
         * settings : {"description":"Testing","mode":"lecture","scheduled":false,"adhoc":false,"participants":"10","billing_code":1234,"auto_recording":false,"active_talker":true,"quality":"HD"}
         * data : {"name":"Mayank"}
         * created : 2018-11-28T09:26:17.837Z
         * room_id : 5bfe5f39e9f8e16af7dd5b77
         */
        var name: String? = null
        var owner_ref: String? = null
        var settings: SettingsData? = null
        var data: UserDataModel? = null
        var created: String? = null
        var room_id: String? = null

    }
    /**
     * room_id : 5b9f8f3c6306115afd6a1f07
     * role : participant
     * mode : group
     */
}