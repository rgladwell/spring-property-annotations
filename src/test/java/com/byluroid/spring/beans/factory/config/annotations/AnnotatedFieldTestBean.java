package com.byluroid.spring.beans.factory.config.annotations;

public class AnnotatedFieldTestBean {

	@Property(key=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_KEY, defaultValue=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_DEFAULT_VALUE)
	String property;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
