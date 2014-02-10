
/*  This file is part of HBCI4Java

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

package org.kapott.hbci.server;

import org.kapott.hbci.manager.IHandlerData;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.exceptions.InvalidUserDataException;



public final class HBCILocalHandler
	implements IHandlerData
{
	
    private MsgGen msggen;
    private HBCIPassportInternal passport;


    public HBCILocalHandler(String hbciversion,HBCIPassport passport, MsgGen msggen)
    {
        try {
            if (passport==null)
                throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_NULL"));
            
            if (hbciversion==null) {
                hbciversion=passport.getHBCIVersion();
            }
            if (hbciversion.length()==0)
                throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_NO_HBCIVERSION"));

//            this.kernel=new HBCIKernelImpl(this,hbciversion);
            
            this.passport=(HBCIPassportInternal)passport;
            this.passport.setParentHandlerData(this);

  		  	this.msggen = msggen;
            //registerInstitute();
            //registerUser();
            
/*            if (!passport.getHBCIVersion().equals(hbciversion)) {
                this.passport.setHBCIVersion(hbciversion);
                this.passport.saveChanges();
            }
*/
  //          dialogs=new Hashtable<String, HBCIDialog>();
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANT_CREATE_HANDLE"),e);
        }
        
        // wenn in den UPD noch keine SEPA-Informationen ueber die Konten enthalten
        // sind, versuchen wir, diese zu holen
    /*    Properties upd=passport.getUPD();
        if (upd!=null && !upd.getProperty("_fetchedSEPA","").equals("1")) {
        	// wir haben UPD, in denen aber nicht "_fetchedSEPA=1" drinsteht
        	updateSEPAInfo();
        }
	*/
	}
	
    public HBCIPassport getPassport()
	{
		return passport;
	}
    public MsgGen getMsgGen()
	{
		return msggen;
		//return kernel.getMsgGen();
	}
	
}