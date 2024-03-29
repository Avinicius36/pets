import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IPet } from 'app/shared/model/pet.model';

type EntityResponseType = HttpResponse<IPet>;
type EntityArrayResponseType = HttpResponse<IPet[]>;

@Injectable({ providedIn: 'root' })
export class PetService {
  public resourceUrl = SERVER_API_URL + 'api/pets';

  constructor(protected http: HttpClient) {}

  create(pet: IPet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pet);
    return this.http
      .post<IPet>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(pet: IPet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pet);
    return this.http
      .put<IPet>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPet>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(pet: IPet): IPet {
    const copy: IPet = Object.assign({}, pet, {
      dataNascimento: pet.dataNascimento != null && pet.dataNascimento.isValid() ? pet.dataNascimento.format(DATE_FORMAT) : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dataNascimento = res.body.dataNascimento != null ? moment(res.body.dataNascimento) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((pet: IPet) => {
        pet.dataNascimento = pet.dataNascimento != null ? moment(pet.dataNascimento) : null;
      });
    }
    return res;
  }
}
