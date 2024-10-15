package com.honvidan.flutter_images_picker.imagepicker.listener

internal interface ResultListener<T> {

    fun onResult(t: T?)
}
