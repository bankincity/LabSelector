package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import java.util.*;
import rte.*;
import utility.rteutility.*;
import layout.receipt.*;
import utility.cfile.Rtemp;
import utility.prename.Prename;
import utility.support.DateInfo;
import utility.branch.sales.BraSales;
import rte.bl.branch.*;
public class  GetReceiptToPayFeeByFile implements  InterfaceRpt
{
	Mrecord irc;       
	Mrecord wrc;
        Mrecord orc;
        Mrecord ulrc;
	Mrecord rpfee;
	Mrecord rpbook;
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
                String filename = args[2]; // masic output stream  

		irc = CFile.opens("irctrl@receipt");
		orc = CFile.opens("orctrl@receipt");
		wrc = CFile.opens("wrctrl@receipt");
		if (Masic.fileStatus("ulrctrl@universal") >= 2)
		{
			ulrc = CFile.opens("ulrctrl@universal");
		}
		rpfee = CFile.opens("confirm_feeforloserp@insuredocument");
		rpbook = CFile.opens("confirm_bookforloserp@insuredocument");
		
		if(args[5].charAt(0) == 'L')
			temptoprint = new TempMasicFile("rptinsure",field1,len1);
		else
			temptoprint = new TempMasicFile("rptinsure",field,len);
		temptoprint.keyField(false,false,new String [] {"typePol","rpNo"});
                temptoprint.buildTemp();

