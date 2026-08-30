import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-azioni',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="azioni-section">
      <div class="action-group" *ngIf="canPlay">
        <h3>Tenta la Soluzione</h3>
        <div class="flex-column">
          <input 
            type="text" 
            [(ngModel)]="soluzione" 
            placeholder="Scrivi la soluzione completa">
          <button 
            class="btn-success btn-large" 
            (click)="tentaSoluzione()"
            [disabled]="!soluzione">
            🎯 RISOLVI
          </button>
        </div>
      </div>

      <div class="action-group" *ngIf="canPlay">
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

      <div class="action-group" *ngIf="canPlay && fase === 'PARLA'">
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

      <div class="action-group" *ngIf="canPlay && fase === 'GIRA'">
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
  @Input() fase?: string;
  @Input() canPlay = false;
  @Input() ultimoSpicchio?: string | number;

  @Output() onGira = new EventEmitter<void>();
  @Output() onConsonante = new EventEmitter<string>();
  @Output() onVocale = new EventEmitter<string>();
  @Output() onSoluzione = new EventEmitter<string>();

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
    if (confirm(`Vuoi comprare la vocale ${vocale}? (Costa punti)`)) {
      this.onVocale.emit(vocale);
    }
  }

  tentaSoluzione(): void {
    if (this.soluzione) {
      this.onSoluzione.emit(this.soluzione);
      this.soluzione = '';
    }
  }

  isConsonante(char: string): boolean {
    const consonanti = 'BCDFGHLMNPQRSTVWXYZ';
    return consonanti.includes(char.toUpperCase());
  }
}
