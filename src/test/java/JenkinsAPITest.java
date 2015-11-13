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

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.mockito.stubbing.Answer;

import plugins.jenkins.JenkinsAPI;
import java.util.*;
import java.io.IOException;

import com.sun.syndication.feed.synd.*;


public class JenkinsAPITest extends junit.framework.TestCase{
	
	// Disabled
	//public void testGetEntries() {
	public void GetEntries() {
		//String domain = "Build DEV4";
		String domain = "All";
		JenkinsAPI jea = new JenkinsAPI(domain, JenkinsAPI.LASTEST_API);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1984);
		Date initial_d = cal.getTime();
		List entries = null;
		try {
			entries = jea.getLastest(initial_d);
			assertTrue("No entries recieved", entries.size() != 0);
			Date new_d = jea.getMostRecent(entries);
			entries = jea.getLastest(new_d);
			assertTrue("There can't be new entries after we check for second time",
						 entries.size() == 0);
		} catch (IOException ex) {
			assertTrue("Exception when getting rss entries: " + ex.getMessage(), false);
			return;
		}
		assertTrue("Error getting entries", entries != null);
	}


}
