package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.MyVector;
import utility.support.DateInfo;
import insure.PlanType;
public class RteUseTakafulTrp  implements Task
{
	Mrecord trptmp;
	Mrecord trprange;
	Mrecord trp;
	Mrecord ordmast;
	String sysdate;
	String branch;
	String policyNo;
	String rpNo;
	String userID;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {branch,policyNo}",-1);
		Object [] parameter = (Object []) param;
		branch = (String)parameter[0];
		policyNo = (String)parameter[1];
		rpNo = (String)parameter[2];
		userID = (String)parameter[3];
		String flagUsed = (String)parameter[4];
		try {
			trprange =  CFile.opens("trptakafulrange@receipt");
			trp = CFile.opens("trpctrl@receipt");
			trptmp = CFile.opens("trptmptakaful@cbranch");
                        ordmast = CFile.opens("ordmast@mstpolicy");
			if(flagUsed.charAt(0) == 'U')
				useTakafulRp();
			else
				cancelUseTakafulRp();
		
		}
		catch (Exception e)
		{
				return new Result(e.getMessage(),1);
		}
		return new Result(new String[]{ordmast.get("policyNo"),ordmast.get("lifePremium"),ordmast.get("branch")},0);
	}
	private void cancelUseTakafulRp() throws Exception
	{
		if(trp.equal(rpNo))
		{
			trp.set("currentStatus","N");
			trp.set("policyNo","00000000");
			trp.set("statusDate","00000000");
			trp.set("premium","000000000");
			trp.set("askUserID",userID);
			trp.update();
			trptmp.set('0');
			trptmp.set("rpNo",rpNo);
			trptmp.set("branch",branch);
			trptmp.insert();
		}
	}
	private void useTakafulRp() throws Exception
	{
		if(ordmast.equal(policyNo))
		{
			if(!PlanType.isIslamPlan(ordmast.get("planCode")))
				throw new Exception(M.stou("ไม่ใช่แบบประกัน ตะกาฟุล"));
			
		}
		else
			throw new Exception(M.stou("ไม่พบข้อมูลกรมธรรม์ที่ระบุ"));
		if(trp.equal(rpNo))
		{
			if(trp.get("currentStatus").charAt(0) != 'N')
				throw new Exception(M.stou("สถานะใบรับเงิน ไม่ถูกต้อง ")+M.stou("สถานะเป็น ")+trp.get("currentStatus"));
			trp.set("policyNo",ordmast.get("policyNo"));
			trp.set("premium",ordmast.get("lifePremium"));
			trp.set("currentStatus","X");
			trp.set("statusDate",DateInfo.sysDate());
			trp.set("askUserID",userID);
			trp.update();
			if(trptmp.equal(branch+rpNo))
				trptmp.delete();
			
		}
                if(trprange.equalGreat(branch+"U"))
		{
			trprange.lock();
			if(trprange.get("used").charAt(0) != 'U')
				throw new Exception(M.stou("ไม่พบช่วงเลขใบรับเงินชั่วคราวตะกาฟุล"));
			if(trprange.get("currentTrp").compareTo(rpNo) != 0)
				throw new Exception(M.stou("ป้อนเลขที่ใบรับเงินชั่วคราว ตะกาฟุล ไม่ถูกต้อง"));
			trp.set('0');
			trp.set("rpNo",rpNo);
			trp.set("policyNo",ordmast.get("policyNo"));
			trp.set("premium",ordmast.get("lifePremium"));
			trp.set("currentStatus","X");
			trp.set("originalStatus","N");
			trp.set("statusDate",DateInfo.sysDate());
			trp.set("askUserID",userID);
//			trp.set("branch",branch);
			trp.set("branchFlag"," ");
			trp.insert();
			
			trprange.set("currentTrp",M.inc(rpNo));
			if (trprange.get("currentTrp").compareTo(trprange.get("endTrp")) >0)
                                trprange.set("used","N");
			trprange.update();
			trprange.release();
		}
	}
}
