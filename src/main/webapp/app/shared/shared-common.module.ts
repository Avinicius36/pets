import { NgModule } from '@angular/core';

import { PetsSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
  imports: [PetsSharedLibsModule],
  declarations: [JhiAlertComponent, JhiAlertErrorComponent],
  exports: [PetsSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class PetsSharedCommonModule {}
