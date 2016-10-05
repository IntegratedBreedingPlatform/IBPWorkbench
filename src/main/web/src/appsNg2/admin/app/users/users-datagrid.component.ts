import { Component, OnInit, ViewChild } from '@angular/core';
import { NgDataGridModel } from './../shared/components/datagrid/ng-datagrid.model';
import { PaginationComponent } from './../shared/components/datagrid/pagination.component';
import { User } from './../shared/models/user.model';
import { Role } from './../shared/models/role.model';
import { FORM_DIRECTIVES } from '@angular/forms';
import './../shared/utils/array.extensions';
import { UserService } from './../shared/services/user.service';
import { RoleService } from './../shared/services/role.service';
import { Dialog } from './../shared/components/dialog/dialog.component';
import {UserComparator} from './user-comparator.component';
import { UserCard } from './user-card.component';

@Component({
    selector: 'users-datagrid',
    templateUrl: 'users-datagrid.component.html',
    styleUrls: [
        './users-datagrid.component.css'
    ],
    directives: [PaginationComponent, FORM_DIRECTIVES, Dialog, UserCard],
    moduleId: module.id
})

export class UsersDatagrid implements OnInit {

    // @ViewChild(UserCard)
    // userCard: UserCard;
    errorServiceMessage: string = "";
    showNewDialog = false;
    showEditDialog = false;
    isEditing = false;
    dialogTitle: string;
    showConfirmStatusDialog = false;
    confirmStatusTitle: string = "Confirm";
    table: NgDataGridModel<User>;
    recentlyRemoveUsers: any[];
    confirmMessage: string = "Please confirm that you would like to deactivate/activate this user account.";
    user: User;
    originalUser: User;

    private roles: Role[];
    public userSelected: User;

    constructor(private userService: UserService, private roleService: RoleService) {
        this.table = new NgDataGridModel<User>([], 25, new UserComparator(), <User>{ status: "true" });
        this.initUser();
    }

    showNewUserForm(userCreateCard: UserCard) {
        this.initUser();
        this.dialogTitle = "Add User";
        this.isEditing = false;
        this.showNewDialog = true;
        userCreateCard.initialize();
    }

    showEditUserForm(user: User, userEditCard: UserCard) {

        this.dialogTitle = "Edit User";
        this.originalUser = user;
        this.user = new User(user.id, user.firstName, user.lastName,
                        user.username, user.role, user.email, user.status);
        this.isEditing = true;
        this.showEditDialog = true;
        userEditCard.initialize();
    }

    initUser() {
        this.user = new User("0", "", "", "", "", "", "true");
    }

    ngOnInit() {
        if(this.table.sortBy == undefined) {
            this.table.sortBy = "lastName";
        }
        //get all users
        this.userService
            .getAll()
            .subscribe(
                users => this.table.items = users,
                error => {
                    this.errorServiceMessage = error;
                    if (error.status === 401) {
                        localStorage.removeItem('xAuthToken');
                        this.handleReAuthentication();
                    }
            });

        // get all roles
        this.roleService
            .getAll()
            .subscribe(
                roles => this.roles = roles,
                error => {
                    // XXX
                    // handleReAuthentication is called on
                    // userService error
            });
    }

    onUserAdded(user: User) {
        this.showNewDialog = false;
        this.table.items.push(user);
        this.sortAfterAddOrEdit();
    }

    sortAfterAddOrEdit() {
        if(this.table.sortBy == undefined) {
            this.sort("lastName", true);
        }
        else {
            this.sort(this.table.sortBy, true);
        }
    }

    onUserEdited(user: User) {
        this.showEditDialog = false;
        this.table.items.remove(this.originalUser);
        this.table.items.push(user);
        this.sortAfterAddOrEdit();
    }

    // TODO
    // - Move to a shared component
    // - see /ibpworkbench/src/main/web/src/apps/ontology/app-services/bmsAuth.js
    handleReAuthentication() {
        alert('Site Admin needs to authenticate you again. Redirecting to login page.');
        window.top.location.href = '/ibpworkbench/logout';
    }

    isSorted(col): boolean {
        return col == this.table.sortBy;
    }

    sort(col, direction?, event?: MouseEvent): void {
        if (event) {
            event.preventDefault();
        }

        if(!direction) {
            if (this.table.sortBy == col) {
                this.table.sortAsc = !this.table.sortAsc;
            } else {
                this.table.sortAsc = true;
            }
        }

        this.table.sortBy = col;
    }

    changedActiveStatus(e: any) {
        if (this.userSelected.status === "true") {
            this.userSelected.status = "false";
        } else {
            this.userSelected.status = "true";
        }
        this.userService
            .update(this.userSelected)
            .subscribe(
            resp => { },
            error => {
                this.errorServiceMessage = error;
            });

        this.userSelected = null;
        this.showConfirmStatusDialog = false;

    }

    showUserStatusConfirmPopUp(e: any){
      this.userSelected = e;
      this.showConfirmStatusDialog = true;
      this.confirmMessage = "Please confirm that you would like to ";

      if (e.status === "true") {
          this.confirmMessage = this.confirmMessage + "deactivate this user account.";
      } else {
          this.confirmMessage = this.confirmMessage + "activate this user account.";
      }
    }

    closeUserStatusConfirmPopUp(){
      this.showConfirmStatusDialog = false;
    }
}
