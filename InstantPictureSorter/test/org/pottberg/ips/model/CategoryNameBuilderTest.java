package org.pottberg.ips.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.junit.Test;

public class CategoryNameBuilderTest {

    @Test
    public void testCategoryNameBuilderName() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderStartDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setStartDate(LocalDate.of(2014, 10, 8));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderAddStartDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setStartDate(LocalDate.of(2014, 10, 8));
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderChangeStartDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setStartDate(LocalDate.of(2014, 10, 8));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setStartDate(LocalDate.of(2014, 10, 9));
	assertEquals("[2014-10-09] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderRemoveStartDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setStartDate(LocalDate.of(2014, 10, 8));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setStartDate(null);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());	
    }
    
    @Test
    public void testCategoryNameBuilderEndDateTwoDays() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setStartDate(LocalDate.of(2014, 10, 8));
	mock.setEndDate(LocalDate.of(2014, 10, 9));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[2014-10-08] Foo (2 Tage)", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderEndDateMultipleDays() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setStartDate(LocalDate.of(2014, 10, 8));
	mock.setEndDate(LocalDate.of(2014, 11, 18));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[2014-10-08] Foo (42 Tage)", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderAddEndDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setStartDate(LocalDate.of(2014, 10, 8));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setEndDate(LocalDate.of(2014, 10, 9));
	assertEquals("[2014-10-08] Foo (2 Tage)", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderChangeEndDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setStartDate(LocalDate.of(2014, 10, 8));
	mock.setEndDate(LocalDate.of(2014, 10, 9));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[2014-10-08] Foo (2 Tage)", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setEndDate(LocalDate.of(2014, 11, 18));
	assertEquals("[2014-10-08] Foo (42 Tage)", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderRemoveEndDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setStartDate(LocalDate.of(2014, 10, 8));
	mock.setEndDate(LocalDate.of(2014, 10, 9));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[2014-10-08] Foo (2 Tage)", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setEndDate(null);
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[2014] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderUserDefinedStartDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 8));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderAddUserDefinedStartDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 8));
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderChangeUserDefinedStartDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 8));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 9));
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-09] Foo", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderRemoveUserDefinedStartDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 8));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setUserDefinedStartDate(null);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("Foo", categoryNameBuilder.getUserDefinedName());	
    }
    
    @Test
    public void testCategoryNameBuilderUserDefinedEndDateTwoDays() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 8));
	mock.setUserDefinedEndDate(LocalDate.of(2014, 10, 9));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo (2 Tage)", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderUserDefinedEndDateMultipleDays() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 8));
	mock.setUserDefinedEndDate(LocalDate.of(2014, 11, 18));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo (42 Tage)", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderAddUserDefinedEndDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 8));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getUserDefinedName());
	
	mock.setUserDefinedEndDate(LocalDate.of(2014, 10, 9));
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo (2 Tage)", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderChangeUserDefinedEndDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 8));
	mock.setUserDefinedEndDate(LocalDate.of(2014, 10, 9));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo (2 Tage)", categoryNameBuilder.getUserDefinedName());
	
	mock.setUserDefinedEndDate(LocalDate.of(2014, 11, 18));
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo (42 Tage)", categoryNameBuilder.getUserDefinedName());
    }
    
    @Test
    public void testCategoryNameBuilderRemoveUserDefinedEndDate() {
	MockCategory mock = new MockCategory();
	mock.setName("Foo");
	mock.setUserDefinedStartDate(LocalDate.of(2014, 10, 8));
	mock.setUserDefinedEndDate(LocalDate.of(2014, 10, 9));
	CategoryNameBuilder categoryNameBuilder = createCategoryBuilder(mock);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo (2 Tage)", categoryNameBuilder.getUserDefinedName());
	
	mock.setUserDefinedEndDate(null);
	assertEquals("[yyyy-mm-dd] Foo", categoryNameBuilder.getSuggestedName());
	assertEquals("[yyyy] Foo", categoryNameBuilder.getFullYearName());
	assertEquals("[2014-10-08] Foo", categoryNameBuilder.getUserDefinedName());
    }

    private CategoryNameBuilder createCategoryBuilder(MockCategory mock) {
	ObjectProperty<Category> categoryProperty = new SimpleObjectProperty<>(mock);
	CategoryNameBuilder categoryNameBuilder = new CategoryNameBuilder(categoryProperty);
	return categoryNameBuilder;
    }

}
