import {NgModule} from '@angular/core';
import {LotCreationDialogComponent} from './inventory/lot-creation-dialog.component';
import {RouterModule} from '@angular/router';
import {GERMPLASM_MANAGER_ROUTES} from './germplasm-manager.route';
import {BmsjHipsterSharedModule} from '../shared';
import {GermplasmSearchComponent} from './germplasm-search.component';
import {GermplasmSearchResolvePagingParams} from './germplasm-search-resolve-paging-params';
import {GermplasmManagerComponent} from './germplasm-manager.component';
import {GermplasmSelectorComponent} from './selector/germplasm-selector.component';
import {
    GermplasmListCreationComponent,
    GermplasmListCreationPopupComponent
} from './germplasm-list/germplasm-list-creation.component';
import {GermplasmManagerContext} from './germplasm-manager.context';
import { GermplasmImportModule } from './import/germplasm-import.module';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(GERMPLASM_MANAGER_ROUTES),
        GermplasmImportModule
    ],
    declarations: [
        LotCreationDialogComponent,
        GermplasmManagerComponent,
        GermplasmSearchComponent,
        GermplasmListCreationComponent,
        GermplasmListCreationPopupComponent,
        GermplasmSelectorComponent
    ],
    entryComponents: [
        LotCreationDialogComponent,
        GermplasmManagerComponent,
        GermplasmSearchComponent,
        GermplasmListCreationComponent,
        GermplasmListCreationPopupComponent,
        GermplasmSelectorComponent
    ],
    providers: [
        GermplasmSearchResolvePagingParams,
        GermplasmManagerContext
    ]
})
export class GermplasmManagerModule {

}
