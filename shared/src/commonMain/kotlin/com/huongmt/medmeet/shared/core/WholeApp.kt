package com.huongmt.medmeet.shared.core

import com.huongmt.medmeet.shared.config.BASE_CHAT_URL
import com.huongmt.medmeet.shared.config.BASE_URL
import com.huongmt.medmeet.shared.core.entity.User

object WholeApp {
    var USER_ID: String = ""
    var USER: User? = null
    var CHAT_BASE_URL: String = BASE_CHAT_URL
    var BACKEND_URL: String = BASE_URL
}
