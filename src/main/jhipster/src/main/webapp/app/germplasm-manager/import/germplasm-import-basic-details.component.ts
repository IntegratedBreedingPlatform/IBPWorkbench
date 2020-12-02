import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmImportComponent, HEADERS } from './germplasm-import.component';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { BREEDING_METHODS_BROWSER_DEFAULT_URL } from '../../app.constants';
import { BreedingMethodManagerComponent } from '../../entities/breeding-method/breeding-method-manager.component';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { DomSanitizer } from '@angular/platform-browser';
import { GermplasmImportInventoryComponent } from './germplasm-import-inventory.component';
import { GermplasmImportContext } from './germplasm-import.context';
import { Location } from '../../shared/location/model/location';
import { LocationService } from '../../shared/location/service/location.service';
import { LocationTypeEnum } from '../../shared/location/model/location.model';

@Component({
    selector: 'jhi-germplasm-import-basic-details',
    templateUrl: './germplasm-import-basic-details.component.html'
})
export class GermplasmImportBasicDetailsComponent implements OnInit {

    @ViewChild('detailsForm')
    detailsForm: ElementRef;

    hasEmptyPreferredName: boolean;
    // Codes that are both attributes
    unmapped = [];
    draggedCode: string;

    breedingMethods: Promise<BreedingMethod[]>;
    favoriteBreedingMethods: Promise<BreedingMethod[]>;
    breedingMethodSelected: string;
    useFavoriteBreedingMethods = true;

    breedingAndCountryLocations: Promise<Location[]>;
    allLocations: Promise<Location[]>;
    favoriteBreedingAndCountryLocations: Promise<Location[]>;
    favoriteAllLocations: Promise<Location[]>;
    locationSelected: string;
    useFavoriteLocations = true;
    radioSelected = true;

    creationDateSelected: NgbDate | null

    referenceSelected: string;

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private germplasmService: GermplasmService,
        private breedingMethodService: BreedingMethodService,
        private locationService: LocationService,
        private sanitizer: DomSanitizer,
        private paramContext: ParamContext,
        private popupService: PopupService,
        public context: GermplasmImportContext
    ) {
    }

    ngOnInit(): void {
        this.context.dataBackup = this.context.data.map((row) => Object.assign({}, row));
        this.loadBreedingMethods();
        this.loadLocations();

        this.hasEmptyPreferredName = this.context.data.some((row) => !row[HEADERS['PREFERRED NAME']]);

        for (const attribute of this.context.attributes) {
            for (const nameType of this.context.nameTypes) {
                if (attribute.code === nameType.code) {
                    this.unmapped.push(attribute.code);
                }
            }
        }
        this.context.nametypesCopy = this.context.nameTypes.filter((name) => {
            return this.context.nameColumnsWithData[name.code] && this.unmapped.indexOf(name.code) === -1;
        });
        this.context.attributesCopy = this.context.attributes.filter((attribute) => {
            return this.unmapped.indexOf(attribute.code) === -1;
        });
    }

    next() {
        this.fillData();

        this.modal.close();
        this.context.dataBackupPrev = this.context.dataBackup;
        const modalRef = this.modalService.open(GermplasmImportInventoryComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    fillData() {
        this.context.data.filter((row) => !row[HEADERS['BREEDING METHOD']])
            .forEach((row) => row[HEADERS['BREEDING METHOD']] = this.breedingMethodSelected);

        this.context.data.filter((row) => !row[HEADERS['LOCATION ABBR']])
            .forEach((row) => row[HEADERS['LOCATION ABBR']] = this.locationSelected);

        this.context.data.filter((row) => !row[HEADERS['CREATION DATE']])
            .forEach((row) => row[HEADERS['CREATION DATE']] = ''
                + this.creationDateSelected.year
                + (this.creationDateSelected.month < 10 ? ('0' + this.creationDateSelected.month) : this.creationDateSelected.month)
                + (this.creationDateSelected.day < 10 ? ('0' + this.creationDateSelected.day) : this.creationDateSelected.day));

        if (this.referenceSelected) {
            this.context.data.filter((row) => !row[HEADERS['REFERENCE']])
                .forEach((row) => row[HEADERS['REFERENCE']] = this.referenceSelected);
        }

        dataLoop: for (const row of this.context.data) {
            if (!row[HEADERS['PREFERRED NAME']]) {
                // names already ordered by priority
                for (const name of this.context.nametypesCopy) {
                    if (row[name.code]) {
                        row[HEADERS['PREFERRED NAME']] = name.code;
                        continue dataLoop;
                    }
                }
            }
        }

        // TODO Complete
    }

    dismiss() {
        this.modal.dismiss();
    }

    back() {
        this.modal.close();
        const modalRef = this.modalService.open(GermplasmImportComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    loadBreedingMethods() {
        this.breedingMethods = this.breedingMethodService.getBreedingMethods().toPromise();
        this.favoriteBreedingMethods = this.breedingMethodService.getBreedingMethods(true).toPromise();
    }

    loadLocations() {
        this.breedingAndCountryLocations = this.locationService.queryLocationsByType([LocationTypeEnum.BREEDING_LOCATION, LocationTypeEnum.COUNTRY], false).toPromise();
        this.favoriteBreedingAndCountryLocations = this.locationService.queryLocationsByType([LocationTypeEnum.BREEDING_LOCATION, LocationTypeEnum.COUNTRY], true).toPromise();
        this.allLocations = this.locationService.queryLocationsByType([], false).toPromise();
        this.favoriteAllLocations = this.locationService.queryLocationsByType([], true).toPromise();
    }

    openBreedingMethodManager() {

        const params = '?programId=' + this.paramContext.selectedProjectId;

        const modal = this.popupService.open(BreedingMethodManagerComponent as Component, { windowClass: 'modal-autofit' });
        modal.then((modalRef) => {
            modalRef.componentInstance.safeUrl =
                this.sanitizer.bypassSecurityTrustResourceUrl(BREEDING_METHODS_BROWSER_DEFAULT_URL + params);
            modalRef.result.then(() => this.loadBreedingMethods());
        });
    }

    hasAllBreedingMethods() {
        return this.context.data.every((row) => row[HEADERS['BREEDING METHOD']]);
    }

    hasAllLocations() {
        return this.context.data.every((row) => row[HEADERS['LOCATION ABBR']]);
    }

    hasAllCreationDate() {
        return this.context.data.every((row) => row[HEADERS['CREATION DATE']]);
    }

    hasAllReference() {
        return this.context.data.every((row) => row[HEADERS['REFERENCE']]);
    }

    hasAllBasicDetails() {
        // TODO complete
        return this.hasAllBreedingMethods() && this.hasAllLocations() && this.hasAllCreationDate();
    }

    canProceed(f) {
        // TODO complete
        const form = f.form;
        return form.valid && (this.breedingMethodSelected || this.hasAllBreedingMethods())
            && (this.locationSelected || this.hasAllLocations())
            && (this.creationDateSelected || this.hasAllCreationDate())
            && this.unmapped.length === 0
    }

    dragStart($event, code) {
        this.draggedCode = code;
    }

    dragEnd($event) {
        this.draggedCode = null;
    }

    drop($event, type: 'names' | 'attributes') {
        if (type === 'names') {
            this.context.nametypesCopy.push(this.context.nameTypes.find((n) => n.code === this.draggedCode));
        } else {
            this.context.attributesCopy.push(this.context.attributes.find((a) => a.code === this.draggedCode));
        }
        this.unmapped = this.unmapped.filter((u) => u !== this.draggedCode);
    }

}
