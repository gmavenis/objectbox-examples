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
        val dialog = AlertDialog.Builder(this)
                .setTitle(if (userModel == null) R.string.add_user else R.string.edit_user)
                .setView(R.layout.dialog_add_user)
                .setPositiveButton(R.string.ok, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                    }
                })
                .setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                    }
                })
                .create()
        dialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(p0: DialogInterface?) {
                val etName = dialog.findViewById<TextInputEditText>(R.id.et_name)
                val etAge = dialog.findViewById<TextInputEditText>(R.id.et_age)
                val etScore1 = dialog.findViewById<TextInputEditText>(R.id.et_score_1)
                val etScore2 = dialog.findViewById<TextInputEditText>(R.id.et_score_2)

                etName?.setText(userModel?.name)
                etAge?.setText(userModel?.age?.toString())
                if (userModel != null && userModel?.scores?.size?.equals(2)!!) {
                    etScore1?.setText(userModel?.scores?.get(0)?.score.toString())
                    etScore2?.setText(userModel?.scores?.get(1)?.score.toString())
                }
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
        mAdapter = UserAdapter(result, object : AdapterItemClickListener {
            override fun onItemClick(model: Any?) {
                showDialogUser(model as UserModel)
            }
        })
        mRecyclerView.adapter = mAdapter
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
}