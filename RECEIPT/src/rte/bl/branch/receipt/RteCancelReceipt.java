package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteCancelReceipt implements Task
{
	Mrecord orc;
	Mrecord irc;
	Mrecord trp;
	Mrecord wrc;
	String userID;
	String sysdate;
	String systime;
	String branch;
	Mrecord xrcchg;
	Mrecord urc;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {userID,branch,Vector}",-1);
		Object [] parameter = (Object []) param;
		
		branch = (String)parameter[0];
		userID =(String)parameter[1];
		Vector vec = (Vector)parameter[2];
		
		System.out.println("vec pass to RteCancelReceipt   size ------>"+vec.size());
		sysdate = DateInfo.sysDate();
		systime =  Masic.time("commontable").substring(8);
		try {
			orc = CFile.opens("orctrl@receipt");
			irc = CFile.opens("irctrl@receipt");
			wrc = CFile.opens("wrctrl@receipt");
			trp = CFile.opens("trpctrl@receipt");
			urc = CFile.opens("ulrctrl@universal");
			xrcchg = CFile.opens("xrpchg@cbranch");
			cancelReceipt(vec);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
	private void cancelReceipt(Vector tvec) throws Exception
	{
		String branch = "000";
		boolean flagReUpdate = false ;
		for (int i = 0 ; i < tvec.size() ; i++)
		{
			// typeofreceipt,rpno,policyno,cancelstatus,reasonCode
			String [] rp = (String [])tvec.elementAt(i);
			if(tvec.size() == 1)
			{
				branch = getBranchOwnerCase(rp[2],rp[0]);
				flagReUpdate = true;
			}
			switch (rp[0].charAt(0)){
				case 'I' :if(searchReceipt(irc,rp[1],rp[2]))
					{
				//		irc.set("userID",userID);
						irc.set("reasonCode",rp[4]);
						insertXrcchg(xrcchg,'I',irc.copy(),rp[3]);
						irc.set("currentStatus",rp[3]);
						irc.set("sysDate",sysdate);
						irc.update();
						if(flagReUpdate && "CZ".indexOf(rp[3].charAt(0)) >=0  && branch.compareTo(rp[1].substring(0,3)) != 0)
							
							searchForReceipt(irc.get("policyNo"),irc.get("payPeriod"),irc.get("originalStatus"),rp[0],irc,branch);
						
					}
					break ;
				case 'O' :if(searchReceipt(orc,rp[1],rp[2]))
					{
				//		orc.set("userID",userID);
						orc.set("reasonCode",rp[4]);
						insertXrcchg(xrcchg,'O',orc.copy(),rp[3]);
						orc.set("currentStatus",rp[3]);
						orc.set("sysDate",sysdate);
						orc.update();
						if(flagReUpdate && "CZ".indexOf(rp[3].charAt(0)) >= 0 && branch.compareTo(rp[1].substring(0,3)) != 0)
							searchForReceipt(orc.get("policyNo"),orc.get("payPeriod"),orc.get("originalStatus"),rp[0],orc,branch);
					}
					break ;
				case 'W' : if(searchReceipt(wrc,rp[1],rp[2]))
					{
				//		wrc.set("userID",userID);
						wrc.set("reasonCode",rp[4]);
						insertXrcchg(xrcchg,'W',wrc.copy(),rp[3]);
						wrc.set("currentStatus",rp[3]);
						wrc.set("sysDate",sysdate);
						wrc.update();
						if(flagReUpdate && "CZ".indexOf(rp[3].charAt(0)) >= 0 && branch.compareTo(rp[1].substring(0,3)) != 0)
							searchForReceipt(wrc.get("policyNo"),wrc.get("payPeriod"),wrc.get("originalStatus"),rp[0],wrc,branch);
					}
					break ;
                                case 'A' :
				case 'U' : if(searchReceipt(urc,rp[1],rp[2]))
					{
						urc.set("reasonCode",rp[4]);
						insertXrcchg(xrcchg,'U',urc.copy(),rp[3]);
						urc.set("currentStatus",rp[3]);
						urc.set("sysDate",sysdate);
						urc.update();
						if(flagReUpdate && "CZ".indexOf(rp[3].charAt(0)) >= 0 && branch.compareTo(rp[1].substring(0,3)) != 0)
							searchForReceipt(urc.get("policyNo"),urc.get("payPeriod"),urc.get("originalStatus"),rp[0],urc,branch);
					}
					break ;
				case 'T' : if(searchReceipt(trp,rp[1],rp[2]))
					{
						insertXrcchg(xrcchg,'T',trp.copy(),rp[3]);
						trp.set("currentStatus",rp[3]);
						trp.set("sysDate",sysdate);
				//		trp.set("userID",userID);
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
				
				CheckAskReceipt.insertXrcchg(xrcchg,typePol.charAt(0),crec.copy(),status,sysdate,systime,"X000021");
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
	private boolean  searchReceipt(Mrecord rc,String rpNo,String policyNo) throws Exception
	{
		for (boolean st = rc.equal(rpNo);st && rpNo.compareTo(rc.get("rpNo")) == 0;st=rc.next())
		{
			if(policyNo.compareTo(rc.get("policyNo")) == 0)
			{
				if ("BPEK".indexOf(rc.get("currentStatus")) >= 0)
					throw new Exception(M.stou("สถานะของใบเสร็จที่ระบุเปลี่ยนเป็น ")+rc.get("currentStatus")+M.stou("  ไม่สามารถแก้ไขสถานะได้"));
				return true;	
			}
		}
		throw new Exception(M.stou("ข้อมูลถูกปรับลงแฟ้มประจำปีแล้วไม่สามารถแก้ไขได้ แจ้งส่วนประมวลผล"));		
	}	
	private void insertXrcchg(Mrecord rcchg,char type,Record rc,String newstatus)
	{
		System.out.println("xrp --------------->"+rc.get("rpNo"));
		CheckAskReceipt.insertXrcchg(rcchg,type,rc,newstatus,sysdate,systime,userID);
		System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
                if (type == 'T')
                        return ;
                System.out.println("reasonCode --- > "+rc.get("reasonCode"));
                if (rc.get("reasonCode").compareTo("43") == 0)
                {
                        insertDataToReserved(rc,type);
                }

	}
        private void insertDataToReserved(Record rc,char type)
        {
                try{
                     Mrecord tores = CFile.opens("helptoreserved@receipt");
                     tores.set("policyType",String.valueOf(type));
                     tores.set("policyNo",rc.get("policyNo"));
                     tores.set("payPeriod",rc.get("payPeriod"));
                     tores.set("rpNo",rc.get("rpNo"));
                     tores.set("clearDate",sysdate);
                     tores.set("userID",userID);
                     tores.insert();
                }
                catch(Exception e)
                {
                        System.out.println("Error...........FOR.............Help Reserved");
                        e.printStackTrace();
                }
        }

}
