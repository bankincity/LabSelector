package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.support.DateInfo;
import  utility.cfile.CFile;
import  utility.cfile.Rtemp;
import  rte.bl.branch.*;
import  rte.*;
import utility.rteutility.*;
public class  GetReceiptByStatusByFile  implements  InterfaceRpt
{
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("args === "+args.length);
		for (int i = 0 ; i < args.length;i++)
		{
			System.out.println("args "+i+"   "+args[i]);
		}
		String remote	= args[0];  // true or false 
		if(remote.compareTo("false") == 0)
                        PublicRte.setRemote(false);
                else
                        PublicRte.setRemote(true);
                String appserv = args[1];  // application server  <--> braxxxapp
                String filename = args[2]; // masic output stream  

		// branch status
		getAllRp(args[3],args[4]);		
		System.out.println("temptoprint------------>"+temptoprint.fileSize()+"  "+tfile.fileSize());
		System.out.println("End startSearch process! ");
		rte.RteRpt.recToTemp(tfile, filename);
		tfile.close();
		temptoprint.close();
                System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
	}
	Mrecord tfile ;
	TempMasicFile  temptoprint;
	String [] field = {"requestDate","askSaleID","ownerSaleID","rpNo","policyNo","payPeriod","premium","receiptFlag","currentStatus","reasonCode"};
	int [] len = {8,10,10,12,8,6,9,1,1,2};
	private void getAllRp(String branch,String status) throws Exception
	{	
		temptoprint = new TempMasicFile("rptreceipt",field,len);
		temptoprint.keyField(false,false,new String [] {"receiptFlag","rpNo","policyNo"});
                temptoprint.buildTemp();
		System.out.println("temptoprintln....................."+temptoprint.name());
		Result res = PublicRte.getResult("searchreceipt","rte.search.receipt.SearchReceiptByStatus", new String [] {status,branch,temptoprint.name()});
		System.out.println("@@@@@@ GetReceiptStatusWByFile @@@@@@ Result of rte.search.receipt.SearchReceiptByStatus--->"+res.status());
		if(res.status() != 0)
                        throw new Exception((String)res.value());
		
		Rtemp trec = new Rtemp(field,len);
                tfile  = Masic.opent((String)res.value(),trec.layout());
		if(status.charAt(0) == 'W')
			checkMasterStatus();
	}
	Mrecord sperson;
	Mrecord ul ;
	public void checkMasterStatus() throws Exception
	{
		Mrecord ord  = CFile.opens("ordmast@mstpolicy");
		Mrecord whl = CFile.opens("whlmast@mstpolicy");
		Mrecord ind = CFile.opens("indmast@mstpolicy");
		if (Masic.fileStatus("universallife@universal") >= 2 )
			ul = CFile.opens("universallife@universal");
		sperson = CFile.opens("person@sales");

		boolean confirm = false ;
		for (boolean st = tfile.first() ; st ; st= tfile.next())
		{
			if(tfile.get("receiptFlag").charAt(0) == 'I')
				confirm = confirmCancel(ind,tfile.get("policyNo"),tfile.get("payPeriod"));
			else if(tfile.get("receiptFlag").charAt(0) == 'W')
				confirm = confirmCancel(whl,tfile.get("policyNo"),tfile.get("payPeriod"));
			else if(tfile.get("receiptFlag").charAt(0) == 'O')
				confirm = confirmCancel(ord,tfile.get("policyNo"),tfile.get("payPeriod"));
			else if(tfile.get("receiptFlag").charAt(0) == 'U')
				confirm = confirmCancel(ul,tfile.get("policyNo"),tfile.get("payPeriod"));
			if(confirm)
				tfile.set("currentStatus","C");
			tfile.update();
		}
	}
	private boolean confirmCancel(Mrecord master,String policyNo,String payPeriod )
	{
		if(master.equal(policyNo))
		{
			tfile.set("ownerSaleID",getDepNo(master.get("salesID")));
			if("XDSEU#*".indexOf(master.get("policyStatus1")) >= 0)
				return true;
			if(payPeriod.compareTo(master.get("payPeriod")) <= 0)
				return true;		
		}
		return false ;
	}
	private String getDepNo(String salesID)
	{
		if(sperson.equal(salesID))
		{
			return (sperson.get("depositNo"));
		}
		return "00000";
	}
}

