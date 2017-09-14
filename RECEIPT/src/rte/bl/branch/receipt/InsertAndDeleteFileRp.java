package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class InsertAndDeleteFileRp implements Task
{
	Mrecord irc;
 	Mrecord orc;
 	Mrecord wrc;
 	Mrecord forc;
 	Mrecord firc;
 	Mrecord fwrc;
	String dyear;
	String iyear;
	String type;
 	String policyNo;
 	String payPeriod;
 	String rpNo;
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {tmp,userID,currentStatus}",-1);
 		Object [] parameter = (Object []) param;
 		dyear = (String)parameter[0];
 		iyear = (String)parameter[1];
 		type = (String)parameter[2];
 		policyNo = (String)parameter[3];
 		payPeriod = (String)parameter[4];
 		rpNo = (String)parameter[5];
 		try
 		{
			boolean res = processData(dyear,iyear,type,policyNo,payPeriod,rpNo);
			return new Result(new Boolean(res),0);
// 			Result res = processData(dyear,iyear,type,policyNo,payPeriod,rpNo);
//			return (res);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
 	}
//	private Result processData(String dyear,String iyear,String type,String policyNo,String payPeriod,String rpNo) throws Exception
	String prefix = "";
	private boolean processData(String dyear,String iyear,String type,String policyNo,String payPeriod,String rpNo) throws Exception
 	{
		Vector v = new Vector();
 		boolean a = false;
		if(dyear.charAt(0) == 'c')
		{
			prefix = "c";
			if(M.itis(dyear.substring(1),'0'))
				dyear = "0000";
			else
				dyear = "25"+dyear.substring(2,4);

		}
		if(M.itis(dyear,'0'))
		{	
			System.out.println("----------TIK-------------"+type);
			switch (type.charAt(0))
			{
				case 'I' : 	firc = CFile.opens(prefix+"irctrl@receipt");
							a = insertData(firc,dyear,iyear,"I",policyNo,payPeriod,rpNo);
							break;
				case 'O' : 	forc = CFile.opens(prefix+"orctrl@receipt");
							a = insertData(forc,dyear,iyear,"O",policyNo,payPeriod,rpNo);
							break;
				case 'W' : 	fwrc = CFile.opens(prefix+"wrctrl@receipt");
							a = insertData(fwrc,dyear,iyear,"W",policyNo,payPeriod,rpNo);
							break;
			}
		}
		else
		{
			switch (type.charAt(0))
			{
				case 'I' : 	firc = CFile.opens(prefix+"irctrl"+dyear+"@receipt");
							a = insertData(firc,dyear,iyear,"I",policyNo,payPeriod,rpNo);
							break;
				case 'O' : 	forc = CFile.opens(prefix+"orctrl"+dyear+"@receipt");
							a = insertData(forc,dyear,iyear,"O",policyNo,payPeriod,rpNo);
							break;
				case 'W' : 	fwrc = CFile.opens("wrctrl"+dyear+"@receipt");
							a = insertData(fwrc,dyear,iyear,"W",policyNo,payPeriod,rpNo);
							break;
			}


		}
		System.out.println("A"+a);
 		return (a);
//		return new Result(v,0);
	}
	
	private boolean insertData(Mrecord f,String dyear,String iyear,String type,String policyNo,String payPeriod,String rpNo) throws Exception
	{
 		boolean b = false;
 		boolean d = false;
			System.out.println("----Type--"+type);
		if(M.itis(dyear,'0') && prefix.trim().length () == 0 ) //prefix.charAt(0) != 'c')
		{
			System.out.println("----IF");
			f.start(1);
			for(boolean ok=f.equalGreat(policyNo);
 						ok && f.get("policyNo").compareTo(policyNo) == 0 ; ok=f.next())
			{
			System.out.println("--1--Type--"+type);
			System.out.println("--1--rpNo--"+f.get("rpNo")+"-----"+rpNo);
			System.out.println("--1--policy--"+f.get("policyNo")+"----"+policyNo);
			System.out.println("--1--payPeriod--"+f.get("payPeriod")+"----"+payPeriod);
			if(payPeriod.compareTo(f.get("payPeriod")) != 0)
					continue;			
			System.out.println("--2--Type--"+type);
			if(rpNo.compareTo(f.get("rpNo")) != 0)
					continue;
			System.out.println("--3--Type--"+type+iyear);
				switch (type.charAt(0))
				{
					case 'I' : 	firc = CFile.opens("irctrl"+iyear+"@receipt");
				 				firc.putBytes(f.getBytes());
				 				d = firc.insert();
								if(d)
								{
									b = f.delete();
								}
								else
								{
									System.out.println("---I--can--not--insert----year"+iyear);
								}
								break;
					case 'O' : 	forc = CFile.opens("orctrl"+iyear+"@receipt");
				 				forc.putBytes(f.getBytes());
				 				d = forc.insert();
								System.out.println("insert-------------"+iyear+"------------"+d);
								if(d)
								{
									b = f.delete();
								}
								else
								{
									System.out.println("--O---can--not--insert----year"+iyear);
								}
								break;
					case 'W' : 	fwrc = CFile.opens("wrctrl"+iyear+"@receipt");
				 				fwrc.putBytes(f.getBytes());
				 				d = fwrc.insert();
								if(d)
								{
									b = f.delete();
								}
								else
								{
									System.out.println("-W----can--not--insert----year"+iyear);
								}
								break;
				}
				System.out.println("if--dyear0000--result--delete->"+b);
			}
				
		}
		else
		{
			System.out.println("----ELSE");
			f.start(0);
			for(boolean ok=f.equalGreat(rpNo);
 						ok && f.get("rpNo").compareTo(rpNo) == 0 ; ok=f.next())
			{
				if(policyNo.compareTo(f.get("policyNo")) != 0)
					continue;
									System.out.println("-------1-------"+iyear);
			System.out.println("--1--Type--"+type);
			System.out.println("--1--rpNo--"+f.get("rpNo")+"-----"+rpNo);
			System.out.println("--1--policy--"+f.get("policyNo")+"----"+policyNo);
			System.out.println("--1--payPeriod--"+f.get("payPeriod")+"----"+payPeriod);
				if(payPeriod.compareTo(f.get("payPeriod")) != 0)
					continue;
									System.out.println("--------2------"+iyear);
				switch (type.charAt(0))
				{
					case 'I' : 	if(M.itis(iyear,'0'))
								{
									System.out.println("--if--iiii-----"+iyear);
									firc = CFile.opens("irctrl@receipt");
								}
								else
								{
									System.out.println("--else--iiii-----"+iyear);
									firc = CFile.opens("irctrl"+iyear+"@receipt");
								}	
									System.out.println("--------5------"+iyear);
				 				firc.putBytes(f.getBytes());
									System.out.println("--------6------"+iyear);
				 				d = firc.insert();
									System.out.println("--------7------"+d);
								if(d)
								{
									b = f.delete();
								}
								else
								{
									System.out.println("---I--can--not--insert----year"+iyear);
								}
								break;
					case 'O' : 	if(M.itis(iyear,'0'))
								{
					 				forc = CFile.opens("orctrl@receipt");
								}
								else
								{
					 				forc = CFile.opens("orctrl"+iyear+"@receipt");
								}
				 				forc.putBytes(f.getBytes());
				 				d = forc.insert();
								if(d)
								{
									b = f.delete();
								}
								else
								{
									System.out.println("--O---can--not--insert----year"+iyear);
								}
								break;
					case 'W' : 	if(M.itis(iyear,'0'))
								{
									fwrc = CFile.opens("wrctrl@receipt");
								}
								else
								{
									fwrc = CFile.opens("wrctrl"+iyear+"@receipt");
								}
				 				fwrc.putBytes(f.getBytes());
				 				d = fwrc.insert();
								if(d)
								{
									b = f.delete();
								}
								else
								{
									System.out.println("-W----can--not--insert----year"+iyear);
								}
								break;
				}
				System.out.println("else--dyearxxxx--result--delete->"+b);
			}

		}
		System.out.println("B"+b);
 		return (b);
 	}
}

