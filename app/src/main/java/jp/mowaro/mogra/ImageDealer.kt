package jp.mowaro.mogra

import java.io.File
import android.content.res.Resources

class ImageDealer {
    private var res: Resources
    private var deck: List<File>
    private var currentIndex: Int

    public constructor(setting: Setting) {
        this.res = setting.context.resources
        this.deck = createDeck(setting)
        this.currentIndex = this.deck.indexOfFirst { f: File -> f.name.equals(setting.bookmark) }
        if (this.currentIndex < 0) this.currentIndex = 0
    }

    public fun deal(): File {
        if (this.currentIndex < 0)
            this.currentIndex = this.deck.count() -1
        else if(this.deck.count() <= this.currentIndex)
            this.currentIndex = 0

        return deck[this.currentIndex]
    }

    public fun dealNext(): File {
        this.currentIndex++
        return deal()
    }

    public fun dealPref(): File {
        this.currentIndex--
        return deal()
    }

    private fun createDeck(setting: Setting): List<File> {
        var deck: List<File> = File(setting.directory).listFiles().toList()
        deck = sortDeck(deck, setting).orEmpty()
        return deck
    }

    private fun sortDeck(fileList: List<File>, setting: Setting): List<File>? {
        var map = mapOf(
            res.getString(R.string.order_by_random) to OrderByRandom(),
            res.getString(R.string.order_by_name) to OrderByName(),
            res.getString(R.string.order_by_name_desc) to OrderByNameDesc(),
            res.getString(R.string.order_by_date) to OrderByDate(),
            res.getString(R.string.order_by_date_desc) to OrderByDataDesc()
        )
        var sorter = map[setting.orderBy]
        return sorter?.sort(fileList)
    }

    interface Sort {
        fun sort(deck: List<File>): List<File>
    }

    private class OrderByRandom :Sort {
        override fun sort(deck: List<File>): List<File> {
            return deck.shuffled()
        }
    }

    private class OrderByDate :Sort {
        override fun sort(deck: List<File>): List<File> {
            return deck.sortedBy { file: File -> file.lastModified() }
        }
    }

    private class OrderByDataDesc: Sort {
        override fun sort(deck: List<File>): List<File> {
            return OrderByDate().sort(deck).reversed()
        }
    }

    private class OrderByName: Sort {
        override fun sort(deck: List<File>): List<File> {
            return deck.sortedBy { file: File -> file.name }
        }
    }

    private class OrderByNameDesc: Sort {
        override fun sort(deck: List<File>): List<File> {
            return OrderByName().sort(deck).reversed()
        }
    }
}