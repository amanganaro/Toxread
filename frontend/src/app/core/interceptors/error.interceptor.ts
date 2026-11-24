import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

/** Converts HTTP errors into Errors carrying a user-readable message. */
export const errorInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      let message: string;
      if (err.status === 0) {
        message = 'Cannot reach the backend server';
      } else if (typeof err.error === 'string' && err.error) {
        message = err.error;
      } else if (err.error?.message) {
        message = err.error.message;
      } else {
        message = `Request failed (${err.status} ${err.statusText})`;
      }
      return throwError(() => new Error(message));
    }),
  );
