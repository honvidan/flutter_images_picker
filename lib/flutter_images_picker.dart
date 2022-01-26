import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

/// FlutterImagesPicker is main class for get Images from Gallery and Camera.
///
/// This will call native library
class FlutterImagesPicker {
  /// A method channel calling native library
  /// `_channel`
  static const MethodChannel _channel = MethodChannel('flutter_images_picker');

  /// A platformVersion
  /// `platformVersion`
  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// pickImages will pickImages, input maxImages
  /// output is List of Files
  static Future<List<File>> pickImages({
    required int maxImages,
    enableGestures = true,
  }) async {
    final List<dynamic> images = await _channel.invokeMethod(
        'pickImages', <String, dynamic>{
      "maxImages": maxImages,
      "enableGestures": enableGestures
    });

    return images.map((f) {
      return File(f["path"]);
    }).toList();
  }
}
