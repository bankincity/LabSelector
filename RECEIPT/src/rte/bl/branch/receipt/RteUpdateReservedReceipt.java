package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteUpdateReservedReceipt  implements Task
{
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {fileName,typeOfPol}",-1);
		Object [] parameter = (Object []) param;
		int countRp = 0 ;
		String userID = "0000000";
		if(parameter.length == 3)
		{
			userID = (String)parameter[2];
		}
		Mrecord rsrange = Masic.opens("reservedrange@receipt");
		String fileName  = (String)parameter[0];
		String typeOfPol  = (String)parameter[1];
		String sdate = DateInfo.sysDate();
		String month = DateInfo.nextMonth(sdate.substring(0,6)).substring(0,6);
		String sfname = typeOfPol.toLowerCase()+"rpserved";
		try {
			if(Masic.fileStatus(sfname+month+"@cbranch") < 2)
			{
				month = sdate.substring(0,6);
				if(Masic.fileStatus(sfname+month+"@cbranch") < 2)
				{
					throw new Exception(M.stou("ไม่สามรถเปิดแฟ้มข้อมูลการสำรองใบเสร็จได้ ")+sfname);
				}
				
			}
			System.out.println("month============"+month+"   "+sfname);		
			Rtemp trec = new Rtemp(new String [] {"rpNo","pol_period"},new int [] {12,14});
			Mrecord temp = Masic.opent(fileName,trec.copy().layout());
			if(temp == null || temp.lastError() != 0 )
				throw new Exception(M.stou("Can not open temp ")+fileName);
			Vector rpprint = new Vector();
			for (boolean st = temp.first();st;st=temp.next())
			{
				rpprint.addElement(temp.get("rpNo")+temp.get("pol_period"));
			}
			Vector rperr = null;
			if (typeOfPol.charAt(0) == 'U')
			{
				rte.bl.universal.receipt.ULReceiptServer rp = new  rte.bl.universal.receipt.ULReceiptServer();
				rperr = rp.alreadyPrintRpForm(rpprint,month,typeOfPol,userID);
			}
			else {
				Receipt rp = new Receipt();
				rperr = rp.alreadyPrintRpForm(rpprint,month,typeOfPol,userID);
			}
			rsrange.set("typePol",typeOfPol);
			temp.first();
			rsrange.set("startRange",temp.get("rpNo"));
			temp.last();
			rsrange.set("endRange",temp.get("rpNo"));
			rsrange.set("printDate",sdate);
			rsrange.insert();
			return  new Result(rperr,0);
			
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),2);
		}
	}
}
