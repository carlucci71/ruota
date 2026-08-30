import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GameInfo, SpinResponse, CallResponse } from '../models/game.model';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private apiUrl = 'http://localhost:8083/api/ruota';

  constructor(private http: HttpClient) {}

  // Game endpoints
  getGameInfo(): Observable<GameInfo> {
    return this.http.get<GameInfo>(`${this.apiUrl}/game`);
  }

  initGame(): Observable<GameInfo> {
    return this.http.delete<GameInfo>(`${this.apiUrl}/game`);
  }

  avviaGame(nome: string | null): Observable<GameInfo> {
    return this.http.post<GameInfo>(`${this.apiUrl}/game`, { nome });
  }

  giraRuota(forzato?: string): Observable<SpinResponse> {
    let params = new HttpParams();
    if (forzato) {
      params = params.set('forzato', forzato);
    }
    return this.http.get<SpinResponse>(`${this.apiUrl}/game/gira`, { params });
  }

  chiamaConsonante(consonante: string, trovato: string | number): Observable<CallResponse> {
    const params = new HttpParams()
      .set('consonante', consonante)
      .set('trovato', trovato.toString());
    return this.http.get<CallResponse>(`${this.apiUrl}/game/consonante`, { params });
  }

  compraVocale(vocale: string): Observable<CallResponse> {
    const params = new HttpParams().set('vocale', vocale);
    return this.http.get<CallResponse>(`${this.apiUrl}/game/vocale`, { params });
  }

  tentaSoluzione(soluzione: string): Observable<CallResponse> {
    const params = new HttpParams().set('soluzione', soluzione);
    return this.http.get<CallResponse>(`${this.apiUrl}/game/soluzione`, { params });
  }

  // Giocatore endpoints
  addGiocatore(nome: string): Observable<GameInfo> {
    return this.http.post<GameInfo>(`${this.apiUrl}/giocatore`, { nome });
  }

  updateGiocatore(nomeVecchio: string, nomeNuovo: string): Observable<GameInfo> {
    return this.http.put<GameInfo>(`${this.apiUrl}/giocatore/${nomeVecchio}`, { nome: nomeNuovo });
  }

  deleteGiocatore(nome: string): Observable<GameInfo> {
    return this.http.delete<GameInfo>(`${this.apiUrl}/giocatore/${nome}`);
  }

  resetGiocatori(): Observable<GameInfo> {
    return this.http.delete<GameInfo>(`${this.apiUrl}/giocatore`);
  }
}
