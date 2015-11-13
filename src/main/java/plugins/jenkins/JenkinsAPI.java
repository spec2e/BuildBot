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
package plugins.jenkins;

import java.net.URL;
import java.net.URI;
import java.net.MalformedURLException;
import java.util.*;
import java.io.InputStreamReader;
import java.io.IOException;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.sun.syndication.io.FeedException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Obtains data from a Jenkins URL
public class JenkinsAPI {

	String JENKINS_URL;
	int PORT;
	public static final String FAIL_API = "rssFailed";
	public static final String LASTEST_API = "rssLatest";
	final Logger logger = LoggerFactory.getLogger(JenkinsAPI.class);

	URL url = null;
	String type;

	public JenkinsAPI(String domain, String type, String jenkins_url, int jenkins_port) {
        JENKINS_URL = jenkins_url;
        PORT = jenkins_port;
		url = getURL(domain, type);
		this.type = type;
	}

	public List<SyndEntryImpl> getFeed() throws IOException {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed; 
		try {
			feed = input.build(new XmlReader(url));
		} catch (IOException ex) {
			throw ex;
		}
		  catch (FeedException ex) {
			logger.error("Exception getting rss", ex);
			return null;
		}
		return feed.getEntries();
	}

	public List<SyndEntryImpl> getLastest(Date afterDate) throws IOException {
		List<SyndEntryImpl> entries = getFeed();
		List<SyndEntryImpl> new_entries = null;
		if (entries != null)
		{
			new_entries = new ArrayList<SyndEntryImpl>();
			for ( SyndEntryImpl entry : entries) {
				if (entry.getPublishedDate().after(afterDate)) 
					new_entries.add(entry);
			}
		}
		return new_entries;
	}

	public Date getMostRecent(List<SyndEntryImpl> entries) {
		Date mostRecent = null;
		assert (entries.size() != 0);
		assert (entries != null);
		for (SyndEntryImpl entry : entries) {
			Date entryDate = entry.getPublishedDate(); 
			if (mostRecent == null || mostRecent.before(entryDate))  
				mostRecent = entryDate;
		}
		return mostRecent;
	}

	private URL getURL(String domain, String type) {
		String urlstr = "/view/" + domain + "/" + type;
		URL url = null;
		URI uri = null;
		try {
			uri = new URI("http", null, JENKINS_URL, PORT, urlstr, null, null);
			url = uri.toURL();
		} catch (URISyntaxException|MalformedURLException ex) {
			logger.error("Exception getURL ", ex);
		}
		return url;
 	}

}
