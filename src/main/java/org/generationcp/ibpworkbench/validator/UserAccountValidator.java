package org.generationcp.ibpworkbench.validator;

import org.generationcp.commons.security.Role;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by cyrus on 11/26/14.
 */

@Configurable
public class UserAccountValidator implements Validator {

	public static final String SIGNUP_FIELD_REQUIRED = "signup.field.required";
	public static final String SIGNUP_FIELD_INVALID_ROLE = "signup.field.invalid.role";
	public static final String SIGNUP_FIELD_PASSWORD_NOT_MATCH = "signup.field.password.not.match";
	public static final String SIGNUP_FIELD_USERNAME_EXISTS = "signup.field.username.exists";
	public static final String DATABASE_ERROR = "database.error";
	public static final String SIGNUP_FIELD_PERSON_EXISTS = "signup.field.person.exists";

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Override
	public boolean supports(Class<?> aClass) {
		return UserAccountModel.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		UserAccountModel userAccount = (UserAccountModel) o;

		validateFieldsEmptyOrWhitespace(errors);

		validatePasswordConfirmationIfEquals(errors, userAccount);

		validateUsernameIfExists(errors, userAccount);

		validatePersonIfExists(errors, userAccount);

		validateUserRole(errors, userAccount);
	}

	protected void validateFieldsEmptyOrWhitespace(Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.FIRST_NAME,
				SIGNUP_FIELD_REQUIRED);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.LAST_NAME,
				SIGNUP_FIELD_REQUIRED);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.EMAIL,
				SIGNUP_FIELD_REQUIRED);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.USERNAME,
				SIGNUP_FIELD_REQUIRED);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.PASSWORD,
				SIGNUP_FIELD_REQUIRED);
		ValidationUtils
				.rejectIfEmptyOrWhitespace(errors,
						UserAccountFields.PASSWORD_CONFIRMATION, SIGNUP_FIELD_REQUIRED);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.ROLE,
				SIGNUP_FIELD_REQUIRED);
	}

	protected void validateUserRole(Errors errors, UserAccountModel userAccount) {
		if (!Role.ADMIN.name().equals(userAccount.getRole()) &&
				!Role.BREEDER.name().equals(userAccount.getRole()) &&
				!Role.TECHNICIAN.name().equals(userAccount.getRole())) {
			errors.rejectValue(UserAccountFields.ROLE, SIGNUP_FIELD_INVALID_ROLE);
		}
	}

	protected void validatePasswordConfirmationIfEquals(Errors errors,
			UserAccountModel userAccount) {
		if (userAccount.getPassword() != null
				&& userAccount.getPasswordConfirmation() != null
				&& !userAccount.getPassword().equals(userAccount.getPasswordConfirmation())) {

			errors.rejectValue(UserAccountFields.PASSWORD_CONFIRMATION,
					SIGNUP_FIELD_PASSWORD_NOT_MATCH);
		}
	}

	protected void validateUsernameIfExists(Errors errors, UserAccountModel userAccount) {
		try {
			if (workbenchDataManager.isUsernameExists(userAccount.getUsername())) {
				errors.rejectValue(UserAccountFields.USERNAME,
						SIGNUP_FIELD_USERNAME_EXISTS);
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue(UserAccountFields.USERNAME, DATABASE_ERROR);
		}
	}

	protected void validatePersonIfExists(Errors errors, UserAccountModel userAccount) {
		try {
			if (workbenchDataManager
					.isPersonExists(userAccount.getFirstName(), userAccount.getLastName())) {
				errors.rejectValue(UserAccountFields.FIRST_NAME,
						SIGNUP_FIELD_PERSON_EXISTS);
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue(UserAccountFields.FIRST_NAME, DATABASE_ERROR);
		}
	}

	public interface UserAccountFields {
		public static final String FIRST_NAME = "firstName";
		public static final String LAST_NAME = "lastName";
		public static final String EMAIL = "email";
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static final String PASSWORD_CONFIRMATION = "passwordConfirmation";
		public static final String ROLE = "role";
	}
}
