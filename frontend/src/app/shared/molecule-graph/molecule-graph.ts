import { Component, ElementRef, OnDestroy, computed, effect, input, signal, viewChild } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import cytoscape from 'cytoscape';
import { Molecule, RaResponse, Rule, SimilarMolecule } from '../../core/models/toxread.models';
import { MoleculeImage } from '../molecule-image/molecule-image';
import { GraphNodeData, buildGraph, toCssColor } from './molecule-graph.model';

interface Hovered {
  data: GraphNodeData;
  x: number;
  y: number;
}

/** Radial graph: input molecule at the centre, similar molecules (inner ring), rules (outer ring) and rule neighbours. */
@Component({
  selector: 'app-molecule-graph',
  imports: [DecimalPipe, MoleculeImage],
  templateUrl: './molecule-graph.html',
  styleUrl: './molecule-graph.scss',
})
export class MoleculeGraph implements OnDestroy {
  readonly result = input.required<RaResponse>();
  private readonly host = viewChild.required<ElementRef<HTMLElement>>('host');
  private readonly container = viewChild.required<ElementRef<HTMLElement>>('container');
  private cy?: cytoscape.Core;

  /** Colour of the root molecule, used by the legend (falls back to the default root red). */
  protected readonly rootColor = computed(() => toCssColor(this.result().molecule.color) ?? '#d32f2f');

  protected readonly hovered = signal<Hovered | null>(null);
  protected readonly tooltipPos = signal({ left: 0, top: 0, ready: false });

  constructor() {
    effect(() => {
      const result = this.result();
      const el = this.container().nativeElement;
      this.cy?.destroy();
      this.hovered.set(null);

      const cy = cytoscape({
        container: el,
        elements: buildGraph(result),
        layout: { name: 'preset' },
        autoungrabify: true,
        minZoom: 0.2,
        maxZoom: 3,
        style: [
          {
            selector: 'node',
            style: {
              width: 26, height: 26, shape: 'ellipse',
              'background-color': '#1976d2', 'border-width': 0,
              label: 'data(label)', 'font-size': 16, 'text-valign': 'bottom', 'text-margin-y': 4,
              'text-wrap': 'ellipsis', 'text-max-width': '100px',
            },
          },
          { selector: 'node[kind = "root"]', style: { width: 40, height: 40, 'background-color': '#d32f2f' } },
          { selector: 'node[color]', style: { 'background-color': 'data(color)' } },
          {
            selector: 'node[kind = "rule"]',
            style: {
              shape: 'rectangle', width: 100, height: 45,
              'background-color': '#e8f5e9', 'border-width': 2, 'border-color': '#2e7d32',
              'text-valign': 'center', 'text-margin-y': 0, 'font-size': 16,
              'text-wrap': 'ellipsis', 'text-max-width': '100px',
            },
          },
          { selector: 'node[kind = "rule-molecule"]', style: { width: 20, height: 20 } },
          { selector: 'node:active, node:selected', style: { 'overlay-opacity': 0 } },
          { selector: 'node:active', style: { 'border-width': 3, 'border-color': '#333' } },
          { selector: 'edge', style: { width: 1.5, 'line-color': '#bbb', 'curve-style': 'straight' } },
          { selector: 'edge[weight]', style: { width: 'mapData(weight, 0, 1, 1, 5)' } },
        ],
      });

      cy.on('mouseover', 'node', (evt) => {
        const pos = evt.renderedPosition;
        this.hovered.set({ data: evt.target.data() as GraphNodeData, x: pos.x, y: pos.y });
        this.placeTooltip(pos.x, pos.y);
      });
      cy.on('mouseout', 'node', () => this.hovered.set(null));
      cy.on('pan zoom', () => this.hovered.set(null));
      this.cy = cy;
    });
  }

  /**
   * Positions the tooltip next to the node once it has rendered (so its real size is known):
   * below/right by default, flipped above/left when there isn't enough room, then clamped.
   */
  private placeTooltip(x: number, y: number) {
    this.tooltipPos.set({ left: 0, top: 0, ready: false });
    requestAnimationFrame(() => {
      const host = this.host().nativeElement;
      const tip = host.querySelector<HTMLElement>('.tooltip');
      if (!tip) return;
      const gap = 16;
      const w = tip.offsetWidth;
      const h = tip.offsetHeight;
      const maxLeft = Math.max(4, host.clientWidth - w - 4);
      const maxTop = Math.max(4, host.clientHeight - h - 4);
      let left = x + gap;
      if (left > maxLeft) left = x - gap - w;
      let top = y + gap;
      if (top > maxTop) top = y - gap - h;
      this.tooltipPos.set({
        left: Math.min(Math.max(4, left), maxLeft),
        top: Math.min(Math.max(4, top), maxTop),
        ready: true,
      });
    });
  }

  protected asMolecule(d: GraphNodeData) { return d.details as Molecule; }
  protected asSimilar(d: GraphNodeData) { return d.details as SimilarMolecule; }
  protected asRule(d: GraphNodeData) { return d.details as Rule; }

  ngOnDestroy(): void {
    this.cy?.destroy();
  }
}
