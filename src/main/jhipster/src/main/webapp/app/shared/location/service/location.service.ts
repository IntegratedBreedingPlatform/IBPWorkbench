import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MAX_PAGE_SIZE, SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { map } from 'rxjs/operators';
import { Location } from '../model/location';
import { LocationType } from '../model/location-type';
import { LocationModel } from '../model/location.model';

@Injectable()
export class LocationService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    queryBreedingLocation(locationId): Observable<Location> {
        return this.http.get<Location>(SERVER_API_URL + `crops/${this.context.cropName}/locations/${locationId}`,
            { observe: 'response' }).pipe(map((res: HttpResponse<Location>) => res.body));
    }

    queryLocationTypes(): Observable<LocationType[]> {
        return this.http.get<LocationType[]>(SERVER_API_URL + `crops/${this.context.cropName}/location-types`,
            { observe: 'response' }).pipe(map((res: HttpResponse<LocationType[]>) => res.body));
    }

    queryLocationsByType(locationTypes, favoriteLocation = false, name?, page = 0, size = MAX_PAGE_SIZE): Observable<HttpResponse<LocationModel[]>> {
        const params = new HttpParams()
            .set('name', name || '')
            .set('locationTypes', locationTypes)
            .set('favoritesOnly', String(favoriteLocation))
            .set('programUUID', this.context.programUUID)
            .set('page', String(page))
            .set('size', size);
        return this.http.get<LocationModel[]>(SERVER_API_URL + `crops/${this.context.cropName}/locations`,
            { params, observe: 'response' });
    }
}
