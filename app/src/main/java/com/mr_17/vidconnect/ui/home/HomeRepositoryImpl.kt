package com.mr_17.vidconnect.ui.home

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.mr_17.vidconnect.data.Resource
import com.mr_17.vidconnect.ui.home.models.LatestEvent
import com.mr_17.vidconnect.ui.home.models.User
import com.mr_17.vidconnect.utils.Constants.DATABASE_REF_USERS
import com.mr_17.vidconnect.utils.Constants.NODE_EMAIL_ADDRESS
import com.mr_17.vidconnect.utils.Constants.NODE_FIRST_NAME
import com.mr_17.vidconnect.utils.Constants.NODE_LAST_NAME
import com.mr_17.vidconnect.utils.Constants.NODE_LATEST_EVENT
import com.mr_17.vidconnect.utils.Constants.NODE_STATUS
import kotlinx.coroutines.tasks.await
import java.util.LinkedList
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val gson: Gson
) : HomeRepository {
    override suspend fun getAllUserData(): Resource<List<User>>? {
        val result = LinkedList<User>()
        return try {
            DATABASE_REF_USERS
                .get()
                .await()
                .children
                .filter {
                    it.key != firebaseAuth.uid
                }
                .forEach {
                    result.add(
                        User(
                            it.key.toString(),
                            it.child(NODE_EMAIL_ADDRESS).value.toString(),
                            it.child(NODE_FIRST_NAME).value.toString(),
                            it.child(NODE_LAST_NAME).value.toString(),
                            it.child(NODE_STATUS).value.toString(),
                        )
                    )
                }
            Resource.Success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun sendMessageToOtherClient(latestEvent: LatestEvent): Resource<String>? {
        val convertedLatestEvent = gson.toJson(latestEvent.copy(senderId = firebaseAuth.uid))
        return try {
            DATABASE_REF_USERS
                .child(latestEvent.targetId)
                .child(NODE_LATEST_EVENT)
                .setValue(convertedLatestEvent)
                .await()
            Resource.Success(latestEvent.targetId)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun subscribeForLatestEvent(listener: Listener) {
        try {
            DATABASE_REF_USERS
                .child(firebaseAuth.uid!!)
                .child(NODE_LATEST_EVENT)
                .addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val latestEvent = try {
                            gson.fromJson(snapshot.value.toString(), LatestEvent::class.java)
                        } catch (e:Exception){
                            e.printStackTrace()
                            null
                        }
                        latestEvent?.let {
                            listener.onLatestEventReceived(it)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface Listener {
        fun onLatestEventReceived(latestEvent: LatestEvent)
    }
}