package org.pottberg.ips.model;

import static javafx.beans.binding.Bindings.createLongBinding;
import static javafx.beans.binding.Bindings.format;
import static javafx.beans.binding.Bindings.when;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javafx.beans.binding.LongBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class CategoryNameBuilder {

    private StringProperty fullYearNameProperty;
    private StringProperty suggestedNameProperty;
    private StringProperty userDefinedNameProperty;
    
    private CategoryNameBuilder() {
	fullYearNameProperty = new SimpleStringProperty();
	suggestedNameProperty = new SimpleStringProperty();
	userDefinedNameProperty = new SimpleStringProperty();
    }

    public CategoryNameBuilder(ObjectProperty<Category> categoryProperty) {
	this();
	categoryProperty.addListener((ChangeListener<Category>) (
	    observableCategory,
	    oldCategory, newCategory) -> {
	    build(newCategory);
	});
	build(categoryProperty.get());
    }
    
    public CategoryNameBuilder(Category category) {
	this();
	build(category);
    }

    private void build(Category newCategory) {

	if (newCategory == null) {
	    fullYearNameProperty.unbind();
	    suggestedNameProperty.unbind();
	    userDefinedNameProperty.unbind();
	    return;
	}

	ObjectProperty<LocalDate> startDate = newCategory.startDateProperty();
	ObjectProperty<LocalDate> endDate = newCategory.endDateProperty();

	ObjectProperty<LocalDate> manualStartDate = newCategory.userDefinedStartDateProperty();
	ObjectProperty<LocalDate> manualEndDate = newCategory.userDefinedEndDateProperty();

	LongBinding duration = createDurationBinding(startDate, endDate);
	LongBinding manualDuration = createDurationBinding(manualStartDate,
	    manualEndDate);

	ReadOnlyStringProperty name = newCategory.nameProperty();

	StringBinding note = createNoteBinding(duration);
	StringBinding manualNote = createNoteBinding(manualDuration);

	suggestedNameProperty.bind(createNameBinding(startDate, name, note,
	    false));

	userDefinedNameProperty.bind(createNameBinding(manualStartDate, name,
	    manualNote, true));

	fullYearNameProperty.bind(createNameBinding(startDate, name));
    }

    private StringBinding createNameBinding(
	ObjectProperty<LocalDate> startDate, ReadOnlyStringProperty name) {
	return when(
	    startDate.isNull())
	    .then(format("[yyyy] %s",
		name))
	    .otherwise(
		format("[%1$tY] %2$s",
		    startDate, name));
    }

    private StringBinding createNameBinding(
	ObjectProperty<LocalDate> startDate, ReadOnlyStringProperty name,
	StringBinding note, boolean isManual) {
	String formatString = "[yyyy-mm-dd] %s";
	if (isManual) {
	    formatString = "%s";
	}
	return when(
	    startDate.isNull())
	    .then(format(formatString,
		name))
	    .otherwise(
		format("[%1$tY-%1$tm-%1$td] %2$s%3$s",
		    startDate, name, note));
    }

    private StringBinding createNoteBinding(LongBinding duration) {
	return when(duration.isEqualTo(1))
	    .then("")
	    .otherwise(format(" (%d Tage)", duration));
    }

    private LongBinding createDurationBinding(
	ObjectProperty<LocalDate> startDate, ObjectProperty<LocalDate> endDate) {
	return createLongBinding(() -> {
	    if (startDate.get() == null || endDate.get() == null) {
		return 1l;
	    }
	    return ChronoUnit.DAYS.between(startDate.get(), endDate.get()
		.plusDays(1));
	}, startDate, endDate);
    }

    public StringProperty suggestedNameProperty() {
	return suggestedNameProperty;
    }

    public StringProperty userDefinedNameProperty() {
	return userDefinedNameProperty;
    }

    public StringProperty fullYearNameProperty() {
	return fullYearNameProperty;
    }

    public String getSuggestedName() {
	return suggestedNameProperty.get();
    }

    public String getUserDefinedName() {
	return userDefinedNameProperty.get();
    }

    public String getFullYearName() {
	return fullYearNameProperty.get();
    }

}
