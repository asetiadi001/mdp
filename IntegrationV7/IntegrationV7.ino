#include <PololuWheelEncoders.h>
#include <DualVNH5019MotorShield.h>
#include <PID_v1.h>

DualVNH5019MotorShield md;
PololuWheelEncoders we;

// Define Pins
const int MotorLeft_dir = 2; // Left motor direction pin
const int MotorRight_dir = 7; //Right motor direction pin
const int MotorLeftPWN = 10;
const int MotorRightPWN = 9;

int URPWM = 6; // PWM Output 0－25000US，Every 50US represent 1cm
int URTRIG = 12; // PWM trigger pin
 
unsigned int Distanceultra = 5;
//uint8_t EnPwmCmd[4]={0x44,0x02,0xbb,0x01};

//-----PID parameters----
//Define Variables we'll be connecting to
double Setpoint, analogLeftMotorInput, analogRightMotorInput;
static double outputleftMotorpower, outputrightMotorpower;

//Define the aggressive and conservative Tuning Parameters
double aggKp=4.7, aggKi=0.2, aggKd=1; //4.5
double consKp=1, consKi=0.05, consKd=0.25; //1,0.05,0.25

//Specify pid controls for left and right motors
PID leftMotorPID(&analogLeftMotorInput, &outputleftMotorpower, &Setpoint, consKp, consKi, consKd, DIRECT);
PID rightMotorPID(&analogRightMotorInput, &outputrightMotorpower, &Setpoint, consKp, consKi, consKd, DIRECT);


//Serial Read String Variable
char inData[1000]; // Allocate some space for the string
char inChar=-1; // Where to store the character read
byte index = 0; // Index into array; where to store the character


char* getRPiMsg() {
  memset(inData, 0, sizeof(inData));  
  Serial.readBytes(inData, 1000);
  return inData;
  
  char* streamBuffer = (char*) malloc (20);
  char charBuffer;
  int index=0;
  
  while(Serial.available() > 0){
    if(index<19){
     charBuffer=Serial.read();
     streamBuffer[index]=charBuffer;
     index++; 
    }
  }
 streamBuffer[index]='\0';
 index=0;
 return streamBuffer; 
}

void setup() {
  Serial.begin(9600);

   Setpoint = 100;
   md.init();
   we.init(11,13,3,5);
  //turn the PID on
   leftMotorPID.SetMode(AUTOMATIC);
   rightMotorPID.SetMode(AUTOMATIC);
   leftMotorPID.SetSampleTime(10);
   rightMotorPID.SetSampleTime(10);   
   
   pinMode(URTRIG,OUTPUT);                     // A low pull on pin COMP/TRIG
   digitalWrite(URTRIG,HIGH);                  // Set to HIGH
//  
   pinMode(URPWM, INPUT); 
}

void doScan() {
    Serial.print("1");
    Serial.print(getFront());
    Serial.print("_");
    Serial.print(getSide(A2));
    Serial.print("_");
    Serial.println(getSide(A5));  
}

void make_move(char c) {
  switch (c) {
    case 'F': cruise_ten();
              break;
    case 'R': moveRight(90, 225);
              break;
    case 'L': moveLeft(90, 225);
              break;
    case 'B': md.setBrakes(400, 400);
              break;  
    case 'S': doScan();
              break;
    case 'C': straighten();
              break;  
    default : md.setSpeeds(0,0);  
  }
  if (c != 'S') {
    Serial.print('1');
    Serial.println(c);
  }  
}
void loop() {
  resetEncoderCount();
  char* temp= getRPiMsg();
  if (strlen(temp) <= 0) {
    return;
  }
  
  if (strlen(temp) == 1) {
    make_move(temp[0]);
  } else {
    int n = strlen(temp);
    for (int i = 0; i < n; i++) {
      make_move(temp[i]);
    }
  }
//    if(strcmp(temp, "F") == 0){
//      //Move 10cm
//      cruise_ten();  
//    } else if(strcmp(temp, "R") == 0){
//      //Rotate right 90 degrees at 280 speed
//      moveRight(90, 280);
//    } else if(strcmp(temp, "L") == 0){
//      //Rotate left 90 degrees at 280 speed
//      moveLeft(90, 280);
//    } else if(strcmp(temp, "B") == 0){
//      //E.Brake
//      md.setBrakes(400, 400);
//    } else if(strcmp(temp, "S") == 0){
//      Serial.print("1");
//      Serial.print(getFront());
//      Serial.print("_");
//      Serial.print(getSide(A2));
//      Serial.print("_");
//      Serial.println(getSide(A5));
//    } else if(strcmp(temp, "Q") == 0){
//      //E.Brake
//      Serial.print(IRMedian(A3,5));
//      Serial.print(",");
//      Serial.println(IRMedian(A4,5));
//    } else if(strcmp(temp, "C") == 0){
//      //E.Brake
//      straighten();
//    } else{
//      md.setSpeeds(0,0);
//    }
//    if(strcmp(temp, "S")) {
//      Serial.print("1");
//      Serial.println(temp);
//    }
//  
//  delay(100);
}


