import type { ElementDefinition } from 'cytoscape';
import { Molecule, RaResponse, Rule, SimilarMolecule } from '../../core/models/toxread.models';

export type NodeKind = 'root' | 'molecule' | 'rule' | 'rule-molecule';

export interface GraphNodeData {
  id: string;
  kind: NodeKind;
  label: string;
  image?: string;
  color?: string;
  details: Molecule | SimilarMolecule | Rule;
}

/** Converts sRGB components (0..1) to a CSS rgb() string. */
export const toCssColor =(c: number[] | null | undefined): string | undefined => {
  if (!c || c.length < 3) return undefined;
  const [r, g, b] = c.map((v) => Math.round(Math.min(1, Math.max(0, v)) * 255));
  return `rgb(${r}, ${g}, ${b})`;
};

const NODE_SIZE = 26; // molecule points
const RULE_SIZE = 120; // rule rectangles are wider than the points
const MIN_R1 = 140;
const RING_GAP = 150;

const toDataUri = (img: string | null | undefined) => (img ? `data:image/png;base64,${img}` : undefined);
const polar = (r: number, a: number) => ({ x: r * Math.cos(a), y: r * Math.sin(a) });
/** Minimum radius so that `n` nodes on a ring keep at least one node width of arc spacing. */
const radiusFor = (n: number, size = NODE_SIZE) => (n * size * 1.3) / (2 * Math.PI);

/**
 * Builds Cytoscape elements with preset positions:
 * root at the centre, similar molecules on an inner ring, rules on an outer ring,
 * and each rule's similar molecules fanned out on a third ring within the rule's angular wedge.
 */
export function buildGraph(result: RaResponse): ElementDefinition[] {
  const els: ElementDefinition[] = [];
  const node = (data: GraphNodeData, pos: { x: number; y: number }) =>
    els.push({ group: 'nodes', data: { ...data }, position: pos });
  const edge = (source: string, target: string, weight?: number) =>
    els.push({ group: 'edges', data: { id: `${source}->${target}`, source, target, weight } });

  node(
    { id: 'root', kind: 'root', label: result.molecule.smiles || result.molecule.cas || 'Input', image: toDataUri(result.molecule.image), color: toCssColor(result.molecule.color), details: result.molecule },
    { x: 0, y: 0 },
  );

  const sims = result.similarMolecules ?? [];
  const rules = result.rules ?? [];

  // Inner ring: similar molecules.
  const r1 = Math.max(MIN_R1, radiusFor(sims.length));
  const stepIn = (2 * Math.PI) / Math.max(1, sims.length);
  sims.forEach((s, i) => {
    const id = `sm-${i}`;
    node(
      { id, kind: 'molecule', label: s.molecule.cas || s.molecule.smiles, image: toDataUri(s.molecule.image), color: toCssColor(s.molecule.color), details: s },
      polar(r1, -Math.PI / 2 + stepIn * (i + 0.5)),
    );
    edge('root', id, s.similarity);
  });

  // Outer ring: rules, each with a wedge proportional to its number of children.
  const weights = rules.map((r) => Math.max(1, r.similarMolecules?.length ?? 0));
  const totalWeight = weights.reduce((a, b) => a + b, 0) || 1;
  const r2 = Math.max(r1 + RING_GAP, radiusFor(rules.length, RULE_SIZE));
  const r3 = Math.max(r2 + RING_GAP, radiusFor(totalWeight));
  let start = -Math.PI / 2;
  rules.forEach((rule, i) => {
    const wedge = (2 * Math.PI * weights[i]) / totalWeight;
    const id = `rule-${i}`;
    node(
      { id, kind: 'rule', label: rule.name, image: toDataUri(rule.image), details: rule },
      polar(r2, start + wedge / 2),
    );
    edge('root', id);

    const children = rule.similarMolecules ?? [];
    children.forEach((s, j) => {
      const cid = `${id}-sm-${j}`;
      node(
        { id: cid, kind: 'rule-molecule', label: s.molecule.cas || s.molecule.smiles, image: toDataUri(s.molecule.image), color: toCssColor(s.molecule.color), details: s },
        polar(r3, start + (wedge * (j + 0.5)) / children.length),
      );
      edge(id, cid, s.similarity);
    });
    start += wedge;
  });

  return els;
}