		//   branch sdate status
		if(args[5].charAt(0) == 'L')
		{
			getReceiptToBook("I",irc,args[3],args[4],"R");		
			getReceiptToBook("O",orc,args[3],args[4],"R");		
			getReceiptToBook("W",wrc,args[3],args[4],"R");		
			if (ulrc != null)
				getReceiptToBook("U",ulrc,args[3],args[4],"R");		
		}
		else {
			getReceiptToFee("I",irc,args[3],args[4],args[5]);		
			getReceiptToFee("O",orc,args[3],args[4],args[5]);		
			getReceiptToFee("W",wrc,args[3],args[4],args[5]);		
			if (ulrc != null)
				getReceiptToFee("U",ulrc,args[3],args[4],args[5]);		
		}
		System.out.println("End startSearch process! ");
		rte.RteRpt.recToTemp(temptoprint, filename);
                System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		temptoprint.close();
	}
	TempMasicFile  temptoprint;
	String [] field = {"typePol","rpNo","policyNo","payPeriod","premium","rpFee"};
        int []  len = {1,12,8,6,9,12};
	String [] field1= {"typePol","rpNo","policyNo","payPeriod","premium","rpFee","sdate","reqName","position","bookNo","requestID","requestPosition"};
	int [] len1= {1,12,8,6,9,12,8,60,30,20,10,10};

	private void getReceiptToFee(String type,Mrecord trec ,String branch,String sdate,String status)
	{
		trec.start(4);
		for (boolean st = trec.equalGreat(status+sdate+branch); st ;st = trec.next())
		{
			if((status+sdate+branch).compareTo(trec.get("currentStatus")+trec.get("sysDate")+trec.get("submitBranch").substring(0,3)) != 0)
				break;
			temptoprint.set('0');
			temptoprint.set("typePol",type);
			temptoprint.set("rpNo",trec.get("rpNo"));
			temptoprint.set("policyNo",trec.get("policyNo"));
			temptoprint.set("payPeriod",trec.get("payPeriod"));
			temptoprint.set("premium",M.setlen(trec.get("premium"),9));
			if(rpfee != null && rpfee.equal(trec.get("rpNo")+trec.get("policyNo")))
			{
				temptoprint.set("rpFee",rpfee.get("rpFee"));
			}
			temptoprint.insert();	
		}
	}
	private void getReceiptToBook(String type,Mrecord trec ,String branch,String sdate,String status)
	{
		trec.start(4);
		for (boolean st = trec.equalGreat(status+sdate+branch); st ;st = trec.next())
		{
			if((status+sdate+branch).compareTo(trec.get("currentStatus")+trec.get("sysDate")+trec.get("submitBranch").substring(0,3)) != 0)
				break;
			temptoprint.set('0');
			temptoprint.set("typePol",type);
			temptoprint.set("rpNo",trec.get("rpNo"));
			temptoprint.set("policyNo",trec.get("policyNo"));
			temptoprint.set("payPeriod",trec.get("payPeriod"));
			temptoprint.set("premium",M.setlen(trec.get("premium"),9));
			if(rpfee != null && rpfee.equal(trec.get("rpNo")+trec.get("policyNo")))
			{
				temptoprint.set("rpFee",rpfee.get("rpFee"));
				temptoprint.set("sdate",rpfee.get("acdate"));
			}
			if(rpbook != null && rpbook.equal(trec.get("rpNo")+trec.get("policyNo")))
			{
				temptoprint.set("bookNo",rpbook.get("bookToInsured"));
			}
			if(trec.get("requestDate").compareTo("00000000") != 0)
			{
				getRequestPerson(branch,trec.get("rpNo"),trec.get("requestDate"),type);
			}
			else {
				temptoprint.set("reqName"," ");
				temptoprint.set("position"," ");
				temptoprint.set("requestID"," ");
				temptoprint.set("requestPosition"," ");
			}
			temptoprint.insert();
		}
	}
	private void  getRequestPerson(String branch,String rpNo,String requestDate,String type)
        {
                try {
			
			String day = "17";
			if (requestDate.compareTo("25530317") >= 0)
				day = utility.receipt.Receipt.getDayToAsk(branch);
                        if(type.charAt(0) == 'I')
                                type = "IX";
                        else if(type.charAt(0) == 'U')
                                type = "AB";
                        else
                                type= "OWZU";
                        if(requestDate.substring(6).compareTo(day) >=0)
                        {
                                String yyyymm = DateInfo.nextMonth(requestDate.substring(0,6)).substring(0,6);
                                Mrecord ask = CFile.opens("ask"+yyyymm+"@cbranch");
                                System.out.println("yyyymm == "+yyyymm+"  "+branch);
                                ask.start(2);
                                for(boolean st = ask.equal(branch+rpNo);st && ( branch+rpNo).compareTo(ask.get("branch")+ask.get("rpNo")) == 0;st=ask.next())
                                {
                                        if(type.indexOf(ask.get("receiptFlag")) >= 0)
					{
                                        	getDepNoAndName(ask.get("askSaleID"));
						return ;
					}
                                }
                        }
                        Mrecord ask = CFile.opens("ask"+requestDate.substring(0,6)+"@cbranch");
                        System.out.println("yyyymm == "+ask.name());
                        ask.start(2);
                //      if(ask.equal(branch+rpNo))
                        for(boolean st = ask.equal(branch+rpNo);st && ( branch+rpNo).compareTo(ask.get("branch")+ask.get("rpNo")) == 0;st=ask.next())
                        {
                                if(type.indexOf(ask.get("receiptFlag")) >= 0)
				{
                                        getDepNoAndName(ask.get("askSaleID"));
				}
                        }

                }
                catch(Exception e)
                {
                }
        }
	BraSales bsale;
	void getDepNoAndName(String saleID)
        {
                 try {
			if(bsale == null)
				bsale = new BraSales();
                        bsale.getBySalesID(saleID);
			temptoprint.set("reqName",bsale.getSnRec("preName")+bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName"));
			temptoprint.set("requestPosition",bsale.getSnRec("strID"));
			temptoprint.set("position",getPosition(temptoprint.get("requestPosition")));
			temptoprint.set("requestID",saleID);
			
                }
                catch (Exception e)
                {
                }
        }
	String getPosition(String strID)
	{
		if(strID.charAt(0) == 'B')
			return M.stou("หน่วย");
		else if(strID.charAt(0) == 'D')
			return M.stou("ศูนย์");
		if(strID.charAt(0) == 'G')
			return M.stou("ภาค");
		if(strID.charAt(0) == 'L')
			return M.stou("ฝ่าย");
		return M.stou("ตัวแทน");
	}

}

