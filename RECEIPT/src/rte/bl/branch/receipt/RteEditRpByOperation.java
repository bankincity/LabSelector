package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteEditRpByOperation  implements Task
{
	Mrecord orc;
	Mrecord irc;
	Mrecord trp;
	Mrecord wrc;
	String userID;
	String sysdate;
	String systime;
	String policyNo;
	String rpNo;
	String payPeriod;
	String action ;
	String [] field;
	String [] data;
    String oldPeriod = "";
	Mrecord xrcchg;
	Mrecord yearrec;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {action,policyNo,rpNo,payPeriod,field [] ,data [] }",-1);
		Object [] parameter = (Object []) param;
		
		action = (String)parameter[0];
		policyNo = (String)parameter[1];
		rpNo =(String)parameter[3];
		payPeriod = ((String)parameter[2]).trim();
		field = (String [])parameter[4];
		data = (String [])parameter[5];
		payDate = (String)parameter[6];
        if(parameter.length == 8)
            oldPeriod = (String)parameter[7];
			
		sysdate = DateInfo.sysDate();
		systime =  Masic.time("commontable").substring(8);
		try {
			orc = CFile.opens("orctrl@receipt");
			irc = CFile.opens("irctrl@receipt");
			wrc = CFile.opens("wrctrl@receipt");
            if(action.equals("DN"))
                nDeleteReceipt();
			else if(action.charAt(0) == 'E')
				updateReceipt();
			else if(action.charAt(0) == 'D')
				deleteReceipt();
		}
		catch (Exception e)
		{
                        e.printStackTrace();
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
	String payDate = "";
	private void updateReceipt() throws Exception
	{
		System.out.println("edit....................."+policyNo);
		switch (policyNo.charAt(0)){
			case 'I' :if(searchReceipt(irc,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						irc.set(field[i],data[i]);
					}
					irc.update();
					break;
						
				}
				yearrec = CFile.opens("irctrl"+payDate.substring(0,4)+"@receipt");
				if(searchReceipt(yearrec,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						yearrec.set(field[i],data[i]);
					}
					yearrec.update();
				}
				else 
					throw new Exception(M.stou("ใบเสร็จไม่อยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
									
				break ;
			case 'O' :if(searchReceipt(orc,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						orc.set(field[i],data[i]);
					}
					orc.update();
					break;
						
				}
				yearrec = CFile.opens("orctrl"+payDate.substring(0,4)+"@receipt");
				if(searchReceipt(yearrec,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						yearrec.set(field[i],data[i]);
					}
					yearrec.update();
				}
				else 
					throw new Exception(M.stou("ใบเสร็จไม่อยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
									
				break ;
			case 'W' :if(searchReceipt(wrc,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						wrc.set(field[i],data[i]);
					}
					wrc.update();
					break;
						
				}
				yearrec = CFile.opens("wrctrl"+payDate.substring(0,4)+"@receipt");
				if(searchReceipt(yearrec,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						yearrec.set(field[i],data[i]);
					}
					yearrec.update();
				}
				else 
					throw new Exception(M.stou("ใบเสร็จไม่อยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
									
				break ;
		}
	}
	private void deleteReceipt() throws Exception
	{
		switch (policyNo.charAt(0)){
			case 'I' :if(searchReceipt(irc,policyNo.substring(1),rpNo,payPeriod))
				{
					System.out.println("found.................................");
					for (int i = 0 ; i < field.length;i++)
					{
						irc.set(field[i],data[i]);
					}
					byte [] buffer = irc.getBytes();
					if(irc.delete())
					{
						irc.putBytes(buffer);
						irc.insert();
					}
					break;
						
				}
				yearrec = CFile.opens("irctrl"+payDate.substring(0,4)+"@receipt");
				if(searchReceipt(yearrec,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						yearrec.set(field[i],data[i]);
					}
					byte [] buffer = yearrec.getBytes();
					if(yearrec.delete())
					{
						yearrec.putBytes(buffer);
						yearrec.insert();
					}
				}
				else 
					throw new Exception(M.stou("ใบเสร็จไม่อยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
									
				break ;
			case 'O' :if(searchReceipt(orc,policyNo.substring(1),rpNo,payPeriod))
				{
					System.out.println("found.................................");
					for (int i = 0 ; i < field.length;i++)
					{
						orc.set(field[i],data[i]);
					}
					byte [] buffer = orc.getBytes();
					if(orc.delete())
					{
						orc.putBytes(buffer);
						orc.insert();
					}
					break;
						
				}
				yearrec = CFile.opens("orctrl"+payDate.substring(0,4)+"@receipt");
				if(searchReceipt(yearrec,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						yearrec.set(field[i],data[i]);
					}
					byte [] buffer = yearrec.getBytes();
					if(yearrec.delete())
					{
						yearrec.putBytes(buffer);
						yearrec.insert();
					}
				}
				else 
					throw new Exception(M.stou("ใบเสร็จไม่อยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
									
				break ;
			case 'W' :if(searchReceipt(wrc,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						wrc.set(field[i],data[i]);
					}
					byte [] buffer = wrc.getBytes();
					if(wrc.delete())
					{
						wrc.putBytes(buffer);
						wrc.insert();
					}
					break;
						
				}
				yearrec = CFile.opens("wrctrl"+payDate.substring(0,4)+"@receipt");
				if(searchReceipt(yearrec,policyNo.substring(1),rpNo,payPeriod))
				{
					for (int i = 0 ; i < field.length;i++)
					{
						yearrec.set(field[i],data[i]);
					}
					byte [] buffer = yearrec.getBytes();
					if(yearrec.delete())
					{
						yearrec.putBytes(buffer);
						yearrec.insert();
					}
				}
				else 
					throw new Exception(M.stou("ใบเสร็จไม่อยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
									
				break ;
		}
	}
    private void nDeleteReceipt() throws Exception
    {
        System.out.println("**----------NDelete");
        System.out.println("**NDelete "+policyNo+" "+rpNo+" "+payPeriod+" "+oldPeriod);
        switch (policyNo.charAt(0)){
            case 'I' :if(searchReceipt(irc,policyNo.substring(1),rpNo,oldPeriod))
                {
                    System.out.println("found-I.................................");
                    for (int i = 0 ; i < field.length;i++)
                    {
                        irc.set(field[i],data[i]);
                    }
                    byte [] buffer = irc.getBytes();
                    if(irc.delete()) 
                    {
                        irc.putBytes(buffer);
                        irc.insert();
                    }
                    break;

                }
                yearrec = CFile.opens("irctrl"+payDate.substring(0,4)+"@receipt");
                if(searchReceipt(yearrec,policyNo.substring(1),rpNo,oldPeriod))
                {
                    System.out.println("found-I-Old.................................");
                    for (int i = 0 ; i < field.length;i++)
                    {
                        yearrec.set(field[i],data[i]);
                    }
                    byte [] buffer = yearrec.getBytes();
                    if(yearrec.delete())
                    {
                        yearrec.putBytes(buffer);
                        yearrec.insert();
                    } 
                }
                else
                    throw new Exception(M.stou("ใบเสร็จไม่อยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
                break ;
            case 'O' :if(searchReceipt(orc,policyNo.substring(1),rpNo,oldPeriod))
                {
                    System.out.println("found.................................");
                    for (int i = 0 ; i < field.length;i++)
                    {
                        orc.set(field[i],data[i]);
                    }
                    byte [] buffer = orc.getBytes();
                    if(orc.delete())
                    {
                        orc.putBytes(buffer);
                        orc.insert();
                    }
                    break;

                }
                yearrec = CFile.opens("orctrl"+payDate.substring(0,4)+"@receipt");
                if(searchReceipt(yearrec,policyNo.substring(1),rpNo,oldPeriod))
                {
                    for (int i = 0 ; i < field.length;i++)
                    {
                        yearrec.set(field[i],data[i]);
                    }
                    byte [] buffer = yearrec.getBytes();
                    if(yearrec.delete())
                    {
                        yearrec.putBytes(buffer);
                        yearrec.insert();
                    }
                }
                else
                    throw new Exception(M.stou("ใบเสร็จไม่อยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
                break ;
                case 'W' :if(searchReceipt(wrc,policyNo.substring(1),rpNo,oldPeriod))
                {
                    for (int i = 0 ; i < field.length;i++)
                    {
                        wrc.set(field[i],data[i]);
                    }
                    byte [] buffer = wrc.getBytes();
                    if(wrc.delete())
                    {
                        wrc.putBytes(buffer);
                        wrc.insert();
                    }
                    break;

                }
                yearrec = CFile.opens("wrctrl"+payDate.substring(0,4)+"@receipt");
                if(searchReceipt(yearrec,policyNo.substring(1),rpNo,oldPeriod))
                {
                    for (int i = 0 ; i < field.length;i++)
                    {
                        yearrec.set(field[i],data[i]);
                    }
                    byte [] buffer = yearrec.getBytes();
                    if(yearrec.delete())
                    {
                        yearrec.putBytes(buffer);
                        yearrec.insert();
                    }
                }
                else
                    throw new Exception(M.stou("ใบเสร็จไม่อยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
                break ;
        }
    }
 
	private boolean  searchReceipt(Mrecord rc,String policyNo,String rpNo,String payPeriod) throws Exception
	{
		System.out.println(policyNo+" "+rpNo+" "+payPeriod);
		for (boolean st = rc.equal(rpNo);st && rpNo.compareTo(rc.get("rpNo")) == 0;st=rc.next())
		{
			System.out.println(rc.get("policyNo")+"--------"+rc.get("payPeriod")+"<><><>"+policyNo+"-------"+payPeriod+"----");
			if(policyNo.compareTo(rc.get("policyNo")) == 0 )
			{
				System.out.println("found..............1");	
				if( payPeriod.trim().compareTo(rc.get("payPeriod").trim()) == 0 )
				{
					System.out.println("found..............2");	
					return true;	
				}
			}
		}
		return false ;
	}	
}
