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
}

void setup() {
  Serial.begin(9600);
  Setpoint = 100;
  md.init();
  we.init(11,13,3,5);
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
  delay(3000);
  exploration();
  return;
  resetEncoderCount();
  char* temp= getRPiMsg();
  if (strlen(temp) <= 0) {
    return;
  }

  if ((strcmp(temp, "START")) == 0) {
    exploration();
  } else {
    int n = strlen(temp);
    for (int i = 0; i < n; i++) {
      make_move(temp[i]);
    }
  }
}
// maze
// 1 - obstacle
// 0 - unexplored
// 2 - free
// > 2 robot path
char maze[22][17];
//direction
//0 - WEST
//1 - NORTH
//2 - EAST
//3 - SOUTH

int dx[] = {0, 1, 0, -1};
int dy[] = {-1, 0, 1, 0};
int x = 10, y = 8, d = 0, cnt = 3;

const int WEST = 0, NORTH = 1, EAST = 2, SOUTH = 3;
const int START_X = 10, START_Y = 8, START_DIR = 0, FINISH_X = 2, FINISH_Y = 2;
const int LENGTH = 3;

bool reach_goal() {
  return x == FINISH_X && y == FINISH_Y;
}

bool outside(int u, int v) {
  return u < 1 || u > 20 || v < 1 || v > 15;
}

bool obstacle(int u, int v, bool reading) {
  return outside(u, v) || reading;
}

void update_maze(int u, int v, bool reading) {
  if (obstacle(u, v, reading)) {
    maze[u][v] = 1;
  } else {
    maze[u][v] = 2 > maze[u][v] ? 2 : maze[u][v];
  }
}

void update_front() {
  int u = x + dx[d], v = y + dy[d];
  int u1 = u - (u == x), v1 = v - (v == y);
  int u2 = u + (u == x), v2 = v + (v == y);
  u += dx[d], v += dy[d];
  u1 += dx[d], v1 += dy[d];
  u2 += dx[d], v2 += dy[d];
  update_maze(u, v, has_obstacle_front_center());
  update_maze(u1, v1, has_obstacle_front_left());
  update_maze(u2, v2, has_obstacle_front_right());
}

void update_left() {
  int u = x + dx[d], v = y + dy[d];
  String s = getSide(A3);
  for (int i = 2; i < (2 + LENGTH); i++) {
    int uu = u + dy[d] * i, vv = v + dx[d] * i;
    update_maze(uu, vv, s[i - 2] - '0');
    if (maze[uu][vv] == 1) break;
  }
}

void update_right() {
  int u = x + dx[d], v = y + dy[d];
  String s = getSide(A4);
  for (int i = 2; i < (2 + LENGTH); i++) {
    int uu = u - dy[d] * i, vv = v - dx[d] * i;
    update_maze(uu, vv, s[i - 2] - '0');
    if (maze[uu][vv] == 1) break;
  }
}

void update_sensor() {
  update_front();
  update_left();
  update_right();
}

void mark() {
  maze[x][y] = cnt++;
}

//robot go forward
void do_go_forward() {
  cruise_ten();
  delay(100);
}

void go_forward() {
  do_go_forward();
  x = x + dx[d], y = y + dy[d];
  mark();
}

bool ok(int x, int y) {
  for (int i = -1; i <= 1; i++)
    for (int j = -1; j <= 1; j++) {
      int u = x + i, v = y + j;
      if (outside(u, v) || maze[u][v] == 1) return false;
    }
  return true;
}

bool can_go(int dir) {
  int u = x + dx[dir], v = y + dy[dir];
  return ok(u, v);
}
//robot turn right
void do_turn_right() {
  moveRight(90, 255);
  delay(100);
}
void turn_right() {
  do_turn_right();
  d = (d + 1)%4;
}
//robot turn left
void do_turn_left() {
  moveLeft(90, 255);
  delay(100);
}

