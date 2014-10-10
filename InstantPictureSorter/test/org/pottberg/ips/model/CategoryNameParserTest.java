package org.pottberg.ips.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.junit.Test;

public class CategoryNameParserTest {

    @Test
    public void testCategoryNameParserName() {
	CategoryNameParser categoryNameParser = createCategoryParser("Foo");
	assertNull(categoryNameParser.getStartDate());
	assertNull(categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserPath() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/Foo");
	assertNull(categoryNameParser.getStartDate());
	assertNull(categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserStartDate() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/[2014-10-08] Foo");
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getStartDate());
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserInvalidStartDate() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/[9999-99-99] Foo");
	assertNull(categoryNameParser.getStartDate());
	assertNull(categoryNameParser.getEndDate());
	assertEquals("[9999-99-99] Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserInvalidStartDateSyntax() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/[2014.10.08] Foo");
	assertNull(categoryNameParser.getStartDate());
	assertNull(categoryNameParser.getEndDate());
	assertEquals("[2014.10.08] Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserInvalidStartDateSeperator() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/2014-10-08 ~ Foo");
	assertNull(categoryNameParser.getStartDate());
	assertNull(categoryNameParser.getEndDate());
	assertEquals("2014-10-08 ~ Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserYearOnly() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/[2014] Foo");
	assertNull(categoryNameParser.getStartDate());
	assertNull(categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserEndDateOneDay() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/[2014-10-08] Foo (1 Tag)");
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getStartDate());
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserEndDateMultipleDays() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/[2014-10-08] Foo (42 Tage)");
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getStartDate());
	assertEquals(LocalDate.of(2014, 11, 18), categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserEndDateWithOutStartDate() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/Foo (42 Tage)");
	assertNull(categoryNameParser.getStartDate());
	assertNull(categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserInvalidEndDate() {
	CategoryNameParser categoryNameParser = createCategoryParser("Bar/[2014-10-08] Foo (42 Bar)");
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getStartDate());
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getEndDate());
	assertEquals("Foo (42 Bar)", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserPathRemoveStartDate() {
	
	ObjectProperty<Path> pathProperty = createPathProperty("Bar/[2014-10-08] Foo");
	CategoryNameParser categoryNameParser = createCategoryParser(pathProperty);
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getStartDate());
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
	
	pathProperty.set(Paths.get("Bar/Foo (42 Tage)"));
	assertNull(categoryNameParser.getStartDate());
	assertNull(categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
    }
    
    @Test
    public void testCategoryNameParserPathRemoveEndDate() {
	
	ObjectProperty<Path> pathProperty = createPathProperty("Bar/[2014-10-08] Foo (42 Tage)");
	CategoryNameParser categoryNameParser = createCategoryParser(pathProperty);
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getStartDate());
	assertEquals(LocalDate.of(2014, 11, 18), categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
	
	pathProperty.set(Paths.get("Bar/[2014-10-08] Foo"));
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getStartDate());
	assertEquals(LocalDate.of(2014, 10, 8), categoryNameParser.getEndDate());
	assertEquals("Foo", categoryNameParser.getName());
    }

    private CategoryNameParser createCategoryParser(String directory) {
	ObjectProperty<Path> pathProperty = createPathProperty(directory);
	return createCategoryParser(pathProperty);
    }

    private CategoryNameParser createCategoryParser(
	ObjectProperty<Path> pathProperty) {
	return new CategoryNameParser(pathProperty);
    }

    private SimpleObjectProperty<Path> createPathProperty(String directory) {
	return new SimpleObjectProperty<>(Paths.get(directory));
    }

}
