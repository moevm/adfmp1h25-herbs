package com.herbsapp.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.herbsapp.R
import com.herbsapp.data.room.dao.RoomDao
import com.herbsapp.data.room.entity.ElementEntity
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext

class AppRepository(val dao: RoomDao, val applicationContext: Context, val auth: FirebaseAuth) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend fun signup(name: String, email: String, password: String): Resource<FirebaseUser> {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            return Resource.Success(result.user!!)
        } catch (e: Exception) {
            return Resource.Failure(e)
        }
    }

    suspend fun changeName(name: String): Resource<String> {
        return try {
            if (auth.currentUser != null) {
                auth.currentUser!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build()).await()
                Resource.Success("success")
            } else {
                Resource.Failure(Exception("user is no login"))
            }
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend fun changeMail(mail: String): Resource<String> {
        return try {
            if (auth.currentUser != null) {
                auth.currentUser!!.updateEmail(mail).await()
                Resource.Success("success")
            } else {
                Resource.Failure(Exception("user is no login"))
            }
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getHerbs(): Flow<List<HerbEntity>> = flow {
        if (dao.getHerbs().isNullOrEmpty()) {
            dao.upsertHerbs(listOf(
                HerbEntity(
                    id = 0,
                    name = applicationContext.getString(R.string.herb_name_1),
                    title = applicationContext.getString(R.string.herb_title_1),
                    description = applicationContext.getString(R.string.herb_description_1),
                    city = "СПБ",
                    views = 41,
                    rating = 4.5f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/5/58/Cirsium_perplexans.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/ba/Farm_weeds_of_Canada_%281906%29_%2814796102803%29.jpg/1024px-Farm_weeds_of_Canada_%281906%29_%2814796102803%29.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1f/Cirsium_arvense_with_Bees_Richard_Bartz.jpg/1024px-Cirsium_arvense_with_Bees_Richard_Bartz.jpg",
                    ),
                    mClass = applicationContext.getString(R.string.class_4),
                    genus = applicationContext.getString(R.string.genus_4),
                    taste = applicationContext.getString(R.string.taste_4),
                    family = applicationContext.getString(R.string.family_4),
                    isLiked = false
                ),
                HerbEntity(
                    id = 1,
                    name = applicationContext.getString(R.string.herb_name_2),
                    title = applicationContext.getString(R.string.herb_title_2),
                    description = applicationContext.getString(R.string.herb_description_2),
                    city = "СПБ",
                    views = 41,
                    rating = 4.5f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/48/Aegopodium_podagraria%2C_2022-05-29%2C_West_End_Park%2C_02.jpg/275px-Aegopodium_podagraria%2C_2022-05-29%2C_West_End_Park%2C_02.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Illustration_Aegopodium_podagraria0_clean.jpg/800px-Illustration_Aegopodium_podagraria0_clean.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/8/83/Aegopodium_podagraria_-_stem_profile.jpg",
                    ),
                    mClass = applicationContext.getString(R.string.class_1),
                    genus = applicationContext.getString(R.string.genus_1),
                    taste = applicationContext.getString(R.string.taste_1),
                    family = applicationContext.getString(R.string.family_1),
                    isLiked = false
                ),
                HerbEntity(
                    id = 2,
                    name = applicationContext.getString(R.string.herb_name_3),
                    title = applicationContext.getString(R.string.herb_title_3),
                    description = applicationContext.getString(R.string.herb_description_3),
                    city = "СПБ",
                    views = 99,
                    rating = 5.0f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/56/Anethum_graveolens_02.jpg/275px-Anethum_graveolens_02.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/Illustration_Anethum_graveolens_clean.jpg/800px-Illustration_Anethum_graveolens_clean.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/c/c9/Anethum_graveolens20090812_475.jpg",
                    ),
                    mClass = applicationContext.getString(R.string.class_2),
                    genus = applicationContext.getString(R.string.genus_2),
                    taste = applicationContext.getString(R.string.taste_2),
                    family = applicationContext.getString(R.string.family_2),
                    isLiked = false
                ),
                HerbEntity(
                    id = 3,
                    name = applicationContext.getString(R.string.herb_name_4),
                    title =  applicationContext.getString(R.string.herb_title_4),
                    description = applicationContext.getString(R.string.herb_description_4),
                    city = "СПБ",
                    views = 22,
                    rating = 4.8f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/48/Chenopodium_album01.jpg/275px-Chenopodium_album01.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/f/fb/Chenopodium_album_Sturm27.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Chenopodium_album_flowers.jpg/1920px-Chenopodium_album_flowers.jpg"
                    ),
                    mClass = applicationContext.getString(R.string.class_3),
                    genus = applicationContext.getString(R.string.genus_3),
                    taste = applicationContext.getString(R.string.taste_3),
                    family = applicationContext.getString(R.string.family_3),
                    isLiked = false
                ),
            ))
        }

        val herbs = dao.getHerbs()
        emit(herbs)
    }

    fun getHerbById(id: Int): Flow<HerbEntity> = flow {
        emit(dao.getHerbById(id))
    }

    fun getElements(): Flow<List<ElementEntity>> = flow { 
        if (dao.getElements().isNullOrEmpty()) {
            dao.insertElements(listOf(
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_1), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_2), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_3), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_4), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_5), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_6), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_7), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_8), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_9), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_10), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_11), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_12), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_13), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_14), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_15), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_16), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_17), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_18), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_19), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_20), false),
                
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_1), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_2), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_3), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_4), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_5), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_6), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_7), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_8), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_9), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_10), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_11), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_12), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_13), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_14), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_15), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_16), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_17), false),
                
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_1), false),
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_2), false),
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_3), false),
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_4), false),
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_5), false),

                ElementEntity(id = 1, signId = 4, applicationContext.getString(R.string.family_1), false),
                ElementEntity(id = 4, signId = 4, applicationContext.getString(R.string.family_2), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_3), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_4), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_5), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_6), false),
                ElementEntity(id = 9, signId = 4, applicationContext.getString(R.string.family_7), false),
                ElementEntity(id = 10, signId = 4, applicationContext.getString(R.string.family_8), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_9), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_10), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_11), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_12), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_13), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_14), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_15), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_16), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_17), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_18), false),
            ))
        }

        val elements = dao.getElements()
        emit(elements)
    }

    suspend fun updateHerb(herbEntity: HerbEntity) {
        dao.updateHerb(herb = herbEntity)
    }

}