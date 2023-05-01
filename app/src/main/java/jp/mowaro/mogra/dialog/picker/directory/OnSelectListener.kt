package jp.mowaro.mogra.dialog.picker.directory

import java.io.File

/**
 * 選択したファイルの情報を取り出すためのリスナーインターフェース
 */
interface OnSelectListener {
    fun onSelect(file: File)
}