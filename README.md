# RunningApp

A running tracker app that allows user to track and monitor their
running activities, such as distance, time, calories burned and pace.

### Usage
- To use RunningApp, follow these steps:
- Open the app on your device.</br>
- Enter your name and weight in the setup screen and click on the "Continue" button.</br>
- Click on the "Floating action Button" then "Start" button in the tracking screen to start tracking your running activity.</br>
- During the run, you can see your current location on the map view and the timer will keep track of your running time.</br>
- Once you are done with the run, click on the "Stop" button to end the tracking.</br>
- You can view your running history in the "Your Runs" screen and sort them using various filters.</br>
- You can view your overall statistics in the "Statistics" screen, including total distance ran, total time, total calories burned, and average speed.</br>
- You can update your name and weight in the "Settings" screen.</br>

### Technologies Used
 #### This app was built using the following technologies:
- Android Jetpack Components
- MVVM architecture
- Navigation Components
- Retrofit
- Google Maps API
- Kotlin 
- RecyclerView
- Dagger-Hilt for dependency injection
- Kotlin Couroutines

### Screenshots

#### Run Fragment
This fragment will show list of saved runs and the runs can also be sorted using date, running Time , distance, average speed and calories burned.It contains a spinner which the user uses to sort their runs. 


<img src="https://res.cloudinary.com/dixttklud/image/upload/v1674788355/RunningAppSS/Screenshot_20230126-110023_RunningApp_nlwwm1.jpg" width="20%" height="20%">

#### Tracking Fragment
In this fragment the user can see his location in the mapView and can stop or pause the run.The users can also see a stopwatch for the reference and there is also a foreground service running which the tracks the location of the user in the background.

<img src="https://res.cloudinary.com/dixttklud/image/upload/v1674788372/RunningAppSS/Screenshot_20230126-110101_RunningApp_mkrosf.jpg" width="20%" height="20%">

#### Statistics Fragment 
In Statistics Fragment the user can see his statistics of the user's total runs such as total average speed , total time ran , total calories burned and total distance ran.

<img src="https://res.cloudinary.com/dixttklud/image/upload/v1674788390/RunningAppSS/Screenshot_20230126-110122_RunningApp_hl9122.jpg" width="20%" height="20%">


#### Setup Fragment
In this fragment the user can store the data(name and weight).Shared Preferences is used to store and update the data.

<img src="https://res.cloudinary.com/dixttklud/image/upload/v1675011114/RunningAppSS/Screenshot_20230129_221931_zl0frd.png" width="20%" height="20%">

### Setting Fragment
In setting Fragment the user can update the data(name and weight).

<img src="https://res.cloudinary.com/dixttklud/image/upload/v1675011114/RunningAppSS/Screenshot_20230129_222100_vfw6vj.png" width="20%" height="20%">

### Installation
To install RunningApp, follow these steps:

- Clone the repository to your local machine using git clone https://github.com/tanmaySinha16/WorkoutApp.git.</br>
- Open the project in Android Studio.</br>
- Build and run the app on an emulator or physical device.</br>

Note: Make sure to allow location permissions for the app to track your running activity.</br>

### Conclusion
This app is a great tool for runners who want to keep track of their running activity and improve their performance. It uses modern Android development practices and libraries to provide a seamless and user-friendly experience.
