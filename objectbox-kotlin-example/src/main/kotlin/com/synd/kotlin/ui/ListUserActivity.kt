package com.synd.kotlin.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.synd.kotlin.adapter.AdapterItemClickListener
import com.synd.kotlin.adapter.UserAdapter
import com.synd.kotlin.api.ApiService
import com.synd.kotlin.api.Repository
import com.synd.kotlin.db.Constants
import com.synd.kotlin.model.UserModel
import io.objectbox.example.kotlin.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ListUserActivity : AppCompatActivity() {

    private val TAG = ListUserActivity::class.java.simpleName
    private lateinit var mAdapter: UserAdapter
    private lateinit var mRecyclerView: RecyclerView

    @Suppress("DEPRECATION")
    private lateinit var loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        initView()
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
        mAdapter = UserAdapter(result, object : AdapterItemClickListener {
            override fun onItemClick(model: Any?) {
                toast((model as UserModel)?.toString())
            }
        })
        mRecyclerView.adapter = mAdapter
    }

    private fun handlerError(error: Throwable) {
        loading?.cancel()
        toast("Error ${error.localizedMessage}")
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}