package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteChangeStatusRp  implements Task
{
	Mrecord mrp;
	Mrecord yrp;
	String userID;
	String typeOfPolicy;
	String sysdate;
	String systime;
	String status;
	String reasonCode;
	Mrecord xrcchg;
  /**==============================================================<br>
                <pre Vspace=100>
                execute String[] {String userID, String newStatus,String reasonCode,String typeOfPolicy,Vector}
				Strting userID 		:= user ID
				String 	newStatus	:= new receipt status
				String reasonCode	:= reason to change status
				String typeOfPolicy	:= type of policy
				Vector 			:= vector of array {rpNo,policyNo,payPeriod,payDate}
                        Return :
                        Status :
                                 0 = success
                                -1 = errorMessage or system error</pre>

        =================================================================*/

	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {userID,status,reasonCode,typeOfPolicy,Vector}",-1);
		Object [] parameter = (Object []) param;
		
		userID =(String)parameter[0];
		status  =(String)parameter[1];
		reasonCode  =(String)parameter[2];
		typeOfPolicy = (String)parameter[3];
		Vector vec = (Vector)parameter[4];
		sysdate = DateInfo.sysDate();
		systime =  Masic.time("commontable").substring(8);
		String filename = "wrctrl";
		try {

			System.out.println("bingo............."+typeOfPolicy); 
			if(typeOfPolicy.charAt(0) == 'U')
			{ 
				mrp = CFile.opens("ulrctrl@universal");
				filename = "ulrctrl";
			}
			else if(typeOfPolicy.charAt(0) == 'O')
			{
				mrp = CFile.opens("orctrl@receipt");
				filename = "orctrl";
			}
			else if(typeOfPolicy.charAt(0) == 'I')
			{
				mrp = CFile.opens("irctrl@receipt");
				filename = "irctrl";
			}
			else
				mrp  = CFile.opens("wrctrl@receipt");
			xrcchg = CFile.opens("xrpchg@cbranch");
			changeReceipt(vec,filename);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
	private void changeReceipt(Vector tvec,String filename) throws Exception
	{
		for (int i = 0 ; i < tvec.size() ; i++)
		{
			// rpno,policyno,payPeriod,payDate
			String [] rp = (String [])tvec.elementAt(i);
			Record trp =null;
			System.out.println("search -------"+rp[0]+"  "+rp[1]+"  "+rp[2]+"  "+mrp.name());
			if(searchReceipt(mrp,rp[0],rp[1],rp[2]))
			{
				mrp.update();
				trp = mrp.copy();	
			}
			else {
				if (typeOfPolicy.charAt(0) == 'U')
					yrp = CFile.opens(filename+rp[3].substring(0,4)+"@universal");
				else
					yrp = CFile.opens(filename+rp[3].substring(0,4)+"@receipt");
				if(searchReceipt(yrp,rp[0],rp[1],rp[2]))
				{
					if (M.itis(yrp.get("rpNo").substring(3,12),'0'))
					{
						if (typeOfPolicy.charAt(0) == 'I')
							yrp.set("rpNo",yrp.get("rpNo").substring(0,6)+yrp.get("payPeriod"));
						else
							yrp.set("rpNo",yrp.get("rpNo").substring(0,8)+yrp.get("payPeriod"));
					}
					mrp.insert(yrp.copy());
					trp = yrp.copy();	
					yrp.delete();
				}
				else 
					throw new Exception(M.stou("ไม่พบข้อมูลใบเสร็จเลขที่ ")+rp[0]+" "+rp[1]+" "+rp[2]);
			}
			if(trp != null)
			{
				trp.set("currentStatus",oldStatus);
				insertXrcchg(xrcchg,typeOfPolicy.charAt(0),trp,status);
			}
		}
	}
	String oldStatus ="";
	private boolean  searchReceipt(Mrecord rc,String rpNo,String policyNo,String payPeriod) throws Exception
	{
		for (boolean st = rc.equal(rpNo);st && rpNo.compareTo(rc.get("rpNo")) == 0;st=rc.next())
		{
			if(policyNo.compareTo(rc.get("policyNo")) == 0 && payPeriod.compareTo(rc.get("payPeriod")) == 0)
			{
				oldStatus = rc.get("currentStatus");
				rc.set("currentStatus",status);
				rc.set("sysDate",sysdate);
			//c.set("userID",userID);
				rc.set("reasonCode",reasonCode);
				return true;	
			}
		}
		return false;
	}	
	private void insertXrcchg(Mrecord rcchg,char type,Record rc,String newstatus)
	{
		System.out.println("xrp --------------->"+rc.get("rpNo")+"  newstatus--->"+newstatus );
		CheckAskReceipt.insertXrcchg(rcchg,type,rc,newstatus,sysdate,systime,userID);
	}
}
