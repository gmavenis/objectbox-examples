package com.synd.kotlin.db

import android.support.v7.app.AppCompatActivity
import com.synd.kotlin.db.entity.ScoreEntity
import com.synd.kotlin.db.entity.ScoreEntity_
import com.synd.kotlin.db.entity.UserEntity
import com.synd.kotlin.db.entity.UserEntity_
import com.synd.kotlin.model.ScoreModel
import com.synd.kotlin.model.UserModel
import com.synd.kotlin.ui.OBApplication
import com.synd.kotlin.viewmodel.BaseViewModel
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

class DBHelper(val application: OBApplication, val activity: AppCompatActivity, val listener: OnDataChangedListener<UserEntity>?) {

    private lateinit var userBox: Box<UserEntity>
    private lateinit var userQuery: Query<UserEntity>

    private lateinit var scoreBox: Box<ScoreEntity>
    private lateinit var scoreQuery: Query<ScoreEntity>

    private lateinit var userViewModel: BaseViewModel<UserEntity>
    private lateinit var scoreViewModel: BaseViewModel<ScoreEntity>

    init {
        userBox = application.boxStore.boxFor<UserEntity>()
        userQuery = userBox.query().order(UserEntity_.uid).build()

        scoreBox = application.boxStore.boxFor<ScoreEntity>()
        scoreQuery = scoreBox.query().order(ScoreEntity_.subject).build()
    }

    fun getAllUser(): List<UserModel> {
        val result = mutableListOf<UserModel>()
        userQuery.find().forEach() {
            val scores = scoreBox.query().equal(ScoreEntity_.uid, it.uid!!).order(ScoreEntity_.subject).build().find()
            val list = mutableListOf<ScoreModel>()
            scores.forEach {
                list.add(it.toModel())
            }
            val userModel = it.toModel()
            userModel.scores = list
            result.add(userModel)
        }
        return result
    }

    fun putUser(userEntity: UserEntity) {
        userBox.put(userEntity)
    }

    fun putScore(scoreEntity: ScoreEntity) {
        scoreBox.put(scoreEntity)
    }

    fun clearAll() {
        scoreBox.removeAll()
        userBox.removeAll()
    }
}
