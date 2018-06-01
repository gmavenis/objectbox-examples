package com.synd.kotlin.ui

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.synd.kotlin.adapter.AdapterItemClickListener
import com.synd.kotlin.adapter.UserAdapter
import com.synd.kotlin.api.ApiService
import com.synd.kotlin.api.Repository
import com.synd.kotlin.db.Constants
import com.synd.kotlin.db.DBHelper
import com.synd.kotlin.db.entity.ScoreEntity
import com.synd.kotlin.db.entity.UserEntity
import com.synd.kotlin.model.ScoreModel
import com.synd.kotlin.model.UserModel
import io.objectbox.example.kotlin.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class ListUserActivity : AppCompatActivity() {

    private val TAG = ListUserActivity::class.java.simpleName
    private lateinit var mAdapter: UserAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var dbHelper: DBHelper

    @Suppress("DEPRECATION")
    private lateinit var loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        initView()
        getLocalData()
    }

    fun addUser(view: View) {
        showDialogUser(null)
    }

    private fun showDialogUser(userModel: UserModel?) {
        var etName: TextInputEditText?
        var etAge: TextInputEditText?
        var etScore1: TextInputEditText?
        var etScore2: TextInputEditText?

        val dialog = AlertDialog.Builder(this)
                .setTitle(if (userModel == null) R.string.add_user else R.string.edit_user)
                .setView(R.layout.dialog_add_user)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create()
        dialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(p0: DialogInterface?) {
                etName = dialog.findViewById<TextInputEditText>(R.id.et_name)
                etAge = dialog.findViewById<TextInputEditText>(R.id.et_age)
                etScore1 = dialog.findViewById<TextInputEditText>(R.id.et_score_1)
                etScore2 = dialog.findViewById<TextInputEditText>(R.id.et_score_2)

                etName?.setText(userModel?.name)
                etAge?.setText(userModel?.age?.toString())
                if (userModel != null && userModel?.scores?.size?.equals(2)!!) {
                    etScore1?.setText(userModel?.scores?.get(0)?.score.toString())
                    etScore2?.setText(userModel?.scores?.get(1)?.score.toString())
                }

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(object : View.OnClickListener {
                            override fun onClick(p0: View?) {
                                if (etName?.length() == 0
                                        || etAge?.length() == 0
                                        || etScore1?.length() == 0
                                        || etScore2?.length() == 0) {
                                    toast(R.string.please_input_all_fields)
                                } else {
                                    dialog.cancel()

                                    val newUserEntity = UserEntity(userModel?.id ?: 0L,
                                            userModel?.uid ?: mAdapter?.itemCount?.toLong()+1L,
                                            etName?.text?.toString(),
                                            etAge?.text?.toString()?.toInt())
                                    dbHelper.putUser(newUserEntity)

                                    val newScoreEntity1 = ScoreEntity(userModel?.scores?.get(0)?.id
                                            ?: 0L,
                                            newUserEntity.uid,
                                            "English",
                                            etScore1?.text?.toString()?.toInt())
                                    val newScoreEntity2 = ScoreEntity(userModel?.scores?.get(1)?.id
                                            ?: 0L,
                                            newUserEntity.uid,
                                            "Math",
                                            etScore2?.text?.toString()?.toInt())

                                    dbHelper.putScore(newScoreEntity1)
                                    dbHelper.putScore(newScoreEntity2)
                                    
                                    getLocalData()

                                    toast(if (userModel == null) R.string.new_user_added else R.string.user_updated)
                                    if (userModel == null) {
                                        mRecyclerView?.smoothScrollToPosition(mAdapter?.itemCount)
                                    }
                                }
                            }
                        })

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener(object : View.OnClickListener {
                            override fun onClick(p0: View?) {
                                dialog.cancel()
                            }
                        })
            }
        })
        dialog.show()
    }

    fun fetchData(view: View) {
        requestApi()
    }

    private fun initView() {
        @Suppress("DEPRECATION")
        loading = ProgressDialog(this)
        loading.setMessage(getString(R.string.loading))

        mRecyclerView = findViewById(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(mRecyclerView.context, layoutManager.orientation)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.addItemDecoration(divider)
    }

    private fun setData(result: List<UserModel>) {
        if (!::mAdapter.isInitialized) {
            mAdapter = UserAdapter(result, object : AdapterItemClickListener {
                override fun onItemClick(model: Any?) {
                    showDialogUser(model as UserModel)
                }
            })
            mRecyclerView.adapter = mAdapter
        } else {
            mAdapter.setData(result)
        }
    }

    private fun getLocalData() {
        dbHelper = DBHelper(application as OBApplication, this, null)
        val result = dbHelper.getAllUser()
        setData(result)
    }

    private fun requestApi() {
        loading?.show()
        Repository.createService(ApiService::class.java, Constants.USER_LIST_URL)
                .getUserList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { result ->
                            handleSuccess(result)
                        },
                        { error ->
                            handlerError(error)
                        }
                )
    }

    private fun handleSuccess(result: List<UserModel>) {
        loading?.cancel()
        dbHelper.clearAll()
        result?.forEach {
            val uid = it.uid
            dbHelper.putUser(UserEntity(0, uid, it.name, it.age))
            Collections.sort(it.scores, object : Comparator<ScoreModel> {
                override fun compare(p0: ScoreModel?, p1: ScoreModel?): Int {
                    return p0?.subject!!.compareTo(p1?.subject!!)
                }
            })
            it.scores?.forEach {
                dbHelper.putScore(ScoreEntity(0, uid, it.subject, it.score))
            }
        }
        setData(result)
    }

    private fun handlerError(error: Throwable) {
        loading?.cancel()
        toast("Error ${error.localizedMessage}")
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun toast(id: Int) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
    }
}