# Images Picker plugin for Flutter

[![pub package](https://img.shields.io/badge/pub-0.1.0-blue)](https://pub.dev/packages/flutter_images_picker)[![license](https://img.shields.io/badge/license-GNUv3-brightgreen)](https://github.com/honvidan/flutter_images_picker/blob/main/LICENSE)


A Flutter plugin for iOS and Android for picking images from the image library,
and taking new pictures with the camera.

## Installation

First, add `flutter_images_picker` as a [dependency in your pubspec.yaml file](https://flutter.dev/docs/development/platform-integration/platform-channels).

### iOS

This plugin requires iOS 9.0 or higher.

Starting with version **0.1.0** the iOS implementation uses ImagePicker to pick (multiple) images on iOS 14 or higher.

Add the following keys to your _Info.plist_ file, located in `<project root>/ios/Runner/Info.plist`:

* `NSPhotoLibraryUsageDescription` - describe why your app needs permission for the photo library. This is called _Privacy - Photo Library Usage Description_ in the visual editor.
* `NSCameraUsageDescription` - describe why your app needs access to the camera. This is called _Privacy - Camera Usage Description_ in the visual editor.
* `NSMicrophoneUsageDescription` - describe why your app needs access to the microphone, if you intend to record videos. This is called _Privacy - Microphone Usage Description_ in the visual editor.

### Android

Starting with version **0.1.0** the Android implementation support to pick (multiple) images on Android 4.3 or higher.

No configuration required - the plugin should work out of the box.

It is no longer required to add `android:requestLegacyExternalStorage="true"` as an attribute to the `<application>` tag in AndroidManifest.xml, as `image_picker` has been updated to make use of scoped storage.

**Note:** Images and videos picked using the camera are saved to your application's local cache, and should therefore be expected to only be around temporarily.
If you require your picked image to be stored permanently, it is your responsibility to move it to a more permanent location.

## Basic usage

```dart
void takeImage(BuildContext context) async {
  List<File?> images  = await FlutterImagesPicker.pickImages(maxImages: 5);
  Navigator.of(context).pop();
}
```

## License

The Flutter images picker project is available for free use, as described by the [LICENSE](https://github.com/honvidan/flutter_images_picker/blob/main/LICENSE) (GNUv3).
