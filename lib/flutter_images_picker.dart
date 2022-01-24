
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterImagesPicker {
  static const MethodChannel _channel = MethodChannel('flutter_images_picker');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
