package com.honvidan.flutter_images_picker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import com.honvidan.flutter_images_picker.imagepicker.ImagePicker
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import java.io.File

/**
 * FlutterImagesPickerPlugin
 */
class FlutterImagesPickerPlugin : FlutterPlugin, ActivityAware, MethodCallHandler, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {

  private var pendingResult: Result? = null

  private var activity: Activity? = null

  val PROFILE_IMAGE_REQ_CODE: Int = 101
  val GALLERY_IMAGE_REQ_CODE: Int = 102
  val CAMERA_IMAGE_REQ_CODE: Int = 103

  private val mCameraUri: Uri? = null
  private val mGalleryUri: Uri? = null
  private val mProfileUri: Uri? = null

  private val imgProfileInfo: ImageView? = null
  private val imgCameraInfo: ImageView? = null
  private val imgGalleryInfo: ImageView? = null

  private val imgProfile: ImageView? = null
  private val imgGallery: ImageView? = null
  private val imgCamera: ImageView? = null

  private val imgProfileCode: ImageView? = null
  private val imgGalleryCode: ImageView? = null
  private val imgCameraCode: ImageView? = null

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
    return false
  }

  fun pickProfileImage() {
    this.activity?.let {
      ImagePicker.with(it)
        // Crop Square image
        .galleryOnly()
        .cropSquare()
        .setImageProviderInterceptor { imageProvider -> // Intercept ImageProvider
          Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
        }
        .setDismissListener {
          Log.d("ImagePicker", "Dialog Dismiss")
        }
        // Image resolution will be less than 512 x 512
        .maxResultSize(200, 200)
        .start(PROFILE_IMAGE_REQ_CODE)
    }
  }

  fun pickGalleryImage() {
    this.activity?.let {
      ImagePicker.with(it)
        // Crop Image(User can choose Aspect Ratio)
        .crop()
        // User can only select image from Gallery
        .galleryOnly()

        .galleryMimeTypes( // no gif images at all
          mimeTypes = arrayOf(
            "image/png",
            "image/jpg",
            "image/jpeg"
          )
        )
        // Image resolution will be less than 1080 x 1920
        .maxResultSize(1080, 1920)
        // .saveDir(getExternalFilesDir(null)!!)
        .start(GALLERY_IMAGE_REQ_CODE)
    }
  }

  fun pickCameraImage() {
    this.activity?.let {
      ImagePicker.with(it)
        // User can only capture image from Camera
        .cameraOnly()
        // Image size will be less than 1024 KB
        // .compress(1024)
        //  Path: /storage/sdcard0/Android/data/package/files
        .saveDir(it.getExternalFilesDir(null)!!)
        //  Path: /storage/sdcard0/Android/data/package/files/DCIM
        .saveDir(it.getExternalFilesDir(Environment.DIRECTORY_DCIM)!!)
        //  Path: /storage/sdcard0/Android/data/package/files/Download
        .saveDir(it.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!)
        //  Path: /storage/sdcard0/Android/data/package/files/Pictures
        .saveDir(it.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
        //  Path: /storage/sdcard0/Android/data/package/files/Pictures/ImagePicker
        .saveDir(File(
          it.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!,
          "ImagePicker"
        ))
        //  Path: /storage/sdcard0/Android/data/package/files/ImagePicker
        .saveDir(it.getExternalFilesDir("ImagePicker")!!)
        //  Path: /storage/sdcard0/Android/data/package/cache/ImagePicker
        .saveDir(File(it.getExternalCacheDir(), "ImagePicker"))
        //  Path: /data/data/package/cache/ImagePicker
        .saveDir(File(it.getCacheDir(), "ImagePicker"))
        //  Path: /data/data/package/files/ImagePicker
        .saveDir(File(it.getFilesDir(), "ImagePicker"))

        // Below saveDir path will not work, So do not use it
        //  Path: /storage/sdcard0/DCIM
        //  .saveDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
        //  Path: /storage/sdcard0/Pictures
        //  .saveDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
        //  Path: /storage/sdcard0/ImagePicker
        //  .saveDir(File(Environment.getExternalStorageDirectory(), "ImagePicker"))

        .start(CAMERA_IMAGE_REQ_CODE)
    }
  }

  private fun presentPicker(maxImages: Int) {
    pickGalleryImage()
  }

  override fun onMethodCall(call: MethodCall, result: Result) {

    this.pendingResult = result;

    when (call.method) {
      PICK_IMAGES -> {
        val maxImages = call.argument<Int>(MAX_IMAGES)!!

        if (maxImages <= 0) {
          return
        }

        presentPicker(maxImages)
      }
      "getPlatformVersion" -> result.success("Android " + android.os.Build.VERSION.RELEASE)
      else -> result.notImplemented()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (resultCode == Activity.RESULT_OK) {

      // Uri object will not be null for RESULT_OK
      val uri = data!!.data
      val list: ArrayList<Map<String,String>> = ArrayList<Map<String,String>>(0)
      val containerMap = HashMap<String, String>()
      containerMap.put("path", uri?.path!!)
      list.add(containerMap)
      this.pendingResult?.success(list)
    } else if (resultCode == ImagePicker.RESULT_ERROR) {
      Toast.makeText(this.activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
    } else {
      Toast.makeText(this.activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
    }
//    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
//      // Get a list of picked images
//      val images: List<Image> = ImagePicker.getImages (data)
//      val list: ArrayList<Map<String,String>> = ArrayList<Map<String,String>>(0)
//
//      for (image in images) {
//        val containerMap = HashMap<String, String>()
//        containerMap.put("path", image.path)
//        list.add(containerMap)
//      }
//
//      this.pendingResult?.success(list)
//    }

    return false
  }

  companion object {

    private val PICK_IMAGES = "pickImages"
    private val MAX_IMAGES = "maxImages"

  }

  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_images_picker")
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

  }

  override fun onDetachedFromActivity() {

  }

}
