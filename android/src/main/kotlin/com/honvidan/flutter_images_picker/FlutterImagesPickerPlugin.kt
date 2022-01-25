package com.honvidan.flutter_images_picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.util.ArrayList

import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.view.FlutterView

import com.imagepicker.features.ImagePicker
import com.imagepicker.model.Image

/**
 * FlutterImagesPickerPlugin
 */
class FlutterImagesPickerPlugin : MethodCallHandler, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {

  private var pendingResult: Result? = null

  private var context: Context? = null
  private var activity: Activity? = null
  private var channel: MethodChannel? = null
  private var messenger: BinaryMessenger? = null

  private constructor(_activity: Activity, _context: Context, _channel: MethodChannel, _messenger: BinaryMessenger) {
    this.activity = _activity
    this.context = _context
    this.channel = _channel
    this.messenger = _messenger
  }


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

    /**
     * Plugin registration.
     */
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_images_picker")
      val instance = FlutterImagesPickerPlugin(registrar.activity(), registrar.context(), channel, registrar.messenger())
      registrar.addActivityResultListener(instance);
      channel.setMethodCallHandler(instance)
    }
  }

}
