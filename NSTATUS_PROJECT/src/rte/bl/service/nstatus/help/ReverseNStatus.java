package rte.bl.service.nstatus.help;
import manit.*;
import utility.support.*;
import utility.cfile.*;
import java.util.*;
import utility.rteutility.*;
import manit.rte.*;
public class ReverseNStatus
{
	static String sysDate;
	static Mrecord  ulinsolvent ;
	public static void main(String [] args) throws Exception
	{
		 Mrecord ul = CFile.opens("universallife@universal");
                 ulinsolvent = CFile.opens("ulmasterinsolvent@universal");
                 Mrecord ulhisinsolvent = CFile.opens("ulhismasterinsolvent@universal");
                 Mrecord ord = CFile.opens("ordmast@mstpolicy");
                 Mrecord whl = CFile.opens("whlmast@mstpolicy");
                 Mrecord ind = CFile.opens("indmast@mstpolicy");
                 Mrecord person = CFile.opens("person@mstperson");
                 person.start(1);
                 Mrecord  name = CFile.opens("name@mstperson");
                 Mrecord  insolventtran = CFile.opens("insolventtran@srvservice");
                 Mrecord insolventrem = CFile.opens("insolventremark@srvservice");
                 sysDate = DateInfo.sysDate();

		Mrecord  insolvent = CFile.opens("insolventpolicy@srvservice");
		insolvent.start(3);
		for (boolean st = insolvent.great(args[0]) ; st && args[0].compareTo(insolvent.get("dataDate")) == 0 ; st =insolvent.next())
		{
			System.out.print("bingo................."+(insolvent.get("policyNo")+" "+insolvent.get("caseID")));
			if ("PA".indexOf(insolvent.get("status")) >= 0)
			{
				Record itrec = searchLastInsolventTran("AP",insolvent.get("policyNo"),insolvent.get("caseID"));
		/*		if(itrec.get("newStatus2").charAt(0) == 'N' && itrec.get("newStatus1").charAt(0) != 'N')
                                        updateBenefitStatus(insolvent.get("policyNo"),insolvent.get("policyType"));
                                else
                                        updateFirstStatus(insolvent.get("policyType"),insolvent.get("policyNo"),itrec);*/
				System.out.println(" >>>>> update <<<<<<<");
	
			}
			System.out.println("");
		//	deleteTran(insolvent.get("policyNo"),insolvent.get("caseID"));
		}
	}
	private static void updateFirstStatus(String policyType,String policyNo,Record itrec) throws Exception
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
                                rv.add(new String[] {"policyStatus1",ulinsolvent.get("policyStatus1")});
                                rv.add(new String[] {"policyStatusDate1",ulinsolvent.get("policyStatusDate1") });
                                rv.add(new String[] {"oldPolicyStatus1", itrec.get("newStatus1")});
                                rv.add(new String[] {"oldPolicyStatusDate1",itrec.get("newStatusDate1")});
                                rv.add(new String[] {"policyStatus2",ulinsolvent.get("policyStatus2")});
                                rv.add(new String[] {"policyStatusDate2",ulinsolvent.get("policyStatusDate2") });
                                rv.add(new String[] {"oldPolicyStatus2", itrec.get("newStatus2")});
                                rv.add(new String[] {"oldPolicyStatusDate2",itrec.get("newStatusDate2")});
                        }
                }
	        else {
                        rv.add(new String[] {"policyStatus1",itrec.get("oldStatus1")});
                        rv.add(new String[] {"policyStatusDate1",itrec.get("oldStatusDate1") });
                        rv.add(new String[] {"oldPolicyStatus1", itrec.get("newStatus1")});
                        rv.add(new String[] {"oldPolicyStatusDate1",itrec.get("newStatusDate1")});
                        rv.add(new String[] {"policyStatus2",itrec.get("oldStatus2")});
                        rv.add(new String[] {"policyStatusDate2",itrec.get("oldStatusDate2") });
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
	private static boolean  updateBenefitStatus(String policyNo,String policyType) throws Exception
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
	private static Record searchLastInsolventTran(String type ,String policyNo,String caseID) throws Exception
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

	private static Record deleteTran(String policyNo,String caseID) throws Exception
        {
                Record rec = null ;
                Mrecord insolventtran1 =CFile.opens("insolventtran@srvservice");
                for(boolean st = insolventtran1.great(policyNo+caseID);st&&(policyNo+caseID).compareTo(insolventtran1.get("policyNo")+insolventtran1.get("caseID")) == 0;st=insolventtran1.next())
                {
                       rec = insolventtran1.copy();
		       insolventtran1.delete();
		      	
                }
                insolventtran1.close();
                if(rec == null)
                        throw new Exception ("Can not find transaction ["+policyNo+" "+caseID+"]");
                return rec ;
        }

}
