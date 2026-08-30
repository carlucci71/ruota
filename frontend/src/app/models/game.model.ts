export interface Giocatore {
  nome: string;
  puntiTotale: number;
  puntiManche: number;
  withJolly: boolean;
  withGarage: boolean;
}

export interface Tabellone {
  titolo: string;
  frase: string;
  consonantiFinite: boolean;
  vocaliFinite: boolean;
}

export interface GameInfo {
  Giocatori?: Giocatore[];
  GIOCATORI?: Giocatore[];
  'Tabellone titolo'?: string;
  TabelloneInProgress?: string;
  TABELLONE?: Tabellone | string;
  GiocatoreTurno?: Giocatore | string;
  GIOCATORE_TURNO?: Giocatore | string;
  Fase?: string;
  FASE?: string;
  VocaliFinite?: string | boolean;
  ConsonantiFinite?: string | boolean;
  Tabelloni?: number;
  JOLLY_USE?: boolean;
  GARAGE_USE?: boolean;
  RADDOPPIA_USE?: boolean;
  VALORE_CRESCE?: number;
  [key: string]: any; // Per gestire chiavi dinamiche
}

export interface SpinResponse extends GameInfo {
  SPICCHIO: string | number;
}

export interface CallResponse extends GameInfo {
  LETTERE_TROVATE?: number;
  LETTERE_GIA_CHIAMATE?: boolean;
  ESAURITE?: boolean;
  RISULTATO?: boolean;
}
