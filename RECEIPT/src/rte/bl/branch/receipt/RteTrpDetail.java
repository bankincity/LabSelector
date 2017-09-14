package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;
import utility.prename.*;
public class RteTrpDetail  implements Task
{
	BraSales bs;
 	Mrecord trp;
 	Mrecord htrp;
	Mrecord fask; 
	Mrecord ind;
	Mrecord ord;
	Mrecord whl;
	Mrecord name;
	Vector tvec ;
	Record trec ;
	String [] field = {"rpNo","policyNo","name","payPeriod","premium","currentStatus","statusDate","reqPerson","submitNo","depositNo","bookNo","userID","originalStatus","requestDate"};
	int [] len = {12,8,80,6,9,1,8,80,12,5,5,7,1,8};
	String branch;
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {flag,branch,rpNo}",-1);
 		Object [] parameter = (Object []) param;
 		String byKey = (String)parameter[0];
 		String keyString  = (String)parameter[1];
		branch = (String)parameter[2];
 		try 
 		{
			if(branch.compareTo("000") == 0)
			{
				 String [] field1= {"rpNo","policyNo","name","payPeriod","premium","currentStatus","statusDate","reqPerson","submitNo","depositNo","bookNo","userID","originalStatus","requestDate","branch"};
               			 int [] len1= {12,8,80,6,9,1,8,80,12,5,5,7,1,8,3};

				trec = new Rtemp(field1,len1);
			}
			else {
				trec = new Rtemp(field,len);
			}
			tvec = new Vector();
 			trp = CFile.opens("trpctrl@receipt");
 			htrp = CFile.opens("histrpctrl@receipt");
			ord = CFile.opens("ordmast@mstpolicy");
			ind = CFile.opens("indmast@mstpolicy");
			whl = CFile.opens("whlmast@mstpolicy");
			name = CFile.opens("name@mstperson");
 			bs = new BraSales();
			searchData(byKey,keyString,trp);
			searchData(byKey,keyString,htrp);

 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
		return new Result(tvec,0);	
 	}
	private void searchData(String byKey,String keyString,Mrecord ftrp)
	{
		String fieldName = "rpNo";

		if(byKey.charAt(0) == 'P')
		{
			ftrp.start(3);
			fieldName = "policyNo";
		}
		else if(byKey.charAt(0) == 'S')
		{
			ftrp.start(2);
			fieldName = "submitNo";
		}
		System.out.println("keyString ----------"+keyString);
		for (boolean st = ftrp.equal(keyString) ; st && keyString.compareTo(ftrp.get(fieldName)) == 0 ; st = ftrp.next())
		{
                        System.out.println("branch === "+ftrp.get("branch"));
			if(branch.compareTo("000") != 0 && branch.compareTo(ftrp.get("branch")) != 0 && ftrp.get("branch").compareTo("MCA") != 0)
				continue;
			trec.set("rpNo",ftrp.get("rpNo"));
			trec.set("policyNo",ftrp.get("policyNo"));
			trec.set("name",getName(ftrp.get("policyNo")));
			trec.set("payPeriod","000000");
			trec.set("premium",ftrp.get("premium"));
			trec.set("currentStatus",ftrp.get("currentStatus"));
			trec.set("originalStatus",ftrp.get("originalStatus"));
			trec.set("statusDate",ftrp.get("statusDate"));
			trec.set("requestDate",ftrp.get("requestDate"));			
			if(ftrp.get("requestDate").compareTo("00000000")  != 0 && ftrp.get("branch").compareTo("MCA") != 0)
			{
				if("NAW".indexOf(ftrp.get("currentStatus")) >= 0)
				{
					trec.set("statusDate",ftrp.get("requestDate"));
				}	
				trec.set("reqPerson",getRequestPerson(ftrp.get("branch"),ftrp.get("rpNo"),ftrp.get("requestDate")));				
				trec.set("userID",ftrp.get("askUserID"));
				trec.set("depositNo",depositNo);
			}
			else {
				trec.set("reqPerson"," ");
				trec.set("depositNo","00000");
				trec.set("userID",ftrp.get("clearUserID"));
			}
			trec.set("bookNo",ftrp.get("seqBook"));
			trec.set("submitNo",ftrp.get("submitNo"));
			if(branch.compareTo("000") == 0)
				trec.set("branch",ftrp.get("branch"));
                        System.out.println("bingo.............."+ftrp.get("branch"));
			tvec.addElement(trec.copy().getBytes());
		}
	}
	
