# DeepSense

Download the models below, extract and copy them onto mobile devices and set the link in DeepSense App to load them.

VGG-F Link: https://drive.google.com/file/d/0B_GMfaURPvQDQk9sU3FHdU1sUzA/view?usp=sharing

Yolo Tiny Link: https://drive.google.com/file/d/0B_GMfaURPvQDZVVFMnBXQUU3X2s/view?usp=sharing

## The app is configured to work on Samsung Galaxy S7 with Mali GPU, if you need to run it on Adreno-based devices
- 1) copy the appropriate shared libraries (libllvm-qcom.so and libOpenCL.so) from distribution/opencl/lib/armeabi-v7a/Adreno-Android5 OR distribution/opencl/lib/armeabi-v7a/Adreno-Android6 into distribution/opencl/lib/armeabi-v7a
- 2) comment out Mali-shared library in app/CMakeLists.txt and uncomment Adreno shared library

## To run the app
- 1) Download and extract the model
- 2) Put the whole model's directory onto device's storage
- 3) Change the path in MainActivity.java
- 4) Run :)

Enjoy DeepSense


changes made in the existing version
1. first clone the project link https://github.com/JC1DA/DeepSense
2. built the code but error was thrown
Error:(21, 28) error: use of undeclared identifier 'malloc'
so, made change in app-src-main-cpp-utilities.cpp
#include <stdlib.h

3. copy the appropriate shared libraries (libllvm-qcom.so and libOpenCL.so) from distribution/opencl/lib/armeabi-v7a/Adreno-Android5 OR distribution/opencl/lib/armeabi-v7a/Adreno-Android6 into distribution/opencl/lib/armeabi-v7a

comment out Mali-shared library in app/CMakeLists.txt and uncomment Adreno shared library

#set_target_properties( lib_opencl PROPERTIES IMPORTED_LOCATION ${distribution_DIR}/opencl/lib/${ANDROID_ABI}/libGLES_mali.so )
set_target_properties( lib_opencl PROPERTIES IMPORTED_LOCATION ${distribution_DIR}/opencl/lib/${ANDROID_ABI}/libllvm-qcom.so )
set_target_properties( lib_opencl PROPERTIES IMPORTED_LOCATION ${distribution_DIR}/opencl/lib/${ANDROID_ABI}/libOpenCL.so )


4. Yolo Tiny Link: https://drive.google.com/file/d/0B_GMfaURPvQDZVVFMnBXQUU3X2s/view?usp=sharing
from this link downloaded the parameters, and copy it into the internal memory of phone
to add the path of folder downloaded

simply comment the line in DeepSense-master/app/src/main/java/com/lanytek/deepsensev3/MainActivity.java
private static String model_yolo_tiny = (new File(Environment.getExternalStorageDirectory(), "YoloModels/Yolo-Tiny-New-Format")).getAbsolutePath();
instead write
private static String model_yolo_tiny = "/sdcard/Yolo-Tiny-New-Format";

after these steps when you build the app and run it on phone
app runs
press init gpu it works and layers 
but when process image button is pressed - nothing works - this made us realised path of image is not given correctly
for this simply put the image in internal storage and set the path in MainActivity.java 
as
private String selectedImagePath ="/sdcard/IMG_20180108_181026061_BURST000_COVER_TOP~3.jpg";
and comment
    private String selectedImagePath = null;

this can e

