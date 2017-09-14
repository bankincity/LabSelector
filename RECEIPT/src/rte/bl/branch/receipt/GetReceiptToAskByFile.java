package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import  utility.branch.sales.BraSales;
import  rte.bl.branch.*;
import java.util.*;
import rte.*;
import utility.rteutility.*;
import layout.receipt.*;
import utility.cfile.Rtemp;
import utility.prename.Prename;
import utility.support.DateInfo;
import utility.address.*;
public class  GetReceiptToAskByFile implements  InterfaceRpt
{
	Mrecord ord;       
	Mrecord whl;
        Mrecord ind;
	Mrecord ulmaster;
	String branch = "";
	Mrecord mastinv;
	Mrecord address;
	Mrecord xrpchg;
	Mrecord irc;
	Mrecord orc;
	Mrecord wrc;
	Mrecord urc;
	String tsysTime;
	String tsysDate;
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("args === "+args.length);
		for (int i = 0 ; i < args.length;i++)
		{
			System.out.println("args "+i+" ----  "+args[i]);
		}
		String remote	= args[0];  // true or false 
		if(remote.compareTo("false") == 0)
			PublicRte.setRemote(false);
		else
			PublicRte.setRemote(true);
                String appserv = args[1];  // application server  <--> braxxxapp
		branch = appserv.substring(3,6);
		tsysTime =  Masic.time("commontable").substring(8);
                tsysDate = DateInfo.sysDate();
		
                String filename = args[2]; // masic output stream  
		ord = CFile.opens("ordmast@mstpolicy");
                whl = CFile.opens("whlmast@mstpolicy");
                ind = CFile.opens("indmast@mstpolicy");
		if (Masic.fileStatus("universallife@universal") > 2)
			ulmaster = CFile.opens("universallife@universal");
		mastinv = CFile.opens("masterinvalid@mstlogfile");
		address = CFile.opens("address@mstperson");

		irc = CFile.opens("irctrl@receipt");
                orc = CFile.opens("orctrl@receipt");
                wrc = CFile.opens("wrctrl@receipt");
		urc = CFile.opens("ulrctrl@universal");
                xrpchg = CFile.opens("xrpchg@cbranch");



		invaddress = new TempMasicFile(appserv,invfield,invlen);
                invaddress.keyField(false,false,new String [] {"policyNo"});
                invaddress.buildTemp();

