package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import  rte.bl.branch.*;
import  rte.*;
import utility.cfile.Rtemp;
import utility.branch.sales.*;
import utility.rteutility.*;
import java.util.*;
import utility.support.DateInfo;
public class  RteCheckForReqTrpOtherBranch  implements Task
{
	Mrecord irc;
        Mrecord orc;
        Mrecord wrc;
        Mrecord trp;
        Mrecord urc;

	String branchCode;
	String depNo ;
	String yyyymm;
	String yyyymm_1;
	String salesID;
	public Result execute(Object param)
        {
		if(!(param instanceof String []))
		{
			return new Result("Invalid parameter String []",-1);
		}
		String [] parameter = (String[])param;	
			
		yyyymm  = (String)parameter[0];
                yyyymm_1 = (String)parameter[1];
		branchCode = (String)parameter[2];
		salesID = (String)parameter[3];
                try {
                         irc = CFile.opens("irctrl@receipt");
                         orc = CFile.opens("orctrl@receipt");
                         wrc = CFile.opens("wrctrl@receipt");
                         trp = CFile.opens("trpctrl@receipt");
                         urc = CFile.opens("ulrctrl@universal");
			 System.out.println("bingo............................."+yyyymm_1+"  "+yyyymm+"   "+branchCode+"   "+salesID);
                         boolean ret =  checkInAsk(yyyymm_1);
	                 System.out.println("ret========"+yyyymm_1+"  "+ret);
                         if (ret)
                                return new Result(new String [] {"1",yyyymm_1},0);
                        ret =  checkInAsk(yyyymm);
	                 System.out.println("ret========"+yyyymm+"  "+ret);
                        if (ret)
                                 return new Result(new String [] {"1",yyyymm},0);
                        
                }
                catch(Exception e)
                {
                        return new Result(e.getMessage(),-1);
                }
	                 System.out.println("ret========"+yyyymm+"  ");
                return new Result(new String [] {"0",yyyymm},0);

        }
        private boolean checkInAsk(String reqMonth) throws Exception
        {
                Mrecord ask = CFile.opens("ask"+reqMonth+"@cbranch");
                for (boolean st = ask.equalGreat(branchCode+salesID) ; st && (branchCode+salesID).compareTo(ask.get("branch")+ask.get("askSaleID")) == 0 ; st = ask.next())
                {
                          switch (ask.get("receiptFlag").charAt(0)){
                                case 'I' : if (CheckAskReceipt.checkUCStatus(irc,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true ;
                                        break ;
                                case 'O' : if (CheckAskReceipt.checkUCStatus(orc,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true;
                                        break ;
                                case 'W' : if (CheckAskReceipt.checkUCStatus(wrc,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true;
                                        break ;
                                case 'A' : if (urc == null)
                                           {
                                                if (Masic.fileStatus("ulrctrl@universal") > 2)
                                                        urc = Masic.opens("ulrctrl@universal");
                                           }
                                           if ( urc != null && CheckAskReceipt.checkUCStatus(urc,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true;
                                        break ;
                                case 'T' : if (CheckAskReceipt.checkUCStatus(trp,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true ;
                                        break ;
                        }

                }
                ask.start(1);
                for (boolean st = ask.equalGreat(branchCode+salesID) ; st && (branchCode+salesID).compareTo(ask.get("branch")+ask.get("ownerSaleID")) == 0 ; st = ask.next())
                {
                          switch (ask.get("receiptFlag").charAt(0)){
                                case 'I' : if (CheckAskReceipt.checkUCStatus(irc,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true ;
                                        break ;
                                case 'O' : if (CheckAskReceipt.checkUCStatus(orc,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true;
                                        break ;
                                case 'W' : if (CheckAskReceipt.checkUCStatus(wrc,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true;
                                        break ;
                                case 'A' : if (urc == null)
                                           {
                                                if (Masic.fileStatus("ulrctrl@universal") > 2)
                                                        urc = Masic.opens("ulrctrl@universal");
                                           }
                                           if ( urc != null && CheckAskReceipt.checkUCStatus(urc,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true;
                                        break ;
                                case 'T' : if (CheckAskReceipt.checkUCStatus(trp,ask.get("rpNo"),ask.get("requestDate") ) )
                                                return true ;
                                        break ;
                        }
                }
                return false ;
        }
}
