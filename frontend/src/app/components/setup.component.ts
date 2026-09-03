import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-setup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="setup-section">
      <div class="message" *ngIf="lastMessage" [ngClass]="lastMessage.type">
        {{ lastMessage.text }}
      </div>

      <div class="action-group">
        <div class="flex-row">
          <input 
            *ngIf="fase === 'SETUP'"
            type="text" 
            [(ngModel)]="nomePerAvvio" 
            placeholder="Nome giocatore iniziale (opzionale)"
            [disabled]="!canStart">
          <button 
            *ngIf="fase === 'SETUP'"
            class="btn-success" 
            (click)="avvia()"
            [disabled]="!canStart">
            🎬 Avvia Gioco
          </button>
          <button 
            class="btn-danger" 
            (click)="reset()">
            🔄 Reset Completo
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .setup-section {
      margin-bottom: 30px;
    }

    .action-group {
      background: #f8f9fa;
      padding: 20px;
      border-radius: 12px;
      border: 2px solid #e9ecef;
    }

    .flex-row {
      display: flex;
      gap: 10px;
      align-items: center;
      
      input {
        flex: 1;
      }
    }

    .message {
      padding: 15px;
      border-radius: 8px;
      margin: 15px 0;
      font-weight: 500;
      
      &.success {
        background: #d4edda;
        color: #155724;
        border: 1px solid #c3e6cb;
      }
      
      &.error {
        background: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
      }
      
      &.info {
        background: #d1ecf1;
        color: #0c5460;
        border: 1px solid #bee5eb;
      }
    }
  `]
})
export class SetupComponent {
  @Input() canStart = false;
  @Input() fase?: string;
  @Input() lastMessage?: { text: string; type: string };

  @Output() onAvvia = new EventEmitter<string>();
  @Output() onReset = new EventEmitter<void>();

  nomePerAvvio = '';

  avvia(): void {
    this.onAvvia.emit(this.nomePerAvvio);
    this.nomePerAvvio = '';
  }

  reset(): void {
    if (confirm('Vuoi resettare completamente il gioco?')) {
      this.onReset.emit();
    }
  }
}
