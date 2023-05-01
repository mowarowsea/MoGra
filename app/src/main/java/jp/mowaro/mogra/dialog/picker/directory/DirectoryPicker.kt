package jp.mowaro.mogra.dialog.picker.directory

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.io.File

/**
 * フォルダ選択ダイアログ
 * 参考：https://www.hiramine.com/programming/android/fileselectiondialog.html
 * @param context 呼び出し元
 * @param listener 結果受取先
 */
class DirectoryPicker(val context: Context, private val listener: OnSelectListener): AdapterView.OnItemClickListener, OnClickListener {
    /** カレントディレクトリ */
    private lateinit var currentDirectory: File
    /** ファイル情報リストビュー表示用アダプタ */
    private var adapter: Adapter? = null
    /** ダイアログ本体 */
    private var dialog: AlertDialog? = null

    /**
     * ディレクトリ選択ダイアログを表示する
     * @param baseDirectory 初期ディレクトリ
     * @throws IllegalArgumentException baseDirectoryがDirectoryでない場合発生する
     */
    fun show(baseDirectory: File) {
        if (!baseDirectory.isDirectory) {throw java.lang.IllegalArgumentException("BaseDirectory must be a Directory")}

        this.currentDirectory = baseDirectory
        this.adapter = Adapter(context, baseDirectory)

        val listView: ListView = createListView()

        this.dialog = createDialog(listView).show()
    }


    /**
     * ダイアログに表示するリストビューを作成する
     * @return ダイアログに表示するリストビュー
     */
    private fun createListView(): ListView {
        val listView = ListView(context)
        listView.isScrollingCacheEnabled = false
        listView.onItemClickListener = this
        listView.adapter = this.adapter
        return listView
    }

    /**
     * ダイアログを作成する
     * @param listView 表示するリストビュー
     * @return ダイアログ
     */
    private fun createDialog(listView: ListView): AlertDialog.Builder {
        return AlertDialog.Builder(context)
        .setTitle(this.currentDirectory.absolutePath)
        .setNegativeButton("Cancel", null)
        .setPositiveButton("select", this)
        .setView(listView)
    }

    /**
     * ディレクトリリストの要素クリック時イベント
     *
     * 現在表示しているダイアログは消去して
     * クリックされたディレクトリをカレントディレクトリとしたディレクトリ選択ダイアログを表示する
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.dialog?.dismiss()
        this.dialog = null

        val selectedFile: File? = this.adapter?.getItem(position)?.file
        if (selectedFile?.isDirectory == true) {
            show(selectedFile)
        }
    }

    /**
     * Selectボタンクリック時イベント
     *
     * [OnSelectListener.onSelect]を発火する。引数はカレントディレクトリである。
     */
    override fun onClick(dialog: DialogInterface?, witch: Int) {
        listener.onSelect(this.currentDirectory)
    }
}