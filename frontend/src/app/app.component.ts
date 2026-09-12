import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GameService } from './services/game.service';
import { GameInfo, SpinResponse, CallResponse, Tabellone, Giocatore } from './models/game.model';
import { GiocatoriComponent } from './components/giocatori.component';
import { TabelloneComponent } from './components/tabellone.component';
import { AzioniComponent } from './components/azioni.component';
import { SetupComponent } from './components/setup.component';
import { MessaggioComponent } from './components/messaggio.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, GiocatoriComponent, TabelloneComponent, AzioniComponent, SetupComponent, MessaggioComponent],
  template: `
    <div class="container">
      <h1>🎡 RUOTA DELLA FORTUNA 🎡</h1>
      
      <app-messaggio [lastMessage]="lastMessage"></app-messaggio>

      <div class="game-container">
        <app-tabellone
          [tabellone]="getTabellone()"
          [giocatoreTurno]="getGiocatoreTurno()"
          [fase]="gameInfo?.Fase"
          [isAutoSingolaChiamata]="isAutoSingolaChiamata()"
          [valoreCresce]="gameInfo?.ValoreCresce"
          [tipoManche]="gameInfo?.TipoManche"
          [categoriaManche]="gameInfo?.CategoriaManche"
          [giocatorePrenotato]="gameInfo?.GiocatorePrenotato"
          [posizione]="gameInfo?.POSIZIONE"
          >
        </app-tabellone>

        <app-azioni
          [giocatori]="gameInfo?.Giocatori || []"
          [fase]="gameInfo?.Fase"
          [canPlay]="canPlay()"
          [ultimoSpicchio]="ultimoSpicchio"
          [tipoManche]="gameInfo?.TipoManche"
          [timerAttivo]="isTimerAttivo()"
          [isAutoSingolaChiamata]="isAutoSingolaChiamata()"
          [tentaCountdown]="tentaCountdown"
          [tentaTimerAttivo]="tentaTimerAttivo"
          (onGira)="giraRuota()"
          (onConsonante)="chiamaConsonante($event)"
          (onVocale)="compraVocale($event)"
          (onSoluzione)="tentaSoluzione($event)"
          (onStopTimer)="stopAutoSingolaChiamataLoopManuale()"
          (onPrenota)="prenota($event)"
          (onStartTimer)="startAutoSingolaChiamataLoop()"
          (onDaiSoluzione)="stopTentaTimerManuale()">
        </app-azioni>

        <app-giocatori
          [giocatori]="gameInfo?.Giocatori || []"
          [fase]="gameInfo?.Fase"
          (onAdd)="addGiocatore($event)"
          (onDelete)="deleteGiocatore($event)"
          (onReset)="resetGiocatori()">
        </app-giocatori>

        <div class="debug-info" *ngIf="showDebug">
          <h3>Debug Info</h3>
          <pre>{{ gameInfo | json }}</pre>
        </div>

        <app-setup
          [fase]="gameInfo?.Fase"
          [canStart]="canStartGame()"
          (onAvvia)="avviaGame($event)"
          (onReset)="initGame()">
        </app-setup>

      </div>
    </div>
  `,
  styles: [`
    .game-container {
      max-width: 100%;
    }

    .debug-info {
      margin-top: 30px;
      padding: 20px;
      background: #f8f9fa;
      border-radius: 10px;
      border: 2px dashed #dee2e6;
      
      h3 {
        margin-bottom: 10px;
      }
      
      pre {
        max-height: 400px;
        overflow: auto;
        font-size: 0.85em;
        background: white;
        padding: 15px;
        border-radius: 5px;
      }
    }
  `]
})
export class AppComponent implements OnInit, OnDestroy {
  gameInfo?: GameInfo;
  ultimoSpicchio?: string | number;
  lastMessage?: { text: string; type: string };
  showDebug = false;
  private autoSingolaChiamataTimer?: ReturnType<typeof setInterval>;
  private timerStoppatoManualmente = false;
  private tentaTimer?: ReturnType<typeof setInterval>;
  tentaCountdown?: number;
  tentaTimerAttivo = false;
  private tentaTimerStoppatoManualmente = false;

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
    this.loadGameInfo();
  }

  ngOnDestroy(): void {
    this.stopAutoSingolaChiamataLoop();
    this.stopTentaCountdown();
  }

  loadGameInfo(): void {
    this.gameService.getGameInfo().subscribe({
      next: (data) => {
        this.setGameInfo(data);
      },
      error: (err) => {
        console.error('Errore caricamento info gioco:', err);
        this.showMessage('Errore di connessione al server', 'error');
      }
    });
  }

  private setGameInfo(data: GameInfo): void {
    this.gameInfo = data;
    this.handleTipoManche();
    this.handleTentaFase();
  }

  isAutoSingolaChiamata(): boolean {
    return this.gameInfo?.TipoManche === 'AUTO_SINGOLA_CHIAMATA' || this.gameInfo?.TipoManche === 'AUTO_SINGOLA_CHIAMATA_NASCONDI';
  }

  private handleTipoManche(): void {
    if (this.isAutoSingolaChiamata()  && this.gameInfo?.Fase === 'GIRA') {
      // Riavvio automatico solo se non è stato stoppato manualmente dall'utente
      if (!this.timerStoppatoManualmente) {
        this.startAutoSingolaChiamataLoop();
      }
      return;
    }

    this.stopAutoSingolaChiamataLoop();
    // Usciti dalla manche, anche un eventuale stop manuale viene dimenticato
    this.timerStoppatoManualmente = false;
  }

  isTimerAttivo(): boolean {
    return !!this.autoSingolaChiamataTimer;
  }

  startAutoSingolaChiamataLoop(): void {
    this.timerStoppatoManualmente = false;

    if (this.autoSingolaChiamataTimer) {
      return;
    }

    this.autoSingolaChiamataTimer = setInterval(() => {
      const nascondi = this.gameInfo?.TipoManche === 'AUTO_SINGOLA_CHIAMATA_NASCONDI';
      this.gameService.autoSingolaChiamata(nascondi).subscribe({
        next: (data) => {
          this.setGameInfo(data);
        },
        error: (err) => {
          console.error('Errore auto singola chiamata:', err);
          this.showMessage('Errore chiamata automatica', 'error');
        }
      });
    }, 3000);
  }

  stopAutoSingolaChiamataLoop(): void {
    if (this.autoSingolaChiamataTimer) {
      clearInterval(this.autoSingolaChiamataTimer);
      this.autoSingolaChiamataTimer = undefined;
    }
  }

  stopAutoSingolaChiamataLoopManuale(): void {
    this.timerStoppatoManualmente = true;
    this.stopAutoSingolaChiamataLoop();
  }

  /**
   * Fase TENTA: parte un timer di 3 secondi in cui si può solo dare la soluzione.
   * Allo scadere del timer il turno passa automaticamente via gameService.passa().
   */
  private handleTentaFase(): void {
    if (this.gameInfo?.Fase === 'TENTA') {
      // Timer già attivo o fermato dall'utente: non ripartire ad ogni aggiornamento dello stato
      if (this.tentaTimer || this.tentaTimerStoppatoManualmente) {
        return;
      }
      this.tentaTimerAttivo = true;
      this.tentaCountdown = 3;
      this.tentaTimer = setInterval(() => {
        this.tentaCountdown = (this.tentaCountdown ?? 3) - 1;
        if (this.tentaCountdown <= 0) {
          this.tentaTimerAttivo = false;
          this.tentaCountdown = 0;
          if (this.tentaTimer) {
            clearInterval(this.tentaTimer);
            this.tentaTimer = undefined;
          }
          this.passaTurno();
        }
      }, 1000);
    } else {
      this.stopTentaCountdown();
    }
  }

  private stopTentaCountdown(): void {
    if (this.tentaTimer) {
      clearInterval(this.tentaTimer);
      this.tentaTimer = undefined;
    }
    this.tentaTimerAttivo = false;
    this.tentaCountdown = undefined;
    this.tentaTimerStoppatoManualmente = false;
  }

  /**
   * Il giocatore preme "DO LA SOLUZIONE": ferma il countdown e l'auto-passa,
   * lasciando il tempo di scrivere e inviare la soluzione.
   */
  stopTentaTimerManuale(): void {
    this.tentaTimerStoppatoManualmente = true;
    this.tentaTimerAttivo = false;
    if (this.tentaTimer) {
      clearInterval(this.tentaTimer);
      this.tentaTimer = undefined;
    }
    this.tentaCountdown = this.tentaCountdown ?? 0;
    this.showMessage('⏸️ Tempo fermato! Ora dai la soluzione', 'info');
  }

  passaTurno(): void {
    // Se l'utente ha già dato la soluzione (fase cambiata) non bisogna passare
    if (this.gameInfo?.Fase !== 'TENTA') {
      return;
    }
    this.gameService.passa().subscribe({
      next: (data) => {
        this.setGameInfo(data);
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore nel passaggio del turno', 'error');
      }
    });
  }

  canStartGame(): boolean {
    const giocatori = this.gameInfo?.Giocatori || [];
    const tabelloneTitolo = this.gameInfo?.['Tabellone titolo'];
    const tabellone = this.gameInfo?.TABELLONE;
    
    // Può avviare se ha giocatori e il tabellone non è attivo (è "--" o non esiste)
    const result = this.gameInfo !== undefined && 
           giocatori.length > 0 && 
           (tabelloneTitolo === '--' || tabelloneTitolo === undefined) &&
           (tabellone === '--' || tabellone === undefined || typeof tabellone === 'string');
    
    return result;
  }

  canPlay(): boolean {
    const fase = this.gameInfo?.Fase;
    if (this.gameInfo?.TipoManche === 'DOPO_CAMPANELLA'){
      return true;
    } else {
      return this.gameInfo !== undefined && 
           (fase === 'GIRA' || fase === 'PARLA' || fase === 'TENTA');
    }
  }

  getTabellone(): Tabellone | undefined {
    const tabellone = this.gameInfo?.TABELLONE;
    if (typeof tabellone === 'object' && tabellone?.frase) {
      return tabellone;
    }
    
    // Costruisce il tabellone dai campi separati
    const titolo = this.gameInfo?.['Tabellone titolo'];
    const frase = this.gameInfo?.['TabelloneInProgress'];
    
    if (titolo && frase && titolo !== '--' && frase !== '--') {
      return {
        titolo: titolo,
        frase: frase,
        consonantiFinite: this.gameInfo?.ConsonantiFinite === 'true' || this.gameInfo?.ConsonantiFinite === true,
        vocaliFinite: this.gameInfo?.VocaliFinite === 'true' || this.gameInfo?.VocaliFinite === true
      };
    }
    
    return undefined;
  }

  getGiocatoreTurno(): Giocatore | undefined {
    const giocatore = this.gameInfo?.GiocatoreTurno;
    if (typeof giocatore === 'object' && giocatore?.nome) {
      return giocatore;
    }
    
    // Se GiocatoreTurno è una stringa con il nome, cerca il giocatore nella lista
    if (typeof giocatore === 'string' && giocatore !== '--') {
      const giocatori = this.gameInfo?.Giocatori || [];
      return giocatori.find(g => g.nome.toUpperCase() === giocatore.toUpperCase());
    }
    
    return undefined;
  }

  addGiocatore(nome: string): void {
    this.gameService.addGiocatore(nome).subscribe({
      next: (data) => {
        this.setGameInfo(data);
        this.showMessage(`Giocatore ${nome} aggiunto!`, 'success');
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore aggiunta giocatore', 'error');
      }
    });
  }

  deleteGiocatore(nome: string): void {
    this.gameService.deleteGiocatore(nome).subscribe({
      next: (data) => {
        this.setGameInfo(data);
        this.showMessage(`Giocatore ${nome} eliminato`, 'info');
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore eliminazione giocatore', 'error');
      }
    });
  }

  prenota(nome: string): void {
    this.gameService.prenota(nome).subscribe({
      next: (data) => {
        this.setGameInfo(data);
        this.showMessage(`Giocatore ${nome} prenota la soluzione`, 'info');
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore prenotazione giocatore', 'error');
      }
    });
  }


  resetGiocatori(): void {
    this.gameService.resetGiocatori().subscribe({
      next: (data) => {
        this.setGameInfo(data);
        this.showMessage('Tutti i giocatori sono stati resettati', 'info');
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore reset giocatori', 'error');
      }
    });
  }

  initGame(): void {
    this.gameService.initGame().subscribe({
      next: (data) => {
        this.setGameInfo(data);
        this.ultimoSpicchio = undefined;
        this.showMessage('Gioco resettato completamente', 'info');
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore reset gioco', 'error');
      }
    });
  }

  avviaGame(nome: string): void {
    const nomeGiocatore = nome.trim() || null;
    this.gameService.avviaGame(nomeGiocatore!).subscribe({
      next: (data) => {
        this.setGameInfo(data);
        this.showMessage('Gioco avviato!', 'success');
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore avvio gioco', 'error');
      }
    });
  }

  giraRuota(): void {
    this.gameService.giraRuota().subscribe({
      next: (data: SpinResponse) => {
        this.setGameInfo(data);
        this.ultimoSpicchio = data.SPICCHIO;
        this.showMessage(`Hai ottenuto: ${data.SPICCHIO}`, 'success');
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore giro ruota', 'error');
      }
    });
  }

  chiamaConsonante(consonante: string): void {
    if (!this.ultimoSpicchio) {
      this.showMessage('Devi prima girare la ruota!', 'error');
      return;
    }

    this.gameService.chiamaConsonante(consonante, this.ultimoSpicchio).subscribe({
      next: (data: CallResponse) => {
        this.setGameInfo(data);
        let ret = 'Trovate ' + data.TROVATE + ' ' + consonante + '.';
        if (data.PUNTI){
          ret = ret + ' Punti '+data.PUNTI;
        }
        this.showMessage(ret, 'success');
          if (data.SPICCHIO){
            this.ultimoSpicchio = data.SPICCHIO;
          } else{
            this.ultimoSpicchio = undefined;
          }
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore chiamata consonante', 'error');
      }
    });
  }

  compraVocale(vocale: string): void {
    this.gameService.compraVocale(vocale).subscribe({
      next: (data: CallResponse) => {
        this.setGameInfo(data);
        
        this.showMessage(`Trovate ${data.TROVATE} ${vocale}.`, 'success');
        this.ultimoSpicchio = undefined;
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore acquisto vocale', 'error');
      }
    });
  }

  tentaSoluzione(soluzione: string): void {
    this.gameService.tentaSoluzione(soluzione).subscribe({
      next: (data: CallResponse) => {
        this.setGameInfo(data);
        
        if (data.FINE && data.FINE === 'OK') {
          this.showMessage('🎉 FINE', 'success');
        } else {
        if (data.ESITO && data.ESITO === 'OK') {
          this.showMessage('SOLUZIONE CORRETTA', 'success');
          if (data.SPICCHIO){
            this.ultimoSpicchio = data.SPICCHIO;
          }
        } else {
          this.showMessage('Soluzione errata', 'error');
        }
      }
        
      if (!data.SPICCHIO){
        this.ultimoSpicchio = undefined;
      }
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Errore tentativo soluzione', 'error');
      }
    });
  }

  showMessage(text: string, type: string): void {
    this.lastMessage = { text, type };
    setTimeout(() => {
      this.lastMessage = undefined;
    }, 50000);
  }
}
