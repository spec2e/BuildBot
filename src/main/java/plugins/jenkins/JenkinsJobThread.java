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

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import BuildBot.MessageAdapter;
import java.io.*;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JenkinsJobThread implements Runnable {

	MessageAdapter msg;
	URL jobUrl;
	String name;
	final short TIMEOUT = 3;
	final Logger logger = LoggerFactory.getLogger(JenkinsJobThread.class);

	public JenkinsJobThread(URL jobUrl, MessageAdapter msg) {
		this.jobUrl = jobUrl;
		this.msg = msg;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public JsonObject getData() throws IOException {
		// Retry 3 times before timeout
		int i = 0;
		while (true) {
			BufferedReader buffer;
			try {
				buffer = new BufferedReader(new InputStreamReader(jobUrl.openStream()));	
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
					continue;
				} catch (InterruptedException e) { break; }
			}
			JsonReader jreader = new JsonReader(buffer);
			JsonObject jsonObject = new JsonParser().parse(jreader).getAsJsonObject();
			return jsonObject;
		}
		return null;
	}

	public void run() {
		// Executes first 
		JsonObject jo = null;
		try {
			jo = getData();
		} catch (IOException ex) {
			msg.sendText("Error starting job");
			return;
		}
		assert(jo != null);
		//setName(jo.get("fullDisplayName").getAsString());
		msg.sendText("job started");
		// 
		JsonObject jsonObject;
		while (true) {
			try {
				jsonObject = getData(); // throws IOException
				boolean building = jsonObject.get("building").getAsBoolean();
				if (!building) {
					String status = jsonObject.get("result").getAsString();
					msg.sendText(
						String.format("%s job has finished with status: %s", getName(), status)
						);
					break;
				}
				Thread.sleep(120000); // Throws InterruptedException
			} catch (IOException ex) {
				logger.error("IOException getting json info about job: " + ex.getMessage(), ex);
				break;
			} catch (InterruptedException ex) {
				break;
			}
		}
	}

}