	private String  getRequestPerson(String branch,String rpNo,String requestDate) 
	{
		try {
			depositNo = "00000";
			if(requestDate.compareTo("00000000") == 0)
				return "";
			String nrequestDate = requestDate;
                    //    Msg.msg(new Mframe(""),requestDate+" "+rpNo);
                        try {
                                if (requestDate.compareTo("25600116") >=0)
                                {
        			        String reqID="";	
                                        String accDate = requestDate.substring(2,6)+"15";
                                        if (requestDate.substring(6).compareTo("16") >=0)
                                                accDate =  requestDate.substring(2,6)+"30";
                                        fask = CFile.opens("ask"+accDate+"@cbranch");
                        //                Msg.msg(new Mframe(""),"fask...."+fask.name()+" "+branch+"  "+rpNo);
                                        
                                        fask.start(2);
                                        for (boolean st = fask.equalGreat(branch+rpNo) ; st && (branch+rpNo).compareTo(fask.get("branch")+fask.get("rpNo")) == 0 ; st = fask.next())
                                        {
                                                if(nrequestDate.compareTo(fask.get("requestDate")) == 0)
                                                        reqID= fask.get("ownerSaleID");

                                        }
                                        
                      //                  Msg.msg(new Mframe(""),"fask...."+fask.name()+" "+branch+"  "+rpNo+" "+reqID);
                                        if(reqID.trim().length() > 0)
                                                return getSaleName(reqID);


                                }
			        if(requestDate.substring(6).compareTo("17") >= 0)
				        requestDate = DateInfo.nextMonth(requestDate);
			
			System.out.println("nmonth----------------------------"+requestDate);
        			fask = CFile.opens("ask"+requestDate.substring(0,6)+"@cbranch");
	        		fask.start(2);
		        	System.out.println("nmonth----------------------------"+requestDate);
        			String reqID="";	

	        		for (boolean st = fask.equalGreat(branch+rpNo) ; st && (branch+rpNo).compareTo(fask.get("branch")+fask.get("rpNo")) == 0 ; st = fask.next())
		        	{
			        	if(nrequestDate.compareTo(fask.get("requestDate")) == 0)
			        		reqID= fask.get("ownerSaleID");
				
	        		}
	        		if(reqID.trim().length() > 0)
		        		return getSaleName(reqID);
                        }
                        catch(Exception e)
                        {
                                
                        }
                        requestDate = nrequestDate;
				
			fask = CFile.opens("ask"+requestDate.substring(0,6)+"@cbranch");
			fask.start(2);
			for (boolean st = fask.equalGreat(branch+rpNo) ; st && (branch+rpNo).compareTo(fask.get("branch")+fask.get("rpNo")) == 0 ; st = fask.next())
			{
				if(nrequestDate.compareTo(fask.get("requestDate")) == 0)
					return getSaleName(fask.get("ownerSaleID"));	
			}
                       
		}
		catch(Exception e)
		{
			System.out.println("e--------:"+e.getMessage());
			return "";
		}		
		return "";
	}	
	private String getName(String policyNo)
	{
		String nameID = "";
		if(ord.equal(policyNo))
		{
			nameID = ord.get("nameID");
		}
		else if(whl.equal(policyNo))
		{
			nameID = whl.get("nameID");
		}
		else if(ind.equal(policyNo))
		{
			nameID = ind.get("nameID");
		}
		if(nameID.trim().length() == 0)
			return "";
		if(name.equal(nameID))
		{
			return  (Prename.getAbb(name.get("preName").trim())+name.get("firstName").trim()+" "+name.get("lastName").trim());

		}
		return "";
	}
	String depositNo;
	private String  getSaleName(String saleID) throws Exception
	{
		System.out.println("saleid ================"+saleID);	
		bs.getBySalesID(saleID);
		depositNo = bs.getSnRec("depositNo");
		System.out.println("saleid ================"+saleID);	
		return (bs.getSnRec("preName").trim()+bs.getSnRec("firstName").trim()+" "+bs.getSnRec("lastName").trim());
	}	
}


