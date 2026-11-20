import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EndpointsResponse, RaRequest, RaResponse } from '../models/toxread.models';

@Injectable({ providedIn: 'root' })
export class ToxreadApiService {
  private readonly http = inject(HttpClient);
  private readonly baseurl = environment.backend_url;

  getEndpoints(): Observable<string[]> {
    return this.http
      .get<EndpointsResponse>(`${this.baseurl}/endpoints`)
      .pipe(map((r) => r.endpoints));
  }

  compute(request: RaRequest): Observable<RaResponse> {
    return this.http.post<RaResponse>(`${this.baseurl}/compute`, request);
  }
}
