import { Component, inject, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import QRCode from 'qrcode';

export interface QrDialogData {
  barcode: string;
  customerName: string;
  deliveryAddress: string;
}

@Component({
  selector: 'app-qr-dialog',
  imports: [MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <h2 mat-dialog-title>Order QR Code</h2>
    <mat-dialog-content class="qr-content">
      <div class="qr-print-area" id="qrPrintArea">
        <canvas #qrCanvas></canvas>
        <div class="order-details">
          <div class="detail-row">
            <span class="detail-label">Barcode</span>
            <code class="detail-value barcode">{{ data.barcode }}</code>
          </div>
          <div class="detail-row">
            <span class="detail-label">Customer</span>
            <span class="detail-value">{{ data.customerName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Address</span>
            <span class="detail-value">{{ data.deliveryAddress }}</span>
          </div>
        </div>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Close</button>
      <button mat-raised-button color="primary" (click)="print()">
        <mat-icon>print</mat-icon> Print
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .qr-content { display: flex; justify-content: center; }

    .qr-print-area {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 16px;
    }

    canvas { margin-bottom: 20px; }

    .order-details {
      width: 100%;
      min-width: 280px;
    }

    .detail-row {
      display: flex;
      justify-content: space-between;
      align-items: baseline;
      padding: 8px 0;
      border-bottom: 1px solid #e2e8f0;
    }

    .detail-row:last-child { border-bottom: none; }

    .detail-label {
      font-size: 12px;
      font-weight: 600;
      color: #64748b;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      flex-shrink: 0;
      margin-right: 16px;
    }

    .detail-value {
      font-size: 14px;
      color: #1e293b;
      text-align: right;
    }

    .barcode {
      background: #f1f5f9;
      padding: 2px 8px;
      border-radius: 4px;
      font-family: monospace;
    }
  `]
})
export class QrDialogComponent implements AfterViewInit {
  readonly data = inject<QrDialogData>(MAT_DIALOG_DATA);

  @ViewChild('qrCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  ngAfterViewInit(): void {
    QRCode.toCanvas(this.canvasRef.nativeElement, this.data.barcode, {
      width: 200,
      margin: 2,
      color: { dark: '#1e293b', light: '#ffffff' }
    });
  }

  print(): void {
    window.print();
  }
}
