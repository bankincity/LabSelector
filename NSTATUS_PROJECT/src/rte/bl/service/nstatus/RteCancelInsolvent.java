package rte.bl.service.nstatus;
import layout.bean.srvservice.InsolventpolicyBean;
import layout.bean.srvservice.InsolventpolicyRecord;
import  manit.*;
import  manit.rte.*;
import rte.bl.dataaccess.DAOInterface;
import rte.bl.dataaccess.PrimaryKey;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import java.util.*;
//port rte.*;
import utility.rteutility.*;
import utility.prename.Prename;
import utility.address.*;
//import insure.*;
import utility.support.DateInfo;
import utility.prename.Prename;
public class  RteCancelInsolvent implements  TaskUsr
{
	Mrecord ul;
	Mrecord ulinsolvent;
	Mrecord ulhisinsolvent;
	Mrecord ord;
        Mrecord whl;
	Mrecord ind;
	Mrecord name;
	Mrecord person;
	
	DAOInterface insolventDAO;
	DAOInterface insolventtranDAO;
	DAOInterface insolventremarkDAO;
	DAOInterface ulinsolventDAO;
	DAOInterface ulhisinsolventDAO;
	Record insolvent;
	Record insolventtran;
	Record insolventrem;
	String sysDate="";
	public Result execute(UserInfo user,Object param) 
        {
		Object [] parameter = (Object [])param;
		// Array of {policyNo,firstname,lastname,idNo,caseID,remark,userID}
			
		String [] data  = (String [])parameter[0];
		try {
			ul = CFile.opens("universallife@universal");
			
			ulinsolvent = CFile.opens("ulmasterinsolvent@universal");
			ulhisinsolvent = CFile.opens("ulhismasterinsolvent@universal");
			
		//	ord = CFile.opens("ordmast@mstpolicy");
         //		whl = CFile.opens("whlmast@mstpolicy");
           // 		ind = CFile.opens("indmast@mstpolicy");
			person = CFile.opens("person@mstperson");
			person.start(1);
			name = CFile.opens("name@mstperson");	
			
		//	ulinsolventDAO = new rte.bl.dataaccess.universal.UlmasterinsolventDAO(user);
			insolventDAO = new rte.bl.dataaccess.srvservice.InsolventpolicyDAO(user);
			insolventtranDAO = new rte.bl.dataaccess.srvservice.InsolventtranDAO(user);
			insolventremarkDAO = new rte.bl.dataaccess.srvservice.InsolventremarkDAO(user);
			
			sysDate = DateInfo.sysDate();
			
			PrimaryKey pk = new PrimaryKey();
			pk.addKey("policyNo", data[0]);
			pk.addKey("caseID",data[4]);
			
			InsolventpolicyBean ibean = (InsolventpolicyBean)insolventDAO.equal(pk);
			if (ibean != null)
			{
				insolvent = InsolventpolicyRecord.getRecord(ibean);
				if(insolvent.get("status").charAt(0) == 'C')
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
				insolvent.insert();

				LocalTask lt = new LocalTask();
				System.out.println("lenght()=================="+data.length);
				Result res = lt.exec("rte.bl.service.nstatus.RteRemoveNstatus",new String [] {data[3],data[1],data[2],data[4],data[5]});
                                System.out.println("result of RteSearchFromBankruptcy -------"+res.status());
                                if(res.status() != 0)
                                          throw new Exception((String)res.value());
				
			}

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
				case 'U' : filename = "universallife@universal"; break;
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
		switch (policyType.charAt(0))
		{
			case 'I' : filename = "indmast@mstpolicy"; break;
			case 'O' : filename = "ordmast@mstpolicy"; break;
			case 'W' : filename = "whlmast@mstpolicy"; break;
			case 'U' : filename = "universallife@universal"; break;
		}
		Vector rv = new Vector();
		if (policyType.charAt(0) == 'U')
		{
			if (ulinsolvent.equal(policyNo))
			{
				rv.add(new String[] {"oldPolicyStatus1", itrec.get("newStatus1")});
             	rv.add(new String[] {"oldPolicyStatusDate1",itrec.get("newStatusDate1")});	
            	rv.add(new String[] {"oldPolicyStatus2", itrec.get("newStatus2")});
            	rv.add(new String[] {"oldPolicyStatusDate2",itrec.get("newStatusDate2")});
				if(ul.equal(policyNo))
				{
					rv.add(new String[] {"policyStatus1",ul.get("oldPolicyStatus1")});
					rv.add(new String[] {"policyStatusDate1",ul.get("oldPolicyStatusDate1") });
        	       
                	rv.add(new String[] {"policyStatus2",ul.get("oldPolicyStatus2")});
              		rv.add(new String[] {"policyStatusDate2",ul.get("oloPolicyStatusDate2") });
                	
				}
				else {
					rv.add(new String[] {"policyStatus1",ulinsolvent.get("policyStatus1")});
					rv.add(new String[] {"policyStatusDate1",ulinsolvent.get("policyStatusDate1") });
        	       
                	rv.add(new String[] {"policyStatus2",ulinsolvent.get("policyStatus2")});
              		rv.add(new String[] {"policyStatusDate2",ulinsolvent.get("policyStatusDate2") });
                
				}
				ulhisinsolvent.putBytes(ulinsolvent.getBytes());
				if(ulhisinsolvent.insert())
					ulinsolvent.delete();
			}	
		}
		else {
			Mrecord master = null;
			if (ord.equal(policyNo))
				master =ord;
			else if (ind.equal(policyNo))
				master = ind;
			else if (whl.equal(policyNo))
				master = whl;
			rv.add(new String[] {"policyStatus1",master.get("oldPolicyStatus1")});
			rv.add(new String[] {"policyStatusDate1",master.get("oldPolicyStatusDate1") });
                    	rv.add(new String[] {"oldPolicyStatus1", itrec.get("newStatus1")});
                	rv.add(new String[] {"oldPolicyStatusDate1",itrec.get("newStatusDate1")});
			rv.add(new String[] {"policyStatus2",master.get("oldPolicyStatus2")});
                	rv.add(new String[] {"policyStatusDate2",master.get("oldPolicyStatusDate2") });
                	rv.add(new String[] {"oldPolicyStatus2", itrec.get("newStatus2")});
                	rv.add(new String[] {"oldPolicyStatusDate2",itrec.get("newStatusDate2")});
		}
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
}


