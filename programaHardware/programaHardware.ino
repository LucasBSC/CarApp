#include <SoftwareSerial.h>

SoftwareSerial bluetooth(10,11);

#define porta 3
#define led2 4

String comando;

void setup() {
  //Serial.begin(9600);
  bluetooth.begin(9600);
  pinMode(porta, OUTPUT);
  pinMode(led2, OUTPUT);
}

void loop() {

  comando = "";

  if (bluetooth.available()) {
    while(bluetooth.available()) {
    char character = bluetooth.read();
    comando += character;
    delay(10);
    }
 // COMANDOS VINDOS DO CELULAR
    if (comando.indexOf("pulsoPorta") >= 0) {
      digitalWrite(porta, !digitalRead(porta));                  
    }

 // LEITURAS DO MICROPROCESSADOR

   // Se porta está aberta, envia mensagem
   if (digitalRead(porta)) {
    bluetooth.print("{");
    bluetooth.print("portaAberta");
    bluetooth.print("}");
   }

   if (!digitalRead(porta)) {
    bluetooth.print("{");
    bluetooth.print("portaFechada");
    bluetooth.print("}");
   }   
   
  }
}
   
