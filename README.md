# Android App Project

A comprehensive Android application demonstrating best practices for error handling, logging, and debugging with GitHub Copilot integration.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [GitHub Copilot Chat Setup](#github-copilot-chat-setup)
- [Logcat Troubleshooting Guide](#logcat-troubleshooting-guide)
- [Common Errors and Solutions](#common-errors-and-solutions)
- [Code Examples](#code-examples)
- [Dependencies](#dependencies)
- [Permissions](#permissions)

## Overview

This Android application demonstrates comprehensive error handling, real-time monitoring, and debugging capabilities. It includes:
- MainActivity with TextView and Button components
- Real-time error monitoring and logging
- NullPointerException detection and handling
- Permission denial tracking
- Crash detection and recovery mechanisms
- Logcat integration for debugging

## Features

### Error Handling
- **NullPointerException Detection**: Automatic detection and handling of null pointer exceptions
- **Permission Denial Tracking**: Monitors and logs permission-related issues
- **Crash Detection**: Global exception handler to catch and log crashes
- **Real-time Error Monitoring**: Live error logging displayed in the app UI

### Logging System
- **Log.d()**: Debug logs for development information
- **Log.e()**: Error logs for exceptions and errors
- **Log.w()**: Warning logs for potential issues
- **Custom Log Buffer**: In-app log display with timestamps

### UI Components
- **TextView**: Welcome message and status display
- **Buttons**: Test app features, trigger errors, check permissions, clear logs
- **ScrollView**: Scrollable log output for real-time monitoring

## Project Structure

```
android-app-project/
├── app/
│   ├── build.gradle                    # App-level Gradle configuration
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml     # App manifest with permissions
│           ├── java/com/genti83/androidapp/
│           │   └── MainActivity.kt     # Main activity with error handling
│           └── res/
│               ├── layout/
│               │   └── activity_main.xml    # UI layout
│               ├── values/
│               │   └── strings.xml          # String resources
│               └── mipmap-*/                # App icons
├── build.gradle                        # Project-level Gradle configuration
├── settings.gradle                     # Gradle settings
├── gradle.properties                   # Gradle properties
└── README.md                          # This file
```

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK API 24 or higher
- Kotlin 1.9.20 or later

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Genti83/android-app-project.git
   cd android-app-project
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository
   - Wait for Gradle sync to complete

3. **Configure Android SDK**
   - Go to File > Project Structure > SDK Location
   - Ensure Android SDK is properly configured
   - Accept any SDK licenses if prompted

4. **Build the project**
   ```bash
   ./gradlew clean build
   ```

5. **Run on device or emulator**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - Or use command line: `./gradlew installDebug`

## GitHub Copilot Chat Setup

### Installation
1. Install GitHub Copilot extension in Android Studio
2. Sign in with your GitHub account
3. Enable Copilot in Settings > Tools > GitHub Copilot

### Usage in This Project

#### Code Assistance
Use Copilot to help with:
- Error handling patterns
- Logging best practices
- Permission handling code
- UI component implementation

#### Example Prompts
```
"Add error handling for network requests"
"Implement permission request for camera"
"Create a custom exception handler"
"Add Logcat filtering for this activity"
```

#### Inline Suggestions
- Copilot provides real-time suggestions as you type
- Press Tab to accept suggestions
- Press Esc to dismiss suggestions

### Best Practices with Copilot
1. Write descriptive comments before complex code blocks
2. Use meaningful variable and function names
3. Break down complex tasks into smaller functions
4. Review and test all Copilot suggestions

## Logcat Troubleshooting Guide

### Accessing Logcat

#### In Android Studio
1. Open Logcat window (View > Tool Windows > Logcat)
2. Select your device and process
3. Use filters to narrow down logs

#### Via Command Line
```bash
# View all logs
adb logcat

# Filter by tag
adb logcat MainActivity:D *:S

# Filter by priority
adb logcat *:E  # Errors only
adb logcat *:W  # Warnings and above

# Clear logs
adb logcat -c
```

### Log Levels

| Level | Method | Usage |
|-------|--------|-------|
| VERBOSE | Log.v() | Detailed development logs |
| DEBUG | Log.d() | Debug information |
| INFO | Log.i() | Informational messages |
| WARN | Log.w() | Warning messages |
| ERROR | Log.e() | Error messages |
| ASSERT | Log.wtf() | Critical issues |

### Filtering Logs in This App

#### By TAG
```bash
# MainActivity logs only
adb logcat MainActivity:D *:S

# All error logs
adb logcat *:E
```

#### By Process
```bash
# Filter by package name
adb logcat --pid=$(adb shell pidof -s com.genti83.androidapp)
```

#### Using grep
```bash
# Find specific errors
adb logcat | grep "NullPointerException"
adb logcat | grep "Permission"
adb logcat | grep "CRASH"
```

### Real-Time Monitoring

The app includes an in-app log viewer:
1. Launch the app
2. View logs in the ScrollView at the bottom
3. Click "Test Error" to generate sample errors
4. Click "Clear Logs" to clear the log buffer

## Common Errors and Solutions

### 1. NullPointerException

**Symptoms:**
```
E/MainActivity: NullPointerException detected: Attempt to invoke virtual method on a null object reference
```

**Causes:**
- Accessing properties of null objects
- findViewById() returning null
- Uninitialized lateinit variables

**Solutions:**
```kotlin
// Use safe call operator
val length = myString?.length ?: 0

// Use Elvis operator for default values
val name = user?.name ?: "Unknown"

// Check for null before use
if (myObject != null) {
    myObject.doSomething()
}

// Use let for null safety
myObject?.let {
    it.doSomething()
}
```

**In This App:**
- All views are initialized in `initializeViews()` with null checks
- NullPointerException handler logs and displays errors
- Safe call operators used throughout

### 2. ResourceNotFoundException

**Symptoms:**
```
E/MainActivity: android.content.res.Resources$NotFoundException: Resource ID #0x7f080001
```

**Causes:**
- Missing resource files
- Incorrect resource IDs
- Resource not found in current configuration

**Solutions:**
```kotlin
// Verify resource exists
try {
    val drawable = ContextCompat.getDrawable(this, R.drawable.my_icon)
} catch (e: Resources.NotFoundException) {
    Log.e(TAG, "Resource not found", e)
    // Use fallback resource
}

// Check strings.xml has the resource
getString(R.string.my_string)

// Verify layout files are properly named
setContentView(R.layout.activity_main)
```

**Prevention:**
- Always verify resource files exist
- Use descriptive resource names
- Check for typos in resource references
- Run "Clean Project" and "Rebuild Project"

### 3. PermissionDenied

**Symptoms:**
```
W/MainActivity: Permission denied: android.permission.CAMERA
E/MainActivity: SecurityException: Permission denial
```

**Causes:**
- Missing permission in AndroidManifest.xml
- Runtime permission not requested (Android 6.0+)
- User denied permission

**Solutions:**

#### Add to AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
```

#### Request Runtime Permission
```kotlin
// Check permission
if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
    != PackageManager.PERMISSION_GRANTED) {
    
    // Request permission
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.CAMERA),
        PERMISSION_REQUEST_CODE
    )
}

// Handle result
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    if (grantResults.isNotEmpty() && 
        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permission granted
    } else {
        // Permission denied
        handlePermissionDenied()
    }
}
```

**In This App:**
- All required permissions declared in AndroidManifest.xml
- Runtime permission requests implemented in `testPermissions()`
- Permission denial tracked and logged
- User-friendly error messages displayed

### 4. GradleSync Failed

**Symptoms:**
```
ERROR: Failed to resolve: androidx.core:core-ktx:1.12.0
Gradle sync failed: Could not download artifact
```

**Causes:**
- Missing internet connection
- Incorrect repository configuration
- Version conflicts
- Corrupted Gradle cache

**Solutions:**

#### Check Internet Connection
```bash
# Test connection
ping google.com
```

#### Clean Gradle Cache
```bash
# In Android Studio: File > Invalidate Caches / Restart
# Or via command line:
./gradlew clean
rm -rf ~/.gradle/caches/
```

#### Update Repositories
Ensure `settings.gradle` has:
```gradle
repositories {
    google()
    mavenCentral()
}
```

#### Sync Project
1. File > Sync Project with Gradle Files
2. Or run: `./gradlew --refresh-dependencies`

#### Check Gradle Version
In `gradle-wrapper.properties`:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
```

**Prevention:**
- Use stable dependency versions
- Keep Gradle and plugins updated
- Regularly sync and clean project
- Use dependency version catalogs

## Code Examples

### Basic Error Handling
```kotlin
try {
    // Your code here
    val result = riskyOperation()
    Log.d(TAG, "Operation successful: $result")
} catch (e: NullPointerException) {
    Log.e(TAG, "Null pointer exception", e)
    handleNullPointerException(e)
} catch (e: Exception) {
    Log.e(TAG, "Unexpected error", e)
    handleGeneralError(e)
}
```

### Logging Best Practices
```kotlin
// Debug logs (development only)
Log.d(TAG, "User clicked button: ${button.text}")

// Warning logs
if (data == null) {
    Log.w(TAG, "Data is null, using default values")
}

// Error logs with exception
try {
    processData(data)
} catch (e: Exception) {
    Log.e(TAG, "Failed to process data", e)
}

// Custom log formatting
val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
Log.d(TAG, "[$timestamp] Operation completed")
```

### Safe View Access
```kotlin
// Using safe cast and null check
val textView = findViewById<TextView>(R.id.myTextView)?.apply {
    text = "Hello World"
    setTextColor(Color.BLACK)
} ?: run {
    Log.e(TAG, "TextView not found")
    return
}

// Using lateinit with proper initialization
private lateinit var myTextView: TextView

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    try {
        myTextView = findViewById(R.id.myTextView)
            ?: throw NullPointerException("myTextView not found")
    } catch (e: Exception) {
        Log.e(TAG, "View initialization failed", e)
        finish()
    }
}
```

### Permission Handling
```kotlin
private fun checkAndRequestPermission() {
    when {
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED -> {
            // Permission granted
            Log.d(TAG, "Camera permission granted")
            useCameraFeature()
        }
        
        shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
            // Show rationale
            Log.w(TAG, "Showing permission rationale")
            showPermissionRationale()
        }
        
        else -> {
            // Request permission
            Log.d(TAG, "Requesting camera permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }
}
```

### Crash Detection
```kotlin
Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
    Log.e(TAG, "CRASH DETECTED on thread: ${thread.name}", throwable)
    
    // Log stack trace
    throwable.stackTrace.forEach { element ->
        Log.e(TAG, "  at $element")
    }
    
    // Save crash report
    saveCrashReport(throwable)
    
    // Optionally send to analytics
    // AnalyticsService.logCrash(throwable)
}
```

## Dependencies

### Core Dependencies
```gradle
// AndroidX Core
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'

// Material Design
implementation 'com.google.android.material:material:1.10.0'

// ConstraintLayout
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

// Lifecycle
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
```

### Testing Dependencies
```gradle
testImplementation 'junit:junit:4.13.2'
androidTestImplementation 'androidx.test.ext:junit:1.1.5'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
```

## Permissions

### Declared in AndroidManifest.xml
```xml
<!-- Network access -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Notifications (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### Permission Types

| Permission | Type | Usage |
|------------|------|-------|
| INTERNET | Normal | Network requests |
| ACCESS_NETWORK_STATE | Normal | Check connectivity |
| POST_NOTIFICATIONS | Dangerous | Show notifications (Android 13+) |

### Runtime Permission Handling
- Automatically requested when needed
- User can grant or deny
- App handles denial gracefully
- Logged in Logcat and in-app log viewer

## Building and Testing

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Install on device
./gradlew installDebug
```

### Testing Error Handling
1. Launch the app
2. Click "Test App Features" - tests normal operation
3. Click "Trigger Error Test" - generates various errors
4. Click "Test Permissions" - checks permission status
5. Observe logs in the ScrollView and Logcat

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
- Check the Common Errors section
- Review Logcat logs
- Open an issue on GitHub
- Use GitHub Copilot for code assistance

## Acknowledgments

- Built with Kotlin and AndroidX
- Logging best practices from Android documentation
- Error handling patterns from industry standards
- GitHub Copilot integration for enhanced development
