package rte.bl.branch.receipt.help;
import manit.*;
import java.io.*;
import utility.cfile.*;
import insure.*;
import java.util.*;
import rte.bl.receipt.*;
public class HelpRPRider
{
	public static void main(String [] args) throws Exception
	{
		Mrecord orps = CFile.opens("orpserved255505@cbranch");
		Mrecord crprider = CFile.opens("rprider@cbranch");
		Mrecord rprider = CFile.opens("rprider@receipt");
		Mrecord orctrl = CFile.opens("orctrl@receipt");
		RandomAccessFile crp = new RandomAccessFile("crp.out","rw");
		RandomAccessFile nrp = new RandomAccessFile("nrp.out","rw");
		RandomAccessFile prp = new RandomAccessFile("prp.out","rw");
		int count = 0 ;
		boolean st = orps.first();
		String month = "255505";
		ReceiptMasterPremium premium = new  ReceiptMasterPremium(orps.get("policyNo"),orps.get("payPeriod"));
		for (;st;st=orps.next())
		{
			if (++count % 5000 == 0)
			{
				Thread.sleep(200);
				System.out.println("count === "+count);
			}
			if (orps.get("planCode").compareTo("90") == 0 || orps.get("planCode").compareTo("99") == 0)
				continue;
               		if(PlanType.isPAPlan(orps.get("planCode")))
				continue;
			if  (!crprider.equal(orps.get("rpNo")+"1")  && !crprider.equal(orps.get("policyNo")+orps.get("payPeriod")+"1") && !rprider.equal(orps.get("rpNo")+"1"))
			{
               			premium.premiumMaster(orps.get("policyNo"),orps.get("payPeriod"));
		
				Vector rider = premium.rider();
                		String rid  = M.setlen(premium.lifePremium(),9)+M.setlen(premium.extraLifePremium(),6);
				String seq = "1";
				Vector trprider  = new Vector();
				for (int i = 0 ; i < rider.size();i++)
    			        {
                        		String [] riderRec = (String[])rider.elementAt(i);
					if(riderRec[0].trim().length() == 2)
   	                                     riderRec[0] +=" ";
        	                        if(riderRec[0].trim().length() == 1)
                	                        riderRec[0] +="  ";
                       		        if(M.match(2,riderRec[0].trim(),0,M.stou("ชว"),0))
                                        	continue;
                                	rid= rprider+riderRec[0]+M.setlen(riderRec[1],9)+M.setlen(riderRec[2],6);
                                	if(rid.length() == 123)
                               		{
                                        	trprider.addElement(seq+rprider);
                                        	seq = M.inc(seq);
                                        	rid = "";
                                	}
				}
				String rr ="";
                                for(int i = 0 ; i < trprider.size() ;i++)
                                {
                                        rr = (String)trprider.elementAt(i);
                                        crprider.set("rpNo",orps.get("policyNo")+orps.get("payPeriod"));
                                        crprider.set("seqNo",rr.substring(0,1));
                                        crprider.set("riderText",rr.substring(1));
                                        crprider.set("printdate",month+"31");
					System.out.println(new String(crprider.getBytes()));
                                        if(!crprider.insert())
                                                throw new Exception(M.stou("ไม่สามารถ Insert ข้อมูลลงแฟ้ม rprider ")+orps.get("policyNo")+" iserror ="+M.itoc(crprider.lastError()));
                                }
				if (rr.length() > 0)
					break;
	

			}
		}
        }

}
