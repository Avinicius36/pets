import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IPessoa } from 'app/shared/model/pessoa.model';

type EntityResponseType = HttpResponse<IPessoa>;
type EntityArrayResponseType = HttpResponse<IPessoa[]>;

@Injectable({ providedIn: 'root' })
export class PessoaService {
  public resourceUrl = SERVER_API_URL + 'api/pessoas';

  constructor(protected http: HttpClient) {}

  create(pessoa: IPessoa): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pessoa);
    return this.http
      .post<IPessoa>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(pessoa: IPessoa): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pessoa);
    return this.http
      .put<IPessoa>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPessoa>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPessoa[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(pessoa: IPessoa): IPessoa {
    const copy: IPessoa = Object.assign({}, pessoa, {
      dataNascimento: pessoa.dataNascimento != null && pessoa.dataNascimento.isValid() ? pessoa.dataNascimento.format(DATE_FORMAT) : null
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
      res.body.forEach((pessoa: IPessoa) => {
        pessoa.dataNascimento = pessoa.dataNascimento != null ? moment(pessoa.dataNascimento) : null;
      });
    }
    return res;
  }
}
