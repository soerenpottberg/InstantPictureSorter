package org.pottberg.ips.bindings;

import static javafx.beans.binding.Bindings.createObjectBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;

public final class Binder {

    @FunctionalInterface
    public interface Getter<Type, Source> {
	public Type getValue(Source observable);
    }

    @FunctionalInterface
    public interface PropertyGetter<PropertyType, Source> {
	public ReadOnlyProperty<PropertyType> readProperty(Source observable);
    }

    public static <Source, Type> ObjectBinding<Type> createBinding(
	ObservableValue<Source> source, Getter<Type, Source> accessor) {
	return createBinding(source, accessor, null);
    }

    public static <Source, Type> ObjectBinding<Type> createBinding(
	ObservableValue<Source> source, Getter<Type, Source> accessor,
	Type defaultValue) {
	return createObjectBinding(() -> {
	    if (source.getValue() == null) {
		return defaultValue;
	    }
	    return accessor.getValue(source.getValue());
	}, source);
    }

    public static <Source, PropertyType> void bindProperty(
	ObservableValue<Source> source, Property<PropertyType> target,
	PropertyGetter<PropertyType, Source> accessor) {
	bindProperty(source, target, accessor, null);
    }

    public static <Source, PropertyType> void bindProperty(
	ObservableValue<Source> source, Property<PropertyType> target,
	PropertyGetter<PropertyType, Source> accessor,
	PropertyType defaultValue) {

	source.addListener((observable, oldValue, newValue) -> {
	    bindTarget(newValue, target, accessor, defaultValue);
	});
	bindTarget(source.getValue(), target, accessor, defaultValue);
    }

    private static <PropertyType, Source> void bindTarget(
	Source source, Property<PropertyType> target,
	PropertyGetter<PropertyType, Source> getProptery,
	PropertyType defaultValue) {
	if (source == null) {
	    target.unbind();
	    target.setValue(defaultValue);
	    return;
	}
	target.bind(getProptery.readProperty(source));
    }

}
