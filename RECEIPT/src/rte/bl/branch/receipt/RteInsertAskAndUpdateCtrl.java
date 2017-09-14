package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteInsertAskAndUpdateCtrl  implements Task
{
	Mrecord ask;
	Mrecord ucrp;
	Mrecord cask;
	Mrecord orc;
	Mrecord irc;
	Mrecord wrc;
	Mrecord ulrc;
	Mrecord rpaskfor;
	String userID;
	String sysdate;
	String branch;
	String yyyymm;
	String ucrpyyyymm  = "";
	String saleID ;
	String askSaleID;
	String requestDate;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {yyyymm,ucrpyyyymm,userID,branch,saleID,askSaleID,requestDate,Vector }",-1);
		Object [] parameter = (Object []) param;
		yyyymm = (String)parameter[0];
		ucrpyyyymm = (String)parameter[1];
		userID =(String)parameter[2];
		branch = (String)parameter[3];
		saleID = (String)parameter[4];
		askSaleID = (String)parameter[5];
		requestDate = (String)parameter[6];
		Vector vdata = (Vector)parameter[7]; // vector of String [typeOfPol+rpNo+policyNo]
		String cbet = (String)parameter[8];
		try {
			ask = CFile.opens("ask"+yyyymm+"@cbranch");
			if(askSaleID.trim().length() > 0)
				cask = CFile.opens("cask"+yyyymm+"@cbranch");
			if(ucrpyyyymm.trim().length() > 0)
				ucrp = CFile.opens(ucrpyyyymm+"@cbranch");
			orc = CFile.opens("orctrl@receipt");
			irc = CFile.opens("irctrl@receipt");
			wrc = CFile.opens("wrctrl@receipt");
			ulrc = CFile.opens("ulrctrl@universal");
			if(cbet.charAt(0) == 'G')
				rpaskfor = CFile.opens("newrpaskfor@receipt");
			
			 insertAskAndUpdateCtrl(vdata);			
			 return (new Result("",0));
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),2);
		}
	}
	private void insertAskAndUpdateCtrl(Vector vec) throws Exception
	{
		ask.set("branch",branch);
		if(askSaleID.trim().length() > 0)
			ask.set("askSaleID",askSaleID);
		else
			ask.set("askSaleID",saleID);
		ask.set("ownerSaleID",saleID);
		ask.set("requestDate",requestDate);
		String trp = "";
		for (int i =0 ; i < vec.size();i++)
		{
			trp = (String)vec.elementAt(i);
			if (trp.charAt(0) == 'U')
				ask.set("receiptFlag","A");
			else	
				ask.set("receiptFlag",trp.substring(0,1));
			ask.set("rpNo",trp.substring(1,13));
			ask.insert();
			String [] tpol = updateCtrl(ask.get("receiptFlag"),ask.get("rpNo"),trp.substring(13),requestDate);
			if(rpaskfor != null)
			{
				rpaskfor.start(1);
				if(rpaskfor.equal(tpol[0]+tpol[1]))
					rpaskfor.delete();
				rpaskfor.start(0);
				rpaskfor.set("typeOfPol",ask.get("receiptFlag"));
				rpaskfor.set("rpNo",ask.get("rpNo"));
				rpaskfor.set("policyNo",tpol[0]);
				rpaskfor.set("payPeriod",tpol[1]);
				rpaskfor.set("requestDate",requestDate);
				rpaskfor.set("ownerSaleID",saleID);
				rpaskfor.set("askSaleID",ask.get("askSaleID"));
				rpaskfor.insert();
			}
			if(ucrp != null)
			{
				ucrp.putBytes(ask.copy().getBytes());
				ucrp.insert();
			}
		}
		if(cask != null)
		{
			cask.set("askSaleID",askSaleID);
			cask.set("ownerSaleID",saleID);
			cask.set("branch",branch);
			cask.set("requestDate",requestDate);
			cask.set("userID",userID);
			cask.set("status","N");
			cask.set("reserve"," ");
			cask.insert();
		}
	}	
	private String []  updateCtrl(String flagReceipt,String rpno,String policyNo,String reqDate) throws Exception
	{
		String [] tpol = {"00000000" ,"000000"};
		Mrecord trec = null;
		if(flagReceipt.charAt(0) == 'I')
			trec = irc;
		else if(flagReceipt.charAt(0) == 'O')
			trec = orc;
		else if(flagReceipt.charAt(0) == 'W')
			trec = wrc;
		else if(flagReceipt.charAt(0) == 'A') // Use for universal 
			trec = ulrc;
		System.out.println("flagReceipt..........."+flagReceipt+"   "+rpno);
		for (boolean st = trec.equal(rpno);st && rpno.compareTo(trec.get("rpNo")) == 0;st=trec.next())
		{
			if(policyNo.compareTo(trec.get("policyNo")) == 0)
			{
				tpol[0] = trec.get("policyNo");
				tpol[1] = trec.get("payPeriod");
				trec.set("requestDate",reqDate);
				trec.set("time",M.inc(trec.get("time")));
				trec.update();
				break;
			}	
		}
		return tpol;
	}
}
