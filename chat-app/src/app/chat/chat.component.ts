import { Component, OnInit } from '@angular/core';
import { Client } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Message } from './models/message';
import { FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  private client: Client; // stomp
  connected: boolean = false;

  message: Message = new Message();
  messages: Array<Message> = new Array();

  writing: string;
  clientId: string;

  textForm: FormControl;
  usernameForm: FormControl;

  constructor() 
  {
    this.clientId = "id-" + new Date().getTime() + '-' + Math.random().toString(36).substr(2);    // generamos identifiar unico para el usuario
  }

  ngOnInit()
  {
    this.textForm = new FormControl('', Validators.required);
    this.usernameForm = new FormControl('', Validators.required);
    this.client = new Client();
    
    this.client.webSocketFactory = () => {      // le pasamos la ruta hacia el broker que configuramos en spring. Trabajamos stomp sobre sockjs
      return new SockJS("http://localhost:8080/chat-websocket");  
    }

    this.client.onConnect = (frame) => {                   // evento cuando nos conectamos
      console.log('Connected: ' + this.client.connected + ' : ' + frame);  // connected boolean, frame contiene toda la informacion de nuestra conexion con el broke
      this.connected = true;

      this.client.subscribe('/chat/message', e => {      // subscribirnos al evento chat/message. Nombre de evento y objeto e. va a estar escuchando cada ves que recibamos un nuevo mensaje del servidor, cuando un usuario escribe un mensaje el broker lo recibe y lo emite a todos los clientes que estan subscripto a este evento
        
        let message: Message = JSON.parse(e.body) as Message;    // casteamos a Message
        message.date = new Date(message.date);                 // convertimos a tipo Date la fecha que nos devuelve el back

        if(!this.message.color && message.type == 'NEW_USER' && this.message.username == message.username) { // asignamos el color solo al cliente que se conecto. this.message es nuestro mensaje sesion
          this.message.color = message.color;
        }
        this.messages.push(message);                // vamos pasando los mensajes a medida que vamos escuchandolos
      });

      this.client.subscribe('/chat/writing', e => {

          let response = JSON.parse(e.body);

          if(response.username != this.message.username)
            this.writing = response.writing;

          setTimeout(() => this.writing = '', 5000);    // mostramos el mensaje escribiendo por 5 segundos
      });

      this.client.subscribe('/chat/history/' + this.clientId, e => {    // subcribir evento para recibir historial de mensajes
        const history = JSON.parse(e.body) as Array<Message>;
        this.messages = history.map(m => {    // la fecha viene como un long en milisegundos, debemos convertilos a tipo Date
          m.date = new Date(m.date);  
          return m;
        }).reverse();     // damos vuelta el arreglo, mensajes mas viejos a los mas nuevos
      });

      this.client.publish({     // notificamos al broken que nos envie el historial
        destination: '/app/history',
        body: this.clientId
      });

      this.message.type = 'NEW_USER';     // cuando se conecta un nuevo usuario

      this.client.publish({        // enviamos mensaje al conectar un nuevo usuario
        destination: '/app/message', 
        body: JSON.stringify(this.message)
      });
    }

    this.client.onDisconnect = (frame) => {     // evento al desconectarse
      console.log('Disconnected: ' + !this.client.connected + ' : ' + frame);
      this.connected = false;
      this.message = new Message();    // reseteamos valores
      this.messages= new Array();
    }
   
  }

  connect(): void     // inicializamos la conexion al broker
  {
    this.message.username = this.username.value;
    this.client.activate(); 
  }

  disconnect(): void     // desconectamos la conexion al broker
  {
    this.client.deactivate(); 
  }

  sendMessage(): void       // enviamos el mensaje al broker
  {
    this.message.text = this.text.value;
    this.message.type = 'MESSAGE';

    this.client.publish({
        destination: '/app/message',    // indicamos el destino del message. al metodo del controlador del broker
        body: JSON.stringify(this.message)    // enviamos el message, casteamos de Message a tipo string
      });

    this.textForm.setValue('');     // reseteamos el message
  }

  writingEvent(): void     //  evento que notifica cuando estemos escribiendo
  {
    this.client.publish({
      destination: '/app/writing',
      body: this.message.username
    });
  }

  get text(){ return this.textForm }
  get username(){ return this.usernameForm }

}
