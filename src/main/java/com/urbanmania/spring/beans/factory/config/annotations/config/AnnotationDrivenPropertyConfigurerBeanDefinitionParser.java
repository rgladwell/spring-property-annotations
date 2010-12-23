package com.urbanmania.spring.beans.factory.config.annotations.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Element;

import com.urbanmania.spring.beans.factory.config.annotations.PropertyAnnotationAndPlaceholderConfigurer;

public class AnnotationDrivenPropertyConfigurerBeanDefinitionParser implements BeanDefinitionParser {

    private static final Log log = LogFactory.getLog(AnnotationDrivenPropertyConfigurerBeanDefinitionParser.class);
    private static final String PROPERTY_CONFIGURER_BEAN_NAME = null;

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition(PROPERTY_CONFIGURER_BEAN_NAME)) {
            log.info("configuring annotation-driven throttling");
            final Object elementSource = parserContext.extractSource(element);
            final RuntimeBeanReference propertyConfigurerReference = setupPropertyConfigurer(element, parserContext, elementSource);
        }

        return null;
    }

    private RuntimeBeanReference setupPropertyConfigurer(Element element, ParserContext parserContext, Object elementSource) {
        final RootBeanDefinition configurer = new RootBeanDefinition(PropertyAnnotationAndPlaceholderConfigurer.class);
        configurer.setSource(elementSource);
        configurer.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        final XmlReaderContext readerContext = parserContext.getReaderContext();
        final String configurerBeanName = readerContext.registerWithGeneratedName(configurer);
        return new RuntimeBeanReference(configurerBeanName);
    }

}
