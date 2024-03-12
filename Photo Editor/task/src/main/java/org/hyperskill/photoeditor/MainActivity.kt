package org.hyperskill.photoeditor

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Images
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStream
import java.lang.Math.pow
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    companion object {
        private const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val PERMISSION_REQUEST_CODE = 0
    }

    private lateinit var currentImage: ImageView
    private lateinit var showGalleryBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var slBrightness: Slider
    private lateinit var slContrast: Slider
    private lateinit var slSaturation: Slider
    private lateinit var slGamma: Slider

    private val imageFilter = ImageFilter()
    private var lastJob: Job? = null  // the field to keep track of the last job in case we wish to cancel it


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        //do not change this line
        currentImage.setImageBitmap(imageFilter.originalBitmap)

        val activityResultLauncher =
                registerForActivityResult(StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val photoUri = result.data?.data ?: return@registerForActivityResult
                        // code to update ivPhoto with loaded image
                        ImageFilter.decodeBitmapFromUri(this, photoUri)?.let { bitmap ->
                            imageFilter.originalBitmap = bitmap
                            slBrightness.value = 0.0f
                            currentImage.setImageBitmap(bitmap)
                        }
                    }
                }

        showGalleryBtn.setOnClickListener {
            activityResultLauncher.launch(
                    Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
            )
        }

        val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        saveImage()
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // feature requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                        showExplanatoryDialog()
                    }
                }

        saveBtn.setOnClickListener {
            if (hasPermission()) {
                saveImage()
                return@setOnClickListener
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }

        slBrightness.addOnChangeListener {_, value, _ ->
            lastJob?.cancel()
            lastJob = GlobalScope.launch(Dispatchers.Default) {
                val (brightnessBitmap, avgBrightness) = imageFilter.adjustBrightness(value.toInt())
                val contrastBitmap = imageFilter.adjustContrast(brightnessBitmap, slContrast.value.toInt(), avgBrightness)
                val saturationBitmap = imageFilter.adjustSaturation(contrastBitmap, slSaturation.value.toInt())
                val gammaBitmap = imageFilter.adjustGamma(saturationBitmap, slGamma.value.toDouble())
                runOnUiThread {
                    currentImage.setImageBitmap(gammaBitmap)
                }
            }
        }

        slContrast.addOnChangeListener { _, value, _ ->
            lastJob?.cancel()
            lastJob = GlobalScope.launch(Dispatchers.Default) {
                val (brightnessBitmap, avgBrightness) = imageFilter.adjustBrightness(slBrightness.value.toInt())
                val contrastBitmap = imageFilter.adjustContrast(brightnessBitmap, value.toInt(), avgBrightness)
                val saturationBitmap = imageFilter.adjustSaturation(contrastBitmap, slSaturation.value.toInt())
                val gammaBitmap = imageFilter.adjustGamma(saturationBitmap, slGamma.value.toDouble())
                runOnUiThread {
                    currentImage.setImageBitmap(gammaBitmap)
                }
            }
        }

        slSaturation.addOnChangeListener { _, value, _ ->
            lastJob?.cancel()
            lastJob = GlobalScope.launch(Dispatchers.Default) {
                val (brightnessBitmap, avgBrightness) = imageFilter.adjustBrightness(slBrightness.value.toInt())
                val contrastBitmap = imageFilter.adjustContrast(brightnessBitmap, slContrast.value.toInt(), avgBrightness)
                val saturationBitmap = imageFilter.adjustSaturation(contrastBitmap, value.toInt())
                val gammaBitmap = imageFilter.adjustGamma(saturationBitmap, slGamma.value.toDouble())
                runOnUiThread {
                    currentImage.setImageBitmap(gammaBitmap)
                }
            }
        }

        slGamma.addOnChangeListener { _, value, _ ->
            lastJob?.cancel()
            lastJob = GlobalScope.launch(Dispatchers.Default) {
                val (brightnessBitmap, avgBrightness) = imageFilter.adjustBrightness(slBrightness.value.toInt())
                val contrastBitmap = imageFilter.adjustContrast(brightnessBitmap, slContrast.value.toInt(), avgBrightness)
                val saturationBitmap = imageFilter.adjustSaturation(contrastBitmap, slSaturation.value.toInt())
                val gammaBitmap = imageFilter.adjustGamma(saturationBitmap, value.toDouble())
                runOnUiThread {
                    currentImage.setImageBitmap(gammaBitmap)
                }
            }
        }
    }

    private fun showExplanatoryDialog() {
        AlertDialog.Builder(this)
                .setTitle("This feature isn't available")
                .setMessage("Because you've denied this permission, this feature won't be available for you. if you want to use this feature, please allow WRITE_EXTERNAL_STORAGE to this App in your device settings")
                .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                    Log.d(this.javaClass.simpleName, "validate message")
                }
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty()) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage()
                } else {
                    showExplanatoryDialog()
                }
            }
        }
    }

    private fun saveImage() {
        val bitmap = currentImage.drawable.toBitmap()
        val values = ContentValues()
        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(Images.Media.MIME_TYPE, "image/jpeg")
        values.put(Images.ImageColumns.WIDTH, bitmap.width)
        values.put(Images.ImageColumns.HEIGHT, bitmap.height)

        val uri = this.contentResolver.insert(
                Images.Media.EXTERNAL_CONTENT_URI, values
        ) ?: return

        contentResolver.openOutputStream(uri).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }

    private fun bindViews() {
        currentImage = findViewById(R.id.ivPhoto)
        showGalleryBtn = findViewById(R.id.btnGallery)
        saveBtn = findViewById(R.id.btnSave)
        slBrightness = findViewById(R.id.slBrightness)
        slContrast = findViewById(R.id.slContrast)
        slSaturation = findViewById(R.id.slSaturation)
        slGamma = findViewById(R.id.slGamma)
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            PermissionChecker.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
        }
    }
}

