import { Moment } from 'moment';
import { IPet } from 'app/shared/model/pet.model';

export interface IPessoa {
  id?: number;
  nome?: string;
  email?: string;
  dataNascimento?: Moment;
  pets?: IPet[];
}

export class Pessoa implements IPessoa {
  constructor(public id?: number, public nome?: string, public email?: string, public dataNascimento?: Moment, public pets?: IPet[]) {}
}
