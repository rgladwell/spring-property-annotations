package com.byluroid.spring.beans.factory.config.annotations;

public class AnnotatedGetterTestBean {

	String property;

	@Property(key=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_KEY, defaultValue=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_DEFAULT_VALUE)
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
