package com.urbanmania.spring.beans.factory.config.annotations;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class PropertyAnnotatedFactoryBean implements FactoryBean {

    @Property(key = PropertyAnnotationAndPlaceholderConfigurerTest.TEST_KEY)
    String property;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getObject() throws Exception {
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Class getObjectType() {
        return null;
    }

    public boolean isSingleton() {
        return true;
    }

}
