package jp.mowaro.mogra.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import jp.mowaro.mogra.R

object Setting {

    private const val preferenceName = "setting"
    private const val orderByKey = "orderBy"
    private const val orientKey = "orient"
    private const val directoryKey = "directory"
    private const val bookmarkKey = "bookmark"
    private lateinit var sharedPreferences: SharedPreferences

    lateinit var orderBy: String
    lateinit var orient: String
    lateinit var directory: String
    lateinit var bookmark: String

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        val res = context.resources
        orderBy = sharedPreferences.getString(orderByKey, "") ?: res.getString(R.string.order_by_name)
        orient = sharedPreferences.getString(orientKey, "") ?: res.getString(R.string.auto_orient_none)
        directory = sharedPreferences.getString(directoryKey, "") ?: Environment.getExternalStorageDirectory().path
        bookmark = sharedPreferences.getString(bookmarkKey, "") ?: ""
    }

    fun commit() {
        val edit = sharedPreferences.edit()
        edit.putString(orderByKey, orderBy)
        edit.putString(orientKey, orient)
        edit.putString(directoryKey, directory)
        edit.putString(bookmarkKey, bookmark)
        edit.apply()
    }
}