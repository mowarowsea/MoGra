package layout

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.io.File

/**
 * ファイル・フォルダ選択ダイアログ
 * 参考：https://www.hiramine.com/programming/android/fileselectiondialog.html
 * @param context 呼び出し元
 * @param listener 結果受取先
 */
class FileSelectionDialog(val context: Context, private val listener: OnFileSelectListener): AdapterView.OnItemClickListener {
    /**
     * ファイル情報
     * ファイル・フォルダ選択ダイアログに表示する要素
     * @param text 表示名
     * @param file ファイルもしくはフォルダ
     */
    class FileInfo(var text:String, var file:File) : Comparable<FileInfo> {
        /**
         * 並び順。
         * ディレクトリ -> ファイル順。ファイル/ディレクトリ同士なら大文字小文字区別せずアルファベット順
         */
        override fun compareTo(other: FileInfo): Int {
            return if (file.isDirectory && !other.file.isDirectory) {
                -1
            } else if (!file.isDirectory && other.file.isDirectory) {
                1
            } else {
                file.name.lowercase().compareTo(other.file.name.lowercase())
            }
        }

    }

    /**
     * アダプタ
     */
    class FileInfoArrayAdapter(var context: Context, var fileList: List<FileInfo>) : BaseAdapter() {
        override fun getCount(): Int {
            return fileList.count()
        }

        override fun getItem(index: Int): FileInfo {
            return fileList[index]
        }

        override fun getItemId(index: Int): Long {
            return index.toLong()
        }

        class ViewHolder(var name: TextView, var size: TextView)

        override fun getView(index: Int, view: View?, parent: ViewGroup?): View {
            var view: View? = view
            var viewHolder: ViewHolder = ViewHolder(TextView(this.context), TextView(this.context))

            view?.let {
                viewHolder = it.tag as ViewHolder
            } ?: run {
                var line: LinearLayout = LinearLayout(this@FileInfoArrayAdapter.context)
                line.orientation = LinearLayout.VERTICAL
                line.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                var name: TextView = TextView(this@FileInfoArrayAdapter.context)
                name.textSize = 24F
                line.addView(name, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                var size: TextView = TextView(this@FileInfoArrayAdapter.context)
                size.textSize = 12F
                line.addView(size, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

                view = line
                viewHolder = ViewHolder(name, size)
                view!!.tag = viewHolder
            }

            var file: FileInfo = fileList[index]
            if (file.file.isDirectory) {
                viewHolder.name.text = file.text + "/"
                viewHolder.size.text = "(directory)"
            } else {
                viewHolder.name.text = file.file.name
                viewHolder.size.text = file.file.length().toString() + "[B]"
            }

            return view!!
        }
    }

    /**
     * 選択したファイルの情報を取り出すためのリスナーインターフェース
     */
    interface OnFileSelectListener {
        fun onFileSelect(file: File)
    }

    private var adapter: FileInfoArrayAdapter? = null
    private var dialog: AlertDialog? = null

    fun show(file: File) {
        val fileInfoList: MutableSet<FileInfo> = mutableSetOf()
        if (!file.parent.isNullOrEmpty()) {
            fileInfoList.add(FileInfo("..", file.parentFile))
        }
        val fileList: Array<File> = file.listFiles()
        fileInfoList.addAll(fileList.map { file: File -> FileInfo(file.name, file) }.sorted())
        this.adapter = FileInfoArrayAdapter(context, fileInfoList.toList())

        var listView: ListView = ListView(context)
        listView.isScrollingCacheEnabled = false
        listView.onItemClickListener = this
        listView.adapter = this.adapter

        this.dialog = AlertDialog.Builder(context)
            .setTitle(file.absolutePath)
            .setNegativeButton("Cancel", null)
            .setView(listView)
            .show()
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.dialog?.let {
            it.dismiss()
        }
        this.dialog = null

        var selectedFile: File = this.adapter!!.getItem(position).file
        if (selectedFile.isDirectory) {
            show(selectedFile)
        } else {
            listener.onFileSelect(selectedFile)
        }

    }
}