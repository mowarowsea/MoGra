package jp.mowaro.mogra.file

import android.content.Context
import java.io.File
import android.content.res.Resources
import jp.mowaro.mogra.R
import jp.mowaro.mogra.util.Setting

class FileProvider (context: Context) {
    private val res: Resources = context.resources
    private var deck: List<File>
    private var currentIndex: Int

    init {
        this.deck = createDeck()
        this.currentIndex = this.deck.indexOfFirst { f: File -> f.name.equals(Setting.bookmark) }
        //bookmarkしたファイルがリスト中に存在しなければ、リストの先頭をcurrentIndexとする
        if (this.currentIndex < 0) this.currentIndex = 0
    }

    fun open(): File {
        //currentIndexがリスト外の場合はループする
        this.currentIndex =
            if (this.currentIndex < 0)
                this.deck.count() -1
            else if(this.deck.count() <= this.currentIndex)
                0
            else
                this.currentIndex

        Setting.bookmark = deck[this.currentIndex].name
        Setting.commit()
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

    private fun createDeck(): List<File> {
        val directory = Setting.directory
        val file = File(directory)
        val deck = file.listFiles().orEmpty().toList()
        return sortDeck(deck)
    }

    private fun sortDeck(fileList: List<File>): List<File> {
        val map = mapOf(
            res.getString(R.string.order_by_random) to OrderByRandom(),
            res.getString(R.string.order_by_name) to OrderByName(),
            res.getString(R.string.order_by_name_desc) to OrderByNameDesc(),
            res.getString(R.string.order_by_date) to OrderByDate(),
            res.getString(R.string.order_by_date_desc) to OrderByDataDesc()
        )
        val orderBy = map[Setting.orderBy]
        return orderBy?.sort(fileList).orEmpty()
    }

    private interface OrderBy {
        fun sort(deck: List<File>): List<File>
    }

    private class OrderByRandom : OrderBy {
        override fun sort(deck: List<File>): List<File> {
            return deck.shuffled()
        }
    }

    private class OrderByDate : OrderBy {
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