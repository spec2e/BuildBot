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
package plugins;

import callbacks.MessageCallback;
import java.lang.IllegalArgumentException;
import com.lotus.sametime.chat.invitation.*;
import com.lotus.sametime.chat.*;
import com.lotus.sametime.im.*;
import com.lotus.sametime.core.comparch.*;
import com.lotus.sametime.core.constants.*;
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.places.Place;
import BuildBot.MessageAdapter;
import java.util.*;



public class Groups implements 
MessageCallback {

	LinkedHashMap<MeetingInfo, Place> meetings;
	InvitationManager inv_manager;
	
	public Groups(InvitationManager inv_manager, LinkedHashMap<MeetingInfo, Place> meetings) {
		this.inv_manager = inv_manager;
		this.meetings = meetings;
	}

	public String getCommand() {
		return "group";
	}

	private String showGroups() {
		StringBuilder namegroups = new StringBuilder(); 
		ArrayList groups = new ArrayList(meetings.keySet());
		
		for (int i = 0; i < groups.size(); i++) {
			MeetingInfo minf = (MeetingInfo)groups.get(i);
			String group = String.format("%d-) %s \n", i + 1, minf.getDisplayName());
			namegroups.append(group);
		}
		if (namegroups.length() == 0)
			return "No groups avaliable";
		else
			return namegroups.toString();
	}

	private void joinGroup(int groupidx, MessageAdapter msg) {
		// invite a peer to join a meeting
		MeetingInfo minf;
		try {
			ArrayList groups = new ArrayList(meetings.keySet());
			minf = (MeetingInfo)groups.get(groupidx - 1);
		}
		catch (IndexOutOfBoundsException ex) {
			msg.sendText("Group not found");
			return;
		}
		STUser peer = msg.getPartner();
		if (msg.isMeeting())
			msg.sendText("This operation is only permitted through private message");
		else 
			inv_manager.createInviter().invite(minf, "", new STUser[]{peer}, false);
	}

	private void removePlace(String name) {
		for ( MeetingInfo mtin: meetings.keySet() ) {
			if (mtin.getName().equals(name)) {
				meetings.remove(mtin);
				return;
			}
		}
	}
	
	private void leave(MessageAdapter msg) {
		if (msg.isMeeting()) {
			Place place = msg.getPlace();
			place.leave(0);
			removePlace(place.getName());
			place.close();
		}
		else 
			msg.sendText("This operation is only permitted inside meetings");
	}

	public void textReceived(MessageAdapter msg, String payload) {
		String[] args = payload.split(" ");
		if (payload.trim().isEmpty()) {
			// If no arguments
			msg.sendText(showGroups());
		}
		else {
			switch (args[0]) {
				 case "join":
					int groupidx = 0;
					try {
						groupidx = Integer.parseInt(args[1]);
					}
					catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
						throw new IllegalArgumentException();
					}
					joinGroup(groupidx, msg);
					break;
				 case "leave":
					leave(msg);
					break;
				 default:	
					throw new IllegalArgumentException();
			}
		}
	}


}
