import { NativeEventEmitter, NativeModules } from 'react-native';
import NativeSantriqxRnModule from './NativeSantriqxRnModule';

const emitter = new NativeEventEmitter(NativeModules.SantriqxRnModule);

const SantriqxSDK = {
  init: (appId: string, apiSecretKey: string, baseUrl: string = '') =>
    NativeSantriqxRnModule.initSdk(appId, apiSecretKey, baseUrl),

  registerDevice: () => NativeSantriqxRnModule.registerDevice(),
  getDeviceInfo: () => NativeSantriqxRnModule.getDeviceInfo(),

  fetchConfig: () => NativeSantriqxRnModule.fetchConfig(),

  startRecording: () => NativeSantriqxRnModule.startRecording(),
  stopRecording: () => NativeSantriqxRnModule.stopRecording(),
  getStreamDetails: (streamKey: string) =>
    NativeSantriqxRnModule.getStreamDetails(streamKey),

  startGyroscope: () => NativeSantriqxRnModule.startGyroscope(),
  stopGyroscope: () => NativeSantriqxRnModule.stopGyroscope(),
  sendSensorData: (gx: number, gy: number, gz: number,
                   ax: number, ay: number, az: number) =>
    NativeSantriqxRnModule.sendSensorData(gx, gy, gz, ax, ay, az),

  onGyroData: (callback: (data: any) => void) =>
    emitter.addListener('onGyroData', callback),

  openFaceRecognition: () => NativeSantriqxRnModule.openFaceRecognition(),
  uploadFace: (imagePath: string, username: string) =>
    NativeSantriqxRnModule.uploadFace(imagePath, username),

  recordTransaction: (fields: object) =>
    NativeSantriqxRnModule.recordTransaction(fields),
  getTransactions: () => NativeSantriqxRnModule.getTransactions(),
};

export default SantriqxSDK;