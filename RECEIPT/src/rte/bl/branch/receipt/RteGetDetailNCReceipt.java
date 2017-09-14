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
import utility.branch.sales.BraSales;
import utility.prename.Prename;
public class  RteGetDetailNCReceipt implements  Task
{
	public Result execute(Object param) 
        {
		//remote ,Vector
		Object [] parameter = (Object [])param;
			
		try {
			String fileName = RteRpt.getTempName("rptbranch");
                	System.out.println(" fileName=" + fileName);

			if(parameter.length > 2)
			{
				System.out.println("parameter size === "+parameter.length);
				getDataToReport((String)parameter[1],(String)parameter[2],(String)parameter[3],(String)parameter[4]);
			}
			else {
				System.out.println("parameter size  2 === "+parameter.length);
				getDataToReport((Vector)parameter[1]);
			}
			rte.RteRpt.recToTemp(temptoprint, fileName);
			if(tempcask == null)
				return new Result(fileName,0);
			String fname = RteRpt.getTempName("rptbranch");
                	System.out.println(" fileName for cask =" + fname);
			rte.RteRpt.recToTemp(tempcask, fname);
			temptoprint.close();
			tempcask.close();
			return new Result(new String[] {fileName,fname},0);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
	}
	TempMasicFile  temptoprint;
	TempMasicFile  tempcask;
	TempMasicFile  rec;
	Mrecord nameMaster;
	Mrecord ul;
	Mrecord ord;
	Mrecord whl;
	Mrecord ind;
	Mrecord cask ;
	BraSales bsale;
	String [] tfield = {"requestDate","askSaleID","ownerSaleID","rpNo","policyNo","payPeriod","premium","receiptFlag","currentStatus","reasonCode"};
        int [] tlen = {8,10,10,12,8,6,9,1,1,2};
	String [] field   = {"aDepNo","depNo","reqDate","typePol","policyNo","payPeriod","rpNo","status","name","aName","oName","premium"};
	int [] len   = {5,5,8,1,8,6,12,1,80,80,80,9};

	private void getDataToReport(String saleID,String yyyymm,String branch,String curKey ) throws Exception
	{
		String [] cafield = {"depositNo","requestDate","askDepositNo","oName","aName","userName","status"};
		int [] calen  ={5,8,5,80,80,80,1};

		System.out.println("bingo ............................................1");
		GetReceiptToClearByFile ncfile = new GetReceiptToClearByFile();

		System.out.println("bingo ............................................2");
                ncfile.getAllRequestedRp(saleID,yyyymm,"no",branch,curKey);
		System.out.println("bingo ............................................3");
		rec  = ncfile.getTempFile();
		System.out.println("rec ----------->"+rec.fileSize()+"  "+saleID+"  "+yyyymm+"  "+branch+"   "+curKey);
		
		openFile(yyyymm);
		
		temptoprint = new TempMasicFile("rptbranch",field,len);
                temptoprint.keyField(true,true,new String [] {"aDepNo","depNo","reqDate","policyNo","payPeriod"});
                temptoprint.buildTemp();

		tempcask = new TempMasicFile("rptbranch",cafield,calen);
                tempcask.keyField(true,true,new String [] {"depositNo","requestDate"});
                tempcask.buildTemp();

		String tSaleID="0000000000";
		String taSaleID="0000000000";
		String tDepno = "00000";
		String tName = "";
		String taDepno = "00000";
		String taName = "";

		for (boolean st = rec.first();st;st=rec.next())
		{
			temptoprint.set("rpNo",rec.get("rpNo"));
			temptoprint.set("policyNo",rec.get("policyNo"));
			temptoprint.set("reqDate",rec.get("requestDate"));
			temptoprint.set("payPeriod",rec.get("payPeriod"));
			temptoprint.set("status",rec.get("currentStatus"));
			temptoprint.set("premium",rec.get("premium"));
			temptoprint.set("typePol",rec.get("receiptFlag"));
			temptoprint.set("name",getName(rec.get("policyNo"),rec.get("receiptFlag")));

		/*	bsale.getBySalesID(rec.get("askSaleID"));
			temptoprint.set("aDepNo",bsale.getSnRec("depositNo"));	
			temptoprint.set("aName",bsale.getSnRec("preName")+bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName"));	

			bsale.getBySalesID(rec.get("ownerSaleID"));
			temptoprint.set("depNo",bsale.getSnRec("depositNo"));	
			temptoprint.set("oName",bsale.getSnRec("preName")+bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName"));	*/

			if(taSaleID.compareTo(rec.get("askSaleID")) != 0)
			{	
				System.out.println("ASK   Saleid................"+taSaleID+"........................"+rec.get("askSaleID"));	
				bsale.getBySalesID(rec.get("askSaleID"));
				taDepno = bsale.getSnRec("depositNo");
				taName = bsale.getSnRec("preName")+bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName");
				taSaleID = rec.get("askSaleID");
			}
			temptoprint.set("aDepNo",taDepno);	
			temptoprint.set("aName",taName);	

			if(tSaleID.compareTo(rec.get("ownerSaleID")) != 0)
			{
				System.out.println("tSaleid................"+tSaleID+"........................"+rec.get("ownerSaleID"));	
				bsale.getBySalesID(rec.get("ownerSaleID"));
				tDepno = bsale.getSnRec("depositNo");
				tName = bsale.getSnRec("preName")+bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName");
				tSaleID = rec.get("ownerSaleID");
				System.out.println("get data from cask...................................................");
				getDataFromCask(tDepno,tName,tSaleID,branch);
			}
			temptoprint.set("depNo",tDepno);	
			temptoprint.set("oName",tName);

			temptoprint.insert();
		}
			
	}
	private void getDataFromCask(String depno,String name,String saleID,String branch)
	{
		tempcask.set(' ');
		tempcask.set("depositNo",depno);
		tempcask.set("oName",name);
		for (boolean st = cask.equal(branch+saleID);st;st=cask.next())
		{
			if((branch+saleID).compareTo(cask.get("branch")+cask.get("ownerSaleID")) != 0)
				break;
			if(cask.get("status").charAt(0) != 'N')
				continue;
			if(tempcask.equal(depno+cask.get("requestDate")))
				continue;
			tempcask.set("requestDate",cask.get("requestDate"));
			tempcask.set("status",cask.get("status"));
			tempcask.set("userName",cask.get("userID"));
			tempcask.insert();
		}
	}
	private void openFile(String yyyymm) throws Exception
	{
		nameMaster  = Masic.opens("name@mstperson"); 
		if(nameMaster == null || nameMaster.lastError() != 0)
			throw new Exception("Can not open name@mstperson");

		ord  = Masic.opens("ordmast@mstpolicy"); 
		if(ord == null || ord.lastError() != 0)
			throw new Exception("Can not open ordmast@mstpolicy");

		whl  = Masic.opens("whlmast@mstpolicy"); 
		if(whl == null || whl.lastError() != 0)
			throw new Exception("Can not open whlmast@mstpolicy");

		ind  = Masic.opens("indmast@mstpolicy"); 
		if(ind == null || ind.lastError() != 0)
			throw new Exception("Can not open indmast@mstpolicy");
		ul  = Masic.opens("universallife@universal"); 
		if(ind == null || ind.lastError() != 0)
			throw new Exception("Can not open indmast@mstpolicy");

		bsale = new BraSales();

		if(yyyymm.trim().length() == 0)
			return;
		cask  = Masic.opens("cask"+yyyymm+"@cbranch"); 
		if(cask == null || cask.lastError() != 0)
			throw new Exception("Can not open caskyyyymm@cbranch");
	}
	private void getDataToReport(Vector vec) throws Exception
	{
		openFile("");
		
		temptoprint = new TempMasicFile("rptbranch",field,len);
                temptoprint.keyField(true,true,new String [] {"aDepNo","depNo","reqDate","policyNo","payPeriod"});
                temptoprint.buildTemp();

                System.out.println("open file get all request rp ");

		Rtemp trec = new Rtemp(tfield,tlen);
		Record rec = trec.copy();
		String tSaleID="0000000000";
		String taSaleID="0000000000";
		String tDepno = "00000";
		String tName = "";
		String taDepno = "00000";
		String taName = "";
		for (int i = 0 ; i < vec.size();i++)
		{
			rec.putBytes((byte [])vec.elementAt(i));
			temptoprint.set("rpNo",rec.get("rpNo"));
			temptoprint.set("policyNo",rec.get("policyNo"));
			temptoprint.set("reqDate",rec.get("requestDate"));
			temptoprint.set("payPeriod",rec.get("payPeriod"));
			temptoprint.set("status",rec.get("currentStatus"));
			temptoprint.set("premium",rec.get("premium"));
			temptoprint.set("typePol",rec.get("receiptFlag"));
			temptoprint.set("name",getName(rec.get("policyNo"),rec.get("receiptFlag")));
			if(taSaleID.compareTo(rec.get("askSaleID")) != 0)
			{	
				bsale.getBySalesID(rec.get("askSaleID"));
				taDepno = bsale.getSnRec("depositNo");
				taName = bsale.getSnRec("preName")+bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName");
				taSaleID = rec.get("askSaleID");
			}
			temptoprint.set("aDepNo",taDepno);	
			temptoprint.set("aName",taName);	

			if(tSaleID.compareTo(rec.get("ownerSaleID")) != 0)
			{	
				bsale.getBySalesID(rec.get("ownerSaleID"));
				tDepno = bsale.getSnRec("depositNo");
				tName = bsale.getSnRec("preName")+bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName");
				tSaleID = rec.get("ownerSaleID");
			}
			temptoprint.set("depNo",tDepno);	
			temptoprint.set("oName",tName);
	
			temptoprint.insert();
		}
			
	}

	private String  getName(String policyNo,String typePol)
	{
		String nameID = "0000000000";
		switch(typePol.charAt(0))
		{
			case 'I' : if(ind.equal(policyNo))
					nameID = ind.get("nameID");
				   break;
			case 'O' : if(ord.equal(policyNo))
					nameID = ord.get("nameID");
				   break;
			case 'W' : if(whl.equal(policyNo))
					nameID = whl.get("nameID");
				   break;
			case 'A' : if(ul.equal(policyNo))
					nameID = ul.get("nameID");
				   break;

		}
		if(nameMaster.equal(nameID))
			return  (Prename.getAbb(nameMaster.get("preName").trim())+nameMaster.get("firstName").trim()+" "+nameMaster.get("lastName").trim());
		return " ";
	}
}