//----------Public functions-----------//
void cruise_ten() {
  // Number of ticks for 10cm
  float noOfTicksForDist = 480; //450//430
  
  float leftTicksForAngleOrDist = 0;
  float rightTicksForAngleOrDist = 0;
  float avgTicksForAngleOrDist = 0;
  
  long firstLeftCount = abs(PololuWheelEncoders::getCountsM1());
  long firstRightCount = abs(PololuWheelEncoders::getCountsM2());
  
  while (avgTicksForAngleOrDist < noOfTicksForDist) {
    leftTicksForAngleOrDist = abs(PololuWheelEncoders::getCountsM1());
    leftTicksForAngleOrDist = leftTicksForAngleOrDist - firstLeftCount;
    
    rightTicksForAngleOrDist = abs(PololuWheelEncoders::getCountsM2());
    rightTicksForAngleOrDist = rightTicksForAngleOrDist - firstRightCount;
    
    avgTicksForAngleOrDist = (leftTicksForAngleOrDist + rightTicksForAngleOrDist) / 2; 
    leftMotorPID.SetOutputLimits(250,270);
    rightMotorPID.SetOutputLimits(250,275);

    forward();
    //md.setBrakes(300,300);
    //return;

    //----cruise------
//    if (getIR(A3)>12 && getIR(A4)>12) {
//      leftMotorPID.SetOutputLimits(250,270);
//      rightMotorPID.SetOutputLimits(250,275);
//      
//      forward();
//    }
//    else {
//      md.setBrakes(300,300);
//      delay(200);
//      //straighten();
//      break;
//    }
  }
  md.setBrakes(250,250);  //initial 300
 // if (has_obstacle_front_left() && has_obstacle_front_right()) {
 //   straighten();
//  }
}

void moveRight(int degree,int motorPower) {
  //distance in mm
  int revolutionNeeded = 8 * degree;
  int totalRevolution = 0;
  
  int m1Power = motorPower;
  int m2Power = (-1 * motorPower);
  
  resetEncoderCount();
  
  while((abs(we.getCountsM1()) < revolutionNeeded) )//&& (Serial.available() <= 0))
  {
    md.setSpeeds(m1Power, m2Power);
  }
  md.setBrakes(m1Power,m2Power);
}

void moveLeft(int degree,int motorPower) {
  //distance in mm
  int revolutionNeeded = 8 * degree;
  int totalRevolution = 0;
  // 500 count 50 
  int m1Power = (-1 * motorPower);
  int m2Power = motorPower;
  
  resetEncoderCount();
  
  while((abs(we.getCountsM1()) < revolutionNeeded))//&& (Serial.available() <= 0))
  {
    md.setSpeeds(m1Power, m2Power);
  }
  md.setBrakes(m1Power,m2Power);
}

int has_obstacle_front_left() {
  float distance = getIR(A3);
//  Serial.println(distance);  
  return distance <= 10;
}

int has_obstacle_front_right() {
  float distance = getIR(A4);
  //Serial.println(distance);  
  return distance <= 10;
}

int has_obstacle_front_center() {
  float distance = getUltra();   
  //Serial.println(distance);    
  return distance <= 8;
}

String getFront() {
   float detectNear = 10;
   float err = 0.5;
   String results = "";
   int grid[3];
   for (int i=0;i<3;i++){
     grid[i] = 0;
   }

   grid[0] = has_obstacle_front_left();
   grid[2] = has_obstacle_front_right();
   grid[1] = has_obstacle_front_center();  
   for (int i=0;i<3;i++){
     results.concat(grid[i]);
   }
   return results;
}

String getSide(int irPin){
   float err = 0.8;
   float reading = IRMedian(irPin,7);
   String results = "";
   int grid[4];
   /*
     [0 1 2 4]
    */
//    Serial.print("Median reading: ");
//    Serial.println(reading);
    for (int i=0;i<4;i++){
      grid[i] = 0;
    }
    
    if ((reading-7)<err){
      grid[0] = 1;
      grid[1] = 2;
      grid[2] = 2;
      grid[3] = 2;
    }
    else if ((reading-17)<err){
      grid[0] = 0;
      grid[1] = 1;
      grid[2] = 2;
      grid[3] = 2;
    }
    else if ((reading-29)<err){
      grid[0] = 0;
      grid[1] = 0;
      grid[2] = 1;
      grid[3] = 2;
    }
    else if ((reading-40)<err){
      grid[0] = 0;
      grid[1] = 0;
      grid[2] = 0;
      grid[3] = 1;
    }
    
    for (int i=0;i<4;i++){
      results.concat(grid[i]);
    }
    
    return results; 
}

