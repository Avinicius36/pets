import { Moment } from 'moment';

export const enum GeneroPet {
  MACHO = 'MACHO',
  FEMEA = 'FEMEA'
}

export interface IPet {
  id?: number;
  nome?: string;
  genero?: GeneroPet;
  dataNascimento?: Moment;
  donoNome?: string;
  donoId?: number;
}

export class Pet implements IPet {
  constructor(
    public id?: number,
    public nome?: string,
    public genero?: GeneroPet,
    public dataNascimento?: Moment,
    public donoNome?: string,
    public donoId?: number
  ) {}
}
