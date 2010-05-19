package com.urbanmania.spring.beans.factory.config.annotations;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.ApplicationContext;
 
public class PropertyAnnotationConfigurer extends PropertyResourceConfigurer implements PropertyListener, BeanFactoryAware {

    private static final Logger log = Logger.getLogger(PropertyAnnotationConfigurer.class.getName());

    ConfigurableBeanFactory beanFactory;
    PropertyLoader[] propertyLoaders;
    String basePackage;
    ApplicationContext applicationContext;
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

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    protected void loadProperties(Properties properties) throws IOException {
        super.loadProperties(properties);

        if(propertyLoaders != null) {
            for(PropertyLoader propertyLoader : propertyLoaders) {
                log.info("Loading propertyLoader=[" + propertyLoader + "]");
                Properties loaded = propertyLoader.loadProperties();
                properties.putAll(loaded);
                propertyLoader.registerPropertyListener(this);
            }
        }
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties properties) throws BeansException {
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
        String value = properties.getProperty(annotation.key());

        if (StringUtils.isEmpty(value)) {
            value = annotation.value();
        }

        if (StringUtils.isEmpty(value)) {
            value = annotation.defaultValue();
        }

        if (StringUtils.isEmpty(value)) {
            throw new BeanConfigurationException("No such property=[" + annotation.key() + "] found in properties.");
        }

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
