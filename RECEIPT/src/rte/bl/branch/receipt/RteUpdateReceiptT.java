package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;
public class RteUpdateReceiptT implements Task
{
	Mrecord ftrp;
	Mrecord fhtrp;
	Mrecord fxrp;
	String statusDate;	
	String currentStatus;	
	String seqBook;	
	String rpNo;	
	String policy;	
	String branch;	
	String premium;
	String userID;
	String oldpolicy;
	TempMasicFile tmp;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {statusDate,currentStatus,seqBook,rpNo,policy,branch,premium,userID}",-1);
 		Object [] parameter = (Object []) param;
  		statusDate = (String)parameter[0];
  		currentStatus = (String)parameter[1];
  		seqBook = (String)parameter[2];
  		rpNo = (String)parameter[3];
  		policy = (String)parameter[4];
  		branch = (String)parameter[5];
  		premium = (String)parameter[6];
  		userID = (String)parameter[7];
  		oldpolicy = (String)parameter[8];
		
    		try 
		{
			ftrp = CFile.opens("trpctrl@receipt");
			fhtrp = CFile.opens("histrpctrl@receipt");
			fxrp = CFile.opens("xrpchg@cbranch");
			tmp = new TempMasicFile("bra"+branch+"app",ftrp.layout());
			tmp.keyField(false,false,new String [] {"rpNo"});
			tmp.buildTemp();
			updateData(statusDate,currentStatus,seqBook,rpNo,policy,premium,userID);
			return new Result(tmp.name(),0);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
    		}
	}
	private void  updateData(String statusDate,String currentStatus,String seqBook,String rpNo,String policy,String premium,String userID) throws Exception
	{
		boolean found = false ;
  		for(boolean ss = ftrp.equal(rpNo); ss ;ss = ftrp.next())
 		{
			if("HQX".indexOf(currentStatus) >= 0 && oldpolicy.compareTo(ftrp.get("policyNo")) == 0)
			{
				found = true;
				break;
			}	
			if(policy.compareTo(ftrp.get("policyNo")) == 0 && statusDate.compareTo(ftrp.get("statusDate"))==0)
			{
				found = true;
    				break;
			}			
		}
		System.out.println("trp........................"+found);
		if(found)
		{
			  //ftrp.set("statusDate",statusDate);
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
			insertXrpChange(ftrp.copy(),oldStatus);
 			tmp.putBytes(ftrp.copy().getBytes());		
			tmp.insert();
		}
		else {
  			for(boolean ss = fhtrp.equal(rpNo); ss ;ss = fhtrp.next())
 			{
				if("HQX".indexOf(currentStatus) >= 0 && oldpolicy.compareTo(fhtrp.get("policyNo")) == 0)
				{
					found = true;
					break;
				}	
				if(policy.compareTo(fhtrp.get("policyNo")) == 0 && statusDate.compareTo(fhtrp.get("statusDate"))==0)
				{
					found = true;
    					break;
				}			
			}
			if(found)
			{
				  //ftrp.set("statusDate",statusDate);
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
 				fhtrp.update();
				insertXrpChange(fhtrp.copy(),oldStatus);
 				tmp.putBytes(fhtrp.copy().getBytes());		
				tmp.insert();
			}
		}
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
                fxrp.insert();

	}
}