void turn_left() {
  do_turn_left();
  d = (d + 3)%4;
}

void turn_to_dir(int new_dir) {
  int diff = (new_dir + 4 - d) % 4;
  if (diff <= 2) {
    for (int i = 0; i < diff; i++) turn_right();
  } else {
    turn_left();
  }
}

void go_with_dir(int dir) {
  turn_to_dir(dir);
  go_forward();
}

bool reach_wall() {
  return x == 2 || x == 19 || y == 2 || y == 14;
}

void find_wall() {
  while (!reach_wall()) {
    update_sensor();
    if (can_go(WEST)) {
      go_with_dir(WEST);
      continue;
    }
    if (can_go(NORTH)) {
      go_with_dir(NORTH);
      continue;
    }
    if (can_go(SOUTH)) {
      go_with_dir(SOUTH);
      continue;
    }
    while (!can_go(NORTH) && !can_go(SOUTH)) {
      update_sensor();
      go_with_dir(EAST);
    }
  }
}

void adjust_wall() {
  turn_right();
}

int get_left(int d) {
  return (d + 3) % 4;
}

int get_right(int d) {
  return (d + 1) % 4;
}

void follow_wall() {
  while (!reach_goal()) {
    update_sensor();
    int left = get_left(d);
    if (can_go(left)) {
      go_with_dir(left);
      continue;
    }
    if (can_go(d)) {
      go_with_dir(d);
      continue;
    }
    int right = get_right(d);
    turn_to_dir(right);
  }
}

void adjust_finish() {

}

void init_location() {
  x = START_X; y = START_Y; d = START_DIR;
  for (int i = -1; i <= 1; i++)
    for (int j = -1; j <= 1; j++) {
      int u = x + i, v = y + j;
      maze[u][v] = 2;
    }
  mark();
}

void exploration() {
  init_location();
  find_wall();
  adjust_wall();
//  cerr << "hello" << endl;
  follow_wall();
  adjust_finish();
}


//----------Public functions-----------//
void cruise_ten() {
  // Number of ticks for 10cm
  resetEncoderCount();

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
    leftMotorPID.SetOutputLimits(235,238);//250,270
    rightMotorPID.SetOutputLimits(235,236);//250,275

    forward();
  }
  md.setBrakes(250,250);  //initial 300
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
  return distance <= 15;
}

int has_obstacle_front_right() {
  float distance = getIR(A4);
  return distance <= 15;
}

int has_obstacle_front_center() {
  float distance = getUltra();
  return distance <= 10;
}

String getFront() {
   int grid[3];
   for (int i=0;i<3;i++){
     grid[i] = 0;
   }
   grid[0] = has_obstacle_front_left();
   grid[2] = has_obstacle_front_right();
   grid[1] = has_obstacle_front_center();
   String results = "";
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
  for (int i=0;i<4;i++){
    grid[i] = 0;
  }

  if ((reading-10)<err){
    grid[0] = 1;
    grid[1] = 2;
    grid[2] = 2;
    grid[3] = 2;
  }
  else if ((reading-20)<err){
    grid[0] = 0;
    grid[1] = 1;
    grid[2] = 2;
    grid[3] = 2;
  }
  else if ((reading-30)<err){
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
  digitalWrite(URTRIG, LOW);
  digitalWrite(URTRIG, HIGH);               // reading Pin PWM will output pulses

  unsigned long DistanceMeasured=pulseIn(URPWM,LOW);
  if(DistanceMeasured==50000){              // the reading is invalid.
    Serial.print("Invalid");
  }
  else{
    return DistanceMeasured/50;           // every 50us low level stands for 1cm
  }
}

void straighten() {
  float difference = IRMedian(A3,3)-IRMedian(A4,3);
  float diff_total = 0;
  float diff_limit = 0.2;
  
  if (getUltra()<5){
    for (int k=0;k<10;k++){
      md.setSpeeds(-150,-150); 
    }
  }
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


