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

import plugins.jenkins.Jenkins;

import org.mockito.*;
import BuildBot.MessageAdapter;

public class JenkinsTest extends junit.framework.TestCase{


	public void testCreateJob() {
		Jenkins jenkins = new Jenkins();
		String payload = "latest Build DEV4";
		MessageAdapter msg = mock(MessageAdapter.class);
		jenkins.textReceived(msg, payload);

		InOrder inOrder = inOrder(msg);

		inOrder.verify(msg).sendText("Starting job...");
		inOrder.verify(msg).sendText("job started");
	}

	
}

