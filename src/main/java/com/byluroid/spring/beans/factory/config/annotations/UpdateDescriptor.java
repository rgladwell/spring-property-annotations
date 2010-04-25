package com.byluroid.spring.beans.factory.config.annotations;


import java.beans.PropertyDescriptor;

public class UpdateDescriptor {

	PropertyDescriptor propertyDescriptor;
	Class<?> beanClass;

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

}
