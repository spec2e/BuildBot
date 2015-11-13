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
package BuildBot;
import com.lotus.sametime.im.ImEvent;
import com.lotus.sametime.places.MyselfEvent;
import com.lotus.sametime.core.types.STUser;
import com.lotus.sametime.places.Place;
import com.lotus.sametime.core.types.STUserInstance;


public class MessageAdapter {

	ImEvent imevent = null;
	MyselfEvent myevent = null;

	public boolean isMeeting() {
		return myevent != null;
	}

	public String getId() {
		String id; 
		if (imevent != null)
			id = imevent.getIm().getPartnerDetails().getId().getId();
		else
			id = getPlace().getName();
		return id;
	}

	public MessageAdapter (ImEvent e) {
		imevent = e;
	}

	public MessageAdapter (MyselfEvent e) {
		myevent = e;
	}


	public void sendText(String text) {
		if (imevent != null)
			imevent.getIm().sendText(true, text);
		else
			myevent.getMyself().getPlace().sendText(text);
	}

	public String getText() {
		if (imevent != null)
			return imevent.getText();
		else
			return myevent.getText();
	}

	public STUser getPartner() {
		if (imevent != null)
			return imevent.getIm().getPartner();
		return null;
	}

	public Place getPlace() {
		if (myevent != null)
			return myevent.getMyself().getPlace(); 
		return null;
	}


}

