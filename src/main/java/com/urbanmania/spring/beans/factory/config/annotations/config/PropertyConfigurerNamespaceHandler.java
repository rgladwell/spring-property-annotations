package com.urbanmania.spring.beans.factory.config.annotations.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class PropertyConfigurerNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        this.registerBeanDefinitionParser("property-placeholder-annotations", new PropertyPlaceholderAndAnnotationBeanDefinitionParser());
    }

}
