export class Message {

    text: string = '';
    date: Date;
    username: string;
    type :string;   // para identifica si el mensaje es para  notificar un nuevo usuario o un nuevo mensaje enviado
    color: string;
}
