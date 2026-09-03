import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Giocatore } from '../models/game.model';

@Component({
  selector: 'app-azioni',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="azioni-section">
      <div class="action-group" *ngIf="canPlay">
        <div class="flex-column">
          <h2>PRENOTATI PER DARE LA SOLUZIONE</h2>
          <div class="players-list" *ngIf="giocatori && giocatori.length > 0 && 
          fase === 'GIRA' && tipoManche === 'AUTO_SINGOLA_CHIAMATA' && timerAttivo">
            <span class="player-card" *ngFor="let giocatore of giocatori">
              <button class="btn-danger btn-small" (click)="provaSoluzioneAutoChiamata(giocatore.nome)">
                {{ giocatore.nome }}
              </button>
            </span>
          </div>

          <button 
            class="btn-primary btn-large" 
            *ngIf="fase === 'GIRA' && tipoManche === 'AUTO_SINGOLA_CHIAMATA' && !timerAttivo"
            (click)="startTimer()">
            ▶️ RIPRENDI TIMER
          </button>
          <span
            *ngIf="fase === 'GIRA' && tipoManche != 'AUTO_SINGOLA_CHIAMATA'
            || (fase === 'GIRA' && tipoManche === 'AUTO_SINGOLA_CHIAMATA' && !timerAttivo)"
          >



<div class="action-group">
        <div class="flex-row">
          <input 
            type="text" 
            [(ngModel)]="soluzione" 
            placeholder="Scrivi la soluzione completa">
          <button 
            class="btn-success btn-success" 
            (click)="tentaSoluzione()"
            [disabled]="!soluzione">
            🎯 RISOLVI
          </button>
        </div>
      </div>
          </span>
        </div>
      </div>

      <div class="action-group" *ngIf="canPlay && tipoManche !== 'AUTO_SINGOLA_CHIAMATA'">
        <h3>Gira la Ruota</h3>
        <div class="flex-row">
          <button 
            class="btn-primary btn-large" 
            (click)="gira()"
            [disabled]="fase !== 'GIRA'">
            🎰 GIRA LA RUOTA
          </button>
        </div>
        <div class="spin-result" *ngIf="ultimoSpicchio">
          <strong>Risultato:</strong> 
          <span class="spicchio-value">{{ ultimoSpicchio }}</span>
        </div>
      </div>

      <div class="action-group" *ngIf="canPlay && fase === 'PARLA' && tipoManche !== 'AUTO_SINGOLA_CHIAMATA'">
        <h3>Chiama Consonante</h3>
        <div class="flex-row">
          <input 
            type="text" 
            [(ngModel)]="consonante" 
            placeholder="Consonante"
            maxlength="1"
            (input)="consonante = consonante.toUpperCase()">
          <button 
            class="btn-primary" 
            (click)="chiamaConsonante()"
            [disabled]="!consonante || !isConsonante(consonante)">
            📢 Chiama Consonante
          </button>
        </div>
      </div>

      <div class="action-group" *ngIf="canPlay && fase === 'GIRA' && tipoManche !== 'AUTO_SINGOLA_CHIAMATA'">
        <h3>Compra Vocale</h3>
        <div class="vowels-grid">
          <button 
            *ngFor="let v of vocali"
            class="btn-warning vowel-btn" 
            (click)="compraVocale(v)">
            {{ v }}
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .azioni-section {
      margin-bottom: 30px;
    }

    .action-group {
      background: #f8f9fa;
      padding: 20px;
      border-radius: 12px;
      margin-bottom: 20px;
      border: 2px solid #e9ecef;

      h3 {
        color: #495057;
        font-size: 1.1em;
        margin-bottom: 15px;
        text-align: left;
      }
    }

    .flex-row {
      display: flex;
      gap: 10px;
      align-items: center;
      
      input {
        flex: 1;
      }
    }

    .flex-column {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .players-list {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
    }

    .btn-large {
      font-size: 1.2em;
      padding: 15px 30px;
      width: 100%;
    }

    .vowels-grid {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 10px;
    }

    .vowel-btn {
      font-size: 1.5em;
      padding: 15px;
      aspect-ratio: 1;
    }

    .spin-result {
      margin-top: 15px;
      padding: 15px;
      background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
      border-radius: 10px;
      text-align: center;
      font-size: 1.3em;
      animation: pulse 0.5s ease;
      
      .spicchio-value {
        color: #d63031;
        font-weight: bold;
        font-size: 1.4em;
        margin-left: 10px;
      }
    }

    @keyframes pulse {
      0% { transform: scale(0.95); }
      50% { transform: scale(1.05); }
      100% { transform: scale(1); }
    }

    @media (max-width: 768px) {
      .vowels-grid {
        grid-template-columns: repeat(3, 1fr);
      }
    }
  `]
})
export class AzioniComponent {
  @Input() giocatori: Giocatore[] = [];
  @Input() fase?: string;
  @Input() canPlay = false;
  @Input() ultimoSpicchio?: string | number;
  @Input() tipoManche?: string;
  @Input() timerAttivo = false;

  @Output() onGira = new EventEmitter<void>();
  @Output() onConsonante = new EventEmitter<string>();
  @Output() onVocale = new EventEmitter<string>();
  @Output() onSoluzione = new EventEmitter<string>();
  @Output() onStopTimer = new EventEmitter<void>();
  @Output() onStartTimer = new EventEmitter<void>();
  @Output() onPrenota = new EventEmitter<string>();

  consonante = '';
  soluzione = '';
  vocali = ['A', 'E', 'I', 'O', 'U'];

  gira(): void {
    this.onGira.emit();
  }

  chiamaConsonante(): void {
    if (this.consonante && this.isConsonante(this.consonante)) {
      this.onConsonante.emit(this.consonante);
      this.consonante = '';
    }
  }

  compraVocale(vocale: string): void {
    if (confirm(`Vuoi comprare la vocale ${vocale}? (Costa 500 punti)`)) {
      this.onVocale.emit(vocale);
    }
  }

  tentaSoluzione(): void {
    if (this.soluzione) {
      this.onSoluzione.emit(this.soluzione);
      this.soluzione = '';
    }
  }

  stopTimer(): void {
    this.onStopTimer.emit();
  }

  startTimer(): void {
    this.onStartTimer.emit();
  }

  provaSoluzioneAutoChiamata(nome: string): void {
      this.stopTimer();
      this.onPrenota.emit(nome);
  }

  isConsonante(char: string): boolean {
    const consonanti = 'BCDFGHLMNPQRSTVWXYZ';
    return consonanti.includes(char.toUpperCase());
  }
}
