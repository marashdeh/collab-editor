import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { AuthService } from './auth.service';
import { WebSocketMessage } from '../models/ws.models';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket: WebSocket | null = null;
  // Observable for components to listen to messages
  public messages$ = new Subject<WebSocketMessage>();

  constructor(private authService: AuthService) {}

  connect(fileId: number): void {
    const token = this.authService.getToken();
    if (!token) {
      console.error("No token found, cannot connect to WebSocket");
      return;
    }

    // NOTE: Browsers cannot set Headers on WebSocket Handshake.
    // We send the token in the Query Param as a fallback.
    const url = `ws://localhost:8080/ws?token=${token}`;

    this.socket = new WebSocket(url);

    this.socket.onopen = () => {
      console.log('WebSocket Connected');
      // Send JOIN message immediately
      this.sendMessage({ type: 'JOIN', fileId: fileId });
    };

    this.socket.onmessage = (event) => {
      try {
        const message: WebSocketMessage = JSON.parse(event.data);
        this.messages$.next(message);
      } catch (e) {
        console.error('Error parsing WS message', event.data);
      }
    };

    this.socket.onclose = () => console.warn('WebSocket Disconnected');
    this.socket.onerror = (err) => console.error('WebSocket Error', err);
  }

  sendMessage(msg: any): void {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(msg));
    }
  }

  disconnect(): void {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }
}
