import { Component, inject, input, output } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { catchError, map, of, startWith } from 'rxjs';
import { ToxreadApiService } from '../../../core/api/toxread-api.service';
import { RaRequest } from '../../../core/models/toxread.models';

type EndpointsState =
  | { status: 'loading' }
  | { status: 'error'; message: string }
  | { status: 'ready'; endpoints: string[] };

@Component({
  selector: 'app-request-form',
  imports: [ReactiveFormsModule],
  templateUrl: './request-form.html',
  styleUrl: './request-form.scss',
})
export class RequestForm {
  private readonly api = inject(ToxreadApiService);
  private readonly fb = inject(FormBuilder);

  readonly busy = input(false);
  readonly submitted = output<RaRequest>();

  protected readonly form = this.fb.nonNullable.group({
    smiles: ['', [Validators.required, Validators.pattern(/^\S+$/)]],
    endpoint: ['', Validators.required],
    numSimilarMolecules: [3, [Validators.required, Validators.min(1), Validators.max(20)]],
  });

  /** Endpoint list from GET /endpoints, loaded once when the form is created. */
  protected readonly endpointsState = toSignal(
    this.api.getEndpoints().pipe(
      map((endpoints): EndpointsState => ({ status: 'ready', endpoints })),
      catchError((e: Error) => of<EndpointsState>({ status: 'error', message: e.message })),
      startWith<EndpointsState>({ status: 'loading' }),
    ),
    { requireSync: true },
  );

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitted.emit(this.form.getRawValue());
  }
}
