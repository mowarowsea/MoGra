package jp.mowaro.mogra.dialog.picker.directory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import java.io.File
import java.io.FileFilter

/**
 * ファイル情報リストビュー表示用アダプタ
 * @param context コンテキスト
 * @param baseDirectory 基点ディレクトリ
 */
class Adapter (var context: Context, private var baseDirectory: File) : BaseAdapter() {
    private var itemList: MutableList<FileInfo> = mutableListOf()
    init {
        baseDirectory.parentFile?.let{
            itemList.add(FileInfo("../", it))
        }
        itemList.addAll(getSubDirectoryList())
    }

    /**
     * サブディレクトリのリストを取得する
     * @return サブディレクトリのリスト
     */
    private fun getSubDirectoryList(): List<FileInfo> {
        return baseDirectory.listFiles(FileFilter {it.isDirectory})
            ?.map { FileInfo(it.name + "/", it) }
            ?: listOf()
    }

    /**
     * リスト件数取得
     * @return リスト件数
     */
    override fun getCount(): Int { return itemList.count() }

    /**
     * 要素取得
     * @param index インデックス
     * @return 要素
     */
    override fun getItem(index: Int): FileInfo { return itemList[index] }

    /**
     * ID取得
     * @param index インデックス
     * @return ID（といってもインデックスをキャストしてそのまま返すだけ）
     */
    override fun getItemId(index: Int): Long { return index.toLong() }

    /**
     * リストビュー取得
     *
     * アダプタ内容を表現するリストビューを取得する
     * ViewHolderパターンを利用しているが、正直よくわかっていない。
     * @param index インデックス
     * @param view ビュー
     * @param parent 親要素
     */
    override fun getView(index: Int, view: View?, parent: ViewGroup?): View {
        view?.let {
            val viewHolder = it.tag as TextView
            viewHolder.text = itemList[index].text
            return it
        }

        val line = LinearLayout(this.context)
        line.orientation = LinearLayout.VERTICAL
        val name = TextView(this.context)
        name.textSize = 20F
        name.text = itemList[index].text
        line.addView(name)

        line.tag = name

        return line
    }

    /**
     * ファイル情報
     *
     * ファイル・フォルダ選択ダイアログに表示する要素
     * @param text 表示名
     * @param file ファイルもしくはフォルダ
     */
    class FileInfo(var text:String, var file: File) : Comparable<FileInfo> {
        /**
         * 比較（ソートのため）
         *
         * ディレクトリ -> ファイル順。ファイル/ディレクトリ同士なら大文字小文字区別せずアルファベット順
         * @param other 比較対象
         * @return 比較結果
         */
        override fun compareTo(other: FileInfo): Int {
            return if (file.isDirectory && !other.file.isDirectory) {
                -1
            } else if (!file.isDirectory && other.file.isDirectory) {
                1
            } else {
                file.absolutePath.lowercase().compareTo(other.file.absolutePath.lowercase())
            }
        }
    }
}