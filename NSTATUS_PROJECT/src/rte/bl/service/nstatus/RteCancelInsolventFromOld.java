package rte.bl.service.nstatus;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.*;
import java.util.*;
import rte.*;
import utility.rteutility.*;
import utility.prename.Prename;
import utility.address.*;
//import insure.*;
import utility.support.DateInfo;
import utility.prename.Prename;
import utility.rteutility.*;
public class  RteCancelInsolventFromOld  implements  Task
{
	Mrecord ord;
        Mrecord whl;
	Mrecord ind;
	Mrecord name;
	Mrecord person;
	Mrecord insolvent;
	Mrecord insolventtran;
	Mrecord insolventrem;
	String sysDate="";
	public Result execute(Object param) 
        {
		Object [] parameter = (Object [])param;
		// Array of {byte [] ,String [] field,int [] len,String [] chgdata }
		byte [] buffer = (byte[])parameter[0];
		String [] field = (String [])parameter[1];
		int [] len  = (int [])parameter[2];
		String [] chgdata = (String [])parameter[3];
		System.out.println(chgdata.length);
		Rtemp trec = new Rtemp(field,len);
		trec.putBytes(buffer);
		
				System.out.println("before insert insolvent....................................");

		try {
			ord = CFile.opens("ordmast@mstpolicy");
         		whl = CFile.opens("whlmast@mstpolicy");
            		ind = CFile.opens("indmast@mstpolicy");
			person = CFile.opens("person@mstperson");
			person.start(1);
			name = CFile.opens("name@mstperson");			
			insolvent = CFile.opens("insolventpolicy@srvservice");
			insolventtran = CFile.opens("insolventtran@srvservice");
			insolventrem = CFile.opens("insolventremark@srvservice");
			sysDate = DateInfo.sysDate();
			String [] data = new String [7];
			data[0] = trec.get("policyNo");
			data[1] = trec.get("firstName");
			data[2] = trec.get("lastName");
			data[3] = trec.get("idNo");
			data[4] = trec.get("caseNo");
			data[5] = chgdata[4];
			data[6] = chgdata[5];
			if(insolvent.equal(data[0]+data[4]))
			{
				
			/*	if(insolvent.get("status").charAt(0) == 'C')
					throw new Exception(M.stou("ข้อมูลนี้เป็นข้อมูลที่ยกเลิกแล้ว"));
				insolvent.set("status","C");
				Record itrec = insertInsolventTransaction(data,"C");
				if(itrec.get("newStatus2").charAt(0) == 'N' && itrec.get("newStatus1").charAt(0) != 'N')
					updateBenefitStatus(itrec.get("policyNo"),insolvent.get("policyType"));
				else 
					updateFirstStatus(insolvent.get("policyType"),insolvent.get("policyNo"),itrec);
				updateRpStatus(insolvent.get("policyType"),insolvent.get("policyNo"));
				Record t = insolvent.copy();
				insolvent.delete();
				insolvent.putBytes(t.getBytes());
				insolvent.insert();*/
				throw new Exception(M.stou("กรมธรรม์นี้ Mark N โดยระบบใหม่ ให้ยกเลิกที่ระบบใหม่"));
			}
			else {
				Mrecord mrec = null;
				if(trec.get("typeOfPol").charAt(0) == 'I')
					mrec = ind;
				else if(trec.get("typeOfPol").charAt(0) == 'O')
					mrec = ord;
				else if(trec.get("typeOfPol").charAt(0) == 'W')
					mrec = whl;
				if(!mrec.equal(trec.get("policyNo")))
				{
					throw new Exception(M.stou("ไม่พบกรมธรรม์เลขที่ ")+trec.get("policyNo")+ "  policy type =  "+trec.get("typeOfPol"));
				}
				insolvent.set(' ');
				insolvent.set("policyNo",trec.get("policyNo"));
				insolvent.set("firstName",trec.get("firstName"));
				insolvent.set("preName",trec.get("preName"));
				insolvent.set("lastName",trec.get("lastName"));
				insolvent.set("idNo",trec.get("idNo"));
				insolvent.set("branch",mrec.get("branch"));
				insolvent.set("planNo",mrec.get("planCode"));			
				insolvent.set("effectiveDate",mrec.get("effectiveDate"));
				insolvent.set("matureDate","00000000");
				insolvent.set("caseID",data[4]);
				insolvent.set("insolventDate",trec.get("insolventDate"));
				insolvent.set("policyType",trec.get("typeOfPol"));
				insolvent.set("status","C");
				insolvent.set("birthDate","00000000");
				insolvent.set("nameID",trec.get("nameID"));
				insolvent.set("personID",trec.get("personID"));
				insolvent.set("dataDate",DateInfo.sysDate());
			//nsolvent.set("dataDate","00000000");
				insolvent.insert();
				System.out.println("before insert insolvent....................................");
				Record itrec = insertInsolventTransaction(data,chgdata,mrec.copy(),"C");
				System.out.println("before update insolvent....................................");
				updateFirstStatus(insolvent.get("policyType"),insolvent.get("policyNo"),itrec);
			
			}

			utility.rteutility.LocalTask lt = new utility.rteutility.LocalTask();
			System.out.println("lenght()=================="+data.length);
			Result res = lt.exec("rte.bl.service.nstatus.RteRemoveNstatus",new String [] {data[3],data[1],data[2],data[4],data[5]});
                        System.out.println("result of RteSearchFromBankruptcy -------"+res.status());
                        if(res.status() != 0)
                               throw new Exception((String)res.value());
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		String s = checkSaleOwner(insolvent.get("policyType"),insolvent.get("policyNo"));
                return new Result(s,0);
	}
	private String checkSaleOwner(String policyType,String policyNo)
        {
                try {
                        String filename ="";
                        switch (policyType.charAt(0))
                        {
                                case 'I' : filename = "indmast@mstpolicy"; break;
                                case 'O' : filename = "ordmast@mstpolicy"; break;
                                case 'W' : filename = "whlmast@mstpolicy"; break;
                        } 
                        Mrecord smast = CFile.opens(filename);
                        Mrecord sale = CFile.opens("person@sales");
                        if (smast.equal(policyNo))
                        {
                                if (sale.equal(smast.get("salesID")))
                                        return "";                      
                                return smast.get("salesID");
                        }
                        return "";
                }
                catch (Exception e)
                {
                        System.out.println("e.getmessge() ---"+e.getMessage());
                        return "xxxxxxxxxx";
                }
        }

	private void updateRpStatus(String policyType,String policyNo) throws Exception
	{
		Result rs = PublicRte.getResult("blbranch","rte.bl.branch.receipt.RteFind_W_ReservedReceipt",new Object[] {policyType, policyNo,"33","X000033"});
		if (rs.status()!=0)
			throw new Exception ("Error RteFind_W_ReservedReceipt = "+(String)rs.value());

	}
	private void updateFirstStatus(String policyType,String policyNo,Record itrec) throws Exception
	{
		String filename ="";
		Mrecord master = null;
		switch (policyType.charAt(0))
		{
			case 'I' : filename = "indmast@mstpolicy";master = ind; break;
			case 'O' : filename = "ordmast@mstpolicy";master = ord; break;
			case 'W' : filename = "whlmast@mstpolicy";master = whl;break;
		}
		Vector rv = new Vector();
		System.out.println("bingo.........................update master...............................");
		if (master.equal(policyNo))
		{
			 rv.add(new String[] {"policyStatus1",master.get("oldPolicyStatus1")});
             rv.add(new String[] {"policyStatusDate1",master.get("oldPolicyStatusDate1") });
             rv.add(new String[] {"policyStatus2",master.get("oldPolicyStatus2")});
             rv.add(new String[] {"policyStatusDate2",master.get("oldPolicyStatusDate2") });
		}
		else {
                rv.add(new String[] {"policyStatus1",itrec.get("oldStatus1")});
                rv.add(new String[] {"policyStatusDate1",itrec.get("oldStatusDate1") });             
                rv.add(new String[] {"policyStatus2",itrec.get("oldStatus2")});
                rv.add(new String[] {"policyStatusDate2",itrec.get("oldStatusDate2") });
               
		}
		rv.add(new String[] {"oldPolicyStatus2", itrec.get("newStatus2")});
        rv.add(new String[] {"oldPolicyStatusDate2",itrec.get("newStatusDate2")});
		rv.add(new String[] {"oldPolicyStatus1", itrec.get("newStatus1")});
        rv.add(new String[] {"oldPolicyStatusDate1",itrec.get("newStatusDate1")});
		Object [] v = new Object [] {filename,new String[] {policyType,policyNo,policyNo,"C"},"U",rv,"Appl_N","A"};
		Object [] ob = new Object[] {"searchmaster", "rte.search.master.log.ProcessMasterAndLog", v};
		Result rs = PublicRte.getResult("blmaster", "rte.ForwardRequest", ob);
		if (rs.status() == 0)
		{
			Vector m = new Vector();
			m.add( M.stou("ยกเลิกบันทึกสถานะ N วันที่") + DateInfo.formatDate(1,sysDate)); 
			rs = PublicRte.getResult("blmaster","rte.bl.master.RemarkFromTransaction", new Object[] {policyNo,"04",m});
		}		
	}
	private boolean  updateBenefitStatus(String policyNo,String policyType) throws Exception
	{
		boolean found = false;
		String filename ="";
		switch (policyType.charAt(0))
		{
			case 'I' : filename = "dvmastindi@mstpolicy"; break;
			case 'O' : filename = "dvmastordo@mstpolicy"; break;
			case 'W' : filename = "dvmastwhlw@mstpolicy"; break;
		}
		Mrecord dv = CFile.opens(filename);
		for (boolean st = dv.equalGreat(policyNo) ; st && policyNo.compareTo(dv.get("policyNo")) == 0 ; st=dv.next())
		{
			if (dv.get("payFlag").charAt(0) == 'N')
			{
				dv.set("payFlag","2");
				dv.update();
				found = true;
			}
		}
		dv.close();
		switch (policyType.charAt(0))
		{
			case 'I' : filename = ""; break;
			case 'O' : filename = "paycmastord@mstpolicy"; break;
			case 'W' : filename = ""; break;
		}
		if(filename.trim().length() > 0)
		{
			dv = CFile.opens(filename);
			for (boolean st = dv.equalGreat(policyNo) ; st && policyNo.compareTo(dv.get("policyNo")) == 0 ; st=dv.next())
			{
				if (dv.get("payFlag").charAt(0) == 'N')
				{
					dv.set("payFlag","2");
					dv.update();
					found = true;
				}
			}
		}
		if(found)
		{
			Vector m = new Vector();
			m.add( M.stou("ยกเลิกบันทึกสถานะ N วันที่") + DateInfo.formatDate(1,sysDate)); 
			Result rs = PublicRte.getResult("blmaster","rte.bl.master.RemarkFromTransaction", new Object[] {policyNo,"04",m});
		}
		return found ;
	}
	private Record searchLastInsolventTran(String type ,String policyNo,String caseID) throws Exception
	{
		Record rec = null ;
		Mrecord insolventtran1 =CFile.opens("insolventtran@srvservice");
		for(boolean st = insolventtran1.great(policyNo+caseID);st&&(policyNo+caseID).compareTo(insolventtran1.get("policyNo")+insolventtran1.get("caseID")) == 0;st=insolventtran1.next())
		{
			if(type.indexOf(insolventtran1.get("typeOfTran")) >= 0 )
				rec = insolventtran1.copy();
		}
		insolventtran1.close();
		if(rec == null)
			throw new Exception ("Can not find transaction ["+type+"  "+policyNo+" "+caseID+"]");
		return rec ;
	}
	private Record  insertInsolventTransaction(String [] data,String status) throws Exception
	{
		Record mrec = searchLastInsolventTran("ASIP",data[0],data[4]);
		insolventtran.set("policyNo",data[0]);
		insolventtran.set("caseID",data[4]);
		insolventtran.set("tranDate",sysDate);
		insolventtran.set("tranTime", Masic.time("commontable").substring(8));
		insolventtran.set("userID",data[6]);
		insolventtran.set("typeOfTran",status);
		insolventtran.set("newStatus1",mrec.get("oldStatus1"));
		insolventtran.set("newStatusDate1",mrec.get("oldStatusDate1"));
		insolventtran.set("newStatus2",mrec.get("oldStatus2"));
		insolventtran.set("newStatusDate2",mrec.get("oldStatusDate2"));
		insolventtran.set("oldStatus1",mrec.get("newStatus1"));
		insolventtran.set("oldStatusDate1",mrec.get("newStatusDate1"));
		insolventtran.set("oldStatus2",mrec.get("newStatus2"));
		insolventtran.set("oldStatusDate2",mrec.get("newStatusDate2"));
		insolventtran.insert();	
		insolventrem.set("policyNo",data[0]);
		insolventrem.set("caseID",data[4]);
		insolventrem.set("tranDate",sysDate);
		insolventrem.set("tranTime",insolventtran.get("tranTime"));
		insolventrem.set("remark",data[5]);
		insolventrem.insert();
		return mrec;
	}
	private Record  insertInsolventTransaction(String [] data,String [] chgdata,Record mrec,String status) throws Exception
	{
		insolventtran.set("policyNo",data[0]);
		insolventtran.set("caseID",data[4]);
		insolventtran.set("tranDate",sysDate);
		insolventtran.set("tranTime", Masic.time("commontable").substring(8));
		insolventtran.set("userID",data[6]);
		insolventtran.set("typeOfTran",status);
		insolventtran.set("newStatus1",chgdata[0]);
		insolventtran.set("newStatusDate1",chgdata[1]);
		insolventtran.set("newStatus2",chgdata[2]);
		insolventtran.set("newStatusDate2",chgdata[3]);
		insolventtran.set("oldStatus1",mrec.get("policyStatus1"));
		insolventtran.set("oldStatusDate1",mrec.get("policyStatusDate1"));
		insolventtran.set("oldStatus2",mrec.get("policyStatus2"));
		insolventtran.set("oldStatusDate2",mrec.get("policyStatusDate2"));
		insolventtran.insert();	
	
		insolventrem.set("policyNo",data[0]);
		insolventrem.set("caseID",data[4]);
		insolventrem.set("tranDate",sysDate);
		insolventrem.set("tranTime",insolventtran.get("tranTime"));
		insolventrem.set("remark",data[5]);
		insolventrem.insert();

		Record  tirec = insolventtran.copy();
		tirec.set("oldStatus1",chgdata[0]);
		tirec.set("oldStatusDate1",chgdata[1]);
		tirec.set("oldStatus2",chgdata[2]);
		tirec.set("oldStatusDate2",chgdata[3]);
		tirec.set("newStatus1",mrec.get("policyStatus1"));
		tirec.set("newStatusDate1",mrec.get("policyStatusDate1"));
		tirec.set("newStatus2",mrec.get("policyStatus2"));
		tirec.set("newStatusDate2",mrec.get("policyStatusDate2"));
		return tirec;
	}
}


