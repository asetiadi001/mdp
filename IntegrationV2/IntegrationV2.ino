#include <PololuWheelEncoders.h>
#include <DualVNH5019MotorShield.h>
#include <PID_v1.h>

const int MotorLeft_dir = 2; // Left motor direction pin
const int MotorRight_dir = 7; //Right motor direction pin
const int MotorLeftPWN = 10;
const int MotorRightPWN = 9;

DualVNH5019MotorShield md;
PololuWheelEncoders we;
//-----PID parameters----
//Define Variables we'll be connecting to
double Setpoint, analogLeftMotorInput, analogRightMotorInput;
static double outputleftMotorpower, outputrightMotorpower;

//Define the aggressive and conservative Tuning Parameters
double aggKp=6, aggKi=0.2, aggKd=1;
double consKp=1, consKi=0.05, consKd=0.25; //1,0.05,0.25

//Specify pid controls for left and right motors
PID leftMotorPID(&analogLeftMotorInput, &outputleftMotorpower, &Setpoint, consKp, consKi, consKd, DIRECT);
PID rightMotorPID(&analogRightMotorInput, &outputrightMotorpower, &Setpoint, consKp, consKi, consKd, DIRECT);

//Serial Read String Variable
char inData[20]; // Allocate some space for the string
char inChar=-1; // Where to store the character read
byte index = 0; // Index into array; where to store the character


char* getRPiMsg(){
  char streamBuffer[20];
  char charBuffer;
  int index=0;
  
  while(Serial.available() > 0){
    if(index<19){
     charBuffer=Serial.read();
     Serial.println(charBuffer);
     streamBuffer[index]=charBuffer;
     index++; 
    }
  }
 streamBuffer[index]='\0';
 index=0;
 return streamBuffer; 
}

//Serial Read String Function
char Comp(char* This) {
    while (Serial.available() > 0) // Don't read unless
                                   // there you know there is data
    {
        if(index < 19) // One less than the size of the array
        {
            inChar = Serial.read(); // Read a character
            Serial.println(inChar);
            Serial.println(sizeof(inChar));
            inData[index] = inChar; // Store it
            index++; // Increment where to write next
            inData[index] = '\0'; // Null terminate the string
        }
    }

    if (strcmp(inData,This)  == 0) {
        for (int i=0;i<19;i++) {
            inData[i]=0;
        }
        index=0;
        return(1);
    }
    else {
        return(0);
    }
}

void setup()
{
  Serial.begin(1200);
   Setpoint = 100;
   md.init();
   we.init(11,13,3,5);
  //turn the PID on
   leftMotorPID.SetMode(AUTOMATIC);
   rightMotorPID.SetMode(AUTOMATIC);
   leftMotorPID.SetSampleTime(10);
   rightMotorPID.SetSampleTime(10);
   leftMotorPID.SetOutputLimits(380,397);
   rightMotorPID.SetOutputLimits(380,405);
   
}

void loop()
{
  Serial.println("Loop started");
  char* temp= getRPiMsg();
  if(strcmp(temp, "F") == 0){
    while(Serial.available() <= 0){
      if (getIR(A3)>18 && getIR(A4)>18) {
        forward2();
      }
      else {
        md.setBrakes(400,400);
        break;
      }
    }    
  }else if(strcmp(temp, "R") == 0){
   moveRight(90, 300);
  } else if(strcmp(temp, "L") == 0){
    moveLeft(90, 300);
  }else if(strcmp(temp, "B") == 0){
    md.setBrakes(400, 400);
  }else{
    md.setSpeeds(0,0);
  }
    
}

void forward2()
{
  digitalWrite(MotorLeft_dir,HIGH);
  digitalWrite(MotorRight_dir,HIGH);
  
  analogLeftMotorInput = analogRead(0);
  analogRightMotorInput = analogRead(1);
  Serial.print("Left motor analog value = ");
  Serial.println(analogLeftMotorInput);
  Serial.print("Right motor analog value = ");
  Serial.println(analogRightMotorInput);
  double gap = abs(Setpoint-analogLeftMotorInput); //distance away from setpoint
  double gap2 = abs(Setpoint-analogRightMotorInput);
  Serial.print("Left motor gap = ");
  Serial.println(gap);
  Serial.print("Right motor gap = ");
  Serial.println(gap2);
  if(gap<10)
  {  //we're close to setpoint, use conservative tuning parameters
    leftMotorPID.SetTunings(consKp, consKi, consKd);
  }
  else
  {
     //we're far from setpoint, use aggressive tuning parameters
     leftMotorPID.SetTunings(aggKp, aggKi, aggKd);
  }
  
  if(gap2<10)
  {  //we're close to setpoint, use conservative tuning parameters
    rightMotorPID.SetTunings(consKp, consKi, consKd);
  }
  else
  {
     //we're far from setpoint, use aggressive tuning parameters
     rightMotorPID.SetTunings(aggKp, aggKi, aggKd);
  }

  leftMotorPID.Compute();
  rightMotorPID.Compute();
  Serial.print("Left motor tune power = ");
  Serial.println(outputleftMotorpower);
  Serial.print("Right motor tune power = ");
  Serial.println(outputrightMotorpower);
  analogWrite(MotorLeftPWN,outputleftMotorpower);
  analogWrite(MotorRightPWN,outputrightMotorpower);
}

void moveRight(int degree,int motorPower)
{
  //distance in mm
  int revolutionNeeded = 8 * degree;
  int totalRevolution = 0;
  //Serial.print("Revolution Needed= ");
  //Serial.println(revolutionNeeded);
  // 500 count 50 
  
  int m1Power = motorPower;
  int m2Power = (-1 * motorPower);
  
  resetEncoderCount();
  
  while((abs(we.getCountsM1()) < revolutionNeeded) && (Serial.available() <= 0))
  {
    md.setSpeeds(m1Power, m2Power);
    //Serial.println("turning");
  }
  md.setBrakes(m1Power,m2Power);
  //md.setSpeeds(0, 0);
  //Serial.println("stop");
}

void moveLeft(int degree,int motorPower)
{
  //distance in mm
  int revolutionNeeded = 8 * degree;
  int totalRevolution = 0;
  // 500 count 50 
  
  int m1Power = (-1 * motorPower);
  int m2Power = motorPower;
  
  resetEncoderCount();
  
  while((abs(we.getCountsM1()) < revolutionNeeded) && (Serial.available() <= 0))
  {
    md.setSpeeds(m1Power, m2Power);
  }
  md.setBrakes(m1Power,m2Power);
  //md.setSpeeds(0, 0);  
}

void resetEncoderCount()
{
  we.getCountsAndResetM1();
  we.getCountsAndResetM2();
}

float getIR(int irPin)
{
  float distance = 0.0;
  int numSample = 25;
  for(int i = 1; i <= numSample; i++)
  {
    distance += (12343.85 * pow(analogRead(irPin),-1.15));
  }
  distance = distance / (float)numSample;
  
  //distance = (12343.85 * pow(analogRead(irPin),-1.15));
  Serial.print("Distance :");
  Serial.print(distance);
  Serial.println(" cm");
  return distance;
}
