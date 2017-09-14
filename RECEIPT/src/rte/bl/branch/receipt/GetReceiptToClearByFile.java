package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.support.DateInfo;
import  utility.cfile.CFile;
import  rte.bl.branch.*;
import  rte.*;
import utility.underwrite.ClientYC;
public class  GetReceiptToClearByFile implements  InterfaceRpt
{
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("args === "+args.length);
		String remote	= args[0];  // true or false 
                String appserv = args[1];  // application server  <--> braxxxapp
                String filename = args[2]; // masic output stream  

		// saleID , yyyymm : current ask  ,  ucrp filename , branch
		getAllRequestedRp(args[3],args[4],args[5],args[6],args[7]);		

		System.out.println("End startSearch process! ");
		rte.RteRpt.recToTemp(temptoprint, filename);
                System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		temptoprint.close();
	}
	public TempMasicFile getTempFile()
	{
		return temptoprint;
	}
	// yyyymm = current ask month
	// ucrpyyyymm = current ucrp month (filename)
	Mrecord irc;
	Mrecord orc;
	Mrecord wrc;
	Mrecord trc;
	Mrecord ord;
	Mrecord whl;
	Mrecord ul;
	Mrecord ind;
	Mrecord name;
	Mrecord person;
	Mrecord ulrc ;
	Mrecord xrpchg;
	public void getAllRequestedRp(String saleID,String yyyymm,String ucrpyyyymm,String branch,String byKey) throws Exception
	{
		Mrecord ask = null; 
		if(yyyymm.trim().length() == 6)
			ask = CFile.opens("ask"+yyyymm+"@cbranch");
		Mrecord ucrp = null;
		if(ucrpyyyymm.trim().length() > 2)
		 	ucrp = CFile.opens(ucrpyyyymm+"@cbranch");
		irc = CFile.opens("irctrl@receipt");
		orc = CFile.opens("orctrl@receipt");
		wrc = CFile.opens("wrctrl@receipt");		
		xrpchg = CFile.opens("xrpchg@cbranch");
		if (Masic.fileStatus("ulrctrl@universal") >= 2)
		{
			ulrc = CFile.opens("ulrctrl@universal");
			ul = CFile.opens("universallife@universal");
		}
		trc = CFile.opens("trpctrl@receipt");		
		ord = CFile.opens("ordmast@mstpolicy");
		whl = CFile.opens("whlmast@mstpolicy");
		ind = CFile.opens("indmast@mstpolicy");
		name = CFile.opens("name@mstperson");
		person  = CFile.opens("person@mstperson");
		temptoprint = new TempMasicFile("rptbranch",field,len);
		temptoprint.keyField(false,false,new String [] {"rpNo","receiptFlag"});
                temptoprint.buildTemp();
		System.out.println("open file get all request rp ");
		if(ask != null)
		{
                        // Comment  on 16/05/2557 
	//		if (CheckAskReceipt.inAskYYMM(ask , saleID ,"" ,1,false,branch) != 0 )
	//		{
				searchRPToClear(ask,saleID,M.ctoi(byKey),branch);
	//		}
		}
		if(ucrp != null)
			searchRPToClear(ucrp,saleID,M.ctoi(byKey),branch); 
	}
	TempMasicFile  temptoprint;
	String [] field = {"requestDate","askSaleID","ownerSaleID","rpNo","policyNo","payPeriod","premium","receiptFlag","currentStatus","reasonCode"};
	int [] len = {8,10,10,12,8,6,9,1,1,2};
	String sysdate ;
	public void searchRPToClear(Mrecord ask ,String saleID,int currkey,String branch) throws Exception
	{
		
		String tsysTime =  Masic.time("commontable").substring(8);
		String tsysDate = DateInfo.sysDate();
		boolean st ;
		String ask_saleID ;
		sysdate = DateInfo.sysDate();
		ask.start(currkey);
		st = ask.equal(branch+saleID);
		System.out.println("ask sale == "+ask.get("askSaleID")+"   "+ask.get("ownerSaleID")+" "+ask.get("rpNo"));
		while (st ==true){
			if (currkey == 0 )
				ask_saleID = ask.get("askSaleID");
			else
				ask_saleID = ask.get("ownerSaleID");
			if (!ask_saleID.equals(saleID))
				break ;
			System.out.println("ask --"+ask.get("rpNo")+"   "+ask.get("askSaleID")+"   "+ask.get("receiptFlag"));
			switch (ask.get("receiptFlag").charAt(0)){
				case 'I' : if (CheckAskReceipt.checkUCStatus(irc,ask.get("rpNo"),ask.get("requestDate") ) )
					 {
					//	temptoprint.set("currentStatus",irc.get("currentStatus"));
						temptoprint.set("requestDate",ask.get("requestDate"));
						temptoprint.set("askSaleID",ask.get("askSaleID"));
						temptoprint.set("ownerSaleID",ask.get("ownerSaleID"));
						temptoprint.set("rpNo",ask.get("rpNo"));
						temptoprint.set("policyNo",irc.get("policyNo"));
						temptoprint.set("payPeriod",irc.get("payPeriod"));
						temptoprint.set("premium",M.setlen(irc.get("premium"),9));           
						temptoprint.set("reasonCode",irc.get("reasonCode"));           
						temptoprint.set("receiptFlag","I");
						temptoprint.set("currentStatus",getCurrentStatus(irc.get("policyNo"),irc.get("currentStatus")));
						if (temptoprint.get("currentStatus").charAt(0) == 'C' && irc.get("currentStatus").charAt(0) == 'N')
						{
							   CheckAskReceipt.insertXrcchg(xrpchg,'I',irc.copy(),"W",tsysDate,tsysTime,"X000021");
							   irc.set("currentStatus","W");
							   irc.set("sysDate",DateInfo.sysDate());
							   irc.set("reasonCode","21");
							   irc.update();



	
                             				   temptoprint.set("reasonCode","21");
						}

						temptoprint.insert();
					}
					break;
				case 'O' :
					 Mrecord ttrc = null;
					 if (orc.equal(ask.get("rpNo")))
					 {
						ttrc = orc ;
						System.out.println("found in orc");
					 }
					 else if (wrc.equal(ask.get("rpNo")))
					 {
						ttrc = wrc ;				
						System.out.println("found in wrc");
					 }
					 if(ttrc == null)
						break;
					 boolean ss = CheckAskReceipt.checkUCStatus(ttrc,ask.get("rpNo"),ask.get("requestDate") ); 
					 if(ss)
					 {
						temptoprint.set("requestDate",ask.get("requestDate"));
						temptoprint.set("askSaleID",ask.get("askSaleID"));
						temptoprint.set("ownerSaleID",ask.get("ownerSaleID"));
						temptoprint.set("rpNo",ask.get("rpNo"));
					//	temptoprint.set("currentStatus",ttrc.get("currentStatus"));
						temptoprint.set("policyNo",ttrc.get("policyNo"));
						temptoprint.set("payPeriod",ttrc.get("payPeriod"));
						temptoprint.set("premium",M.setlen(ttrc.get("premium"),9));           
						temptoprint.set("reasonCode",ttrc.get("reasonCode"));           
						temptoprint.set("receiptFlag","O");
						temptoprint.set("currentStatus",getCurrentStatus(ttrc.get("policyNo"),ttrc.get("currentStatus")));
						 if (temptoprint.get("currentStatus").charAt(0) == 'C' && ttrc.get("currentStatus").charAt(0) == 'N')
						{
							
							   CheckAskReceipt.insertXrcchg(xrpchg,'O',ttrc.copy(),"W",tsysDate,tsysTime,"X000021");
							   ttrc.set("currentStatus","W");
							   ttrc.set("sysDate",DateInfo.sysDate());
							   ttrc.set("reasonCode","21");
							   ttrc.update();
                                			temptoprint.set("reasonCode","21");
						}

						temptoprint.insert();
					}
				//	if("25274344".compareTo(ttrc.get("policyNo")) == 0)
				//		System.out.println("return currentStatus+++++++++++++++++++"+temptoprint.get("currentStatus"));
					break;	
				case 'A' : if (CheckAskReceipt.checkUCStatus(ulrc,ask.get("rpNo"),ask.get("requestDate") ) ){
						temptoprint.set("requestDate",ask.get("requestDate"));
						temptoprint.set("askSaleID",ask.get("askSaleID"));
						temptoprint.set("ownerSaleID",ask.get("ownerSaleID"));
						temptoprint.set("rpNo",ask.get("rpNo"));
				//		temptoprint.set("currentStatus",wrc.get("currentStatus"));
						temptoprint.set("policyNo",ulrc.get("policyNo"));
						temptoprint.set("payPeriod",ulrc.get("payPeriod"));
						temptoprint.set("premium",M.setlen(ulrc.get("premium"),9));           
						temptoprint.set("reasonCode",ulrc.get("reasonCode"));           
						temptoprint.set("receiptFlag","A");
						temptoprint.set("currentStatus",getCurrentStatus(ulrc.get("policyNo"),ulrc.get("currentStatus")));
						if (temptoprint.get("currentStatus").charAt(0) == 'C' && ulrc.get("currentStatus").charAt(0) == 'N')
						{
							
						        CheckAskReceipt.insertXrcchg(xrpchg,'U',ulrc.copy(),"W",tsysDate,tsysTime,"X000021");
							ulrc.set("currentStatus","W");
							ulrc.set("sysDate",DateInfo.sysDate());
							ulrc.set("reasonCode","21");
							ulrc.update();
                          		     		temptoprint.set("reasonCode","21");
						}

						temptoprint.insert();
					}
					break;
				case 'W' : if (CheckAskReceipt.checkUCStatus(wrc,ask.get("rpNo"),ask.get("requestDate") ) ){
						temptoprint.set("requestDate",ask.get("requestDate"));
						temptoprint.set("askSaleID",ask.get("askSaleID"));
						temptoprint.set("ownerSaleID",ask.get("ownerSaleID"));
						temptoprint.set("rpNo",ask.get("rpNo"));
				//		temptoprint.set("currentStatus",wrc.get("currentStatus"));
						temptoprint.set("policyNo",wrc.get("policyNo"));
						temptoprint.set("payPeriod",wrc.get("payPeriod"));
						temptoprint.set("premium",M.setlen(wrc.get("premium"),9));           
						temptoprint.set("reasonCode",wrc.get("reasonCode"));           
						temptoprint.set("receiptFlag","W");
						temptoprint.set("currentStatus",getCurrentStatus(wrc.get("policyNo"),wrc.get("currentStatus")));
				
						if (temptoprint.get("currentStatus").charAt(0) == 'C' && wrc.get("currentStatus").charAt(0) == 'N')
						{
								
							   CheckAskReceipt.insertXrcchg(xrpchg,'W',wrc.copy(),"W",tsysDate,tsysTime,"X000021");
							   wrc.set("currentStatus","W");
							   wrc.set("sysDate",DateInfo.sysDate());
							   wrc.set("reasonCode","21");
							   wrc.update();
                          		     		temptoprint.set("reasonCode","21");
						}
						temptoprint.insert();
					}
					break;
				case 'T' : if (CheckAskReceipt.checkUCStatus(trc,ask.get("rpNo"),ask.get("requestDate") ) ){
						temptoprint.set("requestDate",ask.get("requestDate"));
						temptoprint.set("askSaleID",ask.get("askSaleID"));
						temptoprint.set("ownerSaleID",ask.get("ownerSaleID"));
						temptoprint.set("rpNo",ask.get("rpNo"));
						temptoprint.set("receiptFlag","T");
						temptoprint.set("currentStatus",trc.get("currentStatus"));
						temptoprint.set("policyNo",trc.get("policyNo"));
						temptoprint.set("payPeriod","000000");
						temptoprint.set("premium",M.setlen(trc.get("premium"),9));           
						temptoprint.set("reasonCode","00");
						temptoprint.insert();
					}
					break;
			}
			st = ask.next();
		}
	}
	public String getCurrentStatus(String policyNo,String currentStatus) throws Exception
	{
		if (temptoprint.get("receiptFlag").charAt(0) != 'A')
			return Receipt.getCurrentStatus(temptoprint.get("receiptFlag"), policyNo,temptoprint.get("payPeriod"),currentStatus,ind,ord,whl) ;
		else {
                       if (ul.equal(policyNo))
                       {
                                System.out.println("ul date "+ul.get("dueDate")+"  "+DateInfo.sysDate());
                                if (DateInfo.calDay(ul.get("dueDate"),DateInfo.sysDate(), new byte[3],new byte[3]) >= 31)
                                        return "1";
                       }

			return currentStatus;
                }

	/*	if(currentStatus.charAt(0) == 'W')
			return currentStatus;
		String dueDate = "00000000";
		int  day = 120;
		String med = "N";
		String lapseFlag ="N";
		String nameID = "0000000000";
		Mrecord master = null ;
		switch (temptoprint.get("receiptFlag").charAt(0))
		{
			case 'I' : if(ind.equal(policyNo))
				   {
					dueDate = DateInfo.nextMonth(ind.get("payPeriod")+"01"); 
					master = ind;
				   }
				   	
				  break;
			case 'O' : if( ord.equal(policyNo))
				   {
					dueDate = ord.get("dueDate");
					if(insure.PlanType.isPAPlan(ord.get("planCode")) || ord.get("policyStatus1").charAt(0) == 'F')
						day = 31;
					master = ord ;
				   }
				  break;
			case 'W' : if(whl.equal(policyNo))
				  {	
					dueDate = whl.get("dueDate");
					master = whl;
				  }
				  break;
		}
		if (dueDate.compareTo("00000000") == 0)
			return currentStatus;
		int diff = DateInfo.calDay(dueDate,sysdate,new byte[3],new byte[3]);
		System.out.println("diff ----------"+diff+"-----------------"+day);
		if(diff  > day)
		{
			if(day == 31)
				return "1";
			return "4";
		}
//		if("25274344".compareTo(master.get("policyNo")) == 0)
//			System.out.println("25274344...........................diff ---"+diff);
		if(diff > 31)
		{
			String [] nm = getName(master.get("nameID"));
			if(ClientYC.haveYC(nm[0],nm[1],nm[2],nm[3]))
			{
				if("LAO".indexOf(master.get("policyStatus1")) >=0) // lapse or สิน้สุด APL
					return "Y";
			}
			if("Y".indexOf(master.get("lapseFlag")) >= 0)
				if("LOA".indexOf(master.get("policyStatus1")) >= 0)
					return "L";
			if(master.get("hivFlag").charAt(0) == 'Y')
			{
				if("LO".indexOf(master.get("policyStatus1")) >= 0)
					return "H";
				else if ("A".indexOf(master.get("policyStatus1")) >=0)
					currentStatus = "5";
			}
			if("MB".indexOf(master.get("medical")) >= 0)
			{
				if(master.get("class").charAt(0) == 'S')
					if("LOA".indexOf(master.get("policyStatus1")) >= 0)
						return "S";
				if(master.get("class").charAt(0) != 'S')
				{
					if("O".indexOf(master.get("policyStatus1")) >= 0)
						return "M";
					else if("L".indexOf(master.get("policyStatus1")) >= 0 && diff > 61)
						 return "M";
					else if("A".indexOf(master.get("policyStatus1")) >= 0)
						currentStatus = "5";
					//	return "5";	
				}
			}
			if("NA".indexOf(master.get("medical")) >= 0)
				if("LOA".indexOf(master.get("policyStatus1")) >= 0)
					currentStatus = "5";
				//	return "5";		
		}
		else 
			return currentStatus;	
		
		return currentStatus;	*/
	}
	public String []   getName(String nameID)
	{
		String [] nm = {"0000000000000","","","00000000"}; // id , firstName , lastName,birthDate 
		if(name.equal(nameID))
		{
			nm [1] = name.get("firstName");
			nm [2] = name.get("lastName");
			if(person.equal(name.get("personID")))
			{
				nm[0] = person.get("referenceID");
				nm[3] = person.get("birthDate");
			}
		}
		return nm;
	}
}

