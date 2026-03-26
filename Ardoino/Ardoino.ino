#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>
#include <Wire.h>

Adafruit_MPU6050 mpu;

float velocityX = 0.0;
float distanceX = 0.0;
unsigned long previousTime = 0;

// Driver 1 - RIGHT SIDE
#define AIN1_R 22
#define AIN2_R 23
#define PWMA_R 44
#define BIN1_R 24
#define BIN2_R 25
#define PWMB_R 45

// Driver 2 - LEFT SIDE
#define AIN1_L 26
#define AIN2_L 27
#define PWMA_L 46
#define BIN1_L 28
#define BIN2_L 29
#define PWMB_L 2

void moveForward(int speed) {
  rightForward(speed);
  leftForward(speed);
}

void moveBackward(int speed) {
  rightBackward(speed);
  leftBackward(speed);
}

void turnRight(int speed) {
  leftForward(speed); 
  rightBackward(speed);
}

void turnLeft(int speed) {
  leftBackward(speed);
  rightForward(speed);
}

void stopRobot() {
  stopLMotors();
  stopRMotors();
}

// --- LOW LEVEL HELPER FUNCTIONS ---

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

//MAIN FUNCTIONS

void setup() {
  Serial.begin(115200);
  if (!mpu.begin()) {
    Serial.println("MPU6050 not found!");
    while(1);
  }
  mpu.setAccelerometerRange(MPU6050_RANGE_8_G);
  previousTime = micros();

  // Right driver pins
  pinMode(AIN1_R, OUTPUT);
  pinMode(AIN2_R, OUTPUT);
  pinMode(PWMA_R, OUTPUT);
  pinMode(BIN1_R, OUTPUT);
  pinMode(BIN2_R, OUTPUT);
  pinMode(PWMB_R, OUTPUT);

  // Left driver pins
  pinMode(AIN1_L, OUTPUT);
  pinMode(AIN2_L, OUTPUT);
  pinMode(PWMA_L, OUTPUT);
  pinMode(BIN1_L, OUTPUT);
  pinMode(BIN2_L, OUTPUT);
  pinMode(PWMB_L, OUTPUT);

  stopRobot();
}

void loop() {
  sensors_event_t a, g, temp;
  mpu.getEvent(&a, &g, &temp);

  unsigned long currentTime = micros();
  float dt = (currentTime - previousTime) / 1000000.0;
  previousTime = currentTime;

  float accelX = a.acceleration.x;
  velocityX += accelX * dt;
  distanceX += velocityX * dt;

  if (Serial1.available()) {
    targetDistance = Serial1.readString();
  }

  

  Serial.print("Target: ");
  Serial.print(targetDistance);
  Serial.print(" | Current: ");
  Serial.println(distanceX);

  if (distanceX < targetDistance) {
    moveForward(150);
  } else {
    stopRobot();
  }
}