package com.santriqxrnmodule

import com.facebook.react.bridge.ReactApplicationContext

class SantriqxRnModuleModule(reactContext: ReactApplicationContext) :
  NativeSantriqxRnModuleSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeSantriqxRnModuleSpec.NAME
  }
}
