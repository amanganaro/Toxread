import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/prediction/prediction-page/prediction-page').then((m) => m.PredictionPage),
  },
  { path: '**', redirectTo: '' },
];
