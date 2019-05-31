import { Injectable , Inject} from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { Role } from './../models/role.model';
import ServiceHelper from "./service.helper";

@Injectable()
export class RoleService{
  private baseUrl: string = '/bmsapi';

  private http: Http;

  constructor(@Inject(Http) http:Http) {
      this.http = http;
  }

  getAll(): Observable<Role[]>{
    let Roles$ = this.http
      .get(`${this.baseUrl}/roles/assignable`, {headers: this.getHeaders()})
      .map(mapRoles);
      return Roles$;
  }

  private getHeaders(){
      return ServiceHelper.getHeaders();
  }
}


function mapRoles(response:Response): Role[]{
   return response.json().map(toRole)
}

function toRole(r:any): Role{
  let Role = <Role>({
    id: r.id,
   name: r.name,
  });
  return Role;
}

function mapRole(response:Response): Role{
  return toRole(response.json());
}
