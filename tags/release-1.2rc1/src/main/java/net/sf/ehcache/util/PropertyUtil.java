/**
 *  Copyright 2003-2006 Greg Luck
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Property utilities
 * @author Greg Luck
 * @version $Id$
 */
public final class PropertyUtil {

    private static final Log LOG = LogFactory.getLog(PropertyUtil.class.getName());

    /**
     * Utility class therefore no constructor
     */
    private PropertyUtil() {
        //noop
    }

    /**
     * @return null if their is no property for the key, or their are no properties
     */
    public static String extractAndLogProperty(String name, Properties properties) {
        if (properties == null || properties.size() == 0) {
            return null;
        }
        String foundValue = (String) properties.get(name);
        if (foundValue != null) {
            foundValue = foundValue.trim();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(new StringBuffer().append("Value found for ").append(name).append(": ")
                    .append(foundValue).toString());
        }
        return foundValue;
    }

    /**
     * Parse properties supplied as a comma separated list into a <code>Properties</code> object
     * @param propertiesString a comma separated list such as <code>"propertyA=s, propertyB=t"</code>
     * @return a newly constructed properties object
     */
    public static Properties parseProperties(String propertiesString) {
        if (propertiesString == null) {
            LOG.debug("propertiesString is null.");
            return null;
        }
        Properties properties = new Properties();
        String propertyLines = propertiesString.trim();
        propertyLines = propertiesString.replace(',', '\n');
        try {
            properties.load(new ByteArrayInputStream(propertyLines.getBytes()));
        } catch (IOException e) {
            LOG.error("Cannot load properties from " + propertiesString);
        }
        return properties;
    }
}
