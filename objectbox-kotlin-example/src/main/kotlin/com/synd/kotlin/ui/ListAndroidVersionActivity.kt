package com.synd.kotlin.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.synd.kotlin.adapter.AdapterItemClickListener
import com.synd.kotlin.adapter.AndroidVersionAdapter
import com.synd.kotlin.api.ApiService
import com.synd.kotlin.api.Repository
import com.synd.kotlin.model.AndroidVersion
import io.objectbox.example.kotlin.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ListAndroidVersionActivity : AppCompatActivity() {

    private val TAG = ListAndroidVersionActivity::class.java.simpleName
    private lateinit var mAdapter: AndroidVersionAdapter
    private lateinit var mRecyclerView: RecyclerView

    @Suppress("DEPRECATION")
    private lateinit var loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_android_version)
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
        Repository.createService(ApiService::class.java).getAndroidVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { result ->
                            handleSuccessAndroidVersion(result)
                        },
                        { error ->
                            handlerErrorAndroidVersion(error)
                        }
                )
    }

    private fun handleSuccessAndroidVersion(result: List<AndroidVersion>) {
        loading?.cancel()
        mAdapter = AndroidVersionAdapter(result, object : AdapterItemClickListener {
            override fun onItemClick(androidVersion: AndroidVersion) {
                toast(androidVersion?.toString())
            }
        })
        mRecyclerView.adapter = mAdapter
    }

    private fun handlerErrorAndroidVersion(error: Throwable) {
        loading?.cancel()
        toast("Error ${error.localizedMessage}")
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}