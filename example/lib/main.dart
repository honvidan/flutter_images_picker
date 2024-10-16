import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_images_picker/flutter_images_picker.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      title: "Images Picker",
      home: TestPage(),
    );
  }
}

class TestPage extends StatefulWidget {
  const TestPage({Key? key}) : super(key: key);

  @override
  _TestPageState createState() => _TestPageState();
}

class _TestPageState extends State<TestPage> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String? platformVersion;
    try {
      platformVersion = await FlutterImagesPicker.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion ?? "Unknown";
    });
  }

  void takeImage(BuildContext context) async {
    List<Uri?> images = await FlutterImagesPicker.pickImages(maxImages: 5);

    Navigator.of(context).pop();
  }

  Future _pickImage(BuildContext context) async {
    var isPopup = false;

    showDialog<void>(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          if (!isPopup) {
            isPopup = true;
            takeImage(context);
          }
          return const Center();
        });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
        floatingActionButton: Column(
          mainAxisAlignment: MainAxisAlignment.end,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(top: 16.0),
              child: FloatingActionButton(
                onPressed: () {
                  _pickImage(context);
                },
                tooltip: 'Take a Photo',
                child: const Icon(Icons.photo_library),
              ),
            ),
          ],
        ));
  }
}
