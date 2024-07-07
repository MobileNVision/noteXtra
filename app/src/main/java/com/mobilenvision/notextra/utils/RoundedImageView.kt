package com.mobilenvision.notextra.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView

import kotlin.jvm.JvmOverloads;

class RoundedImageView @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = RoundedImageView::class.java.simpleName
    }

    override fun onDraw(canvas: Canvas) {
        try {
            val drawable = drawable ?: return

            if (width == 0 || height == 0) return

            val bitmap = when (drawable) {
                is BitmapDrawable -> drawable.bitmap.copy(Bitmap.Config.ARGB_8888, true)
                is ColorDrawable -> Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
                    Canvas(it).drawColor(drawable.color)
                }
                else -> return
            }

            val roundBitmap = getRoundedCroppedBitmap(bitmap, width.coerceAtMost(height))
            canvas.drawBitmap(roundBitmap, 0f, 0f, null)
        } catch (e: Exception) {
            Log.e(TAG, "onDraw Exception", e)
        }
    }

    private fun getRoundedCroppedBitmap(bitmap: Bitmap, radius: Int): Bitmap {
        val finalBitmap = if (bitmap.width != radius || bitmap.height != radius) {
            Bitmap.createScaledBitmap(bitmap, radius, radius, false)
        } else {
            bitmap
        }

        val output = Bitmap.createBitmap(finalBitmap.width, finalBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
            color = Color.parseColor("#BAB399")
        }
        val rect = Rect(0, 0, finalBitmap.width, finalBitmap.height)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(finalBitmap.width / 2f, finalBitmap.height / 2f, finalBitmap.width / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(finalBitmap, rect, rect, paint)

        return output
    }
}