		//   yyyymm,saleID,typeOfAsk
		getReceiptToAsk(args[3],args[4],M.ctoi(args[5]));		
		System.out.println("End startSearch process! ");
		rte.RteRpt.recToTemp(temptoprint, filename);
                System.out.println("write temp --> complete");
                System.out.println("write report status to "+appserv+"  "+filename);
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
                System.out.println("success write report status to "+appserv+"  "+filename);
		temptoprint.close();
	}
	
	TempMasicFile  invaddress;
	TempMasicFile  temptoprint;
	Mrecord nameMaster;
	String [] field = {"policyNo","rpNo","payPeriod","premium","receiptFlag","currentStatus","reasonCode","dueDate","name","time","flagForReinsured"};
	int [] len = {8,12,6,9,1,1,2,8,80,1,5};
	// type of Ask '1' ind  , '2' ord
	private void getReceiptToAsk(String yyyymm,String saleID,int typeOfAsk) throws Exception
	{
		

		nameMaster  = Masic.opens("name@mstperson"); 
		if(nameMaster == null || nameMaster.lastError() != 0)
			throw new Exception("Can not open name@mstperson");

		temptoprint = new TempMasicFile("rptbranch",field,len);
                temptoprint.keyField(false,false,new String [] {"policyNo","rpNo"});
                temptoprint.buildTemp();

                System.out.println("open file get all request rp "+saleID+"  "+yyyymm);
        //        RemoteTask rmt = new RemoteTask("searchreceipt");
        //        utility.rteutility.LocalTask rmt = new utility.rteutility.LocalTask();
//		Result res = rmt.exec("rte.search.receipt.SearchBySaleID", new String [] {saleID,"NAXW","P"+yyyymm});
		Result res = PublicRte.getResult("searchreceipt","rte.search.receipt.SearchBySaleID", new String [] {saleID,"NAXW","P"+yyyymm});

                System.out.println("after file get all request rp "+res.status());
		if(res.status() != 0)
			throw new Exception((String)res.value());
		Vector [] vRp = (Vector[])res.value();		
                System.out.println("Vector after search "+vRp.length);
		if(typeOfAsk >= 2)
		{
			temptoprint.set("receiptFlag","O");
			moveDataToTemp(vRp[0],"O");		
			temptoprint.set("receiptFlag","W");
			moveDataToTemp(vRp[2],"W");		
			temptoprint.set("receiptFlag","U");
			moveDataToTemp(vRp[3],"U");		
			typeOfAsk-=2;
		}
		if(typeOfAsk > 0)
		{
			temptoprint.set("receiptFlag","I");
			moveDataToTemp(vRp[1],"I");		
		}
	}
	String [] tfield = {"rpNo","policyNo","payPeriod","premium","time","currentStatus","reasonCode","dueDate","nameID","rpDueDate","submitBranch"};
	int [] tlen = {12,8,6,9,1,1,2,8,13,8,3};

	String [] invfield = {"policyNo","name","address1","address2"};
	int [] invlen = {8,80,130,130};

	String [] finance = {"002","003","004","005","006","008","009","000","931","932","933","934","935","936","937","938"};
	private boolean isFinanceRp(String rpNo)
	{
		for (int i = 0 ; i < finance.length;i++)
		{
			if(finance[i].compareTo(rpNo) == 0)
				return true;
		}
		return false ;
	}
	private void moveDataToTemp(Vector vec ,String flagReceipt) throws Exception
	{
		Rtemp  rec = new Rtemp(tfield,tlen);
		for (int i = 0 ; i < vec.size();i++)
		{
			rec.putBytes((byte [])vec.elementAt(i));
                        System.out.println("startToTemp  move data =="+rec.get("rpNo"));
			if(isFinanceRp(rec.get("rpNo").substring(0,3)))
				continue;
	//		if(rec.get("submitBranch").compareTo("000") != 0 && branch.compareTo(rec.get("submitBranch")) != 0)
	//			continue; 
			temptoprint.set("rpNo",rec.get("rpNo"));
			temptoprint.set("policyNo",rec.get("policyNo"));
			temptoprint.set("payPeriod",rec.get("payPeriod"));
			temptoprint.set("premium",rec.get("premium"));
			temptoprint.set("time",M.inc(rec.get("time")));
			temptoprint.set("currentStatus",rec.get("currentStatus"));
			temptoprint.set("dueDate",rec.get("rpDueDate"));
			temptoprint.set("reasonCode",rec.get("reasonCode"));
			temptoprint.set("name",getName(rec.get("nameID")));
                        System.out.println("inToTemp  move data after get name  =="+rec.get("rpNo"));
			temptoprint.set("flagForReinsured",getMedOrHiv(flagReceipt,rec.get("policyNo")));
                        System.out.println("inToTemp  move data after get medHiv  =="+rec.get("rpNo"));
			temptoprint.set("currentStatus",getFlagForReinsured(flagReceipt,rec.get("policyNo"),rec.get("payPeriod"),rec.get("currentStatus")));
                        System.out.println("inToTemp  move data after flagRein  =="+rec.get("rpNo"));
			if (temptoprint.get("currentStatus").charAt(0) == 'C' && rec.get("currentStatus").charAt(0) == 'N')
			{
				updateStatusToW(rec.get("rpNo"),flagReceipt);	
				temptoprint.set("reasonCode","21");
			}
				
			temptoprint.insert();
			if(mastinv.equal(flagReceipt+rec.get("policyNo")+"MA"))
			{
			
			System.out.println("...................missing address..............."+rec.get("policyNo"));
				invaddress.set("policyNo",rec.get("policyNo"));
				invaddress.set("name",temptoprint.get("name"));
				String [] addr = getInvalidAddress(flagReceipt,rec.get("policyNo"));
				invaddress.set("address1",addr[0]);
				invaddress.set("address2",addr[1]);
				invaddress.insert();
			}
		}
		if(invaddress.fileSize() > 0)
		{
			System.out.println("...........................................missing address...............");
			temptoprint.set(' ');
			temptoprint.set("rpNo","XXXXXXXXXXXX");
			temptoprint.set("name",invaddress.name());			
			temptoprint.insert();
		}			
	}
	private void updateStatusToW(String rpNo,String flagRp)
	{
		Mrecord rc = null;
		if (flagRp.charAt(0) == 'I')
			rc = irc;
		else if (flagRp.charAt(0) == 'O')
			rc = orc;
		else if (flagRp.charAt(0) == 'W')
			rc = wrc;
		else if (flagRp.charAt(0) == 'U')
			rc = urc;
		if (rc.equal(rpNo))
		{
			 CheckAskReceipt.insertXrcchg(xrpchg,flagRp.charAt(0),rc.copy(),"W",tsysDate,tsysTime,"X000021");
                         rc.set("currentStatus","W");
                         rc.set("sysDate",tsysDate);
                         rc.set("reasonCode","21");
                         rc.update();
		}
	}
	private String []  getInvalidAddress(String type,String policyNo)
	{
		Mrecord tmast = null;
		String [] taddress = {"",""};
		if(type.charAt(0) == 'I')
			tmast = ind;
		else if(type.charAt(0) == 'O')
			tmast =ord;
		else if(type.charAt(0) == 'W')
			tmast = whl;
		else if(type.charAt(0) == 'U')
			tmast = ulmaster;
		if(tmast.equal(policyNo))
		{
			if(address.equal(tmast.get("contactAddressID")))
			{
				taddress = AddressFnc.format2LineAddress(false,address.get("address") ,address.get("tumbon"),address.get("zipCode"));
			}
		}
		return taddress;			
	}
	private String  getName(String nameID)
	{
		if(nameMaster.equal(nameID))
			return  (Prename.getAbb(nameMaster.get("preName").trim())+nameMaster.get("firstName").trim()+" "+nameMaster.get("lastName").trim());
		return " ";
	}
	// to cancel receipt 
	public String getFlagForReinsured(String flagReceipt,String policyNo,String payPeriod,String currentStatus) throws Exception
	{
		if (flagReceipt.charAt(0) == 'U')
                {
                        if (DateInfo.calDay(ulmaster.get("dueDate"),tsysDate, new byte[3],new byte[3]) >= 31)
                                return "1";                
			return currentStatus;
                }
		String curr = Receipt.getCurrentStatus(flagReceipt, policyNo,payPeriod,currentStatus,ind,ord,whl) ;
		System.out.println("curr getFlagReinsured -----------------"+flagReceipt+" "+policyNo);
		if(curr.charAt(0) == '5')
		{
			 
			 Result res = PublicRte.getResult("blservice","rte.bl.service.renew.CheckPayPremium",new  String [] {flagReceipt,policyNo,DateInfo.sysDate(), "N","S"});
             		System.out.println("after rte.bl.service.renew.CheckPayPremium..........................."+res.status());
			System.out.println("policyno.........................."+policyNo);
	                if(res.status() < 0 )
        	                throw new Exception((String)res.value());
			else if(res.status() > 0)
				return curr;
				
			System.out.println("bingo..................................................1");
                	Vector vPolicyErr = (Vector)res.value();
			System.out.println("bingo..................................................2");
			Vector vdoc = (Vector)vPolicyErr.elementAt(4);
			for (int i = 0 ; i < vdoc.size() ; i++)
			{
			
				String [] str = (String [])vdoc.elementAt(i);
				if(str[0].charAt(0) == '3')
				{
					if(str[2].charAt(0) == '2')
						curr = "8";
					else if(str[2].charAt(0) == '3')
						curr = "9";
				}
			}			

		}
		System.out.println("curr-----------checkpaypremium---------------------------------------------->"+curr);	
		return curr;
	//eturn "";
	}
	public String getMedOrHiv(String flagMaster ,String policyNo)
	{
		Mrecord master;
		if(flagMaster.charAt(0) == 'I')
			master = ind;
		else if(flagMaster.charAt(0) == 'U')
			master  = ulmaster;
		else if(flagMaster.charAt(0) == 'O')
			master  = ord;
		else
			master = whl;
		String med = "";
		if (!master.equal(policyNo))
			return med ;
                if (master.get("policyStatus1").charAt(0) == 'B')
                        med = "B";
                else {
                        if("MB".indexOf(master.get("medical")) >= 0)
		        	med = "M";
        		else 
	        		med = "N";
                }
		String rem = "N";
		if(master.get("hivFlag").charAt(0) == 'Y')
                	rem = "H";
		if (flagMaster.charAt(0) != 'U')
		{	
			if (master.get("lapseFlag").equals("Y"))
			{
				if (rem.charAt(0) == 'H')
                       		   	rem = "M";
                     		else
					rem = "X";
			}
		}
		return med+rem;		
	}
}


