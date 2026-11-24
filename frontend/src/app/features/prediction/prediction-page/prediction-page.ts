import { Component, inject, signal } from '@angular/core';
import { ToxreadApiService } from '../../../core/api/toxread-api.service';
import { RaRequest, RaResponse } from '../../../core/models/toxread.models';
import { RequestForm } from '../request-form/request-form';
import { Results } from '../results/results';

@Component({
  selector: 'app-prediction-page',
  imports: [RequestForm, Results],
  template: `
    <h1>ToxRead</h1>
    <app-request-form [busy]="loading()" (submitted)="run($event)" />

    @if (error(); as e) {
      <p class="error" role="alert">{{ e }}</p>
    }
    @if (result(); as r) {
      <app-results [result]="r" />
    }
  `,
  styles: `
    :host { display: grid; gap: 1.5rem; max-width: 1100px; margin: 0 auto; padding: 1.5rem; }
    .error { color: #d32f2f; }
  `,
})
export class PredictionPage {
  private readonly api = inject(ToxreadApiService);

  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly result = signal<RaResponse | null>(null);

  protected run(request: RaRequest): void {
    this.loading.set(true);
    this.error.set(null);
    this.result.set(null);
    this.api.compute(request).subscribe({
      next: (r) => {
        this.result.set(r);
        this.loading.set(false);
      },
      error: (e: Error) => {
        this.error.set(e.message);
        this.loading.set(false);
      },
    });
  }
}
