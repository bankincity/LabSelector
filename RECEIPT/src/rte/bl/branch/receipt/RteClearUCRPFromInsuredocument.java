package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.DateInfo;
import java.util.Vector;
public class RteClearUCRPFromInsuredocument  implements Task
{
	Mrecord irc ;
	Mrecord orc ;
	Mrecord wrc ;
	Mrecord trc ;
	Mrecord uask;
	Vector retvec = new Vector();
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {String month,String strID,Vector of rpNo}",-1);
		Object [] parameter = (Object []) param;
		String month = (String)parameter[0];
		String strID = (String)parameter[1];
		Vector  vucrp = (Vector)parameter[2];
		String currdate = DateInfo.sysDate();
		currdate =  DateInfo.previousMonth(currdate).substring(0,6);
		String fileAsk = "askyyyymm@cbranch";
		
		try {
			irc = CFile.opens("irctrl@receipt");
			orc = CFile.opens("orctrl@receipt");
			wrc = CFile.opens("wrctrl@receipt");
			trc = CFile.opens("trpctrl@receipt");

			Mrecord ucrp = CFile.opens("uc"+month+"@cbranch");
			Mrecord ucrpdetail = CFile.opens("ucdetail"+month+"@cbranch");
			Mrecord askyyyymm = CFile.opens(fileAsk);
			if(currdate.compareTo(month) > 0)
			{
				fileAsk = "ucrp"+month+"@cbranch";
				uask = CFile.opens(fileAsk);
			}
			System.out.println("strID-----------------------------"+strID+"  month--------->"+month);
			if(ucrp.equal(strID))
			{
				System.out.println("strID-----------------------------"+strID+"    "+ucrp.get("branch"));
				clearData(vucrp,ucrp,ucrpdetail,askyyyymm,ucrp.get("branch"));
				if(retvec.size() == 0)
				{
					if(checkDetail(ucrp.get("salesID"),ucrpdetail))
					{
						ucrp.delete();
						clearOveride(month,ucrp.get("salesID"));
					}
				}
			}
		}
		catch (Exception e)
		{
				return new Result(e.getMessage(),1);
		}
		return new Result(retvec,0);
	}
	private void clearOveride(String month,String salesID)
	{
		try {
			Mrecord overr = CFile.opens("override"+month+"@cbranch");
			overr.start(2); // salesID+incomeType
			for (boolean st=overr.great(salesID); st && overr.get("salesID").equals(salesID); st=overr.next())
			{
				if (overr.get("unpaidStatus").equals("Y")) {
					overr.set("unpaidStatus", "N");
					overr.update();
				}
			}
			overr.close();
		} catch (Exception e) {
		}
	}
	private boolean checkDetail(String saleID,Mrecord ucdetail)
	{
		ucdetail.start(0);
		if (ucdetail.equalGreat(saleID)  && saleID.compareTo(ucdetail.get("salesID")) == 0)
			return false;
		return true;
	}
	private void clearData(Vector vec ,Mrecord ucrp,Mrecord ucdetail,Mrecord ask,String branch)
	{
		ucdetail.start(1);
		ask.start(2);
		if(uask != null)
			uask.start(2);
		for (int i = 0 ; i < vec.size();i++)
		{
			String [] str = (String [])vec.elementAt(i);
			System.out.println("..................."+str[0]+"  "+str[1]+"          "+str[2]);
			if(ucdetail.equal(str[0]+str[1]))
			{
				if(checkUC(ucdetail.get("receiptFlag"),ucdetail.get("rpNo"),ucdetail.get("requestDate")))
				{
					retvec.addElement(str);
					continue;
				}
				System.out.println("delete ..................."+str[0]+"  "+str[1]+"          "+str[2]);
				ucdetail.delete();
		//updateToNStatus(ucdetail.get("receiptFlag"),ucdetail.get("rpNo"),ucdetail.get("policyNo")); 
				if(ask.equal(branch+ucdetail.get("rpNo")))
				{
					ask.delete();
				}	
				if(uask != null)
				{
					if(uask.equal(branch+ucdetail.get("rpNo")))
					{
						uask.delete();
					}	
				}

			}
			else 
				retvec.addElement(str);
		}
	}
/*rivate void updateToNStatus(String receiptFlag,String rpNo,String policyNo)
	{
		if(receiptFlag.charAt(0) == 'I')
		{
			if(irc.get("rpNo").compareTo(rpNo) == 0)
			{
				irc.set("
			}	
		}	
	}*/
	private boolean checkUC(String receiptFlag,String rpNo,String requestDate)
	{
		switch (receiptFlag.charAt(0))
		{
			case 'I' : if (!CheckAskReceipt.checkUCStatus(irc,rpNo,requestDate))
					return false ;
					
				break;	
			case 'O' : if (!CheckAskReceipt.checkUCStatus(orc,rpNo,requestDate))
					return false ;
				break;
			case 'W' : if (!CheckAskReceipt.checkUCStatus(wrc,rpNo,requestDate))
					return false ;
				break;
			case 'T' : if (!CheckAskReceipt.checkUCStatus(trc,rpNo,requestDate))
					return false ;
				break;		
		}
		return true;	
	}
}
