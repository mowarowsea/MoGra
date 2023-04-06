package jp.mowaro.mogra

import java.io.File
import android.content.res.Resources

class FileProvider (setting: Setting) {
    private var res: Resources
    private var deck: List<File>
    private var currentIndex: Int

    init {
        this.res = setting.context.resources
        this.deck = createDeck(setting)
        this.currentIndex = this.deck.indexOfFirst { f: File -> f.name.equals(setting.bookmark) }
        //bookmarkしたファイルがリスト中に存在しなければ、リストの先頭をcurrentIndexとする
        if (this.currentIndex < 0) this.currentIndex = 0
    }

    fun open(): File {
        //currentIndexがリスト外の場合はループする
        if (this.currentIndex < 0)
            this.currentIndex = this.deck.count() -1
        else if(this.deck.count() <= this.currentIndex)
            this.currentIndex = 0

        return deck[this.currentIndex]
    }

    fun getNext(): File {
        this.currentIndex++
        return open()
    }

    fun getPrev(): File {
        this.currentIndex--
        return open()
    }

    private fun createDeck(setting: Setting): List<File> {
        val directory = setting.directory.toString()
        val file = File(directory)
        val deck = file.listFiles().orEmpty().toList()
        return sortDeck(deck, setting)
    }

    private fun sortDeck(fileList: List<File>, setting: Setting): List<File> {
        val map = mapOf(
            res.getString(R.string.order_by_random) to OrderByRandom(),
            res.getString(R.string.order_by_name) to OrderByName(),
            res.getString(R.string.order_by_name_desc) to OrderByNameDesc(),
            res.getString(R.string.order_by_date) to OrderByDate(),
            res.getString(R.string.order_by_date_desc) to OrderByDataDesc()
        )
        val orderBy = map[setting.orderBy]
        return orderBy?.sort(fileList).orEmpty()
    }

    private interface OrderBy {
        fun sort(deck: List<File>): List<File>
    }

    private class OrderByRandom :OrderBy {
        override fun sort(deck: List<File>): List<File> {
            return deck.shuffled()
        }
    }

    private class OrderByDate :OrderBy {
        override fun sort(deck: List<File>): List<File> {
            return deck.sortedBy { file: File -> file.lastModified() }
        }
    }

    private class OrderByDataDesc: OrderBy {
        override fun sort(deck: List<File>): List<File> {
            return OrderByDate().sort(deck).reversed()
        }
    }

    private class OrderByName: OrderBy {
        override fun sort(deck: List<File>): List<File> {
            return deck.sortedBy { file: File -> file.name }
        }
    }

    private class OrderByNameDesc: OrderBy {
        override fun sort(deck: List<File>): List<File> {
            return OrderByName().sort(deck).reversed()
        }
    }
}