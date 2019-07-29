import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { JhiAlertService } from 'ng-jhipster';
import { IPet, Pet } from 'app/shared/model/pet.model';
import { PetService } from './pet.service';
import { IPessoa } from 'app/shared/model/pessoa.model';
import { PessoaService } from 'app/entities/pessoa';

@Component({
  selector: 'jhi-pet-update',
  templateUrl: './pet-update.component.html'
})
export class PetUpdateComponent implements OnInit {
  isSaving: boolean;

  pessoas: IPessoa[];
  dataNascimentoDp: any;

  editForm = this.fb.group({
    id: [],
    nome: [null, [Validators.required, Validators.maxLength(100)]],
    genero: [],
    dataNascimento: [],
    donoId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected petService: PetService,
    protected pessoaService: PessoaService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ pet }) => {
      this.updateForm(pet);
    });
    this.pessoaService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IPessoa[]>) => mayBeOk.ok),
        map((response: HttpResponse<IPessoa[]>) => response.body)
      )
      .subscribe((res: IPessoa[]) => (this.pessoas = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(pet: IPet) {
    this.editForm.patchValue({
      id: pet.id,
      nome: pet.nome,
      genero: pet.genero,
      dataNascimento: pet.dataNascimento,
      donoId: pet.donoId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const pet = this.createFromForm();
    if (pet.id !== undefined) {
      this.subscribeToSaveResponse(this.petService.update(pet));
    } else {
      this.subscribeToSaveResponse(this.petService.create(pet));
    }
  }

  private createFromForm(): IPet {
    return {
      ...new Pet(),
      id: this.editForm.get(['id']).value,
      nome: this.editForm.get(['nome']).value,
      genero: this.editForm.get(['genero']).value,
      dataNascimento: this.editForm.get(['dataNascimento']).value,
      donoId: this.editForm.get(['donoId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPet>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackPessoaById(index: number, item: IPessoa) {
    return item.id;
  }
}
