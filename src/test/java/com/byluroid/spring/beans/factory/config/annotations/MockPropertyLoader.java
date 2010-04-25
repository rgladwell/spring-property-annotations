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

package com.byluroid.spring.beans.factory.config.annotations;

import java.util.Properties;

/**
 * @author Ricardo Gladwell <ricardo.gladwell@gmail.com>
 */
public class MockPropertyLoader implements PropertyLoader {

	Properties properties;
	PropertyListener listener;

	public MockPropertyLoader(Properties properties) {
		this.properties = properties;
	}

	public Properties loadProperties() {
		return properties;
	}

	public void registerPropertyListener(PropertyListener listener) {
		this.listener = listener;
	}

}
