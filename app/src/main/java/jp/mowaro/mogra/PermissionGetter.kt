package jp.mowaro.mogra

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.provider.Settings

class PermissionGetter {
    /**
     * 全ファイルアクセス権限が許可されているかどうか
     * 許可されていなければ要求する
     * 参考：https://zenn.dev/tbsten/articles/125007cd4826f0
     * @return true:allowed/ false:denied
     */
    fun isAllowedManageExternalStorage(activity: Activity): Boolean {
        if (Environment.isExternalStorageManager()) return true

        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        activity.startActivity(intent)

        return Environment.isExternalStorageManager()
    }
}