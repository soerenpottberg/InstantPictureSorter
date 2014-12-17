package org.pottberg.ips.controller;

import static javafx.beans.binding.Bindings.createStringBinding;

import java.nio.file.Paths;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.CategoryNameBuilder;
import org.pottberg.ips.model.command.Command;
import org.pottberg.ips.model.command.RenameCategoryCommand;

public class CategoryEditFormController {

    @FXML
    private TextField nameTextField;

    @FXML
    private Label fullYearNamePreviewLabel;

    @FXML
    private Label suggestedNamePreviewLabel;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label userDefinedNamePreviewLabel;

    @FXML
    private Label currentPathLabel;

    @FXML
    private Label namePreviewLabel;

    @FXML
    private ToggleGroup dateRangeToggleGroup;

    @FXML
    private RadioButton fullYearNameRadioButton;

    @FXML
    private RadioButton userDefinedNameRadioButton;

    @FXML
    private RadioButton suggestedNameRadioButton;

    private ObjectProperty<Category> categoryProperty;

    private StringProperty newName;

    private CategoryNameBuilder categoryNameBuilder;

    private ChangeListener<LocalDate> selectUserDefinedNameListener;

    private MainController mainController;

    public CategoryEditFormController() {
	categoryProperty = new SimpleObjectProperty<>();
	newName = new SimpleStringProperty();
	categoryNameBuilder = new CategoryNameBuilder(categoryProperty);
    }

    @FXML
    private void initialize() {

	suggestedNamePreviewLabel.textProperty()
	    .bind(categoryNameBuilder.suggestedNameProperty());
	userDefinedNamePreviewLabel.textProperty()
	    .bind(categoryNameBuilder.userDefinedNameProperty());
	fullYearNamePreviewLabel.textProperty()
	    .bind(categoryNameBuilder.fullYearNameProperty());
	namePreviewLabel.textProperty()
	    .bind(newName);

	endDatePicker.setDayCellFactory(datePicker -> {
	    return new DateCell() {
		@Override
		public void updateItem(LocalDate item, boolean empty) {
		    super.updateItem(item, empty);

		    if (startDatePicker.getValue() == null) {
			return;
		    }

		    if (item.isBefore(startDatePicker.getValue())) {
			setDisable(true);
			setStyle("-fx-background-color: #ffc0cb;");
		    }
		}
	    };
	});

	selectUserDefinedNameListener = (observableDate, oldDate, newDate) -> {
	    userDefinedNameRadioButton.setSelected(true);
	};

	categoryProperty.addListener((observableCategory, oldCategory,
	    newCategory) -> {
	    dateRangeToggleGroup.selectToggle(suggestedNameRadioButton);
	    if (oldCategory != null) {
		startDatePicker.valueProperty()
		    .removeListener(selectUserDefinedNameListener);
		endDatePicker.valueProperty()
		    .removeListener(selectUserDefinedNameListener);
		nameTextField.textProperty()
		    .unbindBidirectional(oldCategory.nameProperty());
		startDatePicker.valueProperty()
		    .unbindBidirectional(
			oldCategory.userDefinedStartDateProperty());
		endDatePicker.valueProperty()
		    .unbindBidirectional(
			oldCategory.userDefinedEndDateProperty());
		currentPathLabel.textProperty()
		    .unbind();
	    }
	    if (newCategory != null) {
		nameTextField.textProperty()
		    .bindBidirectional(newCategory.nameProperty());
		startDatePicker.valueProperty()
		    .bindBidirectional(
			newCategory.userDefinedStartDateProperty());
		endDatePicker.valueProperty()
		    .bindBidirectional(newCategory.userDefinedEndDateProperty());
		currentPathLabel.textProperty()
		    .bind(createStringBinding(() -> {
			return newCategory.getDirectoryName();
		    }, newCategory.pathProperty()));
		startDatePicker.valueProperty()
		    .addListener(selectUserDefinedNameListener);
		endDatePicker.valueProperty()
		    .addListener(selectUserDefinedNameListener);
	    }
	});

	ChangeListener<LocalDate> datePickerListener = createDatePickerListener();
	startDatePicker.valueProperty()
	    .addListener(datePickerListener);
	endDatePicker.valueProperty()
	    .addListener(datePickerListener);

	dateRangeToggleGroup.selectedToggleProperty()
	    .addListener((observerableToggle, oldToggle, newToggle) -> {
		bindNewName(newToggle);
	    });
	bindNewName(dateRangeToggleGroup.getSelectedToggle());
    }

    private void bindNewName(Toggle selectedToggle) {
	if (selectedToggle == fullYearNameRadioButton) {
	    newName.bind(fullYearNamePreviewLabel.textProperty());
	} else if (selectedToggle == userDefinedNameRadioButton) {
	    newName.bind(userDefinedNamePreviewLabel.textProperty());
	} else if (selectedToggle == suggestedNameRadioButton) {
	    newName.bind(suggestedNamePreviewLabel.textProperty());
	}
    }

    private ChangeListener<LocalDate> createDatePickerListener() {
	return (observableLocalDate, oldLocalDate, newLocalDate) -> {
	    ensureEndDateAfterStartDate();
	};
    }

    private void ensureEndDateAfterStartDate() {
	if (startDatePicker.getValue() == null
	    || endDatePicker.getValue() == null) {
	    return;
	}
	if (startDatePicker.getValue()
	    .isAfter(endDatePicker.getValue())) {
	    endDatePicker.setValue(startDatePicker.getValue());
	}
    }

    @FXML
    private void renameCategoryClicked(ActionEvent event) {
	Category category = getCategory();
	if (category == null) {
	    return;
	}
	String newName = getNewName();
	Command command = new RenameCategoryCommand(category,
	    Paths.get(newName));
	mainController.executeCommand(command);
    }

    @FXML
    private void resetCategoryClicked(ActionEvent event) {
	reset();
    }

    private String getNewName() {
	return newName.get();
    }

    public Category getCategory() {
	return categoryProperty.get();
    }

    public ObjectProperty<Category> categoryProperty() {
	return categoryProperty;
    }

    public void setMainController(MainController mainController) {
	this.mainController = mainController;
    }

    public void reset() {
	Category category = getCategory();
	if (category == null) {
	    return;
	}
	category.reset();
    }

}