class ImageFilter {

    companion object {
        fun decodeBitmapFromUri(context: Context, uri: Uri): Bitmap? {
            var inputStream: InputStream? = null
            return try {
                // Open an input stream from the URI
                inputStream = context.contentResolver.openInputStream(uri)

                // Decode the input stream into a Bitmap
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                // Close the input stream
                inputStream?.close()
            }
        }
    }

    var originalBitmap: Bitmap = createBitmap()

    fun resetBitmap() {
        originalBitmap = createBitmap()
    }

    fun adjustBrightness(brightness: Int): Pair<Bitmap, Int> {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val pixels = IntArray(width * height)
        originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var allPixelsBrightness: Long = 0

        for (i in pixels.indices) {
            val pixel = pixels[i]
            // Apply brightness adjustment
            val red = (Color.red(pixel) + brightness).coerceIn(0, 255)
            val green = (Color.green(pixel) + brightness).coerceIn(0, 255)
            val blue = (Color.blue(pixel) + brightness).coerceIn(0, 255)

            pixels[i] = Color.rgb(red, green, blue)
            allPixelsBrightness += (red + green + blue) / 3
        }

        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmapOut to (allPixelsBrightness / pixels.size).toInt()
    }

    fun adjustContrast(bitmap: Bitmap, contrast: Int, avgBright: Int): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val alpha: Double = (255 + contrast).toDouble() / (255 - contrast).toDouble()

        for (i in pixels.indices) {
            val pixel = pixels[i]
            // Apply contrast adjustment
            val red = (alpha * (Color.red(pixel) - avgBright) + avgBright).toInt().coerceIn(0, 255)
            val green = (alpha * (Color.green(pixel) - avgBright) + avgBright).toInt().coerceIn(0, 255)
            val blue = (alpha * (Color.blue(pixel) - avgBright) + avgBright).toInt().coerceIn(0, 255)

            pixels[i] = Color.rgb(red, green, blue)
        }

        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmapOut
    }

    fun adjustSaturation(bitmap: Bitmap, saturation: Int): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val alpha: Double = (255 + saturation).toDouble() / (255 - saturation).toDouble()

        for (i in pixels.indices) {
            val pixel = pixels[i]
            // Apply saturation adjustment
            val rgbAvg = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
            val red = (alpha * (Color.red(pixel) - rgbAvg) + rgbAvg).toInt().coerceIn(0, 255)
            val green = (alpha * (Color.green(pixel) - rgbAvg) + rgbAvg).toInt().coerceIn(0, 255)
            val blue = (alpha * (Color.blue(pixel) - rgbAvg) + rgbAvg).toInt().coerceIn(0, 255)

            pixels[i] = Color.rgb(red, green, blue)
        }

        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmapOut
    }

    fun adjustGamma(bitmap: Bitmap, gamma: Double): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            // Apply gamma adjustment
            val red = (255 * (Color.red(pixel).toDouble() / 255.0).pow(gamma)).toInt().coerceIn(0, 255)
            val green = (255 * (Color.green(pixel).toDouble() / 255.0).pow(gamma)).toInt().coerceIn(0, 255)
            val blue = (255 * (Color.blue(pixel).toDouble() / 255.0).pow(gamma)).toInt().coerceIn(0, 255)

            pixels[i] = Color.rgb(red, green, blue)
        }

        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmapOut
    }

    // do not change this function
    private fun createBitmap(): Bitmap {
        val width = 200
        val height = 100
        val pixels = IntArray(width * height)
        // get pixel array from source

        var R: Int
        var G: Int
        var B: Int
        var index: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get color
                R = x % 100 + 40
                G = y % 100 + 80
                B = (x+y) % 100 + 120

                pixels[index] = Color.rgb(R,G,B)

            }
        }
        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }

}