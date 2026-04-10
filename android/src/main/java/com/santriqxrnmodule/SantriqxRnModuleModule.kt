package com.santriqxrnmodule

import android.util.Log
import com.earnscape.gyroscopesdk.SantriqxSDK
import com.earnscape.gyroscopesdk.GyroscopeSDK
import com.earnscape.gyroscopesdk.DeviceService
import com.example.gyroscope.kyc.FaceRecognitionActivity
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import android.content.Intent

class SantriqxRnModuleModule(reactContext: ReactApplicationContext) :
    NativeSantriqxRnModuleSpec(reactContext) {

    companion object {
        const val NAME = NativeSantriqxRnModuleSpec.NAME
        private const val TAG = "SantriqxRnModule"
        private const val FACE_REQUEST_CODE = 2004
        private const val RECORDING_REQUEST_CODE = 2001
    }

    private var gyroscopeSDK: GyroscopeSDK? = null
    private var pendingRecordingPromise: Promise? = null
    private var pendingFacePromise: Promise? = null

    // ── Init SDK ──
    override fun initSdk(appId: String, apiSecretKey: String, baseUrl: String, promise: Promise) {
        try {
            SantriqxSDK.init(
                appId = appId,
                apiSecretKey = apiSecretKey,
                baseUrl = baseUrl
            )
            Log.d(TAG, "✅ SDK initialized")
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("INIT_ERROR", e.message)
        }
    }

    // ── Register Device ──
    override fun registerDevice(promise: Promise) {
        SantriqxSDK.registerDevice(reactApplicationContext) { result ->
            reactApplicationContext.runOnUiQueueThread {
                promise.resolve(convertMap(result))
            }
        }
    }

    // ── Get Device Info ──
    override fun getDeviceInfo(promise: Promise) {
        DeviceService.getFullDeviceInfo(reactApplicationContext) { info ->
            reactApplicationContext.runOnUiQueueThread {
                val map = WritableNativeMap()
                info.forEach { (k, v) -> map.putString(k, v.toString()) }
                promise.resolve(map)
            }
        }
    }

    // ── Fetch Config ──
    override fun fetchConfig(promise: Promise) {
        SantriqxSDK.fetchConfig { result ->
            reactApplicationContext.runOnUiQueueThread {
                promise.resolve(convertMap(result))
            }
        }
    }

    // ── Start Recording ──
    override fun startRecording(promise: Promise) {
        val activity = currentActivity ?: run {
            promise.reject("NO_ACTIVITY", "Activity not available")
            return
        }
        pendingRecordingPromise = promise
        SantriqxSDK.startRecording(
            activity = activity,
            requestCode = RECORDING_REQUEST_CODE,
            onSuccess = { streamKey, rtmpUrl ->
                reactApplicationContext.runOnUiQueueThread {
                    val map = WritableNativeMap()
                    map.putBoolean("success", true)
                    map.putString("streamKey", streamKey)
                    map.putString("rtmpUrl", rtmpUrl)
                    pendingRecordingPromise?.resolve(map)
                    pendingRecordingPromise = null
                }
            },
            onError = { err ->
                reactApplicationContext.runOnUiQueueThread {
                    pendingRecordingPromise?.reject("RECORDING_ERROR", err)
                    pendingRecordingPromise = null
                }
            }
        )
    }

    // ── Stop Recording ──
    override fun stopRecording(promise: Promise) {
        try {
            SantriqxSDK.stopRecording(reactApplicationContext)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("STOP_ERROR", e.message)
        }
    }

    // ── Get Stream Details ──
    override fun getStreamDetails(streamKey: String, promise: Promise) {
        SantriqxSDK.getStreamDetails(streamKey) { result ->
            reactApplicationContext.runOnUiQueueThread {
                promise.resolve(convertMap(result))
            }
        }
    }

    // ── Start Gyroscope ──
    override fun startGyroscope(promise: Promise) {
        try {
            gyroscopeSDK = GyroscopeSDK(reactApplicationContext)
            gyroscopeSDK?.start(autoLog = false) { data ->
                val map = WritableNativeMap()
                map.putDouble("x", data.x.toDouble())
                map.putDouble("y", data.y.toDouble())
                map.putDouble("z", data.z.toDouble())
                map.putDouble("ax", data.ax.toDouble())
                map.putDouble("ay", data.ay.toDouble())
                map.putDouble("az", data.az.toDouble())
                sendEvent("onGyroData", map)
            }
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("GYRO_ERROR", e.message)
        }
    }

    // ── Stop Gyroscope ──
    override fun stopGyroscope(promise: Promise) {
        gyroscopeSDK?.stop()
        promise.resolve(true)
    }

    // ── Send Sensor Data ──
    override fun sendSensorData(
        gx: Double, gy: Double, gz: Double,
        ax: Double, ay: Double, az: Double,
        promise: Promise
    ) {
        SantriqxSDK.sendSensorData(
            reactApplicationContext, gx, gy, gz, ax, ay, az
        ) { result ->
            reactApplicationContext.runOnUiQueueThread {
                promise.resolve(convertMap(result))
            }
        }
    }

    // ── Open Face Recognition ──
    override fun openFaceRecognition(promise: Promise) {
        val activity = currentActivity ?: run {
            promise.reject("NO_ACTIVITY", "Activity not available")
            return
        }
        pendingFacePromise = promise
        try {
            val intent = Intent(activity, FaceRecognitionActivity::class.java)
            activity.startActivityForResult(intent, FACE_REQUEST_CODE)
        } catch (e: Exception) {
            pendingFacePromise = null
            promise.reject("LAUNCH_ERROR", e.message)
        }
    }

    // ── Upload Face ──
    override fun uploadFace(imagePath: String, username: String, promise: Promise) {
        SantriqxSDK.uploadFace(reactApplicationContext, imagePath, username) { result ->
            reactApplicationContext.runOnUiQueueThread {
                promise.resolve(convertMap(result))
            }
        }
    }

    // ── Record Transaction ──
    override fun recordTransaction(fields: ReadableMap, promise: Promise) {
        val map = mutableMapOf<String, String>()
        fields.toHashMap().forEach { (k, v) -> map[k] = v.toString() }
        SantriqxSDK.recordTransaction(reactApplicationContext, map) { result ->
            reactApplicationContext.runOnUiQueueThread {
                promise.resolve(convertMap(result))
            }
        }
    }

    // ── Get Transactions ──
    override fun getTransactions(promise: Promise) {
        SantriqxSDK.getTransactions(reactApplicationContext) { result ->
            reactApplicationContext.runOnUiQueueThread {
                promise.resolve(convertMap(result))
            }
        }
    }

    // ── Helper: Send Event to JS ──
    private fun sendEvent(eventName: String, params: WritableMap) {
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    // ── Helper: Convert Map ──
    private fun convertMap(map: Map<String, Any?>): WritableNativeMap {
        val writableMap = WritableNativeMap()
        map.forEach { (k, v) ->
            when (v) {
                is Boolean -> writableMap.putBoolean(k, v)
                is Int -> writableMap.putInt(k, v)
                is Double -> writableMap.putDouble(k, v)
                is String -> writableMap.putString(k, v)
                null -> writableMap.putNull(k)
                else -> writableMap.putString(k, v.toString())
            }
        }
        return writableMap
    }
}