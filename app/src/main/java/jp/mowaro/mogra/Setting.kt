package jp.mowaro.mogra

import android.content.Context
import android.content.SharedPreferences

class Setting (val context: Context){

    private var preferenceName = "setting"
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    private val orderByKey = "orderBy"
    private val orientKey = "orient"
    private val directoryKey = "directory"
    private val bookmarkKey = "bookmark"

    public var orderBy = sharedPreferences.getString(orderByKey, "")
    public var orient = sharedPreferences.getString(orientKey, "")
    public var directory = sharedPreferences.getString(directoryKey, "")
    public var bookmark = sharedPreferences.getString(bookmarkKey, "")

    public fun commit() {
        val edit = sharedPreferences.edit()
        edit.putString(orderByKey, orderBy)
        edit.putString(orientKey, orient)
        edit.putString(directoryKey, directory)
        edit.putString(bookmarkKey, bookmark)
        edit.commit()
    }
}