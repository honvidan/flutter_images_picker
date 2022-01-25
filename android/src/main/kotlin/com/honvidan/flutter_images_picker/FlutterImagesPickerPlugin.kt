package com.honvidan.flutter_images_picker

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import java.util.ArrayList

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

import com.imagepicker.features.ImagePicker
import com.imagepicker.model.Image
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

/**
 * FlutterImagesPickerPlugin
 */
class FlutterImagesPickerPlugin : FlutterPlugin, ActivityAware, MethodCallHandler, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {

  private var pendingResult: Result? = null

  private var activity: Activity? = null

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
    return false
  }

  private fun presentPicker(maxImages: Int) {
    ImagePicker.create(this.activity)
      .limit(maxImages)
      .showCamera(true)// Activity or Fragment
      .start();
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

    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      // Get a list of picked images
      val images: List<Image> = ImagePicker.getImages (data)
      val list: ArrayList<Map<String,String>> = ArrayList<Map<String,String>>(0)

      for (image in images) {
        val containerMap = HashMap<String, String>()
        containerMap.put("path", image.path)
        list.add(containerMap)
      }

      this.pendingResult?.success(list)
    }

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
