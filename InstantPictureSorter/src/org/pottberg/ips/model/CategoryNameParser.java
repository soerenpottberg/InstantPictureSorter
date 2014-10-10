package org.pottberg.ips.model;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class CategoryNameParser {

    private static final String DAYS_WITH_SPACE = " Tage";
    private static final String ONE_DAY = "1 Tag";
    private static final String DATE_PREFIX = "[";
    private static final String DATE_SEPERATOR = "] ";
    private static final String SPACE_WITH_OPENING_BRACKET = " (";
    private static final String CLOSING_BRACKED = ")";
    private StringProperty nameProperty;
    private ObjectProperty<LocalDate> startDateProperty;
    private ObjectProperty<LocalDate> endDateProperty;
    private String remainingName;

    public CategoryNameParser(ReadOnlyObjectProperty<Path> directory) {
	nameProperty = new SimpleStringProperty();
	startDateProperty = new SimpleObjectProperty<>();
	endDateProperty = new SimpleObjectProperty<>();

	directory.addListener((ChangeListener<Path>) (observableDirectory,
	    oldDirectoy, newDirectory) -> {
	    parse(newDirectory.getFileName()
		.toString());
	});
	parse(directory.get()
	    .getFileName()
	    .toString());
    }

    public void parse(String name) {
	remainingName = name;
	setStartDate(parseStartDate());
	setEndDateByDays(parseEndDate());
	setName(remainingName);
    }

    private LocalDate parseStartDate() {
	int datePrefixPosition = remainingName.indexOf(DATE_PREFIX);
	int dateSeperatorPosition = remainingName.indexOf(DATE_SEPERATOR);
	if (datePrefixPosition == -1 || dateSeperatorPosition == -1) {
	    return null;
	}
	if (datePrefixPosition != 0) {
	    return null;
	}
	String dateString = remainingName.substring(DATE_PREFIX.length(), dateSeperatorPosition);
	LocalDate startDate = null;
	try {
	    startDate = LocalDate.parse(dateString,
		DateTimeFormatter.ISO_LOCAL_DATE);
	} catch (DateTimeParseException dateTimeParseException) {
	    try {
		Integer.parseInt(dateString);
	    } catch (NumberFormatException numberFormatException) {
		return null;
	    }
	}
	remainingName = remainingName.substring(dateSeperatorPosition
	    + DATE_SEPERATOR.length());
	return startDate;
    }

    private Integer parseEndDate() {
	int openingBracketPosition = remainingName.lastIndexOf(SPACE_WITH_OPENING_BRACKET);
	int closingBracketPosition = remainingName.lastIndexOf(CLOSING_BRACKED);
	if (openingBracketPosition == -1 || closingBracketPosition == -1) {
	    return 1;
	}
	if (closingBracketPosition != getLastLetterPosition(remainingName)) {
	    return 1;
	}
	String daySubstring = remainingName.substring(openingBracketPosition
	    + SPACE_WITH_OPENING_BRACKET.length(),
	    closingBracketPosition);
	Integer days = null;
	if (daySubstring.equals(ONE_DAY)) {
	    days = 1;
	} else if (endsWidth(daySubstring, DAYS_WITH_SPACE)) {
	    daySubstring = daySubstring.substring(0,
		substractStringLength(daySubstring, DAYS_WITH_SPACE));
	    try {
		days = Integer.parseInt(daySubstring);
	    } catch (NumberFormatException e) {
		return 1;
	    }
	} else {
	    return 1;
	}
	remainingName = remainingName.substring(0, openingBracketPosition);
	return days;
    }

    private boolean endsWidth(String string, String find) {
	int index = string.lastIndexOf(find);
	if (index == -1) {
	    return false;
	}
	return index == substractStringLength(string, find);
    }

    private int substractStringLength(String firstString, String secondString) {
	return firstString.length() - secondString.length();
    }

    private int getLastLetterPosition(String name) {
	return name.length() - 1;
    }

    private void setStartDate(LocalDate startDate) {
	startDateProperty.set(startDate);
    }

    private void setEndDate(LocalDate endDate) {
	endDateProperty.set(endDate);
    }

    private void setEndDateByDays(Integer days) {
	if (getStartDate() == null || days == null) {
	    setEndDate(null);
	} else {
	    setEndDate(getStartDate().plusDays(days).minusDays(1));
	}
    }

    private void setName(String remainingName) {
	nameProperty.set(remainingName);
    }

    public LocalDate getStartDate() {
	return startDateProperty().get();
    }

    public LocalDate getEndDate() {
	return endDateProperty.get();
    }

    public String getName() {
	return nameProperty.get();
    }

    public ReadOnlyObjectProperty<LocalDate> startDateProperty() {
	return startDateProperty;
    }

    public ReadOnlyObjectProperty<LocalDate> endDateProperty() {
	return endDateProperty;
    }

    public ReadOnlyStringProperty nameProperty() {
	return nameProperty;
    }

}
