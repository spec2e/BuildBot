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
import org.mockito.stubbing.Answer;

import BuildBot.*;
import com.lotus.sametime.im.*;

public class BuildBotTest extends junit.framework.TestCase {

    public void testTextReceived() {
		BuildBot bb = new BuildBot("test", "test", "test");
		ImEvent mockevent = mock(ImEvent.class);
		Im mock = mock(Im.class);
		when(mockevent.getText()).thenReturn("!bb echo hello world");
		when(mockevent.getIm()).thenReturn(mock);
		bb.textReceived(mockevent);
		verify(mock).sendText(true, "hello world");
    }

}
