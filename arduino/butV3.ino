#include <Ultrasonic.h>
#include <Wire.h>
#include <rgb_lcd.h>

#define DEBUG_ENABLED  1
#define MIN_MATCH 10

rgb_lcd lcd;

const int colorR = 200;
const int colorG = 200;
const int colorB = 200;

Ultrasonic rougeSensor(7);
Ultrasonic bleuSensor(8);

int moyRouge = 0;
int moyBleu = 0;
int delta=3 ;//erreur de calcul ou de mesure
int scoreBleu=0;
int scoreRouge=0;
int minutes_remain = 0;       // Nombre de minutes restantes (par défaut 10)
int secondes_remain = MIN_MATCH;     // Nombre de secondes restantes (par défaut 0)


#define buzzer 4   // affectation des broches
int frequence[] = {262, 294, 330, 349, 370, 392, 440, 494};      // tableau de fréquence des notes

char sentData;

void setup()
{  
  int temp;
    Serial.begin(115200);
    while (!Serial) {
      ; // wait for serial port to connect. Needed for native USB port only
    }
    lcd.begin(16, 2);

    lcd.setRGB(colorR, colorG, colorB);

    //Print a message to the LCD.
    lcd.setCursor(0,0);
    lcd.print("Bienvenue sur");
    lcd.setCursor(0,1);
    lcd.print("le BabyFoot");
    //Serial.println("Bienvenue sur le BabyFoot");

    int SerialOK=0;
    //Serial.println("Taper s pour start");
    while(SerialOK==0) {
      if (Serial.available() > 0){
        delay(5);
        sentData = Serial.read();
        if(sentData=='s') {
          SerialOK=1;
        }
      }
    }
//Calcul des distances moyennes dans les cages..
    //Serial.println("\n--Calcul des distances--");
    for(int i=0; i<10;i++){
      //Serial.println(moyRouge);
      temp = rougeSensor.MeasureInCentimeters();
      moyRouge = moyRouge + temp; 
      delay(10);
    }
    moyRouge = moyRouge/10;


    for(int i=0; i<10;i++){
      //Serial.println(moyBleu);
      temp = bleuSensor.MeasureInCentimeters();
      moyBleu = moyBleu + temp; 
      delay(10);
    }
    moyBleu = moyBleu/10;
    
    //Serial.println("\n-Le match commence !");
    afficherScore();
}

void loop()
{  
 
    timer();
    
    int bleu;
    bleu = bleuSensor.MeasureInCentimeters(); // two measurements should keep an interval
    //Serial.print("distance bleu : ");
    //Serial.println(bleu);

    //capture d'un but bleu ou rouge
    if((bleu<moyBleu-delta) || bleu>=moyBleu+delta){
      but('b');
    }

    delay(100);
    
    int rouge;
    rouge = rougeSensor.MeasureInCentimeters(); // two measurements should keep an interval
    //Serial.print("distance rouge : ");
    //Serial.println(rouge);
    if((rouge<moyRouge-delta) || rouge>=moyRouge+delta){
      but('r');
    }

    //reset match
    if (Serial.available()) {
      sentData=Serial.read();
      if(sentData=='r'){
        resetMatch();
      }
    }

    finMatch();
}

//FONCTIONS
void afficherScore(){
  lcd.setCursor(0,0);
  lcd.print("Bleu : ");
  lcd.print(scoreBleu);
  lcd.print("       ");
  lcd.setCursor(0,1);
  lcd.print("Rouge : ");
  lcd.print(scoreRouge);
  lcd.print("       ");
}

void resetMatch(){
  lcd.setRGB(colorR, colorG, colorB);
  //Serial.println("reset du match");
  minutes_remain=MIN_MATCH;
  secondes_remain=0;
  scoreBleu=0;
  scoreRouge=0;
  afficherScore();
}

void timer(){
  static unsigned long last_time = 0; // Temps antérieur
  unsigned long time_now = millis(); //Temps actuel

  // Et que 1 sec s'est écoulée
  if(time_now - last_time >= 1000) {
    // Décrémentation des secondes
    if(--secondes_remain < 0) {
      secondes_remain = 59; // Si il n'y as plus de seconde
      // Décrémentation des minutes
      if(--minutes_remain < 0) {
        minutes_remain = 0;   //
        secondes_remain = 0; // Si il n'y as plus de minute
      }
    }
    last_time = time_now;
  }

  //affichage timer
  lcd.setCursor(11,0);
  if(minutes_remain>9){
    lcd.print(minutes_remain);
  }else{
    lcd.print("0");
    lcd.print(minutes_remain);
  }
  lcd.print(":");
  if(secondes_remain>9){
    lcd.print(secondes_remain);
  }else{
    lcd.print("0");
    lcd.print(secondes_remain);
  }
}

void finMatch(){
  if(minutes_remain==0 && secondes_remain==0){
    for(int i=0;i<10;i++){
      lcd.setRGB(colorR, colorG, colorB);
      delay(150);
      lcd.setRGB(0,255,0);
      delay(150);
    }
    Serial.print("f");
    //Serial.println("--Fin du match--");
    lcd.setCursor(0,0);
    if(scoreRouge>scoreBleu){
      lcd.print("Les Rouges gagne        ");
      Serial.println("r");
    }else{
      if(scoreRouge<scoreBleu){
        lcd.print("Les Bleus gagne        ");
        Serial.println("b");
      }else{
        lcd.print("Egalite !               ");
        Serial.println("e");
      }
      
    }
    lcd.setCursor(0,1);
    lcd.print("Restart? tapez r");

    int SerialOK=0;
    while(SerialOK==0) { // tant qu'un r n'est pas reçu
      if (Serial.available() > 0){
        //delay(5);
        sentData = Serial.read();
        if(sentData=='r') {
          resetMatch();
          SerialOK=1;
        }
      }
    }
  }
}

void but(char e){
  if(e=='b'){
    Serial.println("b");
    scoreBleu++;
    for(int i =0 ; i<5;i++){
      tone(buzzer, frequence[i], 250);
      lcd.setRGB(255, 255, 255);
      delay(250);
      lcd.setRGB(0, 0, 255);
      delay(250);
    }
  }
  if(e=='r'){
    Serial.println("r");
    scoreRouge++;
    for(int i =0 ; i<5;i++){
      tone(buzzer, frequence[i], 250);
      lcd.setRGB(255, 255, 255);
      delay(250);
      lcd.setRGB(255, 0, 0);
      delay(250);
    }
  }
  lcd.setRGB(255, 255, 255);
  afficherScore();
}
