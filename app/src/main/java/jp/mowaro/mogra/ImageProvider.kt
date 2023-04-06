package jp.mowaro.mogra

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.Size

class ImageProvider (var ownerSize: Size, var setting: Setting) {
    private var res: Resources = setting.context.resources

    /**
     * 参考： https://gist.github.com/STAR-ZERO/3413415
     */
    fun get(path: String): Drawable? {
        var options: BitmapFactory.Options = BitmapFactory.Options()

        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        return null

    }

    /**
     * 回転角度計算
     * @param options 読み込んだ画像のオプション情報
     * @param setting 設定
     * @return 回転させるべき角度を設定したMatrix
     */
    fun calcMatrix(options: BitmapFactory.Options, setting: Setting) :Matrix {
        val matrix: Map<String, Map<String, Float>> = mapOf(
            // 元画像が縦長かつ、自動回転が横向きに設定されている場合、回転させる
            "portrait" to mapOf(
                res.getString(R.string.auto_orient_horizontally_left90) to 90F,
                res.getString(R.string.auto_orient_horizontally_right90) to 270F,
            ),
            // 元画像が横長かつ、自動回転が縦向きに設定されている場合、回転させる
            "landscape" to mapOf(
                res.getString(R.string.auto_orient_vertically_left90) to 90F,
                res.getString(R.string.auto_orient_vertically_right90) to 270F
            )
        )

        val heightWidthRatio: Double = (options.outHeight.toDouble() / options.outWidth)
        var originOrient: String = ""
        when {
            heightWidthRatio > 1.0 -> {originOrient = "portrait"}
            heightWidthRatio < 1.0 -> {originOrient = "landscape"}
        }
        var degrees: Float
        matrix[originOrient]?.get(setting.orient)?.let{degrees = it}.also { degrees = 0F }

        val result: Matrix = Matrix()
        result.setRotate(degrees)
        return result
    }


}