import {
  Component,
  ElementRef,
  OnDestroy,
  effect,
  input,
  viewChild,
} from '@angular/core';
import {
  Chart,
  LineController,
  LineElement,
  LinearScale,
  PointElement,
  ScatterController,
  Tooltip,
  Legend,
  type Plugin,
} from 'chart.js';
import { Interpolation } from '../../core/models/toxread.models';

Chart.register(ScatterController, LineController, LinearScale, PointElement, LineElement, Tooltip, Legend);

/** Scatter chart of similar molecules with crosshair lines at the target (x, y). */
@Component({
  selector: 'app-interpolation-chart',
  template: `<div class="wrap"><canvas #canvas></canvas></div>`,
  styles: `.wrap { position: relative; height: 320px; }`,
})
export class InterpolationChart implements OnDestroy {
  readonly data = input.required<Interpolation>();
  //readonly rootColor = input<number[] | null | undefined>(null);
  private readonly canvas = viewChild.required<ElementRef<HTMLCanvasElement>>('canvas');
  private chart?: Chart;

  constructor() {
    effect(() => {
      const d = this.data();
      const bounds = (vals: number[]) => {
        const min = Math.min(...vals);
        const max = Math.max(...vals);
        const pad = (max - min) * 0.1 || Math.abs(max) * 0.1 || 1;
        return { min: min - pad, max: max + pad }
      };
      const xb = bounds([...d.points.map((p) => p.x), d.targetLineX]);
      const yb = bounds([...d.points.map((p) => p.y), d.targetLineY]);
      const el = this.canvas().nativeElement;
      this.chart?.destroy();

      const targetLines: Plugin<'scatter'> = {
        id: 'targetLines',
        afterDatasetsDraw: (chart) => {
          const { ctx, chartArea, scales } = chart;
          const x = scales['x'].getPixelForValue(d.targetLineX);
          const y = scales['y'].getPixelForValue(d.targetLineY);
          ctx.save();
          ctx.strokeStyle = '#d32f2f';
          ctx.setLineDash([6, 4]);
          ctx.beginPath();
          if (x >= chartArea.left && x <= chartArea.right) {
            ctx.moveTo(x, chartArea.top);
            ctx.lineTo(x, chartArea.bottom);
          }
          if (y >= chartArea.top && y <= chartArea.bottom) {
            ctx.moveTo(chartArea.left, y);
            ctx.lineTo(chartArea.right, y);
          }
          ctx.stroke();
          ctx.restore();
        },
      };

      this.chart = new Chart(el, {
        type: 'scatter',
        data: {
          datasets: [
            {
              label: 'Similar molecules',
              data: d.points.map((p) => ({ x: p.x, y: p.y, name: p.name })),
              backgroundColor: '#1976d2',
              pointRadius: 5,
            },
            {
              label: 'Root molecule',
              data: [{ x: d.targetLineX, y: d.targetLineY, name: 'Root molecule' }],
              backgroundColor: '#d32f2f',
              borderColor: '#d32f2f',
              pointRadius: 7,
            }
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: { min: xb.min, max: xb.max, title: { display: true, text: d.axisXTitle } },
            y: { min: yb.min, max: yb.max, title: { display: true, text: d.axisYTitle } },
          },
          plugins: {
            tooltip: {
              callbacks: {
                label: (ctx) => {
                  const raw = ctx.raw as { x: number; y: number; name: string };
                  return `${raw.name} (${raw.x}, ${raw.y})`;
                },
              },
            },
          },
        },
        plugins: [targetLines],
      });
    });
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }
}
