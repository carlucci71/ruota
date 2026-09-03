import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-messaggio',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="message" *ngIf="lastMessage" [ngClass]="lastMessage.type">
      {{ lastMessage.text }}
    </div>
  `,
  styles: [`
    .message {
      margin-bottom: 20px;
    }
  `]
})
export class MessaggioComponent {
  @Input() lastMessage?: { text: string; type: string };
}