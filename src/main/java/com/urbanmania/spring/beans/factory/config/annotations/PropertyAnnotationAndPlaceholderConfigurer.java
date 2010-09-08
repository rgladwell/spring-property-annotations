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

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Ricardo Gladwell <ricardo.gladwell@gmail.com>
 * @see com.urbanmania.spring.beans.factory.config.annotations.PropertyAnnotationConfigurer
 */
public class PropertyAnnotationAndPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements PropertyListener, ApplicationContextAware, BeanFactoryAware {

	private static final Logger log = Logger.getLogger(PropertyAnnotationAndPlaceholderConfigurer.class.getName());

    PropertyLoader[] propertyLoaders;
    String basePackage;
    ApplicationContext applicationContext;
    ConfigurableBeanFactory beanFactory;
    Map<String, List<UpdateDescriptor>> updatableProperties = new Hashtable<String, List<UpdateDescriptor>>();

    public void setPropertyLoaders(PropertyLoader[] propertyLoaders) {
		this.propertyLoaders = propertyLoaders;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);
		this.beanFactory = (ConfigurableBeanFactory) beanFactory;
	}

	@Override
	protected void loadProperties(Properties props) throws IOException {
		super.loadProperties(props);

		if(propertyLoaders != null) {
			for(PropertyLoader propertyLoader : propertyLoaders) {
		        log.info("Loading propertyLoader=[" + propertyLoader + "]");
		        Properties loaded = propertyLoader.loadProperties();
				props.putAll(loaded);
				propertyLoader.registerPropertyListener(this);
			}
		}
	}

	@Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties properties) throws BeansException {
        super.processProperties(beanFactory, properties);

        for (String name : beanFactory.getBeanDefinitionNames()) {
            MutablePropertyValues mpv = beanFactory.getBeanDefinition(name).getPropertyValues();
            Class<?> clazz = beanFactory.getType(name);

            // TODO support proxies
            if (clazz != null && clazz.getPackage() != null) {
            	if(basePackage != null && !clazz.getPackage().getName().startsWith(basePackage)) {
        			continue;
            	}
                
                log.info("Configuring properties for bean=" + name + "[" + clazz + "]");

                for (PropertyDescriptor property : BeanUtils.getPropertyDescriptors(clazz)) {
                    if (log.isLoggable(Level.FINE)) log.fine("examining property=[" + clazz.getName() + "." + property.getName() + "]");
                    Method setter = property.getWriteMethod();
                    Method getter = property.getReadMethod();
                    Property annotation = null;
                    if (setter != null && setter.isAnnotationPresent(Property.class)) {
                        annotation = setter.getAnnotation(Property.class);
                    }
                    else if (setter != null && getter != null && getter.isAnnotationPresent(Property.class)) {
                        annotation = getter.getAnnotation(Property.class);
                    }
                    else if(setter == null && getter != null && getter.isAnnotationPresent(Property.class)) {
                    	throwBeanConfigurationException(clazz, property.getName());
                    }
                    if (annotation != null) {
                        setProperty(properties, name, mpv, clazz, property, annotation);
                    }
                }

                for (Field field : clazz.getDeclaredFields()) {
                    if (log.isLoggable(Level.FINE)) log.fine("examining field=[" + clazz.getName() + "." + field.getName() + "]");
                    if (field.isAnnotationPresent(Property.class)) {
                        Property annotation = field.getAnnotation(Property.class);
                        PropertyDescriptor property = BeanUtils.getPropertyDescriptor(clazz, field.getName());

                        if (property == null || property.getWriteMethod() == null) {
                        	throwBeanConfigurationException(clazz, field.getName());
                        }

                        setProperty(properties, name, mpv, clazz, property, annotation);
                    }
                }
            }
        }
    }

	private void throwBeanConfigurationException(Class<?> clazz, String name) {
		throw new BeanConfigurationException("setter for property=[" + clazz.getName() + "." + name + "] not available.");
	}

	private void setProperty(Properties properties, String name, MutablePropertyValues mpv, Class<?> clazz, PropertyDescriptor property, Property annotation) {
		String value = resolvePlaceholder(annotation.key(), properties, SYSTEM_PROPERTIES_MODE_FALLBACK);

		if (value == null) {
		    value = annotation.value();
		}

		value = parseStringValue(value, properties, new HashSet<String>());
		
		log.info("setting property=[" + clazz.getName() + "." + property.getName() + "] value=[" + annotation.key() + "=" + value + "]");

		mpv.addPropertyValue(property.getName(), value);
		
		if(annotation.update()) {
			registerBeanPropertyForUpdate(clazz, annotation, property);
		}
	}

	public void registerBeanPropertyForUpdate(Class<?> beanClass, Property annotation, PropertyDescriptor property) {
		log.info("watching updates for property=["+property.getPropertyType().getName()+"."+property.getName()+"] key=["+annotation.key()+"]");		
		List<UpdateDescriptor> properties = updatableProperties.get(annotation.key());

		if(properties == null) {
			properties = new ArrayList<UpdateDescriptor>();
		}

		UpdateDescriptor descriptor = new UpdateDescriptor();
		descriptor.setPropertyDescriptor(property);
		descriptor.setBeanClass(beanClass);
		properties.add(descriptor);
		updatableProperties.put(annotation.key(), properties);
	}

	public void propertyChanged(PropertyEvent event) {
		if(updatableProperties.get(event.getKey()) != null) {
			for(UpdateDescriptor update : updatableProperties.get(event.getKey())) {
				for(Object bean : applicationContext.getBeansOfType(update.getBeanClass()).values()) {
					log.info("updating property=["+bean.getClass().getName()+"."+update.getPropertyDescriptor().getName()+"] value=["+event.getValue()+"]");	
					try {
						update.getPropertyDescriptor().getWriteMethod().invoke(bean, this.beanFactory.getTypeConverter().convertIfNecessary(event.getValue(), update.getPropertyDescriptor().getPropertyType()));
					} catch (IllegalArgumentException e) {
						throw new BeanConfigurationException("Error updating "+bean.getClass().getName()+"."+update.getPropertyDescriptor().getName()+"] value=["+event.getValue()+"]", e);
					} catch (IllegalAccessException e) {
						throw new BeanConfigurationException("Error updating "+bean.getClass().getName()+"."+update.getPropertyDescriptor().getName()+"] value=["+event.getValue()+"]", e);
					} catch (InvocationTargetException e) {
						throw new BeanConfigurationException("Error updating "+bean.getClass().getName()+"."+update.getPropertyDescriptor().getName()+"] value=["+event.getValue()+"]", e);
					}
				}
			}
		}
	}

}
 