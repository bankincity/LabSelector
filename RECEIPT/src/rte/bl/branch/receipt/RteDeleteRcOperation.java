package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteDeleteRcOperation implements Task
{
	Mrecord rc;
	Mrecord wrc;
	Mrecord crc;
	Mrecord yearrec;

	String userID;
	String sysdate;
	String systime;
	String typeRp;
	String policyNo;
	String payPeriod;
	String rpNo;
	String currentStatus;
	String premium;
	String payDate;
	public Result execute(Object param)
	{
		if(! (param instanceof String  []))
			return new Result("Invalid Parameter  : Object [] {userID,branch,Vector}",-1);
		String  [] parameter = (String []) param;
		for (int i = 0 ; i < parameter.length;i++)
			System.out.println(" paramer  "+i+"   "+parameter[i]);
		typeRp = parameter[0];
		policyNo = parameter[1];
		payPeriod = parameter[2];
		rpNo = parameter[3];
		currentStatus = parameter[4];
		premium = parameter[5].trim();
		payDate = parameter[6];
		userID = parameter[7];

		
		sysdate = DateInfo.sysDate();
		systime =  Masic.time("commontable").substring(8);
		try {
			if(typeRp.charAt(0) == 'I')
			{
				rc = CFile.opens("irctrl@receipt");
				crc = CFile.opens("circtrl@receipt");
				if(!deleteFromRc(rc))
				{
					if(!deleteFromRc(crc))
					{
						String yyyy = sysdate.substring(0,4);
						while (yyyy.compareTo("2528") >= 0)
						{
							
							if(CFile.isFileExist("irctrl"+yyyy+"@receipt"))
							{
								rc = CFile.opens("irctrl"+yyyy+"@receipt");
								if(deleteFromRc(rc))
									break;
							}
							yyyy = M.dec(yyyy);
						}
					}
				}
			}
			else if(typeRp.charAt(0) == 'W')
			{
				payPeriod = payPeriod.substring(0,4);
				rc = CFile.opens("wrctrl@receipt");
				crc = CFile.opens("corctrl@receipt");
				if(!deleteFromRc(rc))
				{
					if(!deleteFromRc(crc))
					{
						String yyyy = sysdate.substring(0,4);
						while (yyyy.compareTo("2528") >= 0)
						{
					
							if(CFile.isFileExist("wrctrl"+yyyy+"@receipt"))
							{
								rc = CFile.opens("wrctrl"+yyyy+"@receipt");
								if(deleteFromRc(rc))
									break;
							}
							yyyy = M.dec(yyyy);
						}
					}
				}
			}
			else if(typeRp.charAt(0) == 'O')
			{
				payPeriod = payPeriod.substring(0,4);
				rc = CFile.opens("orctrl@receipt");
				crc = CFile.opens("corctrl@receipt");
				if(!deleteFromRc(rc))
				{
					if(!deleteFromRc(crc))
					{
						String yyyy = sysdate.substring(0,4);
						while (yyyy.compareTo("2528") >= 0)
						{
							System.out.println("yyyy---------------"+yyyy);
							if(CFile.isFileExist("orctrl"+yyyy+"@receipt"))
							{
								rc = CFile.opens("orctrl"+yyyy+"@receipt");
								if(deleteFromRc(rc))
									break;
							}
							yyyy = M.dec(yyyy);
						}
					}
				}
			}			
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
	private boolean deleteFromRc(Mrecord trc)
	{
		for (boolean st = trc.equal(rpNo);st && trc.get("rpNo").compareTo(rpNo) == 0;st=trc.next())
		{
			System.out.println(trc.get("rpNo")+" "+trc.get("policyNo")+" "+trc.get("payPeriod")+" "+trc.get("premium")+" "+trc.get("currentStatus")+" "+trc.get("payDate"));
			System.out.println(rpNo+" "+policyNo+" "+payPeriod+" "+premium+" "+currentStatus+" "+payDate);
			if(trc.get("policyNo").compareTo(policyNo) == 0 &&
			  trc.get("payPeriod").compareTo(payPeriod) == 0 &&
			  M.cmps(trc.get("premium"),premium) == 0 &&
			  currentStatus.charAt(0) == trc.get("currentStatus").charAt(0) &&
			  payDate.compareTo(trc.get("payDate")) ==0)
                          
                        
			{
				trc.delete();
				return true;
			}					
		}
		return false ;
	}
}
