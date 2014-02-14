
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

import java.io.File;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kapott.hbci.manager.FileSystemClassLoader;


public class TestserverServlet 
    extends HttpServlet 
{
 
	private static final long serialVersionUID = 5847026834130088733L;
	private String serverDataPath; // adresse der registry der hbciserver-objekte
	private String logPath;
    private TestserverRunner myRunner;
    
    // Server Data pfag den web.xml-daten extrahieren
    public void init(ServletConfig config)
    {
        try {
            super.init();
            
	            serverDataPath="";
	            logPath="";
            
	            try{
	            	serverDataPath=config.getInitParameter("serverDataPath");
	            }catch(Exception ex){}
	            
	            try{
	            	logPath=config.getInitParameter("logPath");
	            }catch(Exception ex){}
            
	            System.out.println("ServerData Path set to "+serverDataPath);
	            System.out.println("log Path set to "+logPath);
            
	            File path = new File(serverDataPath);
	            if(serverDataPath.isEmpty() || !path.exists())
	            {
	            	serverDataPath = config.getServletContext().getRealPath("/server-data");
	            	path = new File(serverDataPath);
	            	
	            	if(!path.exists())
	            		throw new Exception("missing Server Data folder :(");
	            	
	            	System.out.println("ServerData Path changed to "+serverDataPath);
	            }
            
	            path = new File(logPath);
            
	            if(logPath.isEmpty() || !path.exists())
	            {
	            	logPath = config.getServletContext().getRealPath("/log/");
	            	path = new File(logPath);
	            	
	            	if(!path.exists())
	            		throw new Exception("missing Log folder :(");
	            	
	            	System.out.println("log path changed to "+logPath);
	            	
	            }
	            
            
            myRunner = new TestserverRunner(serverDataPath, logPath);
            myRunner.setDaemon(true);
        	myRunner.start();
         
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException 
    {       
    	
    	res.setContentType("text/html");
        PrintWriter out=res.getWriter();
        
        // html-kopf erzeugen
        printHeader(out);
        
    	int lines = 100;
    	String paramlines = req.getParameter("lines");
    	
    	if(paramlines != null && !paramlines.isEmpty())
    	{
    		if(paramlines.compareToIgnoreCase("all")==0)
    			lines = 0;
    		else{
    			//parse as Integer
    			
    			try{
    				int intZahl = Integer.parseInt(paramlines);
    			    			
    			if(intZahl >0)
    				lines = intZahl;
    			}catch(Exception e){
    				//problems occured... use fallback (initial value)
    				out.println("<pre>"+e.getMessage()+"</pre>");
    			} 			
    			
    		}
    	}
    	
    	File logFolder = new File(logPath);
    	    	
    	if(logFolder.exists() && logFolder.isDirectory())
    	{
    		
    		for(final File logEntry : logFolder.listFiles())
    		{
    			if(logEntry.isFile() && logEntry.getName().startsWith("server_"))
    			{
    				out.println("<div>");
    				out.println("<h2>"+logEntry.getName()+":</h2>");
    				out.println("<pre>");
    				
	    				try{

	    					if(lines==0)
	    						cat(logEntry, out);
	    					else
	    						tail(logEntry, out, lines);
	    					
	    				}catch(Exception parEx)
	    				{
	    					out.println(parEx.getMessage());
	    				}    				
    				
    				out.println("</pre>");
    				out.println("</div>");
    				
    			}
    		}
    		
    	}
    	printFooter(out);
    	
    }
    
    
    private void printHeader(PrintWriter out)
    {
		out.println("<!doctype html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>HBCI4Java Testserver LogFiles</title>");
		
		out.println("<script type=\"text/javascript\" src=\"js/jquery-1.11.0.min.js\"></script>");
		out.println("<script type=\"text/javascript\" src=\"js/jquery.snippet.min.js\"></script>");
		out.println("<script type=\"text/javascript\" src=\"js/sh_log.js\"></script>");
		
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/jquery.snippet.min.css\" />");
		
		out.println("</head>");
		out.println("<body>");
		out.println("<H1>HBCI4Java Testserver LogFiles</H1>");
        
    }
    
    private void printFooter(PrintWriter out)
    {
                
        out.println("<script type=\"text/javascript\">");
        out.println("$(document).ready(function(){");
        out.println("$(\"pre\").snippet(\"log\", {style:\"ide-eclipse\",menu:false,showNum:false});");
        out.println("});");
        out.println("</script>");
        
        
        
        out.println("</body></html>");
    }

    
    public void cat( File file , PrintWriter out ) throws IOException {
    	 
    	FileReader fileReader = new FileReader(file);

    	 BufferedReader br = new BufferedReader(fileReader);

    	 String line = null;
    	 while ((line = br.readLine()) != null) {
    		 out.println(line.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
    	 }
    	 br.close();
    }
    
    private void tail( File file, PrintWriter out, int lines) throws IOException {
        RandomAccessFile fileHandler = null;
             fileHandler = 
                new java.io.RandomAccessFile( file, "r" );
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            int line = 0;
            
            boolean char13=false;
            for(long filePointer = fileLength; filePointer != -1; filePointer--){
                fileHandler.seek( filePointer );
                int readByte = fileHandler.readByte();

                
                if(readByte == 0xD){ //13
                	char13=true;
                	line++;
                	
                    if(line>=lines)
                    	break;
                    
                    sb.append((char)0xA);
                	sb.append((char)0xD);
                	continue;
                } else if( readByte == 0xA ) {
                	
                	if(char13){
                		char13=false;
                		continue;
                	}
                	
                	line++;
                    
                	if(line>=lines)
                    	break;
                    
                	sb.append((char)0xA);
                	sb.append((char)0xD);
                	continue;
                } else{
                	char13=false;
                }
                

                
                if(readByte == 0x3C)
                	sb.append(";tl&");
                else if(readByte == 0x3E)
                	sb.append(";tg&");
                else if(readByte == 0x26)
                	sb.append(";pma&");
                else                
                	sb.append(( char ) readByte);
            }

            out.write(sb.reverse().toString());

            if (fileHandler != null )
                try {
                    fileHandler.close();
                } catch (IOException e) {
                    /* ignore */
                }
    }
    

    
@Override
public void destroy() {
	super.destroy();

	if(myRunner != null)
	{
		try{
		myRunner.interrupt();
		myRunner = null;
		} catch(Exception e){}
	}
	
}
    
}
