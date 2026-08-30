import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GameService } from './services/game.service';
import { GameInfo, SpinResponse, CallResponse, Tabellone, Giocatore } from './models/game.model';
import { GiocatoriComponent } from './components/giocatori.component';
import { TabelloneComponent } from './components/tabellone.component';
import { AzioniComponent } from './components/azioni.component';
import { SetupComponent } from './components/setup.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, GiocatoriComponent, TabelloneComponent, AzioniComponent, SetupComponent],
  template: `
    <div class="container">
      <h1>🎡 RUOTA DELLA FORTUNA 🎡</h1>
      
      <div class="game-container">
        <app-tabellone
          [tabellone]="getTabellone()"
          [giocatoreTurno]="getGiocatoreTurno()"
          [fase]="gameInfo?.Fase"
          [tipoManche]="gameInfo?.TipoManche"
          [posizione]="gameInfo?.POSIZIONE"
          >
        </app-tabellone>

        <app-azioni
          [fase]="gameInfo?.Fase"
          [canPlay]="canPlay()"
          [ultimoSpicchio]="ultimoSpicchio"
          [tipoManche]="gameInfo?.TipoManche"
          (onGira)="giraRuota()"
          (onConsonante)="chiamaConsonante($event)"
          (onVocale)="compraVocale($event)"
          (onSoluzione)="tentaSoluzione($event)">
        </app-azioni>

        <app-setup
          [canStart]="canStartGame()"
          [lastMessage]="lastMessage"
          (onAvvia)="avviaGame($event)"
          (onReset)="initGame()">
        </app-setup>

        <app-giocatori
          [giocatori]="gameInfo?.Giocatori || []"
          (onAdd)="addGiocatore($event)"
          (onDelete)="deleteGiocatore($event)"
          (onReset)="resetGiocatori()">
        </app-giocatori>

        <div class="debug-info" *ngIf="showDebug">
          <h3>Debug Info</h3>
          <pre>{{ gameInfo | json }}</pre>
        </div>
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

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
    this.loadGameInfo();
  }

  ngOnDestroy(): void {
    this.stopAutoSingolaChiamataLoop();
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
  }

  private handleTipoManche(): void {
    if (this.gameInfo?.TipoManche === 'AUTO_SINGOLA_CHIAMATA' && this.gameInfo?.Fase === 'GIRA') {
      this.startAutoSingolaChiamataLoop();
      return;
    }

    this.stopAutoSingolaChiamataLoop();
  }

  private startAutoSingolaChiamataLoop(): void {
    if (this.autoSingolaChiamataTimer) {
      return;
    }

    this.autoSingolaChiamataTimer = setInterval(() => {
      this.gameService.autoSingolaChiamata().subscribe({
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

  private stopAutoSingolaChiamataLoop(): void {
    if (this.autoSingolaChiamataTimer) {
      clearInterval(this.autoSingolaChiamataTimer);
      this.autoSingolaChiamataTimer = undefined;
    }
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
    return this.gameInfo !== undefined && 
           (fase === 'GIRA' || fase === 'PARLA');
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
        this.showMessage('Gioco avviato! Gira la ruota!', 'success');
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
        
        if (data.LETTERE_GIA_CHIAMATE) {
          this.showMessage(`La lettera ${consonante} è già stata chiamata!`, 'error');
        } else if (data.ESAURITE) {
          this.showMessage('Consonanti esaurite!', 'error');
        } else if (data.LETTERE_TROVATE !== undefined) {
          if (data.LETTERE_TROVATE > 0) {
            this.showMessage(`Trovate ${data.LETTERE_TROVATE} ${consonante}!`, 'success');
          } else {
            this.showMessage(`Nessuna ${consonante} trovata`, 'error');
          }
        }
        
        this.ultimoSpicchio = undefined;
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
        
        if (data.LETTERE_GIA_CHIAMATE) {
          this.showMessage(`La vocale ${vocale} è già stata chiamata!`, 'error');
        } else if (data.ESAURITE) {
          this.showMessage('Vocali esaurite!', 'error');
        } else if (data.LETTERE_TROVATE !== undefined) {
          if (data.LETTERE_TROVATE > 0) {
            this.showMessage(`Trovate ${data.LETTERE_TROVATE} ${vocale}!`, 'success');
          } else {
            this.showMessage(`Nessuna ${vocale} trovata`, 'error');
          }
        }
        
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
        
        if (data.RISULTATO) {
          this.showMessage('🎉 SOLUZIONE CORRETTA! HAI VINTO! 🎉', 'success');
        } else {
          this.showMessage('❌ Soluzione errata!', 'error');
        }
        
        this.ultimoSpicchio = undefined;
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
    }, 5000);
  }
}
