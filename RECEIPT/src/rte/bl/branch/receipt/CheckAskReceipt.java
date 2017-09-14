package rte.bl.branch.receipt;
import manit.*;
public class CheckAskReceipt
{
	public static int  inAskYYMM(Mrecord  task , String saleID,String reqSaleID , int keyIdx,boolean sameDep,String branch)
	{
                task.start(keyIdx);
                boolean   otherRequestFor = saleID.compareTo(reqSaleID) != 0 ;
                if (task.equal(branch+saleID))
                {
                        if (sameDep){
                                int typeOfRp = 0 ;
                                while (task.get("ownerSaleID").compareTo(saleID) == 0){
                                        if (task.get("askSaleID").compareTo(reqSaleID) != 0){
                                                if (otherRequestFor == false)
                                                        otherRequestFor = true ;
                                                if (!task.next())
                                                        break;
                                                else
                                                        continue ;
                                        }
                                        if (task.get("receiptFlag").charAt(0) == 'T' || task.get("receiptFlag").charAt(0) == 'Z'){
                                                if (typeOfRp != 1)
                                                        typeOfRp +=1;
                                        }
                                        else {
                                                if (typeOfRp != 2)
                                                        typeOfRp +=2;
                                        }
                                        if (typeOfRp == 3)
                                            break ;
                                        if (!task.next())
                                                break;
                                } // while
                                return typeOfRp ;
                           } // same saleID
                           else
                                return 1;
                }// equal
                else
                        return 0 ;
        }
	static Mrecord urc = null;
	public  static boolean checkDepno(Mrecord  ask ,String saleID,int currkey,boolean Rplus,Mrecord irc,Mrecord orc,Mrecord wrc,Mrecord trp,String branch)
	{
		boolean st;
		String ask_saleid ;
		ask.start(currkey);
		st = ask.equal(branch+saleID);
		while (st ==true){
			if (currkey == 0 )
				ask_saleid = ask.get("askSaleID");
			else
				ask_saleid = ask.get("ownerSaleID");
			if (!ask_saleid.equals(saleID))
				break ;
			else if (Rplus)
				return true ;
			switch (ask.get("receiptFlag").charAt(0)){
				case 'I' : if (checkUCStatus(irc,ask.get("rpNo"),ask.get("requestDate") ) )
						return true ;
					break ;
				case 'O' : if (checkUCStatus(orc,ask.get("rpNo"),ask.get("requestDate") ) )
						return true;
					break ;
				case 'W' : if (checkUCStatus(wrc,ask.get("rpNo"),ask.get("requestDate") ) )
						return true;
					break ;
				case 'A' : if (urc == null)
					   {	
						if (Masic.fileStatus("ulrctrl@universal") > 2)
						 	urc = Masic.opens("ulrctrl@universal"); 
					   }
					   if ( urc != null && checkUCStatus(urc,ask.get("rpNo"),ask.get("requestDate") ) )
						return true;
					break ;
				case 'T' : if (checkUCStatus(trp,ask.get("rpNo"),ask.get("requestDate") ) )
						return true ;
					break ;
			}
			st = ask.next();
		}
		return false ;
	}
	     // check  Receipt  : clear or not  < true : not clear>
	public static boolean  checkUCStatus(Mrecord rc,String rpNo,String requestDate)
	{
			System.out.println("CheckReceipt----------rpno --"+rpNo+"---");
		
		for (boolean st = rc.equal(rpNo)  ; st &&  rpNo.compareTo(rc.get("rpNo")) ==0 ; st = rc.next())
		{
			System.out.println("rpno --"+rc.get("rpNo")+"---"+rc.get("currentStatus")+"---"+rc.get("requestDate"));
			if ("ANXUDW".indexOf(rc.get("currentStatus")) >= 0 && requestDate.compareTo(rc.get("requestDate") ) ==0){
				if (rc.get("currentStatus").charAt(0) == 'X')
				{
					if (rc.get("rpNo").substring(0,3).compareTo("000") == 0 || rc.get("rpNo").substring(0,3).compareTo("900") == 0)
						return false ;
				}
				return true;
			}
		}
		return false ;
        }
	public static void  insertXrcchg(Mrecord chg,char type,Record rc,String status,String sysdate,String systime,String userID)
        {
		System.out.println("status---------"+status+"    "+ rc.get("currentStatus"));
		if(status.charAt(0) == rc.get("currentStatus").charAt(0))
			return ;
		
		chg.set("sysDate",sysdate);
		chg.set("sysTime",systime);
		chg.set("rpNo",rc.get("rpNo"));
		chg.set("policyNo",rc.get("policyNo"));
		chg.set("premium",M.setlen(rc.get("premium"),9));
		chg.set("oldStatus",rc.get("currentStatus"));
		chg.set("newStatus",status);
		chg.set("userID",userID);
		if (type == 'I')
		{
			chg.set("typeOfReceipt","I");
			chg.set("payPeriod",rc.get("payPeriod"));
			chg.set("branch",rc.get("submitBranch"));
		}
                else if (type == 'W')
		{
                        chg.set("typeOfReceipt","W");
			chg.set("payPeriod",rc.get("payPeriod")+"00");
			chg.set("branch",rc.get("submitBranch"));
		}
                else if (type == 'O')
		{
                        chg.set("typeOfReceipt","O");
			chg.set("payPeriod",rc.get("payPeriod")+"00");
			chg.set("branch",rc.get("submitBranch"));
		}
                else if (type == 'U')
		{
                        chg.set("typeOfReceipt","U");
			chg.set("payPeriod",rc.get("payPeriod")+"00");
			chg.set("branch",rc.get("submitBranch"));
		}
                else if (type == 'T')
                {
			chg.set("payPeriod","000000");
			chg.set("typeOfReceipt","T");
			chg.set("branch",rc.get("branch"));
		}
		if(!chg.insert())
			System.out.println("can not insert xrpchg "+rc.get("rpNo"));
		System.out.println("insert xrpchg  success   "+rc.get("rpNo"));
		
        }

}
