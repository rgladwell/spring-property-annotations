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

import com.urbanmania.spring.beans.factory.config.annotations.Property;

/**
 * @author Ricardo Gladwell <ricardo.gladwell@gmail.com>
 */
public class ConvertableTestBean {

	int property;

	public int getProperty() {
		return property;
	}

	@Property(key=PropertyAnnotationConfigurerTest.TEST_KEY, update=true)
	public void setProperty(int property) {
		this.property = property;
	}

}
