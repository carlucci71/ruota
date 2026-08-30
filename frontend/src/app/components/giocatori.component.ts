import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Giocatore } from '../models/game.model';

@Component({
  selector: 'app-giocatori',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="giocatori-section">
      <h2>👥 Gestione Giocatori</h2>
      
      <div class="add-player">
        <input 
          type="text" 
          [(ngModel)]="nuovoGiocatore" 
          placeholder="Nome giocatore"
          (keyup.enter)="aggiungi()">
        <button class="btn-success" (click)="aggiungi()">
          Aggiungi Giocatore
        </button>
      </div>

      <div class="players-list" *ngIf="giocatori && giocatori.length > 0">
        <h3>Giocatori iscritti:</h3>
        <div class="player-card" *ngFor="let giocatore of giocatori">
          <div class="player-info">
            <span class="player-name">{{ giocatore.nome }}</span>
            <div class="player-stats">
              <span class="stat">💰 Totale: {{ giocatore.puntiTotale }}</span>
              <span class="stat">🎯 Manche: {{ giocatore.puntiManche }}</span>
              <span class="badge" *ngIf="giocatore.withJolly">🃏 JOLLY</span>
              <span class="badge" *ngIf="giocatore.withGarage">🚗 GARAGE</span>
            </div>
          </div>
          <button class="btn-danger btn-small" (click)="elimina(giocatore.nome)">
            ❌
          </button>
        </div>
      </div>

      <div class="actions" *ngIf="giocatori && giocatori.length > 0">
        <button class="btn-danger" (click)="resetTutti()">
          Reset Tutti i Giocatori
        </button>
      </div>
    </div>
  `,
  styles: [`
    .giocatori-section {
      margin-bottom: 30px;
    }

    .add-player {
      display: flex;
      gap: 10px;
      margin-bottom: 20px;
      
      input {
        flex: 1;
      }
    }

    .players-list {
      margin: 20px 0;
    }

    h3 {
      font-size: 1.2em;
      color: #555;
      margin-bottom: 15px;
    }

    .player-card {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px;
      margin-bottom: 10px;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      border-radius: 10px;
      transition: transform 0.2s ease;
      
      &:hover {
        transform: translateX(5px);
      }
    }

    .player-info {
      flex: 1;
    }

    .player-name {
      font-size: 1.3em;
      font-weight: bold;
      color: #333;
      display: block;
      margin-bottom: 8px;
    }

    .player-stats {
      display: flex;
      gap: 15px;
      flex-wrap: wrap;
    }

    .stat {
      font-size: 0.95em;
      color: #666;
      font-weight: 600;
    }

    .badge {
      padding: 4px 8px;
      background: #ffd700;
      color: #333;
      border-radius: 5px;
      font-size: 0.85em;
      font-weight: bold;
    }

    .btn-small {
      padding: 8px 16px;
      font-size: 0.9em;
    }

    .actions {
      margin-top: 20px;
      text-align: center;
    }
  `]
})
export class GiocatoriComponent {
  @Input() giocatori: Giocatore[] = [];
  @Output() onAdd = new EventEmitter<string>();
  @Output() onDelete = new EventEmitter<string>();
  @Output() onReset = new EventEmitter<void>();

  nuovoGiocatore = '';

  aggiungi(): void {
    if (this.nuovoGiocatore.trim()) {
      this.onAdd.emit(this.nuovoGiocatore.trim());
      this.nuovoGiocatore = '';
    }
  }

  elimina(nome: string): void {
    if (confirm(`Vuoi eliminare ${nome}?`)) {
      this.onDelete.emit(nome);
    }
  }

  resetTutti(): void {
    if (confirm('Vuoi resettare tutti i giocatori?')) {
      this.onReset.emit();
    }
  }
}
