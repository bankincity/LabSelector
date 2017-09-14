package rte.bl.branch.receipt;
import manit.*;
import utility.branch.sales.BraSales;
import rte.bl.branch.TempMasicFile ;
import utility.cfile.CFile;
import utility.rteutility.PublicRte;
import utility.prename.*;
import utility.support.*;
import utility.sales.*;
import java.util.Vector;
// ************************************************************* //
// ถ้ามีแก้ อย่าลืมไปแก้ที่ rte.search.branch.receipt.UCRPYYYYMM //
// ************************************************************* //
public class UCRPYYYYMMOnly 
{
	Mrecord ucrp;
	Mrecord ucyymm;
	Mrecord ucdetail;
	BraSales bsale;
	Mrecord irc;
	Mrecord orc;
	Mrecord wrc;
	Mrecord trc;
	Mrecord urc;
	boolean remote = false ;
	String currentMonth ;
	public UCRPYYYYMMOnly() throws Exception
	{
		PublicRte.setRemote(false);
		bsale = new BraSales();
		irc = CFile.opens("irctrl@receipt");
		orc = CFile.opens("orctrl@receipt");
		wrc = CFile.opens("wrctrl@receipt");
		trc = CFile.opens("trpctrl@receipt");
		String sysMonth = DateInfo.sysDate().substring(0,6);
	}
	public  void buildUCyyyymm(String month,String branch,String rpNo) throws Exception
	{
		ucyymm = CFile.opens("uc"+month+"@cbranch");
		ucdetail = CFile.opens("ucdetail"+month+"@cbranch");

		CFile.build("ucfeeclearing"+month+"@cbranch");

//		ucrp = CFile.opens("ucrp"+month+"@cbranch");
		ucrp = CFile.opens("askyyyymm@cbranch");
		Record trec = null;
		int count = 0;
		ucrp.start(2);
		if(ucrp.equal(branch+rpNo))
		{
			ucyymm.set(' ');
			ucyymm.set("salesID",ucrp.get("askSaleID"));
			ucyymm.set("branch",ucrp.get("branch"));
			try {
				bsale.getBySalesID(ucrp.get("askSaleID"));
				bsale.getStruct();
			}
			catch (Exception e)
			{
				return;
			}
			ucyymm.set("strID",bsale.getSnRec("strID"));
			ucyymm.set("unitSalesID",bsale.getSnSales("B","salesID"));
			ucyymm.set("divisionSalesID",bsale.getSnSales("D","salesID"));
			ucyymm.set("regionSalesID",bsale.getSnSales("G","salesID"));
			ucyymm.set("departSalesID",bsale.getSnSales("L","salesID"));
			ucyymm.insert();
			ucdetail.set('0');
			ucdetail.set("salesID",ucrp.get("askSaleID"));
			ucdetail.set("rpNo",ucrp.get("rpNo"));
			ucdetail.set("receiptFlag",ucrp.get("receiptFlag"));
			ucdetail.set("currentStatus","U");
			ucdetail.set("payDate","00000000");
			ucdetail.set("requestDate",ucrp.get("requestDate"));
			trec = getReceipt(ucrp.get("rpNo"),ucrp.get("receiptFlag"),ucrp.get("requestDate"),true);
			if(trec != null)
			{
				if(ucrp.get("receiptFlag").charAt(0) == 'T')
					ucdetail.set("payPeriod","000000");
				else
					ucdetail.set("payPeriod",trec.get("payPeriod"));
				ucdetail.set("policyNo",trec.get("policyNo"));
				ucdetail.set("premium",M.setlen(trec.get("premium"),9));
				ucdetail.insert();
			}
		}	
		ucdetail.close();	
		ucyymm.close();
		ucrp.close();
	}
	public void buildAskyyyymm(String month) throws Exception
	{
		ucrp = CFile.opens("askyyyymm@cbranch");
		for (boolean st = ucrp.first() ; st ; st=ucrp.next())
		{
			if("IOWT".indexOf(ucrp.get("receiptFlag")) < 0)
				ucrp.delete();
			switch (ucrp.get("receiptFlag").charAt(0))
			{
				case 'I' : if (!CheckAskReceipt.checkUCStatus(irc,ucrp.get("rpNo"),ucrp.get("requestDate"))) 			
					 {
						ucrp.delete();
					 }
					 else {
						irc.set("currentStatus","U");
					 }
					 break;
				case 'O' :
					  Mrecord ttrc = null;
                                         if (orc.equal(ucrp.get("rpNo")))
                                         {
                                                ttrc = orc ;
                                                System.out.println("found in orc");
                                         }
                                         else if (wrc.equal(ucrp.get("rpNo")))
                                         {
                                                ttrc = wrc ;
                                                System.out.println("found in wrc");
                                         }
					 else{
						System.out.println("not found"+new String(ucrp.getBytes()));
						ucrp.delete();
						break;
					}
					if (!CheckAskReceipt.checkUCStatus(ttrc,ucrp.get("rpNo"),ucrp.get("requestDate"))) 			
					 {
						ucrp.delete();
					 }
					 else {
						ttrc.set("currentStatus","U");
					 }
					 break;
				
				case 'W' : if (!CheckAskReceipt.checkUCStatus(wrc,ucrp.get("rpNo"),ucrp.get("requestDate"))) 			
					 {
						ucrp.delete();
					 }
					 else {
						wrc.set("currentStatus","U");
					 }
					 break;
				case 'U' : if (!CheckAskReceipt.checkUCStatus(urc,ucrp.get("rpNo"),ucrp.get("requestDate"))) 			
					 {
						ucrp.delete();
					 }
					 else {
						urc.set("currentStatus","U");
					 }
					 break;
				
				case 'T' : if (!CheckAskReceipt.checkUCStatus(trc,ucrp.get("rpNo"),ucrp.get("requestDate"))) 			
					 {
						ucrp.delete();
					 }
					 else {
						trc.set("currentStatus","U");
					 }
					 break;
				
			}
			
		}
		ucrp.close();
	}
	public TempMasicFile []  searchUCRPForBranch(String month,String branch) throws Exception
	{		
		String [] field = {"strID","name","positionName","unit","division","region","depart","ucCode","divisionCode","regionCode","departCode"};
		int [] len = {10,80,40,80,80,80,80,8,10,10,10};
		String [] field1 = {"strID","rpNo","policyNo","insuredName","payPeriod","premium","receiptFlag","requestDate","currentStatus","payDate","submitNo","debtBook","answerDebtBook"};
		int [] len1 = {10,12,8,80,6,9,1,8,1,8,12,20,20};

		TempMasicFile temp = new TempMasicFile("tmpbranch",field,len);
		temp.keyField(false,false ,new String [] {"strID"});
		temp.buildTemp();
		TempMasicFile temp1 = new TempMasicFile("tmpbranch",field1,len1);
		temp1.keyField(false,false ,new String [] {"strID","policyNo","payPeriod","rpNo"});
		temp1.buildTemp();
		ucyymm = CFile.opens("uc"+month+"@cbranch");
		ucdetail = CFile.opens("ucdetail"+month+"@cbranch");
		for (boolean st = ucyymm.first();st;st=ucyymm.next())
		{
			if(branch.compareTo(ucyymm.get("branch")) != 0)
				continue;
			System.out.println("strid-----------------------"+ucyymm.get("strID"));
			temp.set("strID",ucyymm.get("strID"));
			temp.set("name",getSaleName(ucyymm.get("salesID"),true));
			temp.set("ucCode",personID);
			temp.set("positionName",positionName);
			temp.set("unit",getSaleName(ucyymm.get("unitSalesID"),true));
			temp.set("division",getSaleName(ucyymm.get("divisionSalesID"),true));
			temp.set("divisionCode",positionCode);
			temp.set("region",getSaleName(ucyymm.get("regionSalesID"),true));
			temp.set("regionCode",positionCode);
			temp.set("depart",getSaleName(ucyymm.get("departSalesID"),true));
			temp.set("departCode",positionCode);
			temp.insert();
			for (boolean ss = ucdetail.equalGreat(ucyymm.get("salesID")) ; 
				ss && ucyymm.get("salesID").compareTo(ucdetail.get("salesID")) == 0; 
				ss = ucdetail.next())
			{
				System.out.println("saleid === "+ucyymm.get("salesID")+"    "+ucdetail.get("salesID"));
				temp1.set(' ');
				temp1.set("strID",ucyymm.get("strID"));
				temp1.set("rpNo",ucdetail.get("rpNo"));
				temp1.set("receiptFlag",ucdetail.get("receiptFlag"));
				temp1.set("requestDate",ucdetail.get("requestDate"));
				if("IOW".indexOf(ucdetail.get("receiptFlag")) >= 0)
				{	
					temp1.set("policyNo",ucdetail.get("policyNo"));
					temp1.set("insuredName",getName(temp1.get("policyNo"),ucdetail.get("receiptFlag")));
					temp1.set("payPeriod",ucdetail.get("payPeriod"));
					temp1.set("premium",ucdetail.get("premium"));
				}
				else {
					temp1.set("policyNo",ucdetail.get("policyNo"));
					temp1.set("payPeriod","000000");
					temp1.set("premium","000000000");
				}
			//f("DUOM".indexOf(ucdetail.get("currentStatus")) >= 0 && ucdetail.compareTo("payDate").compareTo("00000000") == 0)
				if(ucdetail.get("currentStatus").charAt(0) != 'S')
					updateCurrentStatus();
				temp1.set("currentStatus",ucdetail.get("currentStatus"));
				temp1.set("payDate",ucdetail.get("payDate"));
				temp1.set("submitNo",ucdetail.get("submitNo"));
				temp1.set("debtBook",ucdetail.get("debtBook"));
				temp1.set("answerDebtBook",ucdetail.get("returnDebtBook"));
				temp1.insert();		
			}
		}
		System.out.println("temp size------------->"+temp.fileSize()+" temp1----------> "+temp1.fileSize());
		return new TempMasicFile [] {temp,temp1};
	}
	public TempMasicFile []  searchDetailUCRPForInsure(String month,String strID) throws Exception
	{		
		String [] field = {"strID","name","positionName","unit","division","region","depart","ucCode","divisionCode","regionCode","departCode","bookToCancel"};
		int [] len = {10,80,40,80,80,80,80,8,10,10,10,20};
		String [] field1 = {"strID","rpNo","policyNo","insuredName","payPeriod","premium","receiptFlag","requestDate","currentStatus","payDate","submitNo","debtBook","answerDebtBook"};
		int [] len1 = {10,12,8,80,6,9,1,8,1,8,12,20,20};

		TempMasicFile temp = new TempMasicFile("tmpbranch",field,len);
		temp.keyField(false,false ,new String [] {"strID"});
		temp.buildTemp();
		TempMasicFile temp1 = new TempMasicFile("tmpbranch",field1,len1);
		temp1.keyField(false,false ,new String [] {"strID","policyNo","payPeriod","rpNo"});
		temp1.buildTemp();
		ucyymm = CFile.opens("uc"+month+"@cbranch");
		ucdetail = CFile.opens("ucdetail"+month+"@cbranch");
		for (boolean st = ucyymm.equalGreat(strID);st;st=ucyymm.next())
		{
			if(strID.compareTo("0000000000") != 0 && strID.compareTo(ucyymm.get("strID")) != 0)
				break;
			System.out.println("strid-----------------------"+ucyymm.get("strID"));
			temp.set(' ');
			temp.set("strID",ucyymm.get("strID"));
			temp.set("name",getSaleName(ucyymm.get("salesID"),true));
			temp.set("ucCode",personID);
			temp.set("positionName",positionName);
			temp.set("unit",getSaleName(ucyymm.get("unitSalesID"),true));
			temp.set("division",getSaleName(ucyymm.get("divisionSalesID"),true));
			temp.set("divisionCode",positionCode);
			temp.set("region",getSaleName(ucyymm.get("regionSalesID"),true));
			temp.set("regionCode",positionCode);
			temp.set("depart",getSaleName(ucyymm.get("departSalesID"),true));
			temp.set("departCode",positionCode);
			temp.set("bookToCancel",ucyymm.get("bookToCancel"));
			temp.insert();
			for (boolean ss = ucdetail.equalGreat(ucyymm.get("salesID")) ; 
				ss && ucyymm.get("salesID").compareTo(ucdetail.get("salesID")) == 0; 
				ss = ucdetail.next())
			{
				System.out.println("saleid === "+ucyymm.get("salesID")+"    "+ucdetail.get("salesID"));
				temp1.set(' ');
				temp1.set("strID",ucyymm.get("strID"));
				temp1.set("rpNo",ucdetail.get("rpNo"));
				temp1.set("receiptFlag",ucdetail.get("receiptFlag"));
				temp1.set("requestDate",ucdetail.get("requestDate"));
				if("IOW".indexOf(ucdetail.get("receiptFlag")) >= 0)
				{	
					temp1.set("policyNo",ucdetail.get("policyNo"));
					temp1.set("insuredName",getName(temp1.get("policyNo"),ucdetail.get("receiptFlag")));
					temp1.set("payPeriod",ucdetail.get("payPeriod"));
					temp1.set("premium",ucdetail.get("premium"));
				}
				else {
					temp1.set("policyNo",ucdetail.get("policyNo"));
					temp1.set("payPeriod","000000");
					temp1.set("premium","000000000");
				}
			//f("DUOM".indexOf(ucdetail.get("currentStatus")) >= 0 && ucdetail.compareTo("payDate").compareTo("00000000") == 0)
				if(ucdetail.get("currentStatus").charAt(0) != 'S')
					updateCurrentStatus();
				temp1.set("currentStatus",ucdetail.get("currentStatus"));
				temp1.set("payDate",ucdetail.get("payDate"));
				temp1.set("submitNo",ucdetail.get("submitNo"));
				temp1.set("debtBook",ucdetail.get("debtBook"));
				temp1.set("answerDebtBook",ucdetail.get("returnDebtBook"));
				temp1.insert();		
			}
		}
		return new TempMasicFile [] {temp,temp1};
	}
	public TempMasicFile searchSummaryUCRP (String month,String branch) throws Exception
	{
		String [] field = {"strID","name","positionName","branch","struct","trpRequestDate","trp","rpRequestDate","premium"};
		int [] len = {10,80,40,3,120,8,5,8,10};
		TempMasicFile temp = new TempMasicFile("rptbranch",field,len);
		temp.keyField(true,false ,new String [] {"strID"});
		temp.buildTemp();
		ucyymm = CFile.opens("uc"+month+"@cbranch");
		ucdetail = CFile.opens("ucdetail"+month+"@cbranch");
		String salesID = "000000000";
		for (boolean st = ucyymm.first() ; st ; st = ucyymm.next())
		{
			if(branch.compareTo("000") != 0 && branch.compareTo(ucyymm.get("branch")) != 0)
				continue;
			salesID = ucyymm.get("salesID");
			temp.set('0');	
			temp.set("strID",ucyymm.get("strID"));
			temp.set("name",getSaleName(ucyymm.get("salesID"),true));
			temp.set("positionName",positionName);
			temp.set("branch",ucyymm.get("branch"));
			String strName = getSaleName(ucyymm.get("unitSalesID"),false)+" / ";
			strName += getSaleName(ucyymm.get("divisionSalesID"),false)+" / ";
			strName += getSaleName(ucyymm.get("regionSalesID"),false)+" / ";
			strName += getSaleName(ucyymm.get("departSalesID"),false);
			temp.set("struct",strName);	
			int trp = 0;
			String totprem = "0";
			Record trec = null;
			boolean ss = ucdetail.equalGreat(ucyymm.get("salesID"));
			while (ss &&  salesID.compareTo(ucdetail.get("salesID")) == 0)
			{
				if(ucdetail.get("receiptFlag").charAt(0) == 'T')
				{
//					trec = getReceiptRecord(ucyymm.get("rpNo"),ucyymm.get("receiptFlag"));
					temp.set("trpRequestDate",ucdetail.get("requestDate"));
					trp++;
				}	
				else {
//					trec = getReceiptRecord(ucyymm.get("rpNo"),ucyymm.get("receiptFlag"));
					temp.set("rpRequestDate",ucdetail.get("requestDate"));
					totprem = M.addnum(totprem,ucdetail.get("premium"));
				}
				ss = ucdetail.next();
			}	
			temp.set("trp",M.setlen(M.itoc(trp),5));	
			temp.set("premium",M.setlen(totprem,10));	
			temp.insert();
		}
		ucyymm.close();
		System.out.println("temp size ============="+temp.fileSize());
		return temp;
	}
	private void updateCurrentStatus() throws Exception
	{
		if(ucdetail.get("receiptFlag").charAt(0) == 'T')
		{
	 		ucdetail.set("currentStatus",trpCurrentStatus(ucdetail.get("rpNo"),ucdetail.get("requestDate")));
		}
		else {
			 Record trec =  getReceiptRecord (ucdetail.get("rpNo"),ucdetail.get("receiptFlag"),ucdetail.get("policyNo"));
			if(trec != null)
				updateStatusUC(trec);
		}
	}
	public TempMasicFile searchStructUCRP(String month,Vector vStrID) throws Exception
	{
		String [] field = {"strID","name","positionName","division","region","depart"};
		int [] len = {10,80,40,80,80,80};
		TempMasicFile temp = new TempMasicFile("rptbranch",field,len);
		temp.keyField(false,false ,new String [] {"strID"});
		temp.buildTemp();
		ucyymm = CFile.opens("uc"+month+"@cbranch");
		for (int i = 0 ; i < vStrID.size() ; i++)
		{
			String [] strID = (String [])vStrID.elementAt(i);
			for (boolean st = ucyymm.equalGreat(strID[1]);st;st=ucyymm.next())
			{
				if(strID[1].compareTo(ucyymm.get("strID")) != 0)
					break;
				temp.set("strID",ucyymm.get("strID"));
				temp.set("name",getSaleName(ucyymm.get("salesID"),true));
				temp.set("positionName",positionName);
				temp.set("division",getSaleName(ucyymm.get("divisionSalesID"),true));
				temp.set("region",getSaleName(ucyymm.get("regionSalesID"),true));
				temp.set("depart",getSaleName(ucyymm.get("departSalesID"),true));
				temp.insert();
			}
		}
		return temp;	
	}
	public TempMasicFile []  searchDetailUCRP(String month,String strID) throws Exception
	{
		
		String [] field = {"strID","name","positionName","division","region","depart"};
		int [] len = {10,80,40,80,80,80};
		String [] field1 = {"strID","rpNo","policyNo","insuredName","payPeriod","premium","currentStatus","payDate","submitNo","debtBook","answerDebtBook"};
		int [] len1 = {10,12,8,80,6,9,1,8,12,20,20};

		TempMasicFile temp = new TempMasicFile("rptbranch",field,len);
		temp.keyField(false,false ,new String [] {"strID"});
		temp.buildTemp();
		TempMasicFile temp1 = new TempMasicFile("rptbranch",field1,len1);
		temp1.keyField(false,false ,new String [] {"strID","policyNo","payPeriod","rpNo"});
		temp1.buildTemp();
		ucyymm = CFile.opens("uc"+month+"@cbranch");
		ucdetail = CFile.opens("ucdetail"+month+"@cbranch");
		for (boolean st = ucyymm.equalGreat(strID);st;st=ucyymm.next())
		{
			if(strID.compareTo(ucyymm.get("strID")) != 0)
				break;
			temp.set("strID",ucyymm.get("strID"));
			temp.set("name",getSaleName(ucyymm.get("salesID"),true));
			temp.set("positionName",positionName);
			temp.set("division",getSaleName(ucyymm.get("divisionSalesID"),true));
			temp.set("region",getSaleName(ucyymm.get("regionSalesID"),true));
			temp.set("depart",getSaleName(ucyymm.get("departSalesID"),true));
			temp.insert();
			for (boolean ss = ucdetail.equalGreat(ucyymm.get("salesID")) ; 
				ss && ucyymm.get("salesID").compareTo(ucdetail.get("salesID")) == 0; 
				ss = ucdetail.next())
			{
				System.out.println("saleid === "+ucyymm.get("salesID")+"    "+ucdetail.get("salesID"));
				temp1.set(' ');
				temp1.set("strID",ucyymm.get("strID"));
				temp1.set("rpNo",ucdetail.get("rpNo"));
				if("IOW".indexOf(ucdetail.get("receiptFlag")) >= 0)
				{	
					temp1.set("policyNo",ucdetail.get("policyNo"));
					temp1.set("insuredName",getName(temp1.get("policyNo"),ucdetail.get("receiptFlag")));
					temp1.set("payPeriod",ucdetail.get("payPeriod"));
					temp1.set("premium",ucdetail.get("premium"));
				}
			//f("DUOM".indexOf(ucdetail.get("currentStatus")) >= 0 && ucdetail.compareTo("payDate").compareTo("00000000") == 0)
				if(ucdetail.get("currentStatus").charAt(0) != 'S')
					updateCurrentStatus();
				temp1.set("currentStatus",ucdetail.get("currentStatus"));
				temp1.set("payDate",ucdetail.get("payDate"));
				temp1.set("submitNo",ucdetail.get("submitNo"));
				temp1.set("debtBook","");
				temp1.set("answerDebtBook","");
				temp1.insert();		
			}
		}
		System.out.println("temp size------------->"+temp.fileSize()+" temp1----------> "+temp1.fileSize());
		return new TempMasicFile [] {temp,temp1};
	}
	private String trpCurrentStatus(String rpNo,String requestDate)
	{
		for (boolean st = trc.equal(rpNo) ; st && rpNo.compareTo(trc.get("rpNo")) == 0 ; st= trc.next())
		{
			if(requestDate.compareTo(trc.get("requestDate")) == 0)
			{
				if("N".indexOf(trc.get("currentStatus")) >= 0)
					return "U";
				return trc.get("currentStatus");
			}
		}
		return "S";	
	}
	private Record getReceipt (String rpNo,String receiptFlag,String requestDate)
	{
		
		return (getReceipt ( rpNo, receiptFlag,requestDate,false));
	}
	private Record getReceipt (String rpNo,String receiptFlag,String requestDate,boolean flagUpdate)
	{
		Mrecord trec = null;
		if(receiptFlag.charAt(0) == 'T')
		{
			if(trc.equal(rpNo))
			{
				trec = trc;
			}
		}
		else if(receiptFlag.charAt(0) == 'I')
		{
			if(irc.equal(rpNo))
			{
				trec = irc;
			}
		}
		else if(receiptFlag.charAt(0) == 'A')
		{
			if(urc.equal(rpNo))
			{
				trec = urc;
			}
		}
		else {
			if(orc.equal(rpNo))
			{
				trec = orc;
			}
			else if (wrc.equal(rpNo))
			{
				trec = wrc ;
			}
		}
		if(trec != null)
		{
			boolean st = trec.equal(rpNo);
			for (;st && rpNo.compareTo(trec.get("rpNo")) == 0 ;st=trec.next())
			{
				if(trec.get("requestDate").compareTo(requestDate) == 0)
				{
					return trec.copy();
				}
			}
		}
		return null;
	}
	private Record getReceiptRecord (String rpNo,String receiptFlag,String policyNo)
	{
		Mrecord trec = null;
		if(receiptFlag.charAt(0) == 'I')
		{
			if(irc.equal(rpNo))
			{
				trec = irc;
			}
		}
		else if(receiptFlag.charAt(0) == 'A')
		{
			if(urc.equal(rpNo))
			{
				trec = urc;
			}
		}
		else {
			if(orc.equal(rpNo))
			{
				trec = orc;
			}
			else if (wrc.equal(rpNo))
			{
				trec = wrc ;
			}
		}
		if(trec != null)
		{
			boolean st = trec.equal(rpNo);
			for (;st && rpNo.compareTo(trec.get("rpNo")) == 0 ;st=trec.next())
			{
				if(trec.get("policyNo").compareTo(policyNo) == 0)
					return trec.copy();
			}
		}
		return null;
	}
	private void updateStatusUC(Record trec) throws Exception
	{
		if("PBE".indexOf(trec.get("currentStatus")) >= 0)
		{
			ucdetail.set("payDate",trec.get("payDate"));
			ucdetail.set("submitNo",trec.get("submitNo"));
			ucdetail.update();			
		}
		else if ("NAXW".indexOf(trec.get("currentStatus")) >= 0 )
		{
			if(ucdetail.get("requestDate").compareTo(trec.get("requestDate")) != 0)
			{
				if (M.itis(trec.get("requestDate"),'0'))
					ucdetail.set("currentStatus",trec.get("currentStatus"));
				else
					ucdetail.set("currentStatus","S");
				ucdetail.set("payDate","00000000");
				ucdetail.set("submitNo","000000000000");
				ucdetail.update();			
			}
			else {
				ucdetail.set("currentStatus","U");
				ucdetail.set("payDate","00000000");
				ucdetail.set("submitNo","000000000000");
				ucdetail.update();
			}
		}		
		else  
		{
			ucdetail.set("currentStatus",trec.get("currentStatus"));
			ucdetail.set("payDate","00000000");
			ucdetail.set("submitNo","000000000000");
			ucdetail.update();			
		}
	}
	Mrecord ord ;
	Mrecord ind;
	Mrecord whl;
	Mrecord nameMaster ;
	private String getName(String policyNo,String typePol) throws Exception
	{
		if (ord == null)
		{
			ord = CFile.opens("ordmast@mstpolicy");
			ind = CFile.opens("indmast@mstpolicy");
			whl = CFile.opens("whlmast@mstpolicy");
			nameMaster = CFile.opens("name@mstperson");	
		}
		Mrecord master = null;
		if(typePol.charAt(0) == 'O')
			master = ord;
		else if(typePol.charAt(0) == 'I')
			master = ind;
		else if(typePol.charAt(0) == 'W')
			master = whl;
		if(master != null && master.equal(policyNo))
		{
			if(nameMaster.equal(master.get("nameID")))
				return  (Prename.getAbb(nameMaster.get("preName").trim())+nameMaster.get("firstName").trim()+" "+nameMaster.get("lastName").trim());
		}
		return "";
	}
	String positionName = "";
	String positionCode = "";
	String personID	    = "";
	private String getSaleName(String saleID,boolean  fullName) throws Exception
	{
		bsale.getBySalesID(saleID);
		positionName = bsale.getSnRec("positionName");
		positionCode = bsale.getSnRec("strID");
		personID = bsale.getSnRec("personID");
		if(fullName)
			return (bsale.getSnRec("preName").trim()+bsale.getSnRec("firstName").trim()+" "+bsale.getSnRec("lastName").trim());
		return bsale.getSnRec("firstName");
	}
	public TempMasicFile searchSummaryUCRPForInsure (String month,String backward,String branch) throws Exception
	{
		currentMonth = DateInfo.previousMonth(DateInfo.sysDate()).substring(0,6);
//		currentMonth = "255202";
		String [] field = {"branch","saleID","name","strID","positionName","trp","premium","debtPremium","fee","month","process"};
		int [] len = {3,10,80,10,50,5,11,11,10,6,6};
		TempMasicFile temp = new TempMasicFile("tmpbranch",field,len);
		temp.keyField(false,false ,new String [] {"branch","month","saleID"});
		temp.buildTemp();
		if(M.ctoi(backward) > 0)
			backward = DateInfo.nextMonth(month+"01",0-M.ctoi(backward));
		else 
			backward = month;
		backward = backward.substring(0,6);
		System.out.println("month == "+month+"backward =="+backward+" branch =="+branch);
		while (month.compareTo(backward) >= 0)
		{
			searchByMonth(month,branch,temp);
			month = DateInfo.previousMonth(month);
		}
		return temp;
	}
	private String [] checkDetailUCRP(String saleID,String month,String branch) 
	{		
		String [] rp = {"0000","00000000000","00000000000","0"};
		
		String totprem = "0";
		String debtprem = "0";
		Record trec = null;
		int trp = 0;
		boolean ss = ucdetail.equalGreat(ucyymm.get("salesID"));
		while (ss &&  saleID.compareTo(ucdetail.get("salesID")) == 0)
		{
		//	System.out.println("bingo.......................1........."+ucdetail.get("receiptFlag")+"  "+ucdetail.get("rpNo"));
			if(ucdetail.get("receiptFlag").charAt(0) == 'T')
			{
	//		System.out.println("bingo.......................1.5");
	 				String tstat = trpCurrentStatus(ucdetail.get("rpNo"),ucdetail.get("requestDate"));
					if("UNWAX".indexOf(tstat) >= 0)
						trp++;
	//		System.out.println("bingo.......................1.75");
			}	
			else if ("U".indexOf(ucdetail.get("currentStatus")) >= 0 )
			{
			 	if(ucdetail.get("submitNo").trim().length()  ==  0 || M.itis(ucdetail.get("submitNo").trim(),'0'))
				{
				//	System.out.println("bingo.......................2");
					trec = getReceiptRecord(ucdetail.get("rpNo"),ucdetail.get("receiptFlag"),ucdetail.get("policyNo"));
					if(trec == null )
						System.out.println("trec --------------------------------is null");
					if(trec != null && "USNAXW".indexOf(trec.get("currentStatus")) >= 0)
						totprem = M.addnum(totprem,ucdetail.get("premium"));
				}
			}
			else if ("D".indexOf(ucdetail.get("currentStatus")) >= 0)
				debtprem = M.addnum(debtprem,ucdetail.get("premium"));
				
		//	System.out.println("bingo.......................3");
			ss = ucdetail.next();
		}
		System.out.println("finish --------------------------"+saleID);	
		rp[0] = M.setlen(M.itoc(trp),5);	
		rp[1] = M.setlen(totprem,10);	
		rp[2] = M.setlen(debtprem,10);	
		rp[3] = getNotPayUCFee(saleID,month,branch);	
		return rp;	
	}
	private String getNotPayUCFee(String saleID,String month,String branch)
	{
		Vector vec = getUCFeeClearing(branch,saleID,month);
		String [] tt = (String [])vec.elementAt(0);
		String total = tt[0];
		total = M.subnum(total,tt[1]);
		
		for (int i = 1 ; i < vec.size();i++)
		{
			tt = (String[])vec.elementAt(i);
			total = M.subnum(total,tt[1]);			
		}
		return total;
	}
	public String [] getUCFeePayByOverride(String saleID,String month) 
	{
		String [] fee = rte.bl.branch.income.Override.getUnpaidAmount(saleID,month);
		return fee;
	}
	public Vector getUCFeeClearing(String branch,String strID,String month)
	{
		Vector vec = new Vector();
		String saleID = strID;
		if(!M.numeric(saleID))
		{
			try {
				ucyymm = CFile.opens("uc"+month+"@cbranch");
			}
			catch(Exception e)
			{
			}
			if(ucyymm != null && ucyymm.equal(strID))
				saleID = ucyymm.get("salesID");
		}
		String [] fee = getUCFeePayByOverride(saleID, month);
		String totalfee = "500";
		String overridefee = fee[0];
		if(M.cmps(fee[0],"500") > 0)
			totalfee = fee[0];
		String paid = fee[1];	
		vec.addElement(new String [] {totalfee,overridefee,paid});
		try {
			System.out.println("saleid to search clearing fee--------------"+branch+" "+saleID);
			Mrecord ucfeeclearing = CFile.opens("ucfeeclearing"+month+"@cbranch");
			for (boolean st = ucfeeclearing.equalGreat(branch+saleID);st && (branch+saleID).compareTo(ucfeeclearing.get("branch")+ucfeeclearing.get("salesID")) == 0 ;st=ucfeeclearing.next())
			{
		
				System.out.println("found  clearing fee--------------"+branch+" "+saleID+"  "+ucfeeclearing.get("rpNo")+"  "+ucfeeclearing.get("clearingFee"));
				vec.addElement(new String [] {ucfeeclearing.get("rpNo"),ucfeeclearing.get("clearingFee")});	
			}
		}
		catch(Exception e)
		{
			System.out.println("UC FEE ERROR ==="+e.getMessage());
		}
		return vec ;
	}
	public void insertUCFee(String branch,String month,String strID,String fee,String rpNo) throws Exception
	{
		String saleID = "0000000000";
		ucyymm = CFile.opens("uc"+month+"@cbranch");
		Mrecord ucfeeclearing = CFile.opens("ucfeeclearing"+month+"@cbranch");
		if(ucyymm != null && ucyymm.equal(strID))
			saleID = ucyymm.get("salesID");
		ucfeeclearing.set("branch",branch);
		ucfeeclearing.set("totalFee","0000");
		ucfeeclearing.set("strID",strID);
		ucfeeclearing.set("salesID",saleID);
		ucfeeclearing.set("clearingFee",fee);
		ucfeeclearing.set("rpNo",rpNo);
		ucfeeclearing.set("clearingDate",DateInfo.sysDate());
		ucfeeclearing.insert();
				
	}
	public void deleteUCFee(String branch,String month,String strID,String rpNo) throws Exception
	{
		String saleID = "0000000000";
		ucyymm = CFile.opens("uc"+month+"@cbranch");
		Mrecord ucfeeclearing = CFile.opens("ucfeeclearing"+month+"@cbranch");
		if(ucyymm != null && ucyymm.equal(strID))
			saleID = ucyymm.get("salesID");
		for (boolean st = ucfeeclearing.equalGreat(branch+saleID);st && (branch+saleID).compareTo(ucfeeclearing.get("branch")+ucfeeclearing.get("salesID")) == 0 ;st=ucfeeclearing.next())
		{
			if(rpNo.compareTo(ucfeeclearing.get("rpNo")) == 0)
				ucfeeclearing.delete();
		}
	}
	private void searchByMonth(String month,String branch,TempMasicFile temp) throws Exception
	{	
		try {
			ucyymm = CFile.opens("uc"+month+"@cbranch");
			ucdetail = CFile.opens("ucdetail"+month+"@cbranch");
		}
		catch (Exception e)
		{
			System.out.println("e.getMessage()..........."+e.getMessage());
			return ;
		}
		String salesID = "000000000";
		System.out.println(month+"    "+currentMonth);
		int nom = calNoOfMonth(month,currentMonth);
		String process = "";
	//	System.out.println("in starch by month branch ========="+branch+" ----------"+month+" nom ===="+nom);
		for (boolean st = ucyymm.first() ; st ; st = ucyymm.next())
		{
			//System.out.println("ucyymm----branch---------"+ucyymm.get("branch"));
			if(branch.compareTo("000") != 0 && branch.compareTo(ucyymm.get("branch")) != 0)
				continue;
			salesID = ucyymm.get("salesID");
			process="";
		/*	if(salesID.compareTo("0000000162") != 0)
				continue;*/
			System.out.println("ucyymm----branch---------"+ucyymm.get("branch")+"  saleid == "+salesID);
			temp.set('0');	
			temp.set("saleID",ucyymm.get("salesID"));
			temp.set("strID",ucyymm.get("strID"));
			String [] struc = checkDetailUCRP(temp.get("saleID"),month,ucyymm.get("branch"));
			temp.set("month",month);
			temp.set("trp",struc[0]);
			temp.set("premium",struc[1]);
			System.out.println(" in search by month ----------"+struc[0]+"  "+struc[1]+" "+struc[2]+" "+struc[3]);
	//		if(M.cmps(struc[0],"0") != 0 || M.cmps(struc[1],"0") != 0 || M.cmps(struc[2],"0") != 0 || M.cmps(struc[3],"0") != 0 )
	//		{
				temp.set("name",getSaleName(ucyymm.get("salesID"),true));
				temp.set("positionName",positionName);
				temp.set("branch",ucyymm.get("branch"));		
	//		}
	//		else 
	//			continue;
//			System.out.println("nom---------------------------"+nom);
			if(nom == 0)
			{
				;
			}
			else if (nom  >=  1)
			{
				if(ucyymm.get("bookToFollow").trim().length() == 0 || M.itis(ucyymm.get("bookToFollow").trim(),'0'))
				{
					if(M.cmps(temp.get("trp"),"0") != 0  || M.cmps(temp.get("premium"),"0") != 0)
						process += "1";
				}
				if (nom >= 2)
				{
					if(M.cmps(struc[3],"0") > 0)
					{
						process+= "2";
						temp.set("fee",struc[3]);
					}
					if(ucyymm.get("bookToCancel").trim().length() == 0 || M.itis(ucyymm.get("bookToCancel"),'0'))
						process += "3";
					if(nom >= 3)
					{
						if(M.cmps(struc[1],"0") > 0)
						{
							process+= "5";
							temp.set("premium",struc[1]);
						}
					}
				}	
			}
			if(M.cmps(struc[2],"0") > 0)
			{
				process+="4" ;
				temp.set("debtPremium",struc[2]);
			}
			System.out.println("process---------------------------"+process);
			temp.set("process",process);
			temp.insert();
		}
	}
	public TempMasicFile  getForUnclear(String month,String branch,String lCode,String gCode) throws Exception
        {
		System.out.println("bingo................start search");
		String field []= {"lcode","hcode","gcode","dcode","bcode","acode","rpflag","code","rpno","tpremium","reqdate","lname","hname","gname","dname","bname","aname","uname"};
		int len []= { 5, 5, 10, 10, 10, 7, 1, 10, 18, 10, 8,50,50,50,50,50,50,50};
		String tKey0 []= {"lcode","gcode","dcode","bcode","acode","rpflag"};
		String tKey1 []= {"lcode","gcode","dcode","bcode","acode","rpno"};
		
		String sysMonth = DateInfo.sysDate().substring(0,6);
                if(month.compareTo(sysMonth) >= 0)
                       throw new Exception (M.stou("ไม่มีแฟ้มข้อมูลขาดค้าง"));
                Mrecord ucrp = null;
                if(CFile.isFileExist("ucrp"+month+"@cbranch"))
                {
                        ucrp = CFile.opens("ucrp"+month+"@cbranch");
                }
                else {
                        ucrp =CFile.opens("askyyyymm@cbranch");
                }
		System.out.println("....................befor build temp...................................");
		TempMasicFile temp = new TempMasicFile("rptbranch",field,len);
                temp.keyField(true,false ,tKey0);
                temp.buildTemp();

		System.out.println("......................after build temp .................................");
                BraSales bs = new BraSales();
		boolean st = ucrp.great(branch) && branch.compareTo(ucrp.get("branch")) == 0;
		if(!st)
			throw new Exception(M.stou("ไม่มีข้อมูลขาดค้าง"));
		Record trec  = null;	
		String ssid = "";
//		int count = 0 ; 
		String name = "";
		TempMasicFile temp1  = null;
		for (;st && branch.compareTo(ucrp.get("branch")) == 0;)
		{
			
			if (ssid.compareTo(ucrp.get("askSaleID")) != 0)
			{
				temp1 = new TempMasicFile("rptbranch",field,len);
            		    	temp1.keyField(true,false ,tKey1);
            			temp1.buildTemp();
		//		count++;
				ssid = ucrp.get("askSaleID");
				temp.set(' ');
				temp.set("tpremium","000000000");
				temp1.set(' ');
				temp1.set("tpremium","000000000");
				try {
					System.out.println("................saleid........................."+ucrp.get("askSaleID")+"........................................");
					bs.getBySalesID(ucrp.get("askSaleID"));
					temp.set("code",bs.getSnRec("strID"));
					System.out.println("............code............................."+temp.get("code")+"........................................");
					name = bs.getSnRec("preName").trim()+bs.getSnRec("firstName").trim()+ "  "+bs.getSnRec("lastName").trim();
					System.out.println("...........name.......1......................"+name+"........................................");
					if(name.trim().length() > 50)
						temp.set("uname",name.substring(0,50));
					else
						temp.set("uname",name);
					System.out.println("...........name........2....................."+temp.get("uname")+"........................................");
					bs.getStruct();
					temp.set("lcode",bs.getSnDepartment("strID"));
					temp.set("hcode",bs.getSnSeniorRegion("strID"));
					temp.set("gcode",bs.getSnRegion("strID"));
					temp.set("dcode",bs.getSnDivision("strID"));
					temp.set("bcode",bs.getSnUnit("strID"));
					temp.set("acode",bs.getSnAgent("strID"));
				}
				catch (Exception e)
				{
						System.out.println("error-----------"+e.getMessage());	
				}
				System.out.println("lcode---->"+lCode+"---"+temp.get("lcode")+"++++++"+"gcode---->"+gCode+"---"+temp.get("gcode"));
				if(!(lCode.compareTo(temp.get("lcode")) <= 0 && gCode.compareTo(temp.get("gcode")) <= 0))
				{
					st = ucrp.next();
						
					continue;
				}
				name  = bs.getSnDepartment("preName")+bs.getSnDepartment("firstName")+" "+bs.getSnDepartment("lastName");
				if(name.trim().length() > 50)
					temp.set("lname",name.substring(0,50));
				else
					temp.set("lname",name);

				name  = bs.getSnSeniorRegion("preName")+bs.getSnSeniorRegion("firstName")+" "+bs.getSnSeniorRegion("lastName");
				if(name.trim().length() > 50)
					temp.set("hname",name.substring(0,50));
				else
					temp.set("hname",name);

				name  = bs.getSnRegion("preName")+bs.getSnRegion("firstName")+" "+bs.getSnRegion("lastName");
				if(name.trim().length() > 50)
					temp.set("gname",name.substring(0,50));
				else
					temp.set("gname",name);

				name  = bs.getSnDivision("preName")+bs.getSnDivision("firstName")+" "+bs.getSnDivision("lastName");
				if(name.trim().length() > 50)
					temp.set("dname",name.substring(0,50));
				else
					temp.set("dname",name);

				name  = bs.getSnUnit("preName")+bs.getSnUnit("firstName")+" "+bs.getSnUnit("lastName");
				if(name.trim().length() > 50)
					temp.set("bname",name.substring(0,50));
				else
					temp.set("bname",name);

				name  = bs.getSnAgent("preName")+bs.getSnAgent("firstName")+" "+bs.getSnAgent("lastName");
				if(name.trim().length() > 50)
					temp.set("aname",name.substring(0,50));
				else
					temp.set("aname",name);

				temp.set("reqdate",ucrp.get("requestDate"));
				temp1.putBytes(temp.getBytes());
			}
			
			while (st && ssid.compareTo(ucrp.get("askSaleID")) == 0)
			{	
				if("IOW".indexOf(ucrp.get("receiptFlag")) >= 0)
				{
					trec = getReceipt(ucrp.get("rpNo"),ucrp.get("receiptFlag"),ucrp.get("requestDate"));
					if("IOW".indexOf(ucrp.get("receiptFlag")) >= 0)
					{
						if(trec != null)
							temp.set("tpremium",M.addnum(temp.get("tpremium"),trec.get("premium")));
					}				
				}
				else if ("X".indexOf(ucrp.get("receiptFlag")) >= 0)
				{
					boolean con = irc.equal(ucrp.get("rpNo"));
					while (con && irc.get("rpNo").equals(ucrp.get("rpNo")))
					{
						if ("AXNU".indexOf(irc.get("currentStatus").charAt(0)) >= 0)
						{
							if (M.match(8,irc.get("requestDate"),0,"00000000",0) ||
								M.cmps(ucrp.get("requestDate"),irc.get("requestDate")) <= 0) {
								temp.set("tpremium",M.addnum(temp.get("tpremium"),irc.get("premium")));
								break;
                                                        }
						}
                                                else if (M.cmps(irc.get("requestDate"),ucrp.get("requestDate")) >= 0 ||
							M.match(4,irc.get("requestDate"),0,ucrp.get("requestDate"),0))
						{ 
					
							temp.set("tpremium",M.addnum(temp.get("tpremium"),irc.get("premium")));
							break;					
						}
                                       		con = irc.next();
					}
				}
				else if ("Y".indexOf(ucrp.get("receiptFlag")) >= 0)
				{
					boolean con = orc.equal(ucrp.get("rpNo"));
					while (con && orc.get("rpNo").equals(ucrp.get("rpNo")))
					{
						if ("AXNU".indexOf(orc.get("currentStatus").charAt(0)) >= 0)
						{
							if (M.match(8,orc.get("requestDate"),0,"00000000",0) ||
								M.cmps(ucrp.get("requestDate"),orc.get("requestDate")) <= 0) {
								temp.set("tpremium",M.addnum(temp.get("tpremium"),orc.get("premium")));
								break;
                                                        }
						}
                                                else if (M.cmps(orc.get("requestDate"),ucrp.get("requestDate")) >= 0 ||
							M.match(4,orc.get("requestDate"),0,ucrp.get("requestDate"),0) )
						{ 
					
							temp.set("tpremium",M.addnum(temp.get("tpremium"),orc.get("premium")));
							break;					
						}
                                       		con = orc.next();
					}
				}
				else if ("TZ".indexOf(ucrp.get("receiptFlag")) >= 0)
				{
					temp1.set("rpno",ucrp.get("rpNo"));	
					temp1.insert();
				}
				st = ucrp.next();
			}
			String trp ="";
			String rpx = "";
			for (boolean ss = temp1.first();ss;)
			{
				trp = temp1.get("rpno");
				rpx = temp1.get("rpno");
				ss = temp1.next();
				while (ss)
				{
					rpx = M.inc(rpx);
					if(M.cmps(temp1.get("rpno"),rpx) != 0)
					{
						rpx = M.dec(rpx);
						if(M.cmps(rpx,trp) == 0)
						{
							temp.set("rpflag","1");
							temp.set("rpno",trp);
							temp.insert();
						}
						else {
							trp = trp+"-"+rpx.substring(9);
							temp.set("rpflag","1");
							temp.set("rpno",trp);
							temp.insert();
						}
						break;
					}
					ss = temp1.next();
				}
			}
			if(trp.trim().length() == 12)
			{	
				if(rpx.compareTo(trp) == 0)
				{
					temp.set("rpflag","1");
					temp.set("rpno",trp);
					temp.insert();
				}
				else {
					trp = trp+"-"+rpx.substring(9);
					temp.set("rpflag","1");
					temp.set("rpno",trp);
					temp.insert();
				}
			}
			if(M.cmps(temp.get("tpremium"),"0") > 0)
			{
				temp.set("rpflag","2");
				temp.insert();
			}
	//		if(temp.get("rpflag").charAt(0) == '2')
	//			break;
		}
		return (temp);
	}
	public TempMasicFile getForTV04(String month,String branch,String startCode) throws Exception
        {
		String sysMonth = DateInfo.sysDate().substring(0,6);
                if(month.compareTo(sysMonth) >= 0)
                       throw new Exception (M.stou("ไม่มีแฟ้มข้อมูลขาดค้าง"));
		Mrecord ucrp = null;
		if(CFile.isFileExist("ucrp"+month+"@cbranch"))
		{
			ucrp = CFile.opens("ucrp"+month+"@cbranch");
		}
		else {
			ucrp =CFile.opens("askyyyymm@cbranch");
		}
		Mrecord sale = CFile.opens("person@sales");

		String field []= {"saleID","code","ucode","sname","uname","policyno","rpno","payperiod","premium","name","bossName","bossCode","bossPos","employeeID"};
		int len []= {10, 10, 10,80,80, 8,12, 6, 9, 80,80,10,30,10};
		String tKey0 [] = {"code","ucode","policyno","rpno"};

		TempMasicFile temp = new TempMasicFile("rptbranch",field,len);
		temp.keyField(false,false ,tKey0);
		temp.buildTemp();
		BraSales bs = new BraSales();
		boolean st = ucrp.great(branch) && branch.compareTo(ucrp.get("branch")) == 0;
		if(!st)
			throw new Exception(M.stou("ไม่มีข้อมูลขาดค้าง"));
		Record trec  = null;	
		String ssid = "";
//		int count = 0 ; 
		for (;st && branch.compareTo(ucrp.get("branch")) == 0;st=ucrp.next())
		{
			if("OWI".indexOf(ucrp.get("receiptFlag")) < 0)
				continue;
			trec = getReceipt(ucrp.get("rpNo"),ucrp.get("receiptFlag"),ucrp.get("requestDate"));
			if(trec == null)
				continue;
			if("NAXWU".indexOf(trec.get("currentStatus")) < 0)
				continue;
			if (ssid.compareTo(ucrp.get("askSaleID")) != 0)
			{
		//		count++;
				ssid = ucrp.get("askSaleID");
				if(sale.equal(ucrp.get("askSaleID")))
				{
					if(startCode.compareTo(sale.get("highStrid")) > 0)
						continue;
				
					temp.set("code",sale.get("highStrid"));
					temp.set("employeeID",sale.get("personID"));
					temp.set("saleID",ucrp.get("askSaleID"));
					bs.getBySalesID(ucrp.get("askSaleID"));
					bs.getStruct();
					temp.set("sname",bs.getSnRec("preName")+bs.getSnRec("firstName")+" "+bs.getSnRec("lastName"));
				
					temp.set("ucode",bs.getSnUnit("strID"));
					temp.set("uname",bs.getSnUnit("preName")+bs.getSnUnit("firstName")+" "+bs.getSnUnit("lastName"));
					if("AB".indexOf(temp.get("code").charAt(0)) >= 0)
					{
						temp.set("bossCode",bs.getSnDivision("strID"));				
						temp.set("bossName",bs.getSnDivision("preName")+bs.getSnDivision("firstName")+" "+bs.getSnDivision("lastName"));
					}
					else if(temp.get("code").charAt(0) == 'D')
					{
						temp.set("bossCode",bs.getSnRegion("strID"));				
						temp.set("bossName",bs.getSnRegion("preName")+bs.getSnRegion("firstName")+" "+bs.getSnRegion("lastName"));
					}
					else if(temp.get("code").charAt(0) == 'G')
					{
						temp.set("bossCode",bs.getSnDepartment("strID"));				
						temp.set("bossName",bs.getSnDepartment("preName")+bs.getSnDepartment("firstName")+" "+bs.getSnDepartment("lastName"));
					}
					if(temp.get("bossCode").charAt(0) == 'L' && temp.get("bossCode").charAt(4) == 'U')
					{
				
						temp.set("bossPos","ผู้ทำหน้าที่ผู้จัดการฝ่ายฯ");
					
					}
					else {
						temp.set("bossPos",StridPos.getDetail(temp.get("bossCode")));
					}
		//			System.out.println("sname---------"+temp.get("sname")+"  bossname--------"+temp.get("bossName"));
				}				
				
			}
		//	if(count > 1)
		//		break;
			temp.set("policyno",trec.get("policyNo"));
			temp.set("rpno",trec.get("rpNo"));
			temp.set("payperiod",trec.get("payPeriod"));
			temp.set("premium",M.setlen(trec.get("premium"),9));
			temp.set("name",getName(trec.get("policyNo"),ucrp.get("receiptFlag")));
			temp.insert();		
			
		}
		return temp;
        }

