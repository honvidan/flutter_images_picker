#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint flutter_images_picker.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'flutter_images_picker'
  s.version          = '0.1.4'
  s.summary          = 'Flutter Images Picker is a flutter plugin.'
  s.description      = <<-DESC
Flutter Images Picker is a flutter plugin.
                       DESC
  s.homepage         = 'https://honvidan.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Dan Hon' => 'dan.honvi@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*.{swift,h,m}'
  s.resource_bundles = { 'ImagePicker' => ['Classes/ImagePicker/Images/*.{png}'] }
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'Lightbox', '~> 2.3.0'
  s.platform = :ios, '11.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
