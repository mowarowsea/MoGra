package jp.mowaro.mogra.image

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.Size
import androidx.core.graphics.drawable.toDrawable
import androidx.exifinterface.media.ExifInterface
import jp.mowaro.mogra.R
import jp.mowaro.mogra.util.Setting

/**
 * 画像供給クラス
 * @param ownerSize 描画領域サイズ
 * @param context コンテキスト
 */
class ImageProvider (private var ownerSize: Size, private val context: Context) {
    /** リソース */
    private var res: Resources = context.resources

    /**
     * ローカルに存在する画像を画面と設定に沿った形で描画して返す
     * 参考： https://gist.github.com/STAR-ZERO/3413415
     * @param path 画像パス
     */
    fun get(path: String): Drawable {
        val bitmap: Bitmap = BitmapFactory.decodeFile(path)
        val exif = ExifInterface(path)
        val orient: AutoOrient = getOrient()
        val matrix: Matrix = orient.getMatrix(Size(bitmap.width, bitmap.height), ownerSize, exif)
        val oriented: Bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true )
        return oriented.toDrawable(res)
    }

    /**
     * 自動回転クラスを取得する
     */
    private fun getOrient(): AutoOrient {
        return when(Setting.orient) {
            res.getString(R.string.auto_orient_vertically_right90)
                -> HorizontallyToVertically90(context)
            res.getString(R.string.auto_orient_vertically_left90)
                -> HorizontallyToVertically270(context)
            else
                -> NoneOrient(context)
        }
    }

    /**
     * 自動回転クラス
     */
    private abstract class AutoOrient(var context: Context) {
        /** 回転角度 */
        abstract var degree: Float

        /**
         * Bitmapを回転させるためのMatrixを作成する
         * @param imageSize 画像サイズ
         * @param ownerSize 描画領域サイズ
         * @param exif 画像Exif情報
         */
        abstract fun getMatrix(imageSize: Size, ownerSize: Size, exif: ExifInterface): Matrix
        fun isVertically(imageSize: Size): Boolean {
            val rate:Float = imageSize.height.toFloat() / imageSize.width
            return (rate > 1)
        }

        /**
         * Exif情報をもとに、回転角度を調整する
         * なぜかはわからないが、ExifのOrientationが90もしくは180の時、画像が反転するので対処する
         * @param exif Exif情報
         */
        fun adjustOrientByExif(exif: ExifInterface) {
            when(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree -= 180
                ExifInterface.ORIENTATION_ROTATE_180 -> degree -= 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree -= 0
            }
        }
    }

    /**
     * 自動回転（しない）クラス
     */
    private class NoneOrient(context: Context): AutoOrient(context) {
        override var degree: Float = 0F
        override fun getMatrix(imageSize: Size, ownerSize: Size, exif: ExifInterface): Matrix {
            adjustOrientByExif(exif)
            val matrix = Matrix()
            matrix.postRotate(degree, 0F, 0F)
            val widthRatio =ownerSize.width.toFloat()/ imageSize.width
            val heightRatio = ownerSize.height.toFloat() / imageSize.height
            val resizeRatio = widthRatio.coerceAtMost(heightRatio)
            matrix.postScale(resizeRatio, resizeRatio)
            matrix.postTranslate(0F, 0F)
            return matrix
        }
    }

    /**
     * 自動回転（横長画像を90度回転）クラス
     * Matrix.postRotateによる回転は、基点が画像の左上なので移動（.postTranslate）が必要だったり
     * 回転させたうえでリサイズ（.postScale）が必要だったりする。
     * 参考：https://www.jisei-firm.com/android_develop14/
     */
    private class HorizontallyToVertically90(context: Context): AutoOrient(context) {
        override var degree: Float = 90F

        override fun getMatrix(imageSize: Size, ownerSize: Size, exif: ExifInterface): Matrix {
            if (isVertically(imageSize)) {
                return NoneOrient(context).getMatrix(imageSize, ownerSize, exif)
            }

            adjustOrientByExif(exif)
            val matrix = Matrix()
            matrix.postRotate(degree, 0F, 0F)
            val widthRatio =ownerSize.width.toFloat()/ imageSize.height
            val heightRatio = ownerSize.height.toFloat() / imageSize.width
            val resizeRatio = widthRatio.coerceAtMost(heightRatio)
            matrix.postScale(resizeRatio, resizeRatio)
            matrix.postTranslate(imageSize.height * resizeRatio, 0F)
            return matrix
        }
    }

    /**
     * 自動回転（横長画像を270度回転）クラス
     */
    private class HorizontallyToVertically270(context: Context): AutoOrient(context) {
        override var degree: Float = 270F

        override fun getMatrix(imageSize: Size, ownerSize: Size, exif: ExifInterface): Matrix {
            if (isVertically(imageSize)) {
                return NoneOrient(context).getMatrix(imageSize, ownerSize, exif)
            }

            adjustOrientByExif(exif)
            val matrix = Matrix()
            matrix.postRotate(degree, 0F, 0F)
            val widthRatio =ownerSize.width.toFloat()/ imageSize.height
            val heightRatio = ownerSize.height.toFloat() / imageSize.width
            val resizeRatio = widthRatio.coerceAtMost(heightRatio)
            matrix.postScale(resizeRatio, resizeRatio)
            matrix.postTranslate(imageSize.height * resizeRatio * -1, 0F)
            return matrix
        }
    }
}