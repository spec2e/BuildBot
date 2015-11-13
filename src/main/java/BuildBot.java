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



import java.lang.IllegalArgumentException;
import com.lotus.sametime.chat.invitation.*;
import com.lotus.sametime.chat.*;
import com.lotus.sametime.awareness.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.core.comparch.*;
import com.lotus.sametime.core.constants.*;
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.places.Place;
import com.lotus.sametime.places.*;
import com.lotus.sametime.places.PlacesService;
import static com.lotus.sametime.places.PlacesConstants.PLACE_PUBLISH_DONT_CARE;
import com.lotus.sametime.im.*;
import callbacks.*;
import plugins.*;
import plugins.jenkins.Jenkins;

import java.util.*;
import java.io.InputStream;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildBot implements 
LoginListener, ImServiceListener, ImListener, InvitationListener,
MyMsgListener
{

	private static final String ESCAPE_KEYWORD = "!bb";
	final Logger logger = LoggerFactory.getLogger(BuildBot.class);

	CommunityService commService;
	Thread engine;
    InstantMessagingService imService;
    STSession stsession;
	List<MessageCallback> msg_callbacks = new LinkedList<MessageCallback>();
	// List of places that we have joined
	LinkedHashMap<MeetingInfo, Place> meetings = new LinkedHashMap<MeetingInfo, Place>();
    Properties app_config = null;

    public Properties getConfig() {
        if (app_config == null) {
            try {
                String propFileName = "config.properties";
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
                app_config = new Properties();
                app_config.load(inputStream);
            }
            catch (Exception ex) {
                System.out.println("Couldnt load config file");
                System.out.println(ex.getMessage());
                System.exit(-1);
            }
        }
        return app_config;
    }

	public BuildBot () {
		try {        
			stsession = new STSession("BuildBotSession");        
		} 
		catch (DuplicateObjectException e) {        
			e.printStackTrace();
			return;        
		}
        // load configuration
        getConfig();
        String serverName = app_config.getProperty("st_server");
        String userId = app_config.getProperty("st_user");
        String password = app_config.getProperty("st_password");
        // Start session
		stsession.loadSemanticComponents();    
		stsession.start();				
		commService = (CommunityService)stsession.getCompApi
			(CommunityService.COMP_NAME);
		commService.addLoginListener(this);
		commService.loginByPassword(serverName, userId, password);				
		// Invitation manager to handled meetings/groups
		InvitationManager inma = new InvitationManager(stsession);
		inma.setListener(this);
		// Register callbacks
		msg_callbacks.add(new plugins.Echo());
		msg_callbacks.add(new Groups(inma, meetings));
        String j_url = app_config.getProperty("jenkins_url");
        int j_port = Integer.parseInt(app_config.getProperty("jenkins_port"));
		msg_callbacks.add(new Jenkins(j_url, j_port));
		

		}

	public void imReceived(ImEvent e) { e.getIm().addImListener(this); }
	public void dataReceived(ImEvent e) {}
	public void imClosed(ImEvent e) {}
	public void imOpened(ImEvent e) {}
	public void openImFailed(ImEvent e) {}

	public void loggedIn(LoginEvent e) {
		imService = (InstantMessagingService)stsession.
		getCompApi(InstantMessagingService.COMP_NAME);
		imService.registerImType(ImTypes.IM_TYPE_CHAT);
		imService.addImServiceListener(this);
		logger.info("Logged in");
	 }

	public void loggedOut(LoginEvent e) {}

	public void messageCaller(MessageAdapter msg) {
		String text = msg.getText();
		if (text.startsWith(ESCAPE_KEYWORD)) {
			boolean handled = false;
			String[] txt_args = text.split(" ");
			for (MessageCallback msgc : msg_callbacks ) {
				if (txt_args.length > 1 && msgc.getCommand().equals(txt_args[1])) {
					// get the payload of the message without the escape string and the command
					String payload = text.replace(ESCAPE_KEYWORD + " " + msgc.getCommand()
									, "").trim();
				try {
					msgc.textReceived(msg, payload);
					handled = true;
					} catch (IllegalArgumentException ex)
					{ /* Catches the exception before the handled flag is set to be true */ }
				}
			}
			if (!handled)
				msg.sendText("Command not recognized");
		}
	}

	public void textReceived(ImEvent e) {
		// Text received in a instant chat
		MessageAdapter msg = new MessageAdapter(e);
		messageCaller(msg);
	}


	public void serviceAvailable(AwarenessServiceEvent e) {
	}

	public void serviceUnavailable(AwarenessServiceEvent e) {
	}

	public void textReceived(MyselfEvent e) {
		// Text received in a meeting
		MessageAdapter msg = new MessageAdapter(e);
		messageCaller(msg);
	}

	public void dataReceived(MyselfEvent event) {
	}


	public void	invitedToMeeting(Invitation invitation) {
		// Change this into a list of callbacks in case we need to listen to more than one
		// For now accept all invitations
		invitation.accept();
		// Join meeting
		MeetingInfo minf = invitation.getMeetingInfo();
		PlacesService placesService =
		  (PlacesService)stsession.getCompApi(PlacesService.COMP_NAME);

		Place m_place = placesService.createPlace(
						minf.getPlaceName(),
						minf.getDisplayName(), // place display name 
						EncLevel.ENC_LEVEL_DONT_CARE, // encryption level
						minf.getType().getValue(), // place type
						PLACE_PUBLISH_DONT_CARE);
		m_place.addPlaceListener(new MyPlaceListener(this));
		m_place.enter();
		meetings.put(invitation.getMeetingInfo(), m_place);
	}

	public void start() {
		while (true) { 
		   try { 
			Thread.sleep(1000);
		   } catch (InterruptedException e) {
				for(Place place : meetings.values()) {
					place.leave(0);
					place.close();
				}
		   }

		}
	}

}
