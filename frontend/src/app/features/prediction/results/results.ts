import { Component, input } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RaResponse } from '../../../core/models/toxread.models';
import { InterpolationChart } from '../../../shared/interpolation-chart/interpolation-chart';
import { MoleculeGraph } from '../../../shared/molecule-graph/molecule-graph';
import { MoleculeImage } from '../../../shared/molecule-image/molecule-image';

@Component({
  selector: 'app-results',
  imports: [DecimalPipe, MoleculeImage, InterpolationChart, MoleculeGraph],
  templateUrl: './results.html',
  styleUrl: './results.scss',
})
export class Results {
  readonly result = input.required<RaResponse>();
}
