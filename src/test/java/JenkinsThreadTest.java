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

import plugins.jenkins.JenkinsThread;
import plugins.jenkins.JenkinsAPI;
import java.util.*;
import java.io.IOException;
import BuildBot.MessageAdapter;
import com.sun.syndication.feed.synd.*;

public class JenkinsThreadTest extends junit.framework.TestCase{

	public void testGetFeed() {
		MessageAdapter msg = mock(MessageAdapter.class);
		JenkinsThread jthread = null;
		try {
			JenkinsAPI jenpi = mockJenkinsAPI();
			jthread = new JenkinsThread(jenpi, msg);
		} catch (Exception ex) { 
			System.out.println(ex.getMessage());
			assertTrue("Unexpected exception", false);
		}
		assertTrue("lastUpdate cannot be null", jthread.getLastUpdate() != null);
		try {
			jthread.getFeed();
		} catch (Exception ex) {}
		assertTrue("lastUpdate cannot be null", jthread.getLastUpdate() != null);
	}

	private JenkinsAPI mockJenkinsAPI() throws IOException {
		JenkinsAPI jenpi = mock(JenkinsAPI.class);
		Date mostRecent = new Date(1438003900);
		when(jenpi.getMostRecent(any(List.class))).thenReturn(mostRecent);
		List list = new ArrayList<SyndEntryImpl>();
		when(jenpi.getFeed()).thenReturn(list);
		when(jenpi.getLastest(any(Date.class))).thenReturn(list);
		return jenpi;
	}


}
