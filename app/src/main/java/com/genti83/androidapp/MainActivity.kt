package com.genti83.androidapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

/**
 * MainActivity - Main entry point for the Android App
 * 
 * Features:
 * - Comprehensive error handling with try-catch blocks
 * - Logcat integration (Log.d, Log.e, Log.w)
 * - Real-time error monitoring
 * - NullPointerException detection
 * - Permission denial tracking
 * - Crash detection and recovery
 */
class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var tvWelcome: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvLogOutput: TextView
    private lateinit var btnTestApp: Button
    private lateinit var btnTestError: Button
    private lateinit var btnTestPermission: Button
    private lateinit var btnClearLogs: Button
    private lateinit var scrollView: ScrollView

    // Logging
    private val TAG = "MainActivity"
    private val logBuffer = StringBuilder()
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    
    // Permission request code
    private val PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "onCreate: Initializing MainActivity")
            setContentView(R.layout.activity_main)
            
            // Initialize UI components with null safety
            initializeViews()
            
            // Set up click listeners
            setupClickListeners()
            
            // Set up global exception handler
            setupGlobalExceptionHandler()
            
            // Log successful initialization
            logMessage("App initialized successfully", LogLevel.DEBUG)
            updateStatus("All systems operational", true)
            
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Critical error during initialization", e)
            handleCriticalError(e)
        }
    }

    /**
     * Initialize all UI components with proper null checking
     */
    private fun initializeViews() {
        try {
            tvWelcome = findViewById(R.id.tvWelcome)
                ?: throw NullPointerException("tvWelcome not found")
            tvStatus = findViewById(R.id.tvStatus)
                ?: throw NullPointerException("tvStatus not found")
            tvLogOutput = findViewById(R.id.tvLogOutput)
                ?: throw NullPointerException("tvLogOutput not found")
            btnTestApp = findViewById(R.id.btnTestApp)
                ?: throw NullPointerException("btnTestApp not found")
            btnTestError = findViewById(R.id.btnTestError)
                ?: throw NullPointerException("btnTestError not found")
            btnTestPermission = findViewById(R.id.btnTestPermission)
                ?: throw NullPointerException("btnTestPermission not found")
            btnClearLogs = findViewById(R.id.btnClearLogs)
                ?: throw NullPointerException("btnClearLogs not found")
            scrollView = findViewById(R.id.scrollView)
                ?: throw NullPointerException("scrollView not found")
                
            Log.d(TAG, "initializeViews: All views initialized successfully")
            
        } catch (e: NullPointerException) {
            Log.e(TAG, "initializeViews: NullPointerException - View not found", e)
            logMessage("NullPointerException detected: ${e.message}", LogLevel.ERROR)
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "initializeViews: Unexpected error", e)
            throw e
        }
    }

    /**
     * Set up click listeners for all buttons
     */
    private fun setupClickListeners() {
        try {
            btnTestApp.setOnClickListener {
                testAppFeatures()
            }

            btnTestError.setOnClickListener {
                testErrorHandling()
            }

            btnTestPermission.setOnClickListener {
                testPermissions()
            }

            btnClearLogs.setOnClickListener {
                clearLogs()
            }
            
            Log.d(TAG, "setupClickListeners: All click listeners set up")
            
        } catch (e: Exception) {
            Log.e(TAG, "setupClickListeners: Error setting up listeners", e)
            logMessage("Error setting up click listeners: ${e.message}", LogLevel.ERROR)
        }
    }

    /**
     * Set up global exception handler to catch unhandled exceptions
     */
    private fun setupGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "CRASH DETECTED on thread: ${thread.name}", throwable)
            logMessage("CRASH DETECTED: ${throwable::class.java.simpleName} - ${throwable.message}", LogLevel.ERROR)
            
            // Log stack trace
            throwable.stackTrace.take(5).forEach { element ->
                Log.e(TAG, "  at $element")
            }
            
            // In a real app, you might send this to a crash reporting service
            // Call the original handler to ensure proper crash reporting
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * Test app features with proper error handling
     */
    private fun testAppFeatures() {
        try {
            Log.d(TAG, "testAppFeatures: Testing app features")
            logMessage("Testing app features...", LogLevel.DEBUG)
            
            // Simulate some operations
            val randomValue = (1..100).random()
            Log.d(TAG, "testAppFeatures: Generated random value: $randomValue")
            logMessage("Random value generated: $randomValue", LogLevel.DEBUG)
            
            // Test string operations
            val testString: String? = if (randomValue > 50) "Valid String" else null
            val result = testString?.length ?: 0
            Log.d(TAG, "testAppFeatures: String length: $result")
            logMessage("String operation result: $result", LogLevel.DEBUG)
            
            if (testString == null) {
                Log.w(TAG, "testAppFeatures: Warning - testString was null")
                logMessage("WARNING: Null string detected but handled safely", LogLevel.WARNING)
            }
            
            updateStatus("Test completed successfully", true)
            Toast.makeText(this, "App features test completed", Toast.LENGTH_SHORT).show()
            
        } catch (e: NullPointerException) {
            Log.e(TAG, "testAppFeatures: NullPointerException caught", e)
            logMessage("NullPointerException: ${e.message}", LogLevel.ERROR)
            handleNullPointerException(e)
        } catch (e: Exception) {
            Log.e(TAG, "testAppFeatures: Unexpected error", e)
            logMessage("Error during testing: ${e.message}", LogLevel.ERROR)
            updateStatus("Error detected - check logs", false)
        }
    }

    /**
     * Test error handling mechanisms
     */
    private fun testErrorHandling() {
        try {
            Log.w(TAG, "testErrorHandling: Initiating error handling test")
            logMessage("Testing error handling mechanisms...", LogLevel.WARNING)
            
            // Test 1: Division by zero
            try {
                val result = 10 / 0
                Log.d(TAG, "testErrorHandling: This should not print: $result")
            } catch (e: ArithmeticException) {
                Log.e(TAG, "testErrorHandling: ArithmeticException caught", e)
                logMessage("ArithmeticException handled: ${e.message}", LogLevel.ERROR)
            }
            
            // Test 2: Intentional NullPointerException (for error handling demonstration)
            try {
                val nullString: String? = null
                // Note: Using !! operator intentionally to demonstrate NullPointerException handling
                val length = nullString!!.length
                Log.d(TAG, "testErrorHandling: This should not print: $length")
            } catch (e: NullPointerException) {
                Log.e(TAG, "testErrorHandling: NullPointerException caught and handled", e)
                logMessage("NullPointerException detected and handled", LogLevel.ERROR)
                handleNullPointerException(e)
            }
            
            // Test 3: Array index out of bounds
            try {
                val array = arrayOf(1, 2, 3)
                val value = array[10]
                Log.d(TAG, "testErrorHandling: This should not print: $value")
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.e(TAG, "testErrorHandling: ArrayIndexOutOfBoundsException caught", e)
                logMessage("ArrayIndexOutOfBoundsException handled: ${e.message}", LogLevel.ERROR)
            }
            
            logMessage("All error tests completed successfully", LogLevel.DEBUG)
            updateStatus("Error handling verified", true)
            Toast.makeText(this, "Error handling tests completed", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Log.e(TAG, "testErrorHandling: Critical error during error testing", e)
            logMessage("Critical error: ${e.message}", LogLevel.ERROR)
            handleCriticalError(e)
        }
    }

    /**
     * Test permission handling
     */
    private fun testPermissions() {
        try {
            Log.d(TAG, "testPermissions: Testing permission handling")
            logMessage("Testing permissions...", LogLevel.DEBUG)
            
            // Check for Internet permission (automatically granted)
            val hasInternet = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED
            
            Log.d(TAG, "testPermissions: Internet permission: $hasInternet")
            logMessage("Internet permission: ${if (hasInternet) "GRANTED" else "DENIED"}", LogLevel.DEBUG)
            
            // Check for notification permission (requires runtime request on Android 13+)
            val hasNotification = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            
            Log.d(TAG, "testPermissions: Notification permission: $hasNotification")
            logMessage("Notification permission: ${if (hasNotification) "GRANTED" else "DENIED"}", LogLevel.DEBUG)
            
            if (!hasNotification && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Log.w(TAG, "testPermissions: Requesting notification permission")
                logMessage("Requesting notification permission...", LogLevel.WARNING)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                updateStatus("All permissions checked", true)
                Toast.makeText(this, "Permission check completed", Toast.LENGTH_SHORT).show()
            }
            
        } catch (e: SecurityException) {
            Log.e(TAG, "testPermissions: SecurityException - Permission denied", e)
            logMessage("Permission denied: ${e.message}", LogLevel.ERROR)
            handlePermissionDenied(e)
        } catch (e: Exception) {
            Log.e(TAG, "testPermissions: Error during permission check", e)
            logMessage("Error checking permissions: ${e.message}", LogLevel.ERROR)
        }
    }

    /**
     * Handle permission request results
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        try {
            when (requestCode) {
                PERMISSION_REQUEST_CODE -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: Permission granted")
                        logMessage("Permission GRANTED", LogLevel.DEBUG)
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(TAG, "onRequestPermissionsResult: Permission denied by user")
                        logMessage("Permission DENIED by user", LogLevel.WARNING)
                        handlePermissionDenied(SecurityException("Permission denied by user"))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onRequestPermissionsResult: Error processing permission result", e)
            logMessage("Error processing permission result: ${e.message}", LogLevel.ERROR)
        }
    }

    /**
     * Handle NullPointerException specifically
     */
    private fun handleNullPointerException(e: NullPointerException) {
        Log.e(TAG, "handleNullPointerException: Handling NPE", e)
        logMessage("NullPointerException detected: ${e.message ?: "Unknown cause"}", LogLevel.ERROR)
        updateStatus(getString(R.string.error_null_pointer), false)
        Toast.makeText(this, R.string.error_null_pointer, Toast.LENGTH_LONG).show()
    }

    /**
     * Handle permission denied scenarios
     */
    private fun handlePermissionDenied(e: SecurityException) {
        Log.e(TAG, "handlePermissionDenied: Permission denied", e)
        logMessage("Permission denied: ${e.message}", LogLevel.ERROR)
        updateStatus(getString(R.string.error_permission_denied), false)
        Toast.makeText(this, R.string.error_permission_denied, Toast.LENGTH_LONG).show()
    }

    /**
     * Handle critical errors
     */
    private fun handleCriticalError(e: Exception) {
        Log.e(TAG, "handleCriticalError: Critical error occurred", e)
        logMessage("CRITICAL ERROR: ${e::class.java.simpleName} - ${e.message}", LogLevel.ERROR)
        updateStatus("Critical error - see logs", false)
        
        // Show error dialog to user
        Toast.makeText(
            this,
            "Critical error: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Clear all logs
     */
    private fun clearLogs() {
        try {
            logBuffer.clear()
            tvLogOutput.text = getString(R.string.log_output)
            Log.d(TAG, "clearLogs: Logs cleared")
            updateStatus("Logs cleared", true)
            Toast.makeText(this, "Logs cleared", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "clearLogs: Error clearing logs", e)
        }
    }

    /**
     * Log a message with timestamp and level
     */
    private fun logMessage(message: String, level: LogLevel) {
        try {
            val timestamp = dateFormat.format(Date())
            val logEntry = "[$timestamp] [${level.name}] $message\n"
            
            logBuffer.append(logEntry)
            
            runOnUiThread {
                tvLogOutput.text = logBuffer.toString()
                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
            
            // Also log to Logcat
            when (level) {
                LogLevel.DEBUG -> Log.d(TAG, message)
                LogLevel.WARNING -> Log.w(TAG, message)
                LogLevel.ERROR -> Log.e(TAG, message)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "logMessage: Error logging message", e)
        }
    }

    /**
     * Update status TextView
     */
    private fun updateStatus(message: String, isSuccess: Boolean) {
        try {
            runOnUiThread {
                tvStatus.text = message
                tvStatus.setTextColor(
                    ContextCompat.getColor(
                        this,
                        if (isSuccess) android.R.color.holo_green_dark else android.R.color.holo_red_dark
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateStatus: Error updating status", e)
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            Log.d(TAG, "onStart: Activity started")
            logMessage("Activity started", LogLevel.DEBUG)
        } catch (e: Exception) {
            Log.e(TAG, "onStart: Error", e)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Log.d(TAG, "onResume: Activity resumed")
            logMessage("Activity resumed", LogLevel.DEBUG)
        } catch (e: Exception) {
            Log.e(TAG, "onResume: Error", e)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            Log.d(TAG, "onPause: Activity paused")
            logMessage("Activity paused", LogLevel.DEBUG)
        } catch (e: Exception) {
            Log.e(TAG, "onPause: Error", e)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            Log.d(TAG, "onStop: Activity stopped")
            logMessage("Activity stopped", LogLevel.DEBUG)
        } catch (e: Exception) {
            Log.e(TAG, "onStop: Error", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            Log.d(TAG, "onDestroy: Activity destroyed")
            logMessage("Activity destroyed", LogLevel.DEBUG)
        } catch (e: Exception) {
            Log.e(TAG, "onDestroy: Error", e)
        }
    }

    /**
     * Enum for log levels
     */
    enum class LogLevel {
        DEBUG, WARNING, ERROR
    }
}
