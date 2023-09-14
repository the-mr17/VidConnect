package com.mr_17.vidconnect.utils

import com.google.firebase.database.FirebaseDatabase

object Constants {
    const val NODE_USERS = "Users"
    const val NODE_UID = "uid"
    const val NODE_FIRST_NAME = "firstName"
    const val NODE_LAST_NAME = "lastName"
    const val NODE_EMAIL_ADDRESS = "emailAddress"
    const val NODE_STATUS = "status"
    const val NODE_LATEST_EVENT = "latestEvent"

    private var DATABASE_REF_ROOT =
        FirebaseDatabase
            .getInstance()
            .reference
        
    var DATABASE_REF_USERS =
        DATABASE_REF_ROOT
            .child(NODE_USERS)

}
