/**
 * Copyright 2010 Ricardo Gladwell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.urbanmania.spring.beans.factory.config.annotations;

import static junit.framework.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;

/**
 * @author Ricardo Gladwell <ricardo.gladwell@gmail.com>
 */
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
	public void testProcessPropertiesWithValue() {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(ValueTestBean.class);
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
		configurer.setBasePackage("com.urbanmania");
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
        assertNull(System.getProperties().get(TEST_KEY));
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
		assertNull(System.getProperties().get(TEST_KEY));
	}

	@Test
	public void testProcessPropertiesWithoutPropertyLoaders() throws Exception {
		try {
			configurer.loadProperties(properties);
		} catch(NullPointerException e) {
			fail("loadProperties failing on null property loaders.");
		}
	}

    @Test
    public void testProcessPropertiesWithAnnotatedFieldOnly() {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(FieldOnlyTestBean.class);
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
    public void testProcessPropertiesWithPlaceholderSubstitution() {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(PlaceholderValueTestBean.class);
        beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);
        properties.put(TEST_KEY, TEST_VALUE);

        configurer.processProperties(beanFactory, properties);

        assertNotNull(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property"));
        assertEquals("testValue-testValue", beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property").getValue());
    }

    @Test
    public void testProcessPropertiesWithEmptyStringProperty() {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(SimplePropetyAnnotatedBean.class);
        beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

        properties.put(TEST_KEY, "");

        configurer.processProperties(beanFactory, properties);

        assertNotNull(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property"));
        assertEquals("", beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property").getValue());
    }

    @Test
    public void testProcessPropertiesWithEmptyStringValue() throws Exception {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(EmptyStringValueTestBean.class);
        beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

        configurer.processProperties(beanFactory, properties);

        assertNotNull(beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property"));
        assertEquals("", beanFactory.getBeanDefinition(TEST_BEAN_NAME).getPropertyValues().getPropertyValue("property").getValue());
    }

    @Test
    public void testProcessPropertiesWithEmptyStringValueForNonStringValue() throws Exception {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(EmptyStringValueForNonStringPropertyTestBean.class);
        beanFactory.registerBeanDefinition(TEST_BEAN_NAME, beanDefinition);

        configurer.processProperties(beanFactory, properties);
        
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerSingleton(TEST_BEAN_NAME, ConvertableTestBean.class);
        configurer.setApplicationContext(context);
        configurer.setBeanFactory(beanFactory);
        configurer.propertyChanged(new PropertyEvent(this, TEST_KEY, "2"));

        try {
            context.getBean(TEST_BEAN_NAME);
        } catch(ClassCastException e) {
            return;
        }
        
        fail("should have thrown ClassCastException converting empty string to integer value.");
    }

}
