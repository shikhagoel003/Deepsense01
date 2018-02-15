# DeepSense

# The app is configured to work on Moto G4 Plus with Adreno GPU, 

Download the models below, extract and copy them onto mobile devices and set the link in DeepSense App to load them.

VGG-F Link: https://drive.google.com/file/d/0B_GMfaURPvQDQk9sU3FHdU1sUzA/view?usp=sharing

Yolo Tiny Link: https://drive.google.com/file/d/0B_GMfaURPvQDZVVFMnBXQUU3X2s/view?usp=sharing

## To run the app
- 1) Download and extract the model
- 2) Put the whole model's directory onto device's storage
- 3) Change the path in MainActivity.java
     Comment the line
     private static String model_yolo_tiny = (new File(Environment.getExternalStorageDirectory(), "YoloModels/Yolo-Tiny-New-    Format")).getAbsolutePath();
     instead write
     private static String model_yolo_tiny = "/sdcard/Yolo-Tiny-New-Format";

     Same can be done for VGG network also.
  
- 4) copy one image onto your device's storage and provide its path in MainActivity.java
      example: 
      comment the line 
      private String selectedImagePath = null;
      and give the path of your image file
      private String selectedImagePath ="/sdcard/IMG_20180108_181026061_BURST000_COVER_TOP~3.jpg";
- 5) Run :)

Enjoy DeepSense

