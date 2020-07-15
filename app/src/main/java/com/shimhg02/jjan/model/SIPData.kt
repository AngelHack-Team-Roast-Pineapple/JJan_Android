package com.shimhg02.jjan.model

class SIPData {
    /**
     * enabled : true
     * uri : sip:292@192.168.200.116:5060
     * name : Mayank
     * auth : Yes
     * secret : No
     * aor : ["sip:292@192.168.200.116:5060","sip:292@192.168.200.116:5060"]
     */
    var isEnabled = false
    var uri: String? = null
    var name: String? = null
    var auth: String? = null
    var secret: String? = null
    var aor: List<String>? = null

}