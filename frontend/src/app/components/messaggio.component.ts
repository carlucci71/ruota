import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-messaggio',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="message" *ngIf="lastMessage?.text?.trim()" [ngClass]="lastMessage?.type">
      {{ lastMessage?.text }}
    </div>
  `,
  styles: [`
    .message {
      position: fixed;
      left: 50%;
      bottom: 24px;
      z-index: 1000;
      width: min(560px, calc(100vw - 32px));
      padding: 14px 20px;
      margin: 0;
      border-radius: 8px;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.22);
      text-align: center;
      overflow-wrap: anywhere;
      transform: translateX(-50%);
      animation: snackbar-in 180ms ease-out;
    }

    @keyframes snackbar-in {
      from {
        opacity: 0;
        transform: translate(-50%, 12px);
      }
      to {
        opacity: 1;
        transform: translate(-50%, 0);
      }
    }

    @media (max-width: 600px) {
      .message {
        bottom: 16px;
      }
    }
  `]
})
export class MessaggioComponent {
  @Input() lastMessage?: { text: string; type: string };
}