export interface EndpointsResponse {
  endpoints: string[];
}

export interface RaRequest {
  smiles: string;
  endpoint: string;
  numSimilarMolecules: number;
}

export interface Molecule {
  cas: string;
  smiles: string;
  image: string;
  mw: number | null;
  logP: number | null;
  logPExperimental: boolean;
  color?: number[] | null;
}

export interface SimilarMolecule {
  molecule: Molecule;
  similarity: number;
  expValueFormatted: string;
}

export interface InterpolationPoint {
  name: string;
  x: number;
  y: number;
}

export interface Interpolation {
  axisXTitle: string;
  axisYTitle: string;
  targetLineX: number;
  targetLineY: number;
  points: InterpolationPoint[];
}

export interface Rule {
  name: string;
  description: string;
  smarts: string;
  image: string;
  experimentalAccuracy: string;
  experimentalAccuracyBiocide: string;
  fisher: string;
  shape: number;
  sizeFactor: number;
  similarMolecules: SimilarMolecule[];
  interpolationDTO: Interpolation | null;
}

export interface RaResponse {
  endpoint: string;
  prediction: string;
  molecule: Molecule;
  similarMolecules: SimilarMolecule[];
  rules: Rule[];
  interpolation: Interpolation[];
}
