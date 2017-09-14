package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import utility.receipt.Receipt;
public class RteBuildAskyyyymm  implements Task
{
	Mrecord orc;
	Mrecord irc;
	Mrecord trp;
	Mrecord wrc;
	Mrecord urc;
	Mrecord ask;
	Mrecord askyymm;
	Mrecord ucrp;

	String sysdate;
	String yyyymm;
	String yyyymm_1;
	String [] megaBranch = {"007","017","012","571","987","994","998"};
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {yyyymm}",-1);
		Object [] parameter = (Object []) param;
		boolean search = true;
		// for delete 
		if(parameter.length == 1)
		{
			yyyymm = (String)parameter[0];
		}
		sysdate = DateInfo.sysDate();
		try {
		//	if(Masic.fileStatus("ask"+yyyymm+"@cbranch") < 2)
		//	{
				askyymm = CFile.opens("askyyyymm@cbranch");
				irc = CFile.opens("irctrl@receipt");
				orc = CFile.opens("orctrl@receipt");
				wrc = CFile.opens("wrctrl@receipt");
				trp = CFile.opens("trpctrl@receipt");
				if (Masic.fileStatus("ulrctrl@universal") >= 2)
					urc = CFile.opens("ulrctrl@universal");				
				
				if (Masic.fileStatus("ask"+yyyymm+"@cbranch") < 2)
				{
					CFile.build("ask"+yyyymm+"@cbranch"); 
					CFile.build("cask"+yyyymm+"@cbranch");
					CFile.build("caskother"+yyyymm+"@receipt");

   					yyyymm_1=  DateInfo.previousMonth(yyyymm);
					ask = CFile.opens("ask"+yyyymm_1+"@cbranch");
   					yyyymm_1=  DateInfo.previousMonth(yyyymm_1);
					ucrp = CFile.openbuild("ucrp"+yyyymm_1+"@cbranch");				
					moveAskyyyymmToUcrp();
				}	
				else {
   					yyyymm_1=  DateInfo.previousMonth(yyyymm);
					ask = CFile.opens("ask"+yyyymm_1+"@cbranch");
   					yyyymm_1=  DateInfo.previousMonth(yyyymm_1);
					ucrp = CFile.openbuild("ucrp"+yyyymm_1+"@cbranch");			
				}	
	//			if (DateInfo.sysDate().substring(6).compareTo("19") >= 0)
				moveAskToAskyyyymm();
		//else
		//		moveAskToAskyyyymmMegaBranch();
				System.out.println("already move ask -- > askyymm ");
		/*	}
			else
				throw new Exception(M.stou("มีแฟ้มเบิกเดือนนี้แล้ว"));*/
					
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result(M.stou("ดำเนินการเรียบร้อยแล้ว"),0);
	}
	private boolean isMegaBranch(String tbranch)
	{
		for (int i = 0 ; i < megaBranch.length;i++)
		{
			if(tbranch.compareTo(megaBranch[i]) == 0)
				return true;
		}
		return false ;
	}
	private void moveAskyyyymmToUcrp() throws Exception
	{
		String field[] = askyymm.layout().fieldName();
		for (boolean st = askyymm.first();st ;st = askyymm.next())
		{
			for (int i = 0 ; i < field.length ; i++)
			{
				ucrp.set(field[i],askyymm.get(field[i]));
                        }
			if(!ucrp.insert())
				throw new Exception(M.stou("insert failed ucrp"+yyyymm_1+" error =="+M.itoc(ucrp.lastError())));
		}
	}	
	private void moveAskToAskyyyymmMegaBranch() throws Exception
	{
		for (int i = 0 ; i < megaBranch.length;i++)
		{
			for (boolean st = ask.great(megaBranch[i]); st && megaBranch[i].compareTo(ask.get("branch")) == 0;st=ask.next())
		
			{
				switch (ask.get("receiptFlag").charAt(0))
				{
					case 'O' :  if (CheckAskReceipt.checkUCStatus(orc,ask.get("rpNo"),ask.get("requestDate")))
							insertToAskyymm();                         
						break;
					case 'W' :  if (CheckAskReceipt.checkUCStatus(wrc,ask.get("rpNo"),ask.get("requestDate")))
							insertToAskyymm();
						break;
					case 'I' :  if (CheckAskReceipt.checkUCStatus(irc,ask.get("rpNo"),ask.get("requestDate")))
							insertToAskyymm();
						break;
					case 'T' :  if (CheckAskReceipt.checkUCStatus(trp,ask.get("rpNo"),ask.get("requestDate")))
							insertToAskyymm();
						break;
					case 'A' :  if (CheckAskReceipt.checkUCStatus(urc,ask.get("rpNo"),ask.get("requestDate")))
							insertToAskyymm();
						break;
					default : 
						break;
				}
			}
		}
	}
	private void moveAskToAskyyyymm() throws Exception
	{
		boolean st = ask.first();
		while (st)
		{
		/*f(isMegaBranch(ask.get("branch")))
			{
				st = ask.next();
				continue;
			}	*/
			switch (ask.get("receiptFlag").charAt(0))
			{
				case 'O' :  if (CheckAskReceipt.checkUCStatus(orc,ask.get("rpNo"),ask.get("requestDate")))
						insertToAskyymm();                         
					break;
				case 'W' :  if (CheckAskReceipt.checkUCStatus(wrc,ask.get("rpNo"),ask.get("requestDate")))
						insertToAskyymm();
					break;
				case 'I' :  if (CheckAskReceipt.checkUCStatus(irc,ask.get("rpNo"),ask.get("requestDate")))
						insertToAskyymm();
					break;
				case 'T' :  if (CheckAskReceipt.checkUCStatus(trp,ask.get("rpNo"),ask.get("requestDate")))
						insertToAskyymm();
					break;
				case 'A' :  if (CheckAskReceipt.checkUCStatus(urc,ask.get("rpNo"),ask.get("requestDate")))
						insertToAskyymm();
					break;
				default : 
					break;
			}
			st = ask.next();
		}
	}
        private void insertToAskyymm() throws Exception
        {
		String field[] = ask.layout().fieldName();
		for (int i = 0 ; i < field.length ; i++)
		{
			askyymm.set(field[i],ask.get(field[i]));
		}
		if(!askyymm.insert())
			throw new Exception(M.stou("insert failed askyyyymm error =="+M.itoc(askyymm.lastError())));
	}
}
