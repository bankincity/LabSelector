package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteCancelReceiptOperation implements Task
{
	Mrecord orc;
	Mrecord crc;
	Mrecord irc;
	Mrecord trp;
	Mrecord wrc;
	String userID;
	String sysdate;
	String systime;
	String branch;
	Mrecord xrcchg;
	Mrecord yearrec;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {userID,branch,Vector}",-1);
		Object [] parameter = (Object []) param;
		
		branch = (String)parameter[0];
		userID =(String)parameter[1];
		Vector vec = (Vector)parameter[2];
		
		System.out.println("vec pass to RteCancelReceiptOperation   size ------>"+vec.size());
		sysdate = DateInfo.sysDate();
		sysdate = "99"+sysdate.substring(2,4)+sysdate.substring(4);
		systime =  Masic.time("commontable").substring(8);
		try {
			orc = CFile.opens("orctrl@receipt");
			irc = CFile.opens("irctrl@receipt");
			wrc = CFile.opens("wrctrl@receipt");
			trp = CFile.opens("trpctrl@receipt");
			xrcchg = CFile.opens("xrpchg@cbranch");
			cancelReceipt(vec);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
	String payDate = "";
	private void cancelReceipt(Vector tvec) throws Exception
	{
		String branch = "000";
		boolean flagReUpdate = false ;
		for (int i = 0 ; i < tvec.size() ; i++)
		{
			// typeofreceipt,rpno,policyno,cancelstatus,reasonCode,payDate,payPeriod
			String [] rp = (String [])tvec.elementAt(i);
			if(tvec.size() == 1)
			{
				branch = getBranchOwnerCase(rp[2],rp[0]);
				flagReUpdate = true;
			}
			payDate = rp[5];
			switch (rp[0].charAt(0)){
				case 'I' :if(searchReceipt(irc,rp[1],rp[2],rp[6]))
					{
						insertXrcchg(xrcchg,'I',irc.copy(),rp[3]);
						irc.set("currentStatus",rp[3]);
						irc.set("sysDate",sysdate);
				//		irc.set("userID",userID);
						irc.set("reasonCode",rp[4]);
						irc.update();
						break;
						
					}
					yearrec = CFile.opens("irctrl"+payDate.substring(0,4)+"@receipt");
					if(searchReceipt(yearrec,rp[1],rp[2],rp[6]))
					{
						insertXrcchg(xrcchg,'I',yearrec.copy(),rp[3]);
						yearrec.set("currentStatus",rp[3]);
						yearrec.set("sysDate",sysdate);
					//	yearrec.set("userID",userID);
						yearrec.set("reasonCode",rp[4]);
						irc.putBytes(yearrec.getBytes());
						irc.insert();
						yearrec.delete();		
						break;	
					}
					crc = CFile.opens("circtrl@receipt");
					if(searchReceipt(crc,rp[1],rp[2],rp[6]))
					{
						System.out.println("foound in circtrl  "+payDate);
						insertXrcchg(xrcchg,'I',crc.copy(),rp[3]);
						crc.set("currentStatus",rp[3]);
						crc.set("sysDate",sysdate);
					//	yearrec.set("userID",userID);
						crc.set("reasonCode",rp[4]);
						irc.putBytes(crc.getBytes());
						irc.insert();
						crc.delete();	
					}
					else 
						throw new Exception(M.stou("ใบเสร็จไมาอยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
									
					break ;
				case 'O' :if(searchReceipt(orc,rp[1],rp[2],rp[6]))
					{
						insertXrcchg(xrcchg,'O',orc.copy(),rp[3]);
						orc.set("currentStatus",rp[3]);
						orc.set("sysDate",sysdate);
					//	orc.set("userID",userID);
						orc.set("reasonCode",rp[4]);
						orc.update();
						break;
					}
					System.out.println("open paydate == "+payDate);
					yearrec = CFile.opens("orctrl"+payDate.substring(0,4)+"@receipt");
					if(searchReceipt(yearrec,rp[1],rp[2],rp[6]))
					{
						System.out.println("foound in year  "+payDate);
						insertXrcchg(xrcchg,'O',yearrec.copy(),rp[3]);
						yearrec.set("currentStatus",rp[3]);
						yearrec.set("sysDate",sysdate);
					//	yearrec.set("userID",userID);
						yearrec.set("reasonCode",rp[4]);
						orc.putBytes(yearrec.getBytes());
						orc.insert();
						yearrec.delete();	
						break;		
					}
					crc = CFile.opens("corctrl@receipt");
					if(searchReceipt(crc,rp[1],rp[2],rp[6]))
					{
						System.out.println("foound in corctrl  "+payDate);
						insertXrcchg(xrcchg,'O',crc.copy(),rp[3]);
						crc.set("currentStatus",rp[3]);
						crc.set("sysDate",sysdate);
					//	yearrec.set("userID",userID);
						crc.set("reasonCode",rp[4]);
						orc.putBytes(crc.getBytes());
						orc.insert();
						crc.delete();	
					}
					else 
						throw new Exception(M.stou("ใบเสร็จไมาอยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
					break ;
				case 'W' : if(searchReceipt(wrc,rp[1],rp[2],rp[6]))
					{
						insertXrcchg(xrcchg,'W',wrc.copy(),rp[3]);
						wrc.set("currentStatus",rp[3]);
						wrc.set("sysDate",sysdate);
					//	wrc.set("userID",userID);
						wrc.set("reasonCode",rp[4]);
						wrc.update();
						break;
					}
					yearrec = CFile.opens("wrctrl"+payDate.substring(0,4)+"@receipt");
					if(searchReceipt(yearrec,rp[1],rp[2],rp[6]))
					{
						insertXrcchg(xrcchg,'W',yearrec.copy(),rp[3]);
						yearrec.set("currentStatus",rp[3]);
						yearrec.set("sysDate",sysdate);
					//	yearrec.set("userID",userID);
						yearrec.set("reasonCode",rp[4]);
						wrc.putBytes(yearrec.getBytes());
						wrc.insert();
						yearrec.delete();		
						break;	
					}
					crc = CFile.opens("corctrl@receipt");
					if(searchReceipt(crc,rp[1],rp[2],rp[6]))
					{
						System.out.println("foound in circtrl  "+payDate);
						insertXrcchg(xrcchg,'W',crc.copy(),rp[3]);
						crc.set("currentStatus",rp[3]);
						crc.set("sysDate",sysdate);
					//	yearrec.set("userID",userID);
						crc.set("reasonCode",rp[4]);
						wrc.putBytes(crc.getBytes());
						wrc.insert();
						crc.delete();	
					}
					else 
						throw new Exception(M.stou("ใบเสร็จไมาอยู่ในแฟ้มคุม หรือ แฟ้ม ปี")+payDate.substring(0,4));
					break ;
				case 'T' : if(searchReceipt(trp,rp[1],rp[2],rp[6]))
					{
						insertXrcchg(xrcchg,'T',trp.copy(),rp[3]);
						trp.set("currentStatus",rp[3]);
						trp.set("sysDate",sysdate);
					//	trp.set("userID",userID);
					//	trp.set("reasonCode",rp[4]);
						trp.update();
					}
					break ;

			}
		}
	}
	private void searchForReceipt(String policyNo,String payPeriod,String status,String typePol,Mrecord crec,String branch) throws Exception
	{
		crec.start(1);
		String sysdate = "00000000";
		String rpNo = "000000000000";
		for (boolean st = crec.equalGreat(policyNo+payPeriod) ; st ; st = crec.next())
		{
			if((policyNo+payPeriod).compareTo(crec.get("policyNo")+crec.get("payPeriod")) != 0)
				break;
			if(status.charAt(0) == crec.get("originalStatus").charAt(0))
			{
				if(branch.compareTo(crec.get("rpNo").substring(0,3)) == 0)
				{
					if(sysdate.compareTo(crec.get("sysDate")) < 0)
					{
						sysdate = crec.get("sysDate");
						rpNo = crec.get("rpNo");
					}	
				}
			}
		}
		if(crec.equal(policyNo+payPeriod+rpNo))
		{
			if("CZ".indexOf(crec.get("currentStatus").charAt(0)) >= 0)
			{
				crec.start(0);
				throw new Exception(M.stou("กรุณาเตือนสาขา ")+ branch+M.stou(" เนื่องจากได้ยกเลิกใบเสร็จของกรมธรรมนี้ งวดชำระนี้แล้ว(")+crec.get("rpNo")+")");
			}
			else if(crec.get("currentStatus").charAt(0) == 'W' && crec.get("reasonCode").compareTo("21") == 0)
			{
				crec.set("currentStatus",status);
				crec.set("reasonCode","00");
				crec.update();
			}				
		}
		crec.start(0);
	}
	private String  getBranchOwnerCase(String policyNo,String typePol) throws Exception
	{
		Mrecord master  = null;
		if(typePol.charAt(0) == 'I')
			master = CFile.opens("indmast@mstpolicy");
		else if(typePol.charAt(0) == 'O')
			master = CFile.opens("ordmast@mstpolicy");
		else 
			master = CFile.opens("whlmast@mstpolicy");	
		if(master.equal(policyNo))
		{
			return master.get("branch");
		}
		return "000";
	}
	private boolean  searchReceipt(Mrecord rc,String rpNo,String policyNo,String payPeriod) throws Exception
	{
		for (boolean st = rc.equal(rpNo);st && rpNo.compareTo(rc.get("rpNo")) == 0;st=rc.next())
		{
			if(policyNo.compareTo(rc.get("policyNo")) == 0 && payPeriod.compareTo(rc.get("payPeriod")) == 0 )
				return true;	
		}
		return false ;
	}	
	private void insertXrcchg(Mrecord rcchg,char type,Record rc,String newstatus)
	{
		rcchg.first();
		System.out.println("xrp --------------->"+rc.get("rpNo")+" "+type+" "+newstatus+" "+userID+" "+sysdate+" "+systime);
		CheckAskReceipt.insertXrcchg(rcchg,type,rc,newstatus,sysdate,systime,userID);
		System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
	}
}
