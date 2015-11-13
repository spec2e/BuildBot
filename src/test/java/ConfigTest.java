/*
  Copyright (C) 2015  Jefry Lagrange

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package test.java;

import static org.junit.Assert.*;
import java.util.*;
import java.io.*;

public class ConfigTest extends junit.framework.TestCase {

    // Tests if the config file is properly set up in the classpath
    // and with the expected options
    public void testConfig() throws Exception {
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        assertNotNull("Can't open config file", inputStream);
        prop.load(inputStream);
        String []keys = { "st_user", "st_password", "st_server" };

        for (String key : keys) {
            String val = prop.getProperty(key);
            String err = key + ": value not set";
            assertNotNull(err, val);
            assertTrue(err, val.length() > 0);
        }

    }

}
