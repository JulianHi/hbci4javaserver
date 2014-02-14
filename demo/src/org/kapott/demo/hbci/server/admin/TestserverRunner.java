
/* 
 	This file is part of HBCI4Java
    Copyright (C) 2001-2005  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.demo.hbci.server.admin;

import org.kapott.demo.hbci.server.TestServer;

public class TestserverRunner extends Thread {

	private TestServer myServer;
	private String serverDataPath;
	private String logPath;
	public TestserverRunner(String serverDataPath, String logPath)
	{
		this.serverDataPath = serverDataPath;
		this.logPath = logPath;
	}
	
	@Override
	public void run() {
		   
		   myServer = new TestServer();
		   myServer.start(serverDataPath,logPath, false);		
		   
	}
}
