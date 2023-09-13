package com.mr_17.vidconnect.ui.home

import com.google.firebase.auth.FirebaseAuth
import com.mr_17.vidconnect.data.Resource
import com.mr_17.vidconnect.ui.home.models.User
import com.mr_17.vidconnect.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.LinkedList
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : HomeRepository {
    override suspend fun getAllUserData(): Resource<List<User>>? {
        val result = LinkedList<User>()
        return try {
            Constants.DATABASE_REF_USERS
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
                            it.child("emailAddress").value.toString(),
                            it.child("firstName").value.toString(),
                            it.child("lastName").value.toString(),
                            it.child("status").value.toString(),
                        )
                    )
                }
            Resource.Success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}