package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.Vector;
public class RteUpdateDivideBook  implements Task
{
	Mrecord ucdetail ;
	Mrecord irc ;
	Mrecord orc;
	Mrecord wrc;
	Mrecord trc;
	Mrecord trpctrl;
	Vector vucrp ;
	String month ;
	String bookNo;
	String debtStatus= "";
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {Vector vucrp  String month String bookNo }",-1);
		Object [] parameter = (Object []) param;
		if(parameter.length == 3)
		{
			vucrp = (Vector)parameter[0];
			month = (String)parameter[1];
			bookNo = (String)parameter[2];
		}
		else if(parameter.length == 4)
		{
			vucrp = (Vector)parameter[0];
			month = (String)parameter[1];
			String [] tt  = (String [])parameter[2];
			bookNo = tt[0];
			debtStatus = (String)parameter[3];
		System.out.println("bingo--------------status == "+debtStatus);
		}
		try {
			ucdetail = CFile.opens("ucdetail"+month+"@cbranch");
			orc = 	CFile.opens("orctrl@receipt");	
			wrc = 	CFile.opens("wrctrl@receipt");	
			irc = 	CFile.opens("irctrl@receipt");	
			trpctrl = CFile.opens("trpctrl@receipt");	
			ucdetail.start(1);
			for (int i = 0 ; i < vucrp.size();i++)
			{
				String  trp = (String)vucrp.elementAt(i);
				if(trp.charAt(0) == 'I')
				{
					trc = irc;
				}
				else if(trp.charAt(0) == 'W')
				{
					trc = wrc;
				}
				else if(trp.charAt(0) == 'O')
				{
					if(orc.equal(trp.substring(1)))
						trc = orc;
					else 
						trc = wrc;
				}
				else if(trp.charAt(0) == 'T')
				{
					trc = trpctrl;
				}
				if(ucdetail.equal(trp))
				{
					
					if(debtStatus.trim().length() == 0)
					{
						ucdetail.set("returnDebtBook",bookNo);
						ucdetail.set("currentStatus","M");
					}
					else if(debtStatus.charAt(0) == 'D')
					{
						ucdetail.set("debtBook",bookNo);
						ucdetail.set("currentStatus",debtStatus);
					
					}
					else {
						ucdetail.set("returnDebtBook",bookNo);
						ucdetail.set("currentStatus",debtStatus);
					}
					ucdetail.update();
				}
				String typePol =  trp.substring(0,1);
				trp = trp.substring(1);
				if(trc.equalGreat(trp) && trp.compareTo(trc.get("rpNo")) == 0)
				{
					if(debtStatus.trim().length() == 0)
						trc.set("currentStatus","M");
					else
						trc.set("currentStatus",debtStatus);
					if (typePol.charAt(0) == 'T')
						trc.set("statusDate",DateInfo.sysDate());
					else
						trc.set("sysDate",DateInfo.sysDate());
					trc.update();
				}	
			}
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
}
