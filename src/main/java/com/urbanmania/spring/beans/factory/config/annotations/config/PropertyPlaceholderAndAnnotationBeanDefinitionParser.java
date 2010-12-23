package com.urbanmania.spring.beans.factory.config.annotations.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.urbanmania.spring.beans.factory.config.annotations.PropertyAnnotationAndPlaceholderConfigurer;

public class PropertyPlaceholderAndAnnotationBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String location = element.getAttribute("location");
        if (StringUtils.hasLength(location)) {
            String[] locations = StringUtils.commaDelimitedListToStringArray(location);
            builder.addPropertyValue("locations", locations);
        }
        String propertiesRef = element.getAttribute("properties-ref");
        if (StringUtils.hasLength(propertiesRef)) {
            builder.addPropertyReference("properties", propertiesRef);
        }
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    }

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return PropertyAnnotationAndPlaceholderConfigurer.class;
    }

}
