# PhotoLab
Written by Jong Ho Baek icanmobile@gmail.com

PhotoLab application(https://youtu.be/W1zf4ZSVNN8) creates the pseudo HDR photos using the tone mapping functionality. 
This application includes a custom camera, photo effect and comparison, and gallery functionalities.

The structure of this application is composed of MVVM (Model-View-Viewmodel) design pattern with Android Jetpack architecture components, 
Dagger2, and activity creation using Java reflection technology based on Json file. 

In addition, this application uses popular 3rd party libraries such as Gson, Glide, RxJava, and Butter Knife. 
The target android sdk version of this application is 28 and minimum sdk version is 21 and it was tested on Galaxy Note9 and Galaxy S7 devices.

The source structure of application is separated into 2 major parts. 
First part is the app and the second part is the module. 
The app supports all user-interfaces and includes three activities and multiple fragments. 
The module includes a photo effect java class, JNI interface, and “LDR tonemapping” open source written in C++.

1. Basic Architecture
• Base UI classes such as BaseApplication, BaseActivity, and BaseFragment classes.
  - Base UI classes support abstract and basic methods for Sub UI classes.
  - Sub UI classes extends and implements the specific UI functionalities based on Base UI classes.
• Dependency Injection for Base UI classes
  - Base UI classes are extended from the base framework of Dagger2.
• Model-View-Viewmodel (MVVM) design pattern using ViewModel and LiveData of Android Jetpack architecture components.
• Activity creation using Java reflection technique based on Json file for updating UI without release process.
  - A Json file includes an activity information such as class, layout, enter/exit animation and
UI resources.
  - BaseActivity creates the specific Activity object and switch between Activities with custom animation using Java reflection technique with Json file information.
  - When the information such as ‘resources’ or ‘actions’ of Json file is updated, the application will be updated automatically. 
    Ex. if we change a button action from PhotoActivity to GalleryActivity in Json file, the application will launch GalleryActivity when user clicks the button.
• Related source location:
  - Package “com.icanmobile.photolab.ui.base”
  - Package “com.icanmobile.photolab.di”
  - Package “com.icanmobile.photolab.data”
  - Package “com.icanmobile.photolab.data.gson.model”
  - Method: “com.icanmobile.photolab.ui.base.BaseActivity” changeActivity method.
  - Json Folder: “PhotoLab/app/src/main/assets/model” folder

2. Camera UI Classes
• Custom camera view
  - Full screen preview functionality.
  - Front/back Camera transition functionality.
  - Still shot functionality with largest resolution which is the same as screen ratio.
• Go to Photo UI.
• Go to Gallery UI.
• Related source location:
  - Package “com.icanmobile.photolab.ui.camera”
  - Package “com.icanmobile.photolab.ui.view.camera”
  - Package “com.icanmobile.photolab.util.file”
  - Json file: “PhotoLab/app/src/main/assets/model/activity_camera.json”
• Reference: Camera2Basic sample made by Google.

3. Photo UI Classes
• Display still shot photo and request LDR tonemapping effect to PhotoEffect module.
• Updating the progress status of LDR tonemapping effect process using RxJava.
• Change still shot photo to the processed photo using LiveData observer.
• Assign the button resources and actions based on Json file.
• Save the processed photo.
• Go to Camera UI.
• Go to Gallery UI.
• Related source location:
  - Package “com.icanmobile.photolab.ui.photo”
  - Package “com.icanmobile.photolab.util.file”
  - Class “package com.icanmobile.photolab.data.gson.model.ButtonModel”
  - Json file: “PhotoLab/app/src/main/assets/model/activity_photo.json”

4. Gallery UI Classes
• JPEG file searching functionality within application private folder.
• Grid and single style photo browser functionalities based on android material design components.
• Related source location:
  - Package “com.icanmobile.photolab.ui.gallery”
  - Package “com.icanmobile.photolab.ui.gallery.data”
  - Json file: “PhotoLab/app/src/main/assets/model/activity_gallery.json”
• Reference: android-transition-examples made by Google

5. Photo Effect Module
• Photo Effect class which is an interface between application UI and JNI.
• “LDR Tonemapping” open source written in C++.
• Threading functionality for image processing in C++.
• Callback functionality for the progress status in C++.
• Related source location:
  - UI Class “com.icanmobile.photolab.ui.photo.PhotoViewModel”
  - Module: “PhotoLab/effects”
  - Package “com.icanmobile.effects”
  - JNI folder: “PhotoLab/effects/src/main/cpp”
  - LDR Tonemapping folder: “PhotoLab/effects/src/main/cpp/ldr”
• Reference: LDR Tonemapping by Nasca Octavian PAUL
