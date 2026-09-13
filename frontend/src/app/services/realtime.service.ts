import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { RealtimeMessage } from '../models/game.model';

/**
 * Servizio che mantiene una connessione WebSocket verso il backend
 * (ws://<host>/api/ruota/game/ws) ed espone un flusso RxJS di messaggi.
 *
 * Gestisce automaticamente la riconnessione con backoff esponenziale se la
 * connessione cade (es. riavvio del backend) e pubblica lo stato della
 * connessione per poterlo mostrare in UI.
 *
 * Gli eventi WebSocket sono agganciati fuori dalla zone di Angular e i
 * messaggi vengono riemessi dentro la zone (zone.run) per garantire il
 * change detection dell'interfaccia.
 */
@Injectable({
  providedIn: 'root'
})
export class RealtimeService {
  private socket?: WebSocket;
  private messages = new Subject<RealtimeMessage>();
  private statoConnessione = new BehaviorSubject<boolean>(false);
  private shouldReconnect = true;
  private tentativoRiconnessione = 0;
  private reconnectTimer?: ReturnType<typeof setTimeout>;

  constructor(private zone: NgZone) {}

  /** Avvia la connessione (riconnessione automatica attiva). */
  connect(): void {
    this.shouldReconnect = true;
    this.open();
  }

  /** Chiude la connessione (senza tentativi di riconnessione). */
  disconnect(): void {
    this.shouldReconnect = false;
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = undefined;
    }
    if (this.socket) {
      const socket = this.socket;
      this.socket = undefined;
      socket.close();
    }
    this.setConnesso(false);
  }

  /** Stream dei messaggi ricevuti dal server. */
  getMessages(): Observable<RealtimeMessage> {
    return this.messages.asObservable();
  }

  /** Stato della connessione WebSocket (true = connesso). */
  getStatoConnessione(): Observable<boolean> {
    return this.statoConnessione.asObservable();
  }

  isConnesso(): boolean {
    return this.statoConnessione.value;
  }

  /** Chiede al server lo stato corrente (usato per il resync, es. al focus del tab). */
  richiediStato(): void {
    if (this.socket && this.statoConnessione.value && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify({ action: 'getState' }));
    }
  }

  private open(): void {
    if (!this.shouldReconnect || this.socket) {
      return;
    }
    // Il WebSocket vive fuori dalla zone di Angular: nessun timer/evento
    // inutile deve innescare il change detection.
    this.zone.runOutsideAngular(() => this.creaSocket());
  }

  private creaSocket(): void {
    try {
      const socket = new WebSocket(this.buildUrl());
      this.socket = socket;

      socket.addEventListener('open', () => {
        this.tentativoRiconnessione = 0;
        // Chiede lo stato corrente appena connesso (oltre a quello pushato dal server)
        socket.send(JSON.stringify({ action: 'getState' }));
        this.setConnesso(true);
      });

      socket.addEventListener('message', (event: MessageEvent) => {
        let messaggio: RealtimeMessage;
        try {
          messaggio = JSON.parse(event.data) as RealtimeMessage;
        } catch {
          return; // messaggi non JSON: ignorati
        }
        console.log('[WS] Messaggio broadcast ricevuto nel frontend:', messaggio);
        // Riemette dentro la zone per garantire il change detection
        this.zone.run(() => this.messages.next(messaggio));
      });

      socket.addEventListener('close', () => {
        this.setConnesso(false);
        if (this.socket === socket) {
          this.socket = undefined;
        }
        this.programmaRiconnessione();
      });

      socket.addEventListener('error', () => {
        this.setConnesso(false);
        // La chiusura (close) segue sempre un errore e gestisce la riconnessione
      });
    } catch {
      this.socket = undefined;
      this.programmaRiconnessione();
    }
  }

  private setConnesso(valore: boolean): void {
    this.zone.run(() => this.statoConnessione.next(valore));
  }

  private programmaRiconnessione(): void {
    if (!this.shouldReconnect || this.reconnectTimer) {
      return;
    }
    const ritardo = Math.min(1000 * Math.pow(2, this.tentativoRiconnessione), 30000);
    this.tentativoRiconnessione++;
    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = undefined;
      this.open();
    }, ritardo);
  }

  private buildUrl(): string {
    const protocollo = window.location.protocol === 'https:' ? 'wss' : 'ws';
    return `${protocollo}://${window.location.host}/api/ruota/game/ws`;
  }
}