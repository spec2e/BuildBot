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

import plugins.Groups;
import com.lotus.sametime.chat.invitation.*;
import com.lotus.sametime.places.Place;
import com.lotus.sametime.im.*;
import com.lotus.sametime.chat.MeetingInfo;
import java.util.*;
import BuildBot.MessageAdapter;

public class GroupsTest extends junit.framework.TestCase {
	
    public void testTextReceived() {
		InvitationManager mock = mock(InvitationManager.class);
		LinkedHashMap<MeetingInfo, Place> meetings = new LinkedHashMap<MeetingInfo, Place>();
		Groups g = new Groups(mock, meetings);
		MessageAdapter mockevent = mock(MessageAdapter.class);
		g.textReceived(mockevent, "");
		verify(mockevent).sendText("No groups avaliable");
		//g.textReceived(mockevent, "join 1");
		//verify(mockevent).sendText("Group not found");
	}

	public void testLeave() {
		// Add a mock meeting and check whether one leaves the meeting
		InvitationManager mock = mock(InvitationManager.class);
		// Create a mock meeting
		LinkedHashMap<MeetingInfo, Place> meetings = new LinkedHashMap<MeetingInfo, Place>();
		MeetingInfo meeting = mock(MeetingInfo.class);
		Place place = mock(Place.class);
		when(meeting.getName()).thenReturn("testplace");
		when(place.getName()).thenReturn("testplace");
		meetings.put(meeting, place);
		
		Groups g = new Groups(mock, meetings);
		MessageAdapter mockevent = mock(MessageAdapter.class);
		when(mockevent.isMeeting()).thenReturn(true);
		when(mockevent.getPlace()).thenReturn(place);
		g.textReceived(mockevent, "leave");
		verify(place).leave(0);
		assertTrue("Failed to remove meeting from hashmap", meetings.size() == 0);
	}

}

