#include <SoftwareSerial.h>

SoftwareSerial bluetooth(10,11);

#define porta 3
#define partidaVeiculo 4
#define painel 5
#define posChave 6

String comando;

void setup() {
  bluetooth.begin(9600);
  Serial.begin(9600);
  pinMode(porta, OUTPUT);
  pinMode(partidaVeiculo, OUTPUT);
  pinMode(painel, OUTPUT);
  pinMode(posChave, OUTPUT);
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

    if (comando.indexOf("painel") >= 0) {
      digitalWrite(painel, !digitalRead(painel));
      delay(100);
    }

    if (comando.indexOf("posChave") >= 0) {
      digitalWrite(posChave, !digitalRead(posChave));
      delay(500);
    }

    if (comando.indexOf("partidaVeiculo") >= 0) {
      digitalWrite(partidaVeiculo, HIGH);
      delay(1200);
      digitalWrite(partidaVeiculo, LOW);
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

   if (digitalRead(posChave)) {
    delay(100);
    bluetooth.print("{");
    bluetooth.print("carroLigado");
    bluetooth.print("}");
   }

   if (!digitalRead(posChave)) {
    delay(100);
    bluetooth.print("{");
    bluetooth.print("carroDesligado");
    bluetooth.print("}");
   }
   
  }
}
   
