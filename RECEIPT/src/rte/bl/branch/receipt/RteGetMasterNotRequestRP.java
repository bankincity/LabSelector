package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.BraSales;
import utility.prename.*;
public class RteGetMasterNotRequestRP  implements Task
{
	String flag;
	String policyNo;
	
	Mrecord master;
	Mrecord nameMaster;
	Mrecord policynotreq;
	BraSales bsale;
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {flagPolicy,policyNo,branch,flagAll or Not}",-1);
 		Object [] parameter = (Object []) param;
 		try 
 		{
			sysdate = DateInfo.sysDate();
                	systime =  Masic.time("commontable").substring(8);
 			bsale = new BraSales();
			if(parameter.length == 2)
				return (deletePolicyNotReq((String)parameter[0],(String)parameter[1]));
			if(parameter.length == 3)
				return (updateRpAndInsertMaster((String)parameter[0],(Vector)parameter[1],(String)parameter[2]));
	 		flag = (String)parameter[0];
 			policyNo = (String)parameter[1];
			String branch   = (String)parameter[2];
 			String flagAll  = (String)parameter[3];	
			System.out.println("bingo.....................................1");
 			nameMaster  = CFile.opens("name@mstperson");
 			policynotreq  = CFile.opens("policynotreqrp@receipt");

			if(flagAll.charAt(0) == 'N')
			{
				openMaster(flag,policyNo);
				return (searchData(policyNo));
			}
			return searchAllData(branch);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),-1);
 		}
	}
	String sysdate;
	String systime;
	private Result deletePolicyNotReq(String policyNo,String branch) throws Exception
	{
 		policynotreq  = CFile.opens("policynotreqrp@receipt");
		if(policynotreq.equalGreat(policyNo+"U") && (policyNo+"U").compareTo(policynotreq.get("policyNo")+policynotreq.get("used")) == 0)

		{
			policynotreq.set("used","X");
			policynotreq.set("recordDate",DateInfo.sysDate());
			byte [] buffer = policynotreq.getBytes();
			policynotreq.delete();
			if(policynotreq.equal(policyNo+"X"+DateInfo.sysDate()))
				policynotreq.delete();
			
			policynotreq.putBytes(buffer);
			policynotreq.insert();
		}
		return new Result("",0);
	}
	private Result updateRpAndInsertMaster(String typePol,Vector vec,String branch) throws Exception
	{
		String [] data = (String [])vec.elementAt(0);
 		policynotreq  = CFile.opens("policynotreqrp@receipt");
		policynotreq.set("branch",data[0]);
		policynotreq.set("policyNo",data[1]);
		policynotreq.set("used","U");
		policynotreq.set("userID",data[3]);
		policynotreq.set("remark",data[4]);
		policynotreq.set("recordDate",data[5]);
		policynotreq.set("salesID",getSaleID(data[0],data[2]));
		if (!policynotreq.insert())
		{
			System.out.println("can not insert policynotreqrp error ----->"+policynotreq.lastError());
		}	
		Mrecord rrec = null;
		if(typePol.charAt(0) == 'O')
			rrec = CFile.opens("orctrl@receipt");	
		else if(typePol.charAt(0) == 'I')
			rrec = CFile.opens("irctrl@receipt");	
		else if(typePol.charAt(0) == 'W')
			rrec = CFile.opens("wrctrl@receipt");	
		else if(typePol.charAt(0) == 'U')
			rrec = CFile.opens("ulrctrl@universal");	
		for (int i = 1 ; i < vec.size();i++)
		{
			String trp = (String)vec.elementAt(i);
			Record trec = null;
			if (rrec.equal(trp))
			{
				trec = rrec.copy();
				rrec.set("currentStatus","G");
				rrec.set("sysDate",DateInfo.sysDate());
				rrec.update();
			}
			if(trec != null)
				insertXrpChange(typePol.charAt(0),trec,"G",data[3]);
		}
		return new Result("",0);
		
	}
	Mrecord rcchg;
        public  void insertXrpChange(char type,Record  rc,String newstatus,String userID) throws Exception
        {
		if(rcchg == null)
                        rcchg = CFile.opens("xrpchg@cbranch");
                CheckAskReceipt.insertXrcchg(rcchg,type,rc,newstatus,sysdate,systime,userID);
        }
	private Result searchData(String policyNo) throws Exception
	{
		if(policynotreq.equalGreat(policyNo+"U") && (policyNo+"U").compareTo(policynotreq.get("policyNo")+policynotreq.get("used")) == 0)

		{
			String [] field = {"policyNo","insuredName","recordDate","depositNo","saleName","remark","userID"};
			int [] len = {8,80,8,5,80,100,7};
			Rtemp trec = new Rtemp(field,len);
			trec.set("policyNo",policynotreq.get("policyNo"));
			trec.set("insuredName",getName());
			trec.set("recordDate",policynotreq.get("recordDate"));
			trec.set("saleName",getSaleName(policynotreq.get("salesID")));
			trec.set("depositNo",depNo);
			trec.set("remark",policynotreq.get("remark"));
			trec.set("userID",policynotreq.get("userID"));
			Vector vec = new Vector();
			vec.addElement(field);
			vec.addElement(len);
			vec.addElement(trec.getBytes());
			return new Result(vec,1);
		}
		else {
			String [] field = {"policyNo","insuredName","recordDate","depositNo","saleName","remark","userID"};
			int [] len = {8,80,8,5,80,100,7};	
			Rtemp trec = new Rtemp(field,len);
			trec.set(' ');
			depNo = "00000";
			trec.set("policyNo",master.get("policyNo"));
			trec.set("insuredName",getName());
			trec.set("saleName",getSaleName(master.get("salesID")));
			trec.set("depositNo",depNo);
			Vector vec = new Vector();
			vec.addElement(field);
			vec.addElement(len);
			vec.addElement(trec.getBytes());
			return new Result(vec,0);
		}
	}
	private Result searchAllData(String branch) throws Exception
	{
		
		String [] field = {"policyNo","insuredName","recordDate","depositNo","saleName","remark","userID"};
		int [] len = {8,80,8,5,80,100,7};
		TempMasicFile trec = new TempMasicFile("bra"+branch+"app",field,len);
		trec.keyField(false,false,new String [] {"policyNo"});
		trec.buildTemp();
		policynotreq.start(1);
		for (boolean st = policynotreq.equalGreat(branch) ; st && branch.compareTo(policynotreq.get("branch")) == 0;st = policynotreq.next())
		{
			if(policynotreq.get("used").charAt(0) != 'U')
				continue;
			trec.set("policyNo",policynotreq.get("policyNo"));
			openMaster(trec.get("policyNo"));
			trec.set("insuredName",getName());
			trec.set("recordDate",policynotreq.get("recordDate"));
			trec.set("saleName",getSaleName(policynotreq.get("salesID")));
			trec.set("depositNo",depNo);
			trec.set("remark",policynotreq.get("remark"));
			trec.set("userID",policynotreq.get("userID"));
			trec.insert();
		}
		policynotreq.start(0);
		if(trec.fileSize() == 0)
			throw new Exception(M.stou("ไม่มีข้อมูลการป้อนกรมธรรม์ที่แจ้งไม่เบิกใบเสร็จ"));
		Vector vec = new Vector();
		vec.addElement(field);
		vec.addElement(len);
		vec.addElement(trec.name());
		return new Result(vec,0);
	}
	private void openMaster(String policyNo) throws Exception
	{
		master = CFile.opens("indmast@mstpolicy");
		if(!master.equal(policyNo))
		{
			master = CFile.opens("ordmast@mstpolicy");
			if(!master.equal(policyNo))
			{
				master = CFile.opens("whlmast@mstpolicy");
				if(!master.equal(policyNo))
				{
					master = CFile.opens("universallife@universal");
					if (!master.equal(policyNo))
						master = null;
				}
				
			}
		}
	}
	private void openMaster(String typeOfPol,String policyNo) throws Exception
	{
		if(typeOfPol.charAt(0) == 'I')
			master = CFile.opens("indmast@mstpolicy");
		else if(typeOfPol.charAt(0) == 'O')
			master = CFile.opens("ordmast@mstpolicy");
		else if(typeOfPol.charAt(0) == 'W')
			master = CFile.opens("whlmast@mstpolicy");
		else if(typeOfPol.charAt(0) == 'U')
			master = CFile.opens("universallife@universal");
		if(master == null || !master.equal(policyNo))
			throw new Exception(M.stou("ไม่พบกรมธรรม์ที่ระบุในแฟ้ม master"));
	}
	String depNo = "00000";
	private String getSaleName(String saleID) throws Exception
        {
                bsale.getBySalesID(saleID);
		depNo = bsale.getSnRec("depositNo");
                return (bsale.getSnRec("preName").trim()+bsale.getSnRec("firstName").trim()+" "+bsale.getSnRec("lastName").trim());
        }
	private String getSaleID(String branch,String depositNo) throws Exception
        {
                bsale.getByDepositNo(branch,depositNo);
                return (bsale.getSnRec("salesID"));
        }
	private  String getName() throws Exception
	{
		if(master == null)
			return "";
		if(nameMaster.equal(master.get("nameID")))
			return  (Prename.getAbb(nameMaster.get("preName").trim())+nameMaster.get("firstName").trim()+" "+nameMaster.get("lastName").trim());
		return "";
	}
}


