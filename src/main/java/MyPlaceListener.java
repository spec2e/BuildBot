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

import com.lotus.sametime.places.*;

public class MyPlaceListener implements PlaceListener {
	
	MyMsgListener buildbot;

	public MyPlaceListener(MyMsgListener bb) {
		this.buildbot = bb;
	}


	public void activityAdded(PlaceEvent event) {
		//A new activity was added to the place.
	}

	public void activityRemoved(PlaceEvent event) {
		//An activity was removed from the place.
	}

	public void addActivityFailed(PlaceEvent event) {
	   //An attempt to add an activity failed.
	}

	public void addAllowedUsersFailed(PlaceEvent event) {
	   //The 'Add allowed users' operation failed (probably because of an unauthorized attempt).
	}

	public void entered(PlaceEvent event) {
	    //The place was entered successfully.
	    Place place = event.getPlace();
		MyselfInPlace minplace = place.getMyselfInPlace();
	    minplace.addMyMsgListener(buildbot);
	}

	public void enterFailed(PlaceEvent event) {
	   //The request to enter a place failed
	}

	public void invite15UserFailed(PlaceEvent event) {
	   //An invitation to a 1.5 client failed.
	}

	public void left(PlaceEvent event) {
	   //The place was left.
	}

	public void removeAllowedUsersFailed(PlaceEvent event) {
	   //The 'Remove allowed users' operation failed (probably because of an unauthorized attempt).
	}

	public void sectionAdded(PlaceEvent event) {
	   //A new section was added to the place.
	}

	public void sectionRemoved(PlaceEvent event) {
	   //A section was removed from the plac
	}

	public void attributeChanged(PlaceMemberEvent event) {
          // An attribute value was changed.
	}

	public void attributeRemoved(PlaceMemberEvent event) {
          // An attribute was removed.
	}

	public void changeAttributeFailed(PlaceMemberEvent event) {
          // An attempt to change an attribute value failed.
	}

	public void queryAttrContentFailed(PlaceMemberEvent event) {
          // A query for an attribute's contents failed.
	}

	public void removeAttributeFailed(PlaceMemberEvent event) {
          // An attempt to remove an attribute failed.
	}

	public void sendFailed(PlaceMemberEvent event) {
          // An attempt to send a message failed.
	}


}
