import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { PetsSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [PetsSharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [PetsSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PetsSharedModule {
  static forRoot() {
    return {
      ngModule: PetsSharedModule
    };
  }
}