	/*  ------------------process---------------------------------
		1 : ไม่ได้พิมพ์หนังสือติมตามใบรับเงินเบี้ย ฯ ขาดค้าง
		2 : ไม่เรียกเก็บค่าปรับขาดค้าง
		3 : ไม่พิมพ์หนังสือแจ้งหมดสิทธิ์รับค่าจัดงาน
		4 : ไม่ตั้งหนี้ยักยอกหรือไม่ปฏิเสธความรับผิดชอบ
		5 : ไม่ตั้งหนี้หารผู้บังคับบัญชา
	  ---------------------------------------------------------------*/
	private int calNoOfMonth(String month,String currentMonth)
	{
		int nom = 0;
		while (month.compareTo(currentMonth) < 0)
		{
			nom++;
			month = DateInfo.nextMonth(month);
		}
		return nom;
	}
	public  boolean isUCSales(String strID,String month,boolean thismonth) throws Exception
	{
		ucyymm = CFile.opens("uc"+month+"@cbranch");
		ucdetail = CFile.opens("ucdetail"+month+"@cbranch");
		System.out.println("strid ===="+strID+"   "+month+"   "+thismonth);
		if(ucyymm.equalGreat(strID) && strID.compareTo(ucyymm.get("strID")) == 0)
		{
			if(thismonth)
				return true;
		System.out.println("saleid ===="+ucyymm.get("salesID"));
			for (boolean ss = ucdetail.equalGreat(ucyymm.get("salesID")) ; 
				ss && ucyymm.get("salesID").compareTo(ucdetail.get("salesID")) == 0; 
				ss = ucdetail.next())
			{
				  if(ucdetail.get("currentStatus").charAt(0) != 'S')
                                        updateCurrentStatus();
				
				System.out.println("===="+ucdetail.get("rpNo")+"  "+ucdetail.get("currentStatus")+"========");
				  if("DU".indexOf(ucdetail.get("currentStatus")) >= 0)
					return true;

			}
		}
		return false ;
	}
	public static void main(String [] args) throws Exception 
	{
		UCRPYYYYMMOnly u = new UCRPYYYYMMOnly();
//		System.out.println("start build askyyyymm "+args[0]);
//		u.buildAskyyyymm(args[0]);
		System.out.println("start build ucyyyymm "+args[0]+ "   "+args[1]);
		u.buildUCyyyymm(args[0],args[1],args[2]);
	}
}
