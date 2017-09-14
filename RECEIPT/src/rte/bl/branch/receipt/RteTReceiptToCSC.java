package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteTReceiptToCSC  implements Task
{
	Mrecord trptmp;
	Mrecord trprange;
	Mrecord trptmptakaful;
	Mrecord trptakafulrange;
	Mrecord trp;
	Mrecord ask;

	String userID;
	String sysdate;
	String branch;
	String yyyymm;
	String saleID ;
	String askSaleID;
	String requestDate;
        String trpNo;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {userID,branch,saleID,trpNo}",-1);
		String  [] parameter = (String []) param;
                requestDate = sysdate = utility.support.DateInfo.sysDate();
                yyyymm = sysdate.substring(0,6);

		userID =(String)parameter[0];
		branch = (String)parameter[1];
		askSaleID = saleID = (String)parameter[2];
                
                trpNo = (String)parameter[3];
		try {
			ask = CFile.opens("ask"+yyyymm+"@cbranch");

		/*	if(askSaleID.trim().length() == 10)
				cask = CFile.opens("cask"+yyyymm+"@cbranch");
			else
        			askSaleID = saleID; */

			trp = CFile.opens("trpctrl@receipt");

			trptmp = CFile.opens("trptmp@cbranch");
			trptmptakaful = CFile.opens("trptmptakaful@cbranch");

			trprange = CFile.opens("trprange@receipt");
			trptakafulrange = CFile.opens("trptakafulrange@receipt");
                        
                        if (trprange.great(branch+"U") && (branch+"U").compareTo(trprange.get("branch")+trprange.get("used")) == 0)
                        {
                                System.out.println("bingo........found range");
                                if (trpNo.compareTo( trprange.get("endTrp")) <= 0)
                                {
                                         System.out.println("bingo........in  range");
                                         if (trprange.get("currentTrp").compareTo(trpNo) <= 0)
                                         {
                                              System.out.println("bingo........less  range");
                                              String trp = trpNo;
                                              trp = M.inc(trp);
                                              trprange.set("currentTrp",trp);
                                              if (trp.compareTo(trprange.get("endTrp")) >0)
                                              {
                                                        trprange.set("used","N");
                                              }
                                              trprange.update();        
                                         }       
                                         insertAskAndUpdateTrp();
                                         return new Result("",0);
                                 }       
                        }
			
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),-1);
		}
                return new Result(M.stou("กรุณาตรวจสอบช่วงใบเสร็จชั่วคราว"),1);
	}
	private void insertAskAndUpdateTrp() throws Exception
	{
                ask.set(' ');
		ask.set("branch",branch);
		ask.set("askSaleID",askSaleID);
		ask.set("ownerSaleID",saleID);
		ask.set("requestDate",requestDate);
		ask.set("receiptFlag","T");
		ask.set("rpNo",trpNo);
		ask.insert();
		updateTrpCtrl(ask.get("rpNo"),requestDate);
	}	
	private void updateTrpCtrl(String rpno,String reqDate) throws Exception
	{
		boolean found = false ;
		for (boolean st = trp.equal(rpno) ; st && rpno.compareTo(trp.get("rpNo")) == 0 ;st = trp.next())
		{
			if(branch.compareTo(trp.get("branch")) == 0)
			{
				trp.set("requestDate",reqDate);
				trp.set("askUserID",userID);
				trp.update();
				found = true;
			}
		}
                System.out.println("found in trp............."+found);
		if(!found)
		{
			trp.set('0');
			trp.set("rpNo",rpno);
			trp.set("requestDate",reqDate);
			trp.set("statusDate",reqDate);
			trp.set("askUserID",userID);
			trp.set("currentStatus","N");
			trp.set("originalStatus","N");
			trp.set("branch",branch);
			trp.set("branchFlag"," ");
			trp.insert();			
		}
	}
        public static void main(String args[])
        {
                 RteTReceiptToCSC rte = new  RteTReceiptToCSC();
                 Result res = rte.execute (new String[] {"9002772","834","0000600696","777000000003"});
                 if (res.status() != 0)
                        System.out.println((String)res.value());
        }
}
