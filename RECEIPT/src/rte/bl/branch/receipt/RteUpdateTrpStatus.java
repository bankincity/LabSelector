package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;
public class RteUpdateTrpStatus  implements Task
{
	Mrecord ftrp;
	Mrecord fhtrp;
	Mrecord fxrp;
	String currentStatus;	
	String seqBook;	
	String policy;	
	String branch;	
	String premium;
	String userID;
	String [] field = {"rpNo","policyNo","name","payPeriod","premium","currentStatus","statusDate","reqPerson","submitNo","depositNo","bookNo","userID","originalStatus","requestDate"};
        int [] len = {12,8,80,6,9,1,8,80,12,5,5,7,1,8};

	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {byte [] data ,newStatus,bookNo,policyNo,premium,branch,userID}",-1);
 		Object [] parameter = (Object []) param;
  		byte [] data  = (byte [])parameter[0];
  		currentStatus = (String)parameter[1];
  		seqBook = (String)parameter[2];
  		policy = (String)parameter[3];
  		premium = (String)parameter[4];
  		branch = (String)parameter[5];
  		userID = (String)parameter[6];
		
    		try 
		{
			ftrp = CFile.opens("trpctrl@receipt");
			fhtrp = CFile.opens("histrpctrl@receipt");
			fxrp = CFile.opens("xrpchg@cbranch");
			Rtemp trec = new Rtemp(field,len);
			
			trec.putBytes(data);
			if (!updateData(currentStatus,seqBook,policy,premium,userID,branch,trec.copy()))
				updateHistData(currentStatus,seqBook,policy,premium,userID,branch,trec.copy());
				
			return new Result("",0);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
    		}
	}
	private boolean  updateHistData(String currentStatus,String seqBook,String policy,String premium,String userID,String branch,Record trec) throws Exception
	{
		
		String oldrec = trec.get("rpNo")+trec.get("policyNo")+M.setlen(trec.get("premium"),9);
//		String oldrec2 = trec.get("statusDate")+trec.get("currentStatus")+trec.get("originalStatus");
		String oldrec2 = trec.get("currentStatus")+trec.get("originalStatus");
		System.out.println("oldrec 1 -- > "+oldrec);
		System.out.println("oldrec 2 -- > "+oldrec2);
		boolean found = false;
  		for(boolean ss = fhtrp.equal(trec.get("rpNo")); ss && trec.get("rpNo").compareTo(fhtrp.get("rpNo")) == 0 ;ss = fhtrp.next())
 		{
			String tstr = new String(fhtrp.getBytes());
			System.out.println("tstr ------------------->"+tstr.substring(0,29)+"   "+tstr.substring(45,47));
			if(oldrec.compareTo(tstr.substring(0,29)) == 0 && oldrec2.compareTo(tstr.substring(45,47)) == 0)
			{
				System.out.println("tstr ---------ss---------->"+tstr.substring(0,29)+"   "+tstr.substring(37,47));
				String oldStatus = fhtrp.get("currentStatus");
				fhtrp.set("statusDate",DateInfo.sysDate());
		 		fhtrp.set("currentStatus",currentStatus);
				if("HXQ".indexOf(currentStatus) >=  0)
				{
					fhtrp.set("policyNo",policy);
					fhtrp.set("premium",premium);
				}
				else {
 					fhtrp.set("seqBook",seqBook);
				}
				ftrp.putBytes(fhtrp.getBytes());
				if(ftrp.insert())
					fhtrp.delete();
				found = true;
				
				System.out.println("tstr ---------after---------->"+tstr.substring(0,29)+"   "+tstr.substring(37,47));
				insertXrpChange(fhtrp.copy(),oldStatus);
				if("N".indexOf(currentStatus) < 0 )
				{
					if(fhtrp.get("requestDate").compareTo("00000000") == 0)
						deleteTrpTmp(fhtrp.copy());
				}
				else {
					if(fhtrp.get("requestDate").compareTo("00000000") == 0)
						insertTrpTmp(fhtrp.copy());
				}
			}
		}
		return found ;
	}
	private boolean  updateData(String currentStatus,String seqBook,String policy,String premium,String userID,String branch,Record trec) throws Exception
	{
		
		String oldrec = trec.get("rpNo")+trec.get("policyNo")+M.setlen(trec.get("premium"),9);
//		String oldrec2 = trec.get("statusDate")+trec.get("currentStatus")+trec.get("originalStatus");
		String oldrec2 = trec.get("currentStatus")+trec.get("originalStatus");
		System.out.println("oldrec 1 -- > "+oldrec);
		System.out.println("oldrec 2 -- > "+oldrec2);
		boolean found = false;
  		for(boolean ss = ftrp.equal(trec.get("rpNo")); ss && trec.get("rpNo").compareTo(ftrp.get("rpNo")) == 0 ;ss = ftrp.next())
 		{
			String tstr = new String(ftrp.getBytes());
			System.out.println("tstr ------------------->"+tstr.substring(0,29)+"   "+tstr.substring(45,47));
			if(oldrec.compareTo(tstr.substring(0,29)) == 0 && oldrec2.compareTo(tstr.substring(45,47)) == 0)
			{
				System.out.println("tstr ---------ss---------->"+tstr.substring(0,29)+"   "+tstr.substring(37,47));
				String oldStatus = ftrp.get("currentStatus");
				ftrp.set("statusDate",DateInfo.sysDate());
		 		ftrp.set("currentStatus",currentStatus);
				if("HXQ".indexOf(currentStatus) >=  0)
				{
					ftrp.set("policyNo",policy);
					ftrp.set("premium",premium);
				}
				else {
 					ftrp.set("seqBook",seqBook);
				}
		 		ftrp.update();
				found = true;
				
				System.out.println("tstr ---------after---------->"+tstr.substring(0,29)+"   "+tstr.substring(37,47));
				insertXrpChange(ftrp.copy(),oldStatus);
				if("N".indexOf(currentStatus) < 0 )
				{
					if(ftrp.get("requestDate").compareTo("00000000") == 0)
						deleteTrpTmp(ftrp.copy());
				}
				else {
					if(ftrp.get("requestDate").compareTo("00000000") == 0)
						insertTrpTmp(ftrp.copy());
				}
			}
		}
		return found ;
 	}
	private void insertXrpChange(Record tmp,String oldStatus)
	{
		fxrp.set("sysDate",DateInfo.sysDate());
                fxrp.set("rpNo",tmp.get("rpNo"));
                fxrp.set("policyNo",tmp.get("policyNo"));
                fxrp.set("premium",tmp.get("premium"));
                fxrp.set("newStatus",tmp.get("currentStatus"));
                fxrp.set("oldStatus",oldStatus);
                fxrp.set("payPeriod","000000");
                fxrp.set("sysTime",Masic.time("commontable").substring(8));
                fxrp.set("typeOfReceipt","T");
                fxrp.set("userID",userID);
		fxrp.set("branch",tmp.get("branch"));
                fxrp.insert();

	}
	private void deleteTrpTmp(Record tmp) throws Exception
	{
		Mrecord trptmp = CFile.opens("trptmp@cbranch");
		Mrecord trptakaful = CFile.opens("trptmptakaful@cbranch");
		if(trptmp.equal(branch+tmp.get("rpNo")))
		{
			trptmp.delete();
		}		
		else if(trptakaful.equal(branch+tmp.get("rpNo")))
		{
			trptakaful.delete();
		}		
	}
	private void insertTrpTmp(Record tmp) throws Exception
	{
		
		Mrecord trptmp = CFile.opens("trptmp@cbranch");
		Mrecord trptakaful = CFile.opens("trptmptakaful@cbranch");
		if(!trptmp.equal(branch+tmp.get("rpNo")))
		{
			trptmp.set('0');
			trptmp.set("branch",branch);
			trptmp.set("rpNo",tmp.get("rpNo"));
			trptmp.insert();
		}		
		else if(trptakaful.equal(branch+tmp.get("rpNo")))
		{
			trptakaful.set('0');
			trptakaful.set("branch",branch);
			trptakaful.set("rpNo",tmp.get("rpNo"));
			trptakaful.insert();
		}		
	}
}
