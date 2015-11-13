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

import callbacks.MessageCallback;
import BuildBot.MessageAdapter;
import java.io.IOException;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Jenkins implements MessageCallback {

	//JenkinsAPI jenapi = new JenkinsAPI();	
	final Logger logger = LoggerFactory.getLogger(Jenkins.class);

	public String getCommand() {
		return "jenkins";
	}

	HashMap jobs = new HashMap();

    String jenkins_url;
    int jenkins_port;

    public Jenkins(String j_url, int port) {
        jenkins_url = j_url;
        jenkins_port = port;
    }

	private void dispatchThread(String jobType, MessageAdapter msg, String domain) {
		logger.debug("dispatch thread on domain: " + domain);
		System.out.println("dispatch thread on domain: " + domain);
		JenkinsAPI jenpi = new JenkinsAPI(domain, jobType, jenkins_url, jenkins_port);
		JenkinsThread jthread;
		jthread = new JenkinsThread(jenpi, msg);
		String name = String.format("On domain %s, type %s", domain, jobType.substring(3));
		addThread(name, msg.getId(), jthread);
	}

	private void addThread(String name, String id, Runnable jenkinsRun) {
		List jlist = (List)jobs.get(id);
		if (jlist == null) {
			jlist = new ArrayList<Thread>();
			jobs.put(id, jlist);
		}
		Thread thread = new Thread(jenkinsRun);
		thread.setName(name);
		thread.start();
		jlist.add(thread);
	}


	private void dispatchJobThread(String url, MessageAdapter msg) {
		// Dispatch a thread to monitor a single job
		// This thread finishes when the job ends
		URL u;
		JenkinsJobThread jthread;
		try {
			u = new URL(url + "api/json");
			jthread = new JenkinsJobThread(u, msg);
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException();
		}
		addThread(url, msg.getId(), jthread);
	}
	

	private String showJobs(String idChat) {
		StringBuilder name_jobs = new StringBuilder(); 
		List jlist = (List)jobs.get(idChat);
		String message;
		if (jlist == null || jlist.size() == 0)
			message = "No jobs avaliable";
		else {
			int i = 0;
			for (Iterator<Thread> iterator = jlist.iterator(); iterator.hasNext();) {
				Thread thread = iterator.next();
				if (thread.getState() == Thread.State.TERMINATED) {
					iterator.remove();
					continue;
				}
				String line = String.format("%d-) %s \n", ++i, thread.getName());
				name_jobs.append(line);
			}
			message = name_jobs.toString();
		}
		return message;
	}

	private String stopJob(String idChat, int jobId) {
		List jlist = (List)jobs.get(idChat);
		String message;
		if (jlist == null || jlist.get(jobId - 1) == null)
			message = String.format("No such job %d", jobId);
		else {
			Thread job = (Thread)jlist.get(jobId - 1);
			jlist.remove(jobId - 1);
			message = String.format("The job %d has stopped", jobId);
			try {	
				job.interrupt();
				job.join();
			} catch (Exception ex) {
				logger.error("Error stoping thread: " + ex.getMessage(), ex);
			}
		}
		return message;
	}

	public void textReceived(MessageAdapter msg, String payload) {
		String[] args = payload.split(" ");
		if (payload.trim().isEmpty()) {
			// If no arguments
			msg.sendText(showJobs(msg.getId()));
		}
		else {
			try {
				switch (args[0]) {
					 case "latest":
						// Dispatch thread to watch lastest builds on domain
						msg.sendText("Starting job...");
						dispatchThread(JenkinsAPI.LASTEST_API, msg, payload.substring(args[0].length() + 1 ));
						break;
					 case "failures":
						// Dispatch thread to watch lastest failed builds on domain
						msg.sendText("Starting job...");
						dispatchThread(JenkinsAPI.FAIL_API, msg, payload.substring(args[0].length() + 1 ));
						break;
					 case "watch":
						msg.sendText("Starting job...");
						dispatchJobThread(args[1], msg);
						break;
					 case "stop":
						int jobId = 0;
						jobId = Integer.parseInt(args[1]);
						msg.sendText(stopJob(msg.getId(), jobId));
						break;
					 default:	
						throw new IllegalArgumentException();
				}
			} catch (ArrayIndexOutOfBoundsException| NumberFormatException ex) {
				// We catch this when we expect an arg but get nothing instead
				throw new IllegalArgumentException();
			}
		}
	}


}

