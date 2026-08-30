import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Tabellone, Giocatore } from '../models/game.model';

@Component({
  selector: 'app-tabellone',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="tabellone-section">
      <div class="game-board" *ngIf="tabellone && isTabelloneValid()">
        <div class="category">
          <strong> {{ tabellone.titolo }} </strong>
        </div>
        
        <div class="frase">
          <div class="frase-row" *ngFor="let row of getFraseRows()">
            <div class="word" *ngFor="let word of row">
              <div class="letter" *ngFor="let char of word.split('')">
                <span [class.revealed]="char !== '-'">
                  {{ char === '-' ? '' : char }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="status">
          <span class="status-item" [class.finished]="tabellone.consonantiFinite">
            🔤 Consonanti: {{ tabellone.consonantiFinite ? 'FINITE' : 'Disponibili' }}
          </span>
          <span class="status-item" [class.finished]="tabellone.vocaliFinite">
            🅰️ Vocali: {{ tabellone.vocaliFinite ? 'FINITE' : 'Disponibili' }}
          </span>
        </div>
      </div>

      <div class="current-player" *ngIf="giocatoreTurno">
        <div class="player-info-row">
          <div class="turn-info">
            🎯 <strong>Turno:</strong> <span class="player-name">{{ giocatoreTurno.nome }}</span>
          </div>
          <div class="points-info">
            💰 <strong>Punti:</strong> {{ giocatoreTurno.puntiManche }}
          </div>
          <div class="phase-info" *ngIf="fase">
            <strong>Fase:</strong> 
            <span class="phase-badge" [ngClass]="'phase-' + fase.toLowerCase()">
              {{ fase }}
            </span>
          </div>
          <div class="phase-info" *ngIf="tipoManche">
            <strong>Tipo Manche:</strong> 
            <span class="tipoManche-badge">
              {{ tipoManche }}
            </span>
          </div>
          <div class="specials-info" *ngIf="giocatoreTurno.withJolly || giocatoreTurno.withGarage">
            <span *ngIf="giocatoreTurno.withJolly" class="special">🃏 JOLLY</span>
            <span *ngIf="giocatoreTurno.withGarage" class="special">🚗 GARAGE</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .tabellone-section {
      margin-bottom: 30px;
    }

    .game-board {
      background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
      padding: 25px;
      border-radius: 15px;
      box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
    }

    .category {
      text-align: center;
      font-size: 1.3em;
      color: #d35400;
      margin-bottom: 20px;
      padding: 10px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 8px;
      font-weight: bold;
    }

    .frase {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
      margin: 25px 0;
      min-height: 100px;
    }

    .frase-row {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-wrap: wrap;
      gap: clamp(18px, 2.5vw, 32px);
      width: 100%;
      max-width: 100%;
    }

    .word {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      flex-shrink: 0;
    }

    .letter {
      --cell-size: clamp(22px, 2.2vw, 38px);
      width: var(--cell-size);
      height: calc(var(--cell-size) * 1.25);
      display: flex;
      align-items: center;
      justify-content: center;
      background: white;
      border: 3px solid #e67e22;
      border-radius: 8px;
      font-size: clamp(0.9rem, 1.5vw, 1.5rem);
      font-weight: bold;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      flex-shrink: 0;
      
      span {
        color: #999;
        
        &.revealed {
          color: #2c3e50;
          animation: reveal 0.3s ease;
        }
      }
    }

    @keyframes reveal {
      0% { transform: scale(0.5); opacity: 0; }
      100% { transform: scale(1); opacity: 1; }
    }

    .status {
      display: flex;
      justify-content: space-around;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 2px solid rgba(255, 255, 255, 0.5);
    }

    .status-item {
      padding: 8px 16px;
      background: rgba(255, 255, 255, 0.9);
      border-radius: 20px;
      font-weight: 600;
      color: #27ae60;
      
      &.finished {
        color: #e74c3c;
        text-decoration: line-through;
      }
    }

    .current-player {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 15px 20px;
      border-radius: 15px;
      margin: 20px 0;
    }

    .player-info-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 20px;
      flex-wrap: wrap;
    }

    .turn-info, .points-info, .phase-info {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 1.1em;
    }

    .player-name {
      color: #ffd700;
      font-size: 1.3em;
      font-weight: bold;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
    }

    .specials-info {
      display: flex;
      gap: 10px;
      
      .special {
        background: #ffd700;
        color: #333;
        padding: 5px 12px;
        border-radius: 15px;
        font-weight: bold;
        font-size: 0.9em;
      }
    }

    .phase-badge {
      display: inline-block;
      padding: 5px 15px;
      border-radius: 15px;
      font-weight: bold;
      text-transform: uppercase;
      font-size: 0.9em;
      
      &.phase-gira {
        background: #3498db;
        color: white;
      }
      
      &.phase-parla {
        background: #e74c3c;
        color: white;
      }
      
      &.phase-fine {
        background: #2ecc71;
        color: white;
      }
    }

    .tipoManche-badge {
      display: inline-block;
      padding: 5px 15px;
      border-radius: 15px;
      font-weight: bold;
      text-transform: uppercase;
      font-size: 0.9em;
      background: #3498db;
      color: white;
    }

`]
})
export class TabelloneComponent {
  @Input() tabellone?: Tabellone;
  @Input() giocatoreTurno?: Giocatore;
  @Input() fase?: string;
  @Input() tipoManche?: string;

  isTabelloneValid(): boolean {
    return typeof this.tabellone === 'object' && !!this.tabellone.frase;
  }

  getFraseRows(): string[][] {
    if (!this.tabellone || typeof this.tabellone !== 'object' || !this.tabellone.frase) {
      return [];
    }

    const words = this.tabellone.frase.split(/\s+/).filter(word => word.length > 0);
    const rows: string[][] = [];
    let currentRow: string[] = [];
    let currentCount = 0;

    for (const word of words) {
      const wordLength = word.length;

      if (currentRow.length > 0 && currentCount + wordLength > 18) {
        rows.push(currentRow);
        currentRow = [];
        currentCount = 0;
      }

      currentRow.push(word);
      currentCount += wordLength;
    }

    if (currentRow.length > 0) {
      rows.push(currentRow);
    }

    return rows;
  }
}
