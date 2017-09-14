package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import utility.support.MyVector;
public class RteDeleteAsk  implements Task
{
	Mrecord trptmp;
	Mrecord trptmptakaful;
	Mrecord orc;
	Mrecord irc;
	Mrecord trp;
	Mrecord wrc;
	Mrecord ask;
	Mrecord cask;
	Mrecord ucrp;

	String userID;
	String sysdate;
	String branch;
	MyVector vec ;
	String yyyymm;
	String ucrpyyyymm  = "";
	String saleID ;
	String askSaleID;
	String requestDate;
	boolean  flagSuper  = false;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {yyyymm,ucrpyyyymm,userID,branch,saleID,askSaleID,requestDate}",-1);
		Object [] parameter = (Object []) param;
		boolean search = true;
		// for delete 
		if(parameter.length == 7)
		{
			yyyymm = (String)parameter[0];
			ucrpyyyymm = (String)parameter[1];
			branch = (String)parameter[2];
			userID =(String)parameter[3];
			if(userID.charAt(0) == 'S')
			{
				flagSuper = true;
				userID = userID.substring(1);
			}
			saleID = (String)parameter[4];
			askSaleID = (String)parameter[5];
			requestDate = (String)parameter[6];
			search = false ;
		}
		else {
			yyyymm = (String)parameter[0];
			branch = (String)parameter[1];
			saleID = (String)parameter[2];
			askSaleID = (String)parameter[3];
			requestDate = (String)parameter[4];
		}
		sysdate = DateInfo.sysDate();

		try {
			ask = CFile.opens("ask"+yyyymm+"@cbranch");
			cask = CFile.opens("cask"+yyyymm+"@cbranch");
			if(ucrpyyyymm.trim().length() > 0)
				ucrp = CFile.opens(ucrpyyyymm+"@cbranch");
			orc = CFile.opens("orctrl@receipt");
			irc = CFile.opens("irctrl@receipt");
			wrc = CFile.opens("wrctrl@receipt");
			trp = CFile.opens("trpctrl@receipt");
			trptmp = CFile.opens("trptmp@cbranch");
			trptmptakaful = CFile.opens("trptmptakaful@cbranch");
			if (search)
			{
				 vec = new MyVector();
				 searchAskReceipt();				
			}
			else {
				 vec = new MyVector();
				 searchAskReceipt();			
				 deleteAskReceipt();	
			}
			return new Result(vec,0);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
	}	
	int ret;
	public void searchAskReceipt() throws Exception
	{
		ask.start(1);
		boolean st = ask.equal(branch+saleID);
		String errMessage = "";
		System.out.println(branch+saleID+"-------------------------------------in search to Cancel request -------------------");
 		while (st  && saleID.equals(ask.get("ownerSaleID")) )
		{
			if (saleID.equals(ask.get("ownerSaleID")) &&askSaleID.equals(ask.get("askSaleID"))&&requestDate.equals(ask.get("requestDate"))) {
			//	System.out.println(ask.get("rpNo")+" "+ask.get("receiptFlag"));
				ret = checkReceipt(ask.get("receiptFlag").charAt(0),ask.get("rpNo"),ask.get("requestDate"),false);
				switch (ret){
					case 0 :
						errMessage = M.stou("ใบเสร็จเลขที่ ")+ ask.get("receiptFlag")+"  "+ask.get("rpNo")+M.stou(" ยังไม่ได้เคลียร์ ");	
					default : 
						vec.add (ask.get("receiptFlag")+ask.get("rpNo"));
						break ;
				}
				//if (ret == 2 || ret == 1 || ret == 3)
				if ( ret == 0 )
					throw new Exception (errMessage);
			}
			st =  ask.next();
		}
	}
  	private int  checkReceipt(char type,String rpNo,String reqDate,boolean update)
        {
		Mrecord  rec =  null;
		if (type == 'I')
			rec = (Mrecord)irc;
		else if (type == 'O')
			rec = (Mrecord)orc;
		else if (type == 'W')
			rec = (Mrecord)wrc;
		else if (type == 'T')
			rec = (Mrecord)trp;
		else 
			return 3;
		for (boolean st = rec.equal(rpNo) ; st && rpNo.equals(rec.get("rpNo")) ; st = rec.next())
		{
			if (reqDate.equals(rec.get("requestDate")))
			{
				if("NAXW".indexOf(rec.get("currentStatus")) >= 0)
					return 0 ;
				return 2;
			}
		}
		return 1 ;
        }
	void deleteAskReceipt() throws Exception
	{
		String tstr = "";	
		System.out.println("delete ask........................................"+vec.size());
		for (int i = 0 ; i < vec.size();i++)
		{
			tstr = (String)vec.elementAt(i);
			System.out.println("tstr .................."+tstr);
			removeAsk(tstr.substring(1));			
		}
	}
	void removeAsk(String rpNo) throws Exception
        {
		ask.start(2);
		for (boolean st = ask.equal(branch+rpNo) ; st && (branch+rpNo).equals(ask.get("branch")+ask.get("rpNo")) ; st = ask.next()){
			System.out.println("rpno .................."+rpNo);
			System.out.println("buffer ......"+new String(ask.getBytes()));
			if (requestDate.equals(ask.get("requestDate")) && ask.get("ownerSaleID").equals(saleID) && ask.get("askSaleID").equals(askSaleID)){
				ask.delete();
				break;
			}
                 }
		 if(ucrp == null)
			return ;
		ucrp.start(2);
		 for (boolean st = ucrp.equal(branch+rpNo) ; st && rpNo.equals(ucrp.get("rpNo")) ; st = ucrp.next())
		 {
			if (requestDate.equals(ucrp.get("requestDate")) && ucrp.get("ownerSaleID").equals(saleID) && ucrp.get("askSaleID").equals(askSaleID)){
				ucrp.delete();
				break;
			}
                 }
        }
}
