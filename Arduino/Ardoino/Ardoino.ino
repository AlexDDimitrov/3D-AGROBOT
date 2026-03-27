#include <Wire.h>

#define MPU_ADDR      0x68
#define PWR_MGMT_1    0x6B
#define ACCEL_CONFIG  0x1C
#define ACCEL_XOUT_H  0x3B

float targetDistance = -1.0; 
float velocityX      = 0.0;
float distanceX      = 0.0;
unsigned long previousTime = 0;

// Hardware Pins 
#define AIN1_R 22
#define AIN2_R 23
#define PWMA_R 44
#define BIN1_R 24
#define BIN2_R 25
#define PWMB_R 45

#define AIN1_L 26
#define AIN2_L 27
#define PWMA_L 46
#define BIN1_L 28
#define BIN2_L 29
#define PWMB_L 2

void rightForward(int speed) {
  digitalWrite(AIN1_R, HIGH); digitalWrite(AIN2_R, LOW);
  analogWrite(PWMA_R, speed);
  digitalWrite(BIN1_R, HIGH); digitalWrite(BIN2_R, LOW);
  analogWrite(PWMB_R, speed);
}

void rightBackward(int speed) {
  digitalWrite(AIN1_R, LOW); digitalWrite(AIN2_R, HIGH);
  analogWrite(PWMA_R, speed);
  digitalWrite(BIN1_R, LOW); digitalWrite(BIN2_R, HIGH);
  analogWrite(PWMB_R, speed);
}

void leftForward(int speed) {
  digitalWrite(AIN1_L, HIGH); digitalWrite(AIN2_L, LOW);
  analogWrite(PWMA_L, speed);
  digitalWrite(BIN1_L, HIGH); digitalWrite(BIN2_L, LOW);
  analogWrite(PWMB_L, speed);
}

void leftBackward(int speed) {
  digitalWrite(AIN1_L, LOW); digitalWrite(AIN2_L, HIGH);
  analogWrite(PWMA_L, speed);
  digitalWrite(BIN1_L, LOW); digitalWrite(BIN2_L, HIGH);
  analogWrite(PWMB_L, speed);
}

void stopLMotors() {
  digitalWrite(AIN1_L, LOW); digitalWrite(AIN2_L, LOW);
  analogWrite(PWMA_L, 0);
  digitalWrite(BIN1_L, LOW); digitalWrite(BIN2_L, LOW);
  analogWrite(PWMB_L, 0);
}

void stopRMotors() {
  digitalWrite(AIN1_R, LOW); digitalWrite(AIN2_R, LOW);
  analogWrite(PWMA_R, 0);
  digitalWrite(BIN1_R, LOW); digitalWrite(BIN2_R, LOW);
  analogWrite(PWMB_R, 0);
}

void moveForward(int speed){ 
  rightForward(speed);
  leftForward(speed);
}

void moveBackward(int speed){
  rightBackward(speed);
  leftBackward(speed);
}

void turnRight(int speed){
  leftForward(speed);
  rightBackward(speed);
}

void turnLeft(int speed){ 
  leftBackward(speed);  
  rightForward(speed);
}

void stopRobot(){
  stopLMotors();
  stopRMotors();
}

bool mpuInit() {
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(PWR_MGMT_1);
  Wire.write(0x00);
  if (Wire.endTransmission(true) != 0) return false;
  delay(100);
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(ACCEL_CONFIG);
  Wire.write(0x10); // ±8g
  return (Wire.endTransmission(true) == 0);
}

float readAccelX() {
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(ACCEL_XOUT_H);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_ADDR, 2, true);
  if (Wire.available() < 2) return 0.0;
  
  int16_t raw = (Wire.read() << 8) | Wire.read();
  float accel_cm_s2 = (raw / 4096.0) * 980.66; 
  return accel_cm_s2;
}

String parseCommand(String str) {
    str.trim();
    
    if (str.length() == 0) return "";
    
    char cmd = str.charAt(0);
    
    if (cmd == 'L') {
        return "left";
    } else if (cmd == 'R') {
        return "right";
    } else if (cmd == 'M') {
        String numPart = str.substring(1);
        numPart.trim();
        int value = numPart.toInt();
        return String(value);
    }
    
    return "";
}

void setup() {
  Serial.begin(115200);
  Serial1.begin(115200);  // Communication with ESP32
  Wire.begin();
  
  if (!mpuInit()) Serial.println("MPU6050 Fail!");

  pinMode(AIN1_R , OUTPUT);
  pinMode(AIN2_R , OUTPUT);
  pinMode(PWMA_R , OUTPUT);
  pinMode(BIN1_R , OUTPUT);
  pinMode(BIN2_R , OUTPUT);
  pinMode(PWMB_R , OUTPUT);

  pinMode(AIN1_L , OUTPUT);
  pinMode(AIN2_L , OUTPUT);
  pinMode(PWMA_L , OUTPUT);
  pinMode(BIN1_L , OUTPUT);
  pinMode(BIN2_L , OUTPUT);
  pinMode(PWMB_L , OUTPUT);

  stopRobot();
  previousTime = micros();
}

void loop() {
  unsigned long currentTime = micros();
  float dt = (currentTime - previousTime) / 1000000.0;
  previousTime = currentTime;

  float accelX = readAccelX();
  
  if (abs(accelX) < 15.0) accelX = 0;

  if (targetDistance > 0) {
    velocityX += accelX * dt;
    distanceX += velocityX * dt;
  } else {
    velocityX = 0;
    distanceX = 0;
  }

  if (Serial1.available()) {
    String str = Serial1.readStringUntil('\n');
    str.trim();

    String result = parseCommand(str);

    if (result == "left") {
      turnLeft(150);
      delay(2000);
      stopRobot();
    } else if (result == "right") {
      turnRight(150);
      delay(2000);
      stopRobot();
    } else if (result.length() > 0) {
      float val = result.toFloat(); 
      if (val > 0) {
        targetDistance = val;
        distanceX = 0;
        velocityX = 0;
        Serial.print("Target Set (cm): ");
        Serial.println(val);
      }
    }
  }

  if (targetDistance > 0) {
    if (distanceX < targetDistance) {
      moveForward(160);
    } else {
      stopRobot();
      
      // Send "R" to ESP32/Raspberry Pi
      Serial1.println("R"); 
      Serial.println("Destination Reached!");
      
      targetDistance = -1.0;
      velocityX = 0;
      distanceX = 0;
    }
  }

  static unsigned long lastPrint = 0;
  if (millis() - lastPrint > 500 && targetDistance > 0) {
    Serial.print("Dist (cm): "); 
    Serial.println(distanceX);
    lastPrint = millis();
  }
}