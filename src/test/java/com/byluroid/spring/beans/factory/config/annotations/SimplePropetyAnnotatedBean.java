package com.byluroid.spring.beans.factory.config.annotations;

public class SimplePropetyAnnotatedBean {

	String property;

	public String getProperty() {
		return property;
	}

	@Property(key=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_KEY)
	public void setProperty(String property) {
		this.property = property;
	}

}
