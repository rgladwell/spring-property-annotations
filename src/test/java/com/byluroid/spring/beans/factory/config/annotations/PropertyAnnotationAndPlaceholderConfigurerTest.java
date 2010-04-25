package com.byluroid.spring.beans.factory.config.annotations;

import static junit.framework.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;

public class PropertyAnnotationAndPlaceholderConfigurerTest {

	public static final String TEST_BEAN_NAME = "testBean";
	public static final String TEST_KEY = "testKey";
	public static final String TEST_VALUE = "testValue";
	public static final String TEST_DEFAULT_VALUE = "testDefaultValue";
	public static final String TEST_CHANGED_VALUE = "testChangedValue";

	PropertyAnnotationAndPlaceholderConfigurer configurer;
	DefaultListableBeanFactory beanFactory;
	Properties properties;

	@Before
	public void setUp() {
		configurer = new PropertyAnnotationAndPlaceholderConfigurer();
		beanFactory = new DefaultListableBeanFactory();
		properties = new Properties();
	}

	@Test
	public void testEmptyProcessProperties() {
		configurer.processProperties(beanFactory, properties);
	}

	@Test
	public void testProcessProperties() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(SimplePropetyAnnotatedBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		properties.put(TEST_KEY, TEST_VALUE);

		configurer.processProperties(beanFactory, properties);

		assertNotNull(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property"));
		assertEquals(TEST_VALUE, beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property").getValue());
	}

	@Test
	public void testProcessPropertiesWithDefaultValue() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(DefaultValueTestBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		configurer.processProperties(beanFactory, properties);

		assertNotNull(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property"));
		assertEquals(TEST_DEFAULT_VALUE, beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property").getValue());
	}

	@Test
	public void testProcessPropertiesWithFieldAnnotation() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(AnnotatedFieldTestBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		properties.put(TEST_KEY, TEST_VALUE);

		configurer.processProperties(beanFactory, properties);

		assertNotNull(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property"));
		assertEquals(TEST_VALUE, beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property").getValue());
	}

	@Test
	public void testProcessPropertiesWithDefaultValueWithFieldAnnotation() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(AnnotatedFieldTestBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		configurer.processProperties(beanFactory, properties);

		assertNotNull(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property"));
		assertEquals(TEST_DEFAULT_VALUE, beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property").getValue());
	}

	@Test
	public void testProcessPropertiesWithinBasePackage() {
		configurer.setBasePackage("com.byluroid");
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(SimplePropetyAnnotatedBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		properties.put(TEST_KEY, TEST_VALUE);

		configurer.processProperties(beanFactory, properties);

		assertNotNull(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property"));
		assertEquals(TEST_VALUE, beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property").getValue());
	}

	@Test
	public void testProcessPropertiesWithoutBasePackage() {
		configurer.setBasePackage("com.example");
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(SimplePropetyAnnotatedBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		properties.put(TEST_KEY, TEST_VALUE);

		configurer.processProperties(beanFactory, properties);

		assertTrue(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().isEmpty());
	}
	
	@Test
	public void testProcessPropertiesWithAnnotatedGetter() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(AnnotatedGetterTestBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		properties.put(TEST_KEY, TEST_VALUE);

		configurer.processProperties(beanFactory, properties);

		assertNotNull(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property"));
		assertEquals(TEST_VALUE, beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property").getValue());
	}	
	
	@Test
	public void testProcessPropertiesWithAnnotatedFieldNoSetter() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(NoSetterTestBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		properties.put(TEST_KEY, TEST_VALUE);

		try {
			configurer.processProperties(beanFactory, properties);
		} catch(BeanConfigurationException e) {
			return;
		}

		fail("Should throw BeanConfigurationException on no property setter.");
	}
	
	@Test
	public void testProcessPropertiesWithAnnotatedGetterAndNoSetter() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(AnnotatedGetterWithNoSetterTestBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		properties.put(TEST_KEY, TEST_VALUE);

		try {
			configurer.processProperties(beanFactory, properties);
		} catch(BeanConfigurationException e) {
			return;
		}

		fail("Should throw BeanConfigurationException on no property setter.");
	}

	@Test
	public void testProcessPropertiesWithoutDefaultValue() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(SimplePropetyAnnotatedBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		try {
			configurer.processProperties(beanFactory, properties);
		} catch(BeanConfigurationException e) {
			return;
		}

		fail("Should throw BeanConfigurationException on empty property value.");
	}

	@Test
	public void testProcessPropertiesAndUpdate() throws Exception {
		configurer.setPropertyLoaders(new MockPropertyLoader[] { new MockPropertyLoader(properties) });
		
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(UpdateableTestBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		properties.put(TEST_KEY, TEST_VALUE);

		configurer.loadProperties(properties);
		configurer.processProperties(beanFactory, properties);
		
		StaticApplicationContext context = new StaticApplicationContext();
		context.registerSingleton(TEST_BEAN_NAME, UpdateableTestBean.class);
		configurer.setApplicationContext(context);

		configurer.setBeanFactory(beanFactory);
		configurer.propertyChanged(new PropertyEvent(this, TEST_KEY, TEST_CHANGED_VALUE));

		assertEquals(TEST_CHANGED_VALUE, ((UpdateableTestBean) context.getBean(TEST_BEAN_NAME)).getProperty());
	}

	@Test
	public void testProcessPropertiesConvertAndUpdate() throws Exception {
		configurer.setPropertyLoaders(new MockPropertyLoader[] { new MockPropertyLoader(properties) });
		
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(ConvertableTestBean.class);
		beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

		properties.put(TEST_KEY, "1");

		configurer.loadProperties(properties);
		configurer.processProperties(beanFactory, properties);
		
		StaticApplicationContext context = new StaticApplicationContext();
		context.registerSingleton(TEST_BEAN_NAME, ConvertableTestBean.class);
		configurer.setApplicationContext(context);

		configurer.setBeanFactory(beanFactory);
		configurer.propertyChanged(new PropertyEvent(this, TEST_KEY, "2"));

		assertEquals(2, ((ConvertableTestBean) context.getBean(TEST_BEAN_NAME)).getProperty());
	}


	@Test
	public void testProcessPropertiesWithoutPropertyLoaders() throws Exception {
		try {
			configurer.loadProperties(properties);
		} catch(NullPointerException e) {
			fail("loadProperties failing on null property loaders.");
		}
	}
}
