import { Component, computed, input } from '@angular/core';

/** Renders a base64 PNG depiction (as returned by the backend), or a placeholder if missing. */
@Component({
  selector: 'app-molecule-image',
  template: `
    @if (src(); as s) {
      <img [src]="s" [alt]="alt()" />
    } @else {
      <span class="placeholder">No image</span>
    }
  `,
  styles: `
    img { max-width: 100%; height: auto; display: block; }
    .placeholder { color: #888; font-size: 0.85rem; }
  `,
})
export class MoleculeImage {
  readonly image = input<string | null | undefined>(null);
  readonly alt = input('Molecule structure');

  protected readonly src = computed(() => {
    const img = this.image();
    return img ? `data:image/png;base64,${img}` : null;
  });
}
