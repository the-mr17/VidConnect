package com.mr_17.vidconnect.utils

import com.google.firebase.database.FirebaseDatabase

object Constants {
    var NODE_USERS = "Users"
    var NODE_UID = "uid"
    var NODE_FIRST_NAME = "firstName"
    var NODE_LAST_NAME = "lastName"

    private var DATABASE_REF_ROOT =
        FirebaseDatabase
            .getInstance()
            .reference
        
    var DATABASE_REF_USERS =
        Constants
            .DATABASE_REF_ROOT
            .child(Constants.NODE_USERS)

}