//----------Private functions------------//
void resetEncoderCount() {
  we.getCountsAndResetM1();
  we.getCountsAndResetM2();
}

float getIR(int irPin) {
  float distance = 0.0;
  int numSample = 5;
  for(int i = 1; i <= numSample; i++)   {
    distance += (12343.85 * pow(analogRead(irPin),-1.15));
    delay(5);
  }
  distance = distance / (float)numSample;
//  Serial.println(distance);
//  Serial.print(irPin);
//  Serial.print(" Distance :");
//  Serial.print(distance);
//  Serial.println(" cm");
  return distance;
}

float IRMedian(int irPin, int numRead){
  float readings[numRead];
  float tmp;
  for(int i=0;i<numRead;i++){
    readings[i] = getIR(irPin);
  }
  for(int j=1;j<numRead;j++){
    for(int k=j;k>0;k--){
      if (readings[k]>readings[k-1]){
        tmp = readings[k-1];
        readings[k-1] = readings[k];
        readings[k] = tmp;
      }
    }
  }
  return readings[numRead/2];
}

float getUltra() {
//  pinMode(MotorLeft_dir, INPUT);
//  pinMode(MotorRight_dir, OUTPUT);
//  pinMode(MotorLeftPWN, OUTPUT);
//  pinMode(MotorRightPWN, OUTPUT);
//  int numSample = 25;
//  float distance = 0.0;
//  for(int i=0;i<numSample;i++) {
//    distance += digitalRead(MotorLeft_dir);
//  }
//  distance = distance / (float)numSample;
//  return distance;
    //unsigned int Distance=0;
   
    float Distanceultra2 = 0;
    digitalWrite(URTRIG, LOW);
    digitalWrite(URTRIG, HIGH);               // reading Pin PWM will output pulses
     
    unsigned long DistanceMeasured=pulseIn(URPWM,LOW);
    if(DistanceMeasured==50000){              // the reading is invalid.
      Serial.print("Invalid");    
   }
    else{
      Distanceultra2=DistanceMeasured/50;           // every 50us low level stands for 1cm
   }
return Distanceultra2;


}

void straighten() {
  float difference = IRMedian(A3,3)-IRMedian(A4,3);
  float diff_total = 0;
  float diff_limit = 0.2;
  
  for(int i=0;i<21;i++){
     if (i % 3 == 0) {
       diff_total = 0;
     }
     diff_total = diff_total + difference;
     if (i % 3 == 2) {
       difference = diff_total / 3;
       if (abs(difference) > 10) {
         return;
       }
       else if (difference > diff_limit) {
         moveRight(1,200);
       }
       else if (difference < -diff_limit) {
         moveLeft(1,200);
       }
     }
     difference = IRMedian(A3,3)-IRMedian(A4,3);
  } 
}

void sprint() {
  if (getIR(A3)>20 && getIR(A4)>20) {
    leftMotorPID.SetOutputLimits(380,397);
    rightMotorPID.SetOutputLimits(380,405);
    
    forward();
  }
  else {
    md.setBrakes(400,400);
    delay(100);
    straighten();
  }
}

void cruise() {
  if (getIR(A3)>12 && getIR(A4)>12) {
    leftMotorPID.SetOutputLimits(250,270);
    rightMotorPID.SetOutputLimits(250,275);
  
    forward();
  }
  else {
    md.setBrakes(300,300);
    delay(200);
    straighten();
  }
}

void forward() {
  pinMode(MotorLeft_dir, OUTPUT);
  pinMode(MotorRight_dir, OUTPUT);
  pinMode(MotorLeftPWN, OUTPUT);
  pinMode(MotorRightPWN, OUTPUT);
  
  digitalWrite(MotorLeft_dir,HIGH);
  digitalWrite(MotorRight_dir,HIGH);
  analogLeftMotorInput = analogRead(0);
  analogRightMotorInput = analogRead(1);
  
  double gap = abs(Setpoint-analogLeftMotorInput); //distance away from setpoint
  double gap2 = abs(Setpoint-analogRightMotorInput);

  if(gap<10)  {  //we're close to setpoint, use conservative tuning parameters
    leftMotorPID.SetTunings(consKp, consKi, consKd);
  }
  else  {  //we're far from setpoint, use aggressive tuning parameters
     leftMotorPID.SetTunings(aggKp, aggKi, aggKd);
  }
  
  if(gap2<10)  {  //we're close to setpoint, use conservative tuning parameters
    rightMotorPID.SetTunings(consKp, consKi, consKd);
  }
  else  {  //we're far from setpoint, use aggressive tuning parameters
     rightMotorPID.SetTunings(aggKp, aggKi, aggKd);
  }

  leftMotorPID.Compute();
  rightMotorPID.Compute();

  analogWrite(MotorLeftPWN,outputleftMotorpower);
  analogWrite(MotorRightPWN,outputrightMotorpower);
}
