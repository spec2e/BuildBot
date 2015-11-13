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

import java.util.*;
import com.sun.syndication.feed.synd.*;
import BuildBot.MessageAdapter;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JenkinsThread implements Runnable
{

	Date lastUpdate;
	JenkinsAPI jenpi;
	MessageAdapter msg;
	final short TIMEOUT = 3;
	final Logger logger = LoggerFactory.getLogger(JenkinsThread.class);

	public JenkinsThread(JenkinsAPI jenpi, MessageAdapter msg) {
		this.jenpi = jenpi;
		this.msg = msg;
	}
	
	public Date getLastUpdate() {
		assert (lastUpdate != null);
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<SyndEntryImpl> getFeed() throws IOException {
		List<SyndEntryImpl> entries = null;
		// Retry 3 times before timeout
		int i = 0;
		while (true) {
			try {
				Date lastUpdate = getLastUpdate();
				if (lastUpdate == null) {
					entries = jenpi.getFeed();
				}
				else
				{
					entries = jenpi.getLastest(lastUpdate);
				}
				break;
			} catch (FileNotFoundException ex) {
				msg.sendText("The remote resource could not be found. Please check the domain.");
				throw (IOException)ex; 
			} catch (IOException ex) {
				// Happens when we get a general IO error
				if (i >= TIMEOUT) {
					// TODO: Improve error message
					msg.sendText("Error getting information from jenkin server on domain");
					throw ex; 
				}
				i++;
				try {
					// Wait before making the next call after IOException
					// Do this only 3 times
					Thread.sleep(5000);
				} catch (InterruptedException e) { break; }
			}
		}
		return entries;
	}

	public void run() {
		List<SyndEntryImpl> entries = null;
		// First run get the feed to get last update and 
		// check if it is working
		try {
			entries = getFeed();
			if (entries.size() == 0) {
				long currentTime = System.currentTimeMillis();
				setLastUpdate(new Date(currentTime));
			}
			else
				setLastUpdate(jenpi.getMostRecent(entries));
		}
		catch (IOException ex) {
			msg.sendText("Error starting job");
			return;
		}
		msg.sendText("job started");
		while (true) {
			try {
				entries = getFeed(); // Throws IOException
				for (SyndEntryImpl entry : entries) {
					msg.sendText(entry.getTitle() + "\n" + entry.getLink());
				}
				if (entries.size() > 0)
					setLastUpdate(jenpi.getMostRecent(entries));
				Thread.sleep(120000); // Throws InterruptedException
			} catch (IOException ex) {
				logger.error("IOException getting rss entries: " + ex.getMessage(), ex);
				break;
			} catch (InterruptedException ex) {
				break;
			}
		}
	}

}
