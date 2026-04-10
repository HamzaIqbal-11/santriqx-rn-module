import { TurboModuleRegistry, type TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  // Init
  initSdk(appId: string, apiSecretKey: string, baseUrl: string): Promise<boolean>;

  // Device
  registerDevice(): Promise<Object>;
  getDeviceInfo(): Promise<Object>;

  // Config
  fetchConfig(): Promise<Object>;

  // Recording
  startRecording(): Promise<Object>;
  stopRecording(): Promise<boolean>;
  getStreamDetails(streamKey: string): Promise<Object>;

  // Sensors
  startGyroscope(): Promise<boolean>;
  stopGyroscope(): Promise<boolean>;
  sendSensorData(gx: number, gy: number, gz: number, ax: number, ay: number, az: number): Promise<Object>;

  // Face
  openFaceRecognition(): Promise<Object>;
  uploadFace(imagePath: string, username: string): Promise<Object>;

  // Transactions
  recordTransaction(fields: Object): Promise<Object>;
  getTransactions(): Promise<Object>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('SantriqxRnModule');