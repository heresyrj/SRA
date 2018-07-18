# SRA Challenge Doc

# Background

This document serves as a record for implementing SRA challenge.
Since I’m not an native app developer, this project took some learning.

**Choice of platforms:**

- Native Android
- Firebase

**Why native Android:** 
During the first phone interview, I was asked about Android development for [the Smart Refrigerator](https://www.ruanjian.io/minibay/) project. However, I was not responsible for that. Considering the team may have work to be done for Android, I decide to use native android instead of React Native or Flutter.

**Why Firebase:**
Firebase provides clean and powerful APIs. Since Google IO 2018, Firebase announced MLKit which contains Vision features. It allows both online and offline mode vision recognition ability, and also support custom model serving. Since setting up a server is not the major concern. I decide to use Firebase.

NOTE:
Only test on Pixel 2 with API 27

# Steps


## Setting up

app/build.gradle

    dependencies {
        ...
        // ML Kit dependencies
        implementation 'com.google.firebase:firebase-core:16.0.1'
        implementation 'com.google.firebase:firebase-ml-vision:16.0.0'
        implementation 'com.google.firebase:firebase-ml-vision-image-label-model:15.0.0'
        implementation 'com.google.firebase:firebase-ml-model-interpreter:16.0.0'
    }
    apply plugin: 'com.google.gms.google-services'

build.gradle

    buildscript {
        ext.kotlin_version = '1.2.30'
        repositories {
            google()
            jcenter()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:3.1.3'
            classpath 'com.google.gms:google-services:4.0.2'
    
            // NOTE: Do not place your application dependencies here; they belong
            // in the individual module build.gradle files
        }
    }


Naturally, request relative permissions for the app.
app/src/main/AndroidManifest.xml
Add the following

        <uses-permission android:name="android.permission.CAMERA" />
        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        <uses-feature android:name="android.hardware.camera" />
        <uses-feature android:name="android.hardware.camera.autofocus" />



## Firebase Console
1. Set up Firebase project then download auth json file
2. Put the json file in /src folder



## Implementation of Camera Preview
    /app/src/main/java/com/rj/facesmile/camera

Manages the camera and allows UI updates on top of it . This receives preview frames from the camera at a specified rate, sending those frames to child classes detectors classifiers as fast as it is able to process.


## Implementation of Vision
    /app/src/main/java/com/rj/facesmile/vision

Write the intermediate interface for the application based on Firebase Vision API. Also referencing best practice to write the graphic overlay


## Implementation of Face Detection
    /app/src/main/java/com/rj/facesmile/facedetection

Built on top of vision, this package actually responsible for detecting face and extract wanted features (Processor). Then the faceGraphic draw the overlay for the to be displayed information.


## Activities and UI

**Main Activity**
Serves as an placeholder for any information to be displayed or app root for navigation

![](https://d2mxuefqeaa7sj.cloudfront.net/s_29D3B1366AED6854C677CE7770E982B8FB3D988CA178B3AF935F10F8C7C79917_1531899043245_Screen+Shot+2018-07-18+at+12.29.29+AM.png)




**Detect Activity**
The main activity that show the camera preview and overlay with detected face and smile possibility. If happiness possibility is greater than 0.9, a random emoji will drawn over the face and the bounding box will face.
Use the ‘X’ at the bottom to close/reset the session.
Use the ‘Camera’ button to flip camera facing. 

![](https://d2mxuefqeaa7sj.cloudfront.net/s_29D3B1366AED6854C677CE7770E982B8FB3D988CA178B3AF935F10F8C7C79917_1531899053885_Screen+Shot+2018-07-18+at+12.29.51+AM.png)




# Video Demo
https://player.vimeo.com/video/280494383
