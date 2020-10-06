import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Ng2Webstorage, LocalStorageService, SessionStorageService  } from 'ngx-webstorage';

import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { BmsjHipsterSharedModule, RouteAccessService } from './shared';
import { BmsjHipsterAppRoutingModule} from './app-routing.module';
import { BmsjHipsterEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import {
    ErrorComponent,
    JhiMainComponent,
    PageRibbonComponent
} from './layouts';
import { LabelPrintingModule } from './label-printing/label-printing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { GermplasmManagerModule } from './germplasm-manager/germplasm-manager.module';
import { GermplasmSearchComponent } from './germplasm-manager/germplasm-search.component';
import { GermplasmTreeTableComponent } from './shared/tree/germplasm/germplasm-tree-table.component';
import { StudyTreeComponent } from './shared/tree/study/study-tree.component';
import { ModalConfirmComponent } from './shared/modal/modal-confirm.component';

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        BmsjHipsterAppRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        BmsjHipsterSharedModule,
        BmsjHipsterEntityModule,
        LabelPrintingModule,
        GermplasmManagerModule
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        PageRibbonComponent,
        ErrorComponent,
        GermplasmTreeTableComponent,
        StudyTreeComponent
    ],
    entryComponents: [
        GermplasmTreeTableComponent,
        StudyTreeComponent
    ],
    providers: [
        RouteAccessService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
            deps: [
                LocalStorageService,
                SessionStorageService
            ]
        }
    ],
    bootstrap: [ JhiMainComponent ]
})
export class BmsjHipsterAppModule {}
