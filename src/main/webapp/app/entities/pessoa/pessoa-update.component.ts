import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { IPessoa, Pessoa } from 'app/shared/model/pessoa.model';
import { PessoaService } from './pessoa.service';

@Component({
  selector: 'jhi-pessoa-update',
  templateUrl: './pessoa-update.component.html'
})
export class PessoaUpdateComponent implements OnInit {
  isSaving: boolean;
  dataNascimentoDp: any;

  editForm = this.fb.group({
    id: [],
    nome: [null, [Validators.required, Validators.maxLength(100)]],
    email: [],
    dataNascimento: []
  });

  constructor(protected pessoaService: PessoaService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ pessoa }) => {
      this.updateForm(pessoa);
    });
  }

  updateForm(pessoa: IPessoa) {
    this.editForm.patchValue({
      id: pessoa.id,
      nome: pessoa.nome,
      email: pessoa.email,
      dataNascimento: pessoa.dataNascimento
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const pessoa = this.createFromForm();
    if (pessoa.id !== undefined) {
      this.subscribeToSaveResponse(this.pessoaService.update(pessoa));
    } else {
      this.subscribeToSaveResponse(this.pessoaService.create(pessoa));
    }
  }

  private createFromForm(): IPessoa {
    return {
      ...new Pessoa(),
      id: this.editForm.get(['id']).value,
      nome: this.editForm.get(['nome']).value,
      email: this.editForm.get(['email']).value,
      dataNascimento: this.editForm.get(['dataNascimento']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPessoa>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
