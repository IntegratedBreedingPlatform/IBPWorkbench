
package org.generationcp.breeding.manager.listimport;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;
import org.apache.commons.io.FilenameUtils;
import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.customfields.UploadField;
import org.generationcp.breeding.manager.listimport.exceptions.GermplasmImportException;
import org.generationcp.breeding.manager.listimport.listeners.GermplasmImportButtonClickListener;
import org.generationcp.breeding.manager.listimport.util.GermplasmListUploader;
import org.generationcp.breeding.manager.pojos.ImportedGermplasm;
import org.generationcp.breeding.manager.validator.ShowNameHandlingPopUpValidator;
import org.generationcp.commons.parsing.FileParsingException;
import org.generationcp.commons.parsing.InvalidFileDataException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.components.validator.ErrorCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Configurable
public class GermplasmImportFileComponent extends AbsoluteLayout
		implements InitializingBean, InternationalizableComponent, BreedingManagerLayout, NameHandlingDialogSource {

	private static final String ERROR_IMPORTING = "Error importing ";
	private static final String ERROR = "Error";
	private static final long serialVersionUID = 9097810121003895303L;
	private static final Logger LOG = LoggerFactory.getLogger(GermplasmImportFileComponent.class);

	private final GermplasmImportMain source;

	public static final String NEXT_BUTTON_ID = "next button";

	private Label selectFileLabel;
	private UploadField uploadComponents;
	private Button cancelButton;
	private Button nextButton;
	private Button openTemplateButton;
	private GermplasmListUploader germplasmListUploader;
	private final Set<String> extensionSet = new HashSet<>();

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ShowNameHandlingPopUpValidator showNameHandlingPopUpValidator;

	public GermplasmImportFileComponent(final GermplasmImportMain source) {
		this.source = source;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
		this.initializeExtensionSet();
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setCaption(this.nextButton, Message.NEXT);
		this.messageSource.setCaption(this.openTemplateButton, Message.HERE);
	}

	public void initializeExtensionSet() {
		this.extensionSet.add("xls");
		this.extensionSet.add("xlsx");
	}

	public void nextButtonClickAction() {

		// NOTE: Display Error message if Germplasm Import file contains invalid extension like .doc, .pdf, .docx etc.
		// Valid File Extensions are .xls and .xlsx
		final String extension = FilenameUtils.getExtension(this.germplasmListUploader.getOriginalFilename()).toLowerCase();
		if (!this.extensionSet.contains(extension)) {
			MessageNotifier.showError(this.getWindow(), GermplasmImportFileComponent.ERROR,
					this.messageSource.getMessage("GERMPLSM_INVALID_FILE_EXTENSION_ERROR"));
			return;
		}

		try {
			this.germplasmListUploader.doParseWorkbook();

			if ("".equals(this.germplasmListUploader.hasWarnings())) {
				MessageNotifier.showMessage(this.source.getWindow(), "Success", "File was successfully uploaded");
			} else {
				MessageNotifier.showWarning(this.source.getWindow(), "Warning", this.germplasmListUploader.hasWarnings());
			}

			this.proceedToNextScreen();

		} catch (final GermplasmImportException e) {
			GermplasmImportFileComponent.LOG.debug(GermplasmImportFileComponent.ERROR_IMPORTING + e.getMessage(), e);
			MessageNotifier.showError(this.getWindow(), e.getCaption(), e.getMessage());
		} catch (final FileParsingException e) {
			GermplasmImportFileComponent.LOG.debug(GermplasmImportFileComponent.ERROR_IMPORTING + e.getMessage(), e);
			final String message = this.messageSource.getMessage(e.getMessage(), e.getMessageParameters(), Locale.getDefault());
			MessageNotifier.showError(this.getWindow(), GermplasmImportFileComponent.ERROR, message);
		} catch (final InvalidFileDataException e) {
			// Display Error message if Observations is empty and disable Next Button.
			GermplasmImportFileComponent.LOG.debug(GermplasmImportFileComponent.ERROR_IMPORTING + e.getMessage(), e);
			final String message = this.messageSource.getMessage(e.getMessage(), e.getMessageParameters(), Locale.getDefault());
			MessageNotifier.showError(this.getWindow(), GermplasmImportFileComponent.ERROR, message);
			this.nextButton.setEnabled(false);
		}
	}

	/**
	 * Will display a pop up for Name Handling Dialog, if the imported germplasm list has name types, if not proceed to the next screen
	 */
	void proceedToNextScreen() {
		final List<ImportedGermplasm> importedGermplasms =
				this.getGermplasmListUploader().getImportedGermplasmList().getImportedGermplasm();
		final ErrorCollection validationErrorMessages = this.showNameHandlingPopUpValidator.validate(importedGermplasms);

		if (validationErrorMessages.isEmpty()) {
			final NameHandlingDialog nameHandlingDialog = new NameHandlingDialog(this, this.germplasmListUploader.getNameFactors());
			nameHandlingDialog.setDebugId("nameHandlingDialog");
			this.getWindow().addWindow(nameHandlingDialog);
		} else {
			// no need to show the name handling window.
			this.source.nextStep();
		}
	}

	public GermplasmImportMain getSource() {
		return this.source;
	}

	public void initializeUploadField() {
		this.uploadComponents = new UploadField() {

			private static final long serialVersionUID = 1L;

			@Override
			public void uploadFinished(final Upload.FinishedEvent event) {
				super.uploadFinished(event);
				GermplasmImportFileComponent.this.nextButton.setEnabled(true);
			}
		};
		this.uploadComponents.discard();

		this.uploadComponents.setButtonCaption(this.messageSource.getMessage(Message.UPLOAD));
		this.uploadComponents.setNoFileSelectedText(this.messageSource.getMessage("NO_FILE_SELECTED"));
		this.uploadComponents.setSelectedFileText(this.messageSource.getMessage("SELECTED_IMPORT_FILE"));
		this.uploadComponents.setDeleteCaption(this.messageSource.getMessage("CLEAR"));
		this.uploadComponents.setFieldType(UploadField.FieldType.FILE);
		this.uploadComponents.setButtonCaption("Browse");

		this.uploadComponents.getRootLayout().setWidth("100%");
		this.uploadComponents.getRootLayout().setStyleName("bms-upload-container");
		this.addListenersForUploadField();
	}

	public UploadField getUploadComponent() {
		return this.uploadComponents;
	}

	@Override
	public void instantiateComponents() {
		// the &nbsp is neaded to add a whitespace between the text and following button-link
		this.selectFileLabel = new Label(this.messageSource.getMessage(Message.SELECT_GERMPLASM_LIST_FILE) + "&nbsp");
		this.selectFileLabel.setDebugId("selectFileLabel");
		this.selectFileLabel.setContentMode(Label.CONTENT_XHTML);

		this.initializeUploadField();

		this.germplasmListUploader = new GermplasmListUploader();

		this.cancelButton = new Button(this.messageSource.getMessage(Message.CANCEL));
		this.cancelButton.setDebugId("cancelButton");

		this.nextButton = new Button();
		this.nextButton.setDebugId("nextButton");
		this.nextButton.setData(GermplasmImportFileComponent.NEXT_BUTTON_ID);
		this.nextButton.setEnabled(false);
		this.nextButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		this.openTemplateButton = new Button();
		this.openTemplateButton.setDebugId("openTemplateButton");
		this.openTemplateButton.setImmediate(true);
		this.openTemplateButton.setStyleName(BaseTheme.BUTTON_LINK);

	}

	@Override
	public void initializeValues() {
		// do nothing
	}

	public void addListenersForUploadField() {
		this.uploadComponents.setDeleteButtonListener(new Button.ClickListener() {

			private static final long serialVersionUID = -1357425494204377238L;

			@Override
			public void buttonClick(final ClickEvent event) {
				GermplasmImportFileComponent.this.nextButton.setEnabled(false);
			}
		});
		this.uploadComponents.setFileFactory(this.germplasmListUploader);
	}

	@Override
	public void addListeners() {

		this.addListenersForUploadField();

		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -8787686200326172252L;

			@Override
			public void buttonClick(final ClickEvent event) {
				GermplasmImportFileComponent.this.cancelButtonAction();
			}

		});

		this.nextButton.addListener(new GermplasmImportButtonClickListener(this));

		this.openTemplateButton.addListener(new ClickListener() {

			private static final long serialVersionUID = -5277793372784918711L;

			@Override
			public void buttonClick(final ClickEvent event) {
				// Just download the new expanded template
				try {
					new GermplasmListTemplateDownloader().exportGermplasmTemplate(event.getComponent());
				} catch (final GermplasmListTemplateDownloader.FileDownloadException e) {
					GermplasmImportFileComponent.LOG.error(e.getMessage(), e);
					MessageNotifier.showError(GermplasmImportFileComponent.this.getWindow(),
							GermplasmImportFileComponent.this.messageSource.getMessage(Message.ERROR), e.getMessage());
				}
			}
		});
	}

	@Override
	public void layoutComponents() {
		// align the message consisting of the text, the link to the template and the end sentence period into one line
		final HorizontalLayout downloadMessageLayout = new HorizontalLayout();
		downloadMessageLayout.setDebugId("downloadMessageLayout");
		downloadMessageLayout.addComponent(this.selectFileLabel);
		downloadMessageLayout.addComponent(this.openTemplateButton);
		downloadMessageLayout.addComponent(new Label(this.messageSource.getMessage(Message.PERIOD)));
		this.addComponent(downloadMessageLayout, "top:20px;");

		this.addComponent(this.uploadComponents, "top:50px");

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setWidth("100%");
		buttonLayout.setHeight("40px");
		buttonLayout.setSpacing(true);

		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.nextButton);
		buttonLayout.setComponentAlignment(this.cancelButton, Alignment.BOTTOM_RIGHT);
		buttonLayout.setComponentAlignment(this.nextButton, Alignment.BOTTOM_LEFT);

		this.addComponent(buttonLayout, "top:230px");
	}

	public GermplasmListUploader getGermplasmListUploader() {
		return this.germplasmListUploader;
	}

	void setGermplasmListUploader(final GermplasmListUploader germplasmListUploader) {
		this.germplasmListUploader = germplasmListUploader;
	}

	protected void cancelButtonAction() {
		this.source.reset();
	}

	@Override
	public void setImportedNameAsPreferredName(final boolean setImportedNameAsPreferredName, final String preferredNameType) {
		this.germplasmListUploader.getImportedGermplasmList().setSetImportedNameAsPreferredName(setImportedNameAsPreferredName);
		this.germplasmListUploader.getImportedGermplasmList().setPreferredNameCode(preferredNameType);
		this.source.nextStep();
	}

	public void setShowNameHandlingPopUpValidationRule(final ShowNameHandlingPopUpValidator showNameHandlingPopUpValidator) {
		this.showNameHandlingPopUpValidator = showNameHandlingPopUpValidator;
	}
}
