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
import utility.receipt.*;
public class  GetReceiptTRPToPayFeeByFile implements  InterfaceRpt
{
        Mrecord trc;
	Mrecord rpfee;
	Mrecord rpbook;
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("AT GetReceiptTRPToPayFeeByFile args === "+args.length);
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

		trc = CFile.opens("trpctrl@receipt");
		rpfee = CFile.opens("confirm_feeforlosetrp@insuredocument");
		rpbook = CFile.opens("confirm_bookforlosetrp@insuredocument");
		
		if(args[5].charAt(0) == 'L')
			temptoprint = new TempMasicFile("rptinsure",field1,len1);
		else
			temptoprint = new TempMasicFile("rptinsure",field,len);
		temptoprint.keyField(false,false,new String [] {"typePol","rpNo"});
        temptoprint.buildTemp();

		//   branch sdate status
		if(args[5].charAt(0) == 'L')
		{
			System.out.println(" ----->>> BUA TEST 1 :" ); 
			getReceiptToBook("T",trc,args[3],args[4],"R");		
		}
		else {
			getReceiptToFee("T",trc,args[3],args[4],args[5]);		
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
			if((status+sdate+branch).compareTo(trec.get("currentStatus")+trec.get("statusDate")+trec.get("branch").substring(0,3)) != 0)
				break;
			temptoprint.set('0');
			temptoprint.set("typePol",type);
			temptoprint.set("rpNo",trec.get("rpNo"));
			temptoprint.set("policyNo",trec.get("policyNo"));
			temptoprint.set("payPeriod","000000");
			temptoprint.set("premium",M.setlen(trec.get("premium"),9));
			if(rpfee != null && rpfee.equal(trec.get("rpNo")+trec.get("policyNo")+branch))
			{
				temptoprint.set("rpFee",rpfee.get("rpFee"));
			}
			temptoprint.insert();	
		}
	}
	private void getReceiptToBook(String type,Mrecord trec ,String branch,String sdate,String status)
	{
		trec.start(4);
		System.out.println(status+sdate+branch);
		for (boolean st = trec.equalGreat(status+sdate+branch); st ;st = trec.next())
		{
			if((status+sdate+branch).compareTo(trec.get("currentStatus")+trec.get("statusDate")+trec.get("branch").substring(0,3)) != 0)
				break;
			temptoprint.set(' ');
			
			temptoprint.set("typePol",type);
			temptoprint.set("rpNo",trec.get("rpNo"));
			temptoprint.set("policyNo",trec.get("policyNo"));
			temptoprint.set("payPeriod","00000");
			temptoprint.set("premium",M.setlen(trec.get("premium"),9));
			if(rpfee != null && rpfee.equal(trec.get("rpNo")+trec.get("policyNo")+branch))
			{
				temptoprint.set("rpFee",rpfee.get("rpFee"));
				temptoprint.set("sdate",rpfee.get("acdate"));
			}
			if(rpbook != null && rpbook.equal(trec.get("rpNo")+trec.get("policyNo")+branch))
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
	
        private  String getAccDate(String date)
        {
                String accDate = date.substring(2,6)+"15";
                if (date.substring(6).compareTo("16") >=0)
                   accDate = date.substring(2,6)+"30";
                return accDate;
        }

	private void  getRequestPerson(String branch,String rpNo,String requestDate,String type)
        {
                try {
			String day = "20";
			if (requestDate.compareTo("25530317") >= 0)
				day = utility.receipt.Receipt.getDayToAsk(branch);
                        if(type.charAt(0) == 'T')
                                type = "TZ";

                        String accDate = getAccDate(requestDate);

			String askID="";
                        Mrecord ask = null;
                        System.out.println("accDate == "+accDate);
                        if (Masic.fileStatus("ask"+accDate+"@cbranch") >= 2)
                        {
                                        System.out.println("open file == "+accDate);
                                        ask = CFile.opens("ask"+accDate+"@cbranch");
                                        ask.start(2);
                                        for (boolean st = ask.equal(branch+rpNo); st && (branch+rpNo).compareTo(ask.get("branch")+ask.get("rpNo")) == 0 ;st=ask.next())
                                        {
                                                System.out.println("ask=="+new String(ask.getBytes()));
                                                if(type.indexOf(ask.get("receiptFlag")) >= 0)
                                                {
						        askID = ask.get("ownerSaleID");
                                                }
                                        }
				        if(askID.trim().length() > 0)
				        {
					        getDepNoAndName(askID);
					        return ;
				        }				
                                        
                        }

                        if(requestDate.substring(6).compareTo(day) >=0)
                        {
                                String yyyymm = DateInfo.nextMonth(requestDate.substring(0,6)).substring(0,6);
                                ask = CFile.opens("ask"+yyyymm+"@cbranch");
                                System.out.println("yyyymm == "+yyyymm+"  "+branch);
                                ask.start(2);
                                for(boolean st = ask.equal(branch+rpNo);st && ( branch+rpNo).compareTo(ask.get("branch")+ask.get("rpNo")) == 0;st=ask.next())
                                {
                                        if(type.indexOf(ask.get("receiptFlag")) >= 0)
					{
						askID = ask.get("ownerSaleID");
					}
                                }
				if(askID.trim().length() > 0)
				{
					getDepNoAndName(askID);
					return ;
				}				
                        }
                        ask = CFile.opens("ask"+requestDate.substring(0,6)+"@cbranch");
                        System.out.println("yyyymm == "+ask.name());
                        ask.start(2);
                //      if(ask.equal(branch+rpNo))
                        for(boolean st = ask.equal(branch+rpNo);st && ( branch+rpNo).compareTo(ask.get("branch")+ask.get("rpNo")) == 0;st=ask.next())
                        {
                                if(type.indexOf(ask.get("receiptFlag")) >= 0)
				{
					askID = ask.get("ownerSaleID");
				}
                        }
			if(askID.trim().length() > 0)
			{
				getDepNoAndName(askID);
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
			temptoprint.set("position",bsale.getSnRec("positionName"));
			temptoprint.set("requestID",saleID);
			temptoprint.set("requestPosition",bsale.getSnRec("strID"));
			
                }
                catch (Exception e)
                {
                }
        }

}

