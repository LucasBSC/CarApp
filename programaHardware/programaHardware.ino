#include <SoftwareSerial.h>

SoftwareSerial bluetooth(10,11);
#define abrirPorta 3
#define fecharPorta 4
#define partidaVeiculo 5
#define painel 6
#define posChave 7

String comando;

void setup() {
  bluetooth.begin(9600);
  pinMode(abrirPorta, OUTPUT);
  pinMode(fecharPorta, OUTPUT);
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
     if (comando.indexOf("pulsoAbrir") >= 0) {
      digitalWrite(abrirPorta, HIGH);
      delay(1000);
      // MENSAGEM PARA APLICATIVO
      bluetooth.print("{");
      bluetooth.print("portaAberta");
      bluetooth.print("}");
      delay(50);
      digitalWrite(abrirPorta, LOW);                  
    }

    if (comando.indexOf("pulsoFechar") >= 0) {
      digitalWrite(fecharPorta, HIGH);
      delay(1000);
      // MENSAGEM PARA APLICATIVO
      bluetooth.print("{");
      bluetooth.print("portaFechada");
      bluetooth.print("}");
      delay(50);
      digitalWrite(fecharPorta, LOW);                  
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
   
