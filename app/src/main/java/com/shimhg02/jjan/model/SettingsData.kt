package com.shimhg02.jjan.model

import java.io.Serializable

class SettingsData : Serializable {
    /**
     * description : Testing
     * mode : lecture
     * scheduled : false
     * adhoc : false
     * participants : 10
     * billing_code : 1234
     * auto_recording : false
     * active_talker : true
     * quality : HD
     */
    var description: String? = null
    var mode: String? = null
    var isScheduled = false
    var isAdhoc = false
    var participants: String? = null
    var billing_code = 0
    var isAuto_recording = false
    var isActive_talker = false
    var quality: String? = null

}