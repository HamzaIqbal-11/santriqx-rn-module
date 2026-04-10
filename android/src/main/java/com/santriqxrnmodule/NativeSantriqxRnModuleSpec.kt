package com.santriqxrnmodule

import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap

abstract class NativeSantriqxRnModuleSpec(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val NAME = "SantriqxRnModule"
    }

    abstract fun initSdk(appId: String, apiSecretKey: String, baseUrl: String, promise: Promise)
    abstract fun registerDevice(promise: Promise)
    abstract fun getDeviceInfo(promise: Promise)
    abstract fun fetchConfig(promise: Promise)
    abstract fun startRecording(promise: Promise)
    abstract fun stopRecording(promise: Promise)
    abstract fun getStreamDetails(streamKey: String, promise: Promise)
    abstract fun startGyroscope(promise: Promise)
    abstract fun stopGyroscope(promise: Promise)
    abstract fun sendSensorData(gx: Double, gy: Double, gz: Double, ax: Double, ay: Double, az: Double, promise: Promise)
    abstract fun openFaceRecognition(promise: Promise)
    abstract fun uploadFace(imagePath: String, username: String, promise: Promise)
    abstract fun recordTransaction(fields: ReadableMap, promise: Promise)
    abstract fun getTransactions(promise: Promise)
}