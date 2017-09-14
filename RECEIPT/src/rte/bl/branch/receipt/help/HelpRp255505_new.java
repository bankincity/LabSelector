package rte.bl.branch.receipt.help;
import manit.*;
import java.io.*;
import utility.cfile.*;
import insure.*;
public class HelpRp255505_new
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
		RandomAccessFile srp = new RandomAccessFile("srp.out","rw");
		int count = 0 ;
		for (boolean st = orps.first();st;st=orps.next())
		{
			if (++count % 5000 == 0)
			{
				Thread.sleep(200);
				System.out.println("count === "+count);
			}
			if(orps.get("rpFlag").charAt(0) == '9' || orps.get("rpFlag").charAt(0) == '2')
				continue;
			if (orps.get("planCode").compareTo("90") == 0 || orps.get("planCode").compareTo("99") == 0)
				continue;
			if(PlanType.isPAPlan(orps.get("planCode")))
                              continue;
			if ( PlanType.isGratefulPlan(orps.get("planCode")))
				continue;
			if (orps.get("rpNo").compareTo("000000000000") != 0)
			{
				if (rprider.equalGreat(orps.get("rpNo")) && orps.get("rpNo").compareTo(rprider.get("rpNo")) == 0)
					continue;
				
				if (orctrl.equal(orps.get("rpNo")))
                                {
					
					if("25520101".compareTo(orps.get("effectiveDate")) > 0)

                			{	
						if  (crprider.equalGreat(orps.get("policyNo")+orps.get("payPeriod")) && crprider.get("rpNo").compareTo(orps.get("policyNo")+orps.get("payPeriod")) == 0)
						{
							for (boolean ss = crprider.equalGreat(orps.get("policyNo")+orps.get("payPeriod"));ss && (orps.get("policyNo")+orps.get("payPeriod")).compareTo(crprider.get("rpNo")) == 0;ss=crprider.next())
							{
								rprider.set("rpNo",orps.get("rpNo"));
								rprider.set("seqNo",crprider.get("seqNo"));
								rprider.set("riderText",crprider.get("riderText"));
								rprider.insert();
							}
						}
						else 
							nrp.write(("T"+orps.get("rpNo")+":"+orps.get("policyNo")+":"+orps.get("payPeriod")+"\r\n").getBytes());					
					}	
					else {			
						if  (crprider.equalGreat(orps.get("policyNo")+orps.get("payPeriod")) && crprider.get("rpNo").compareTo(orps.get("policyNo")+orps.get("payPeriod")) == 0)
						{
							crp.write((orps.get("branch")+":"+orps.get("agent")+":"+orps.get("rpNo")+":"+orps.get("policyNo")+":"+orps.get("payPeriod")+"\r\n").getBytes());
							insertXrpChange('O',orctrl.copy(),"C","0000"+orps.get("branch"));
                                        		orctrl.set("currentStatus","C");
                                  	   	     	orctrl.set("reasonCode","18");
                                       		 	orctrl.set("sysDate","25550405");
							orctrl.set("submitBranch","xxx");
                                       		 	if(!orctrl.update())
							{
								System.out.println("error update iserror"+orctrl.lastError());
								System.exit(99);
							}
							orps.set("rpFlag","0");
							orps.set("rpNo","000000000000");
							if (!orps.update())
							{
								System.out.println("error update iserror"+orps.lastError());
								System.exit(99);
							}
						}
						else {
				
							nrp.write(("P"+orps.get("rpNo")+":"+orps.get("policyNo")+":"+orps.get("payPeriod")+"\r\n").getBytes());							
						}
					}
				
				}
				else { 
					prp.write((orps.get("rpNo")+":"+orps.get("policyNo")+":"+orps.get("payPeriod")+"\r\n").getBytes());							
					orps.set("rpFlag","0");
					orps.set("rpNo","000000000000");
					if (!orps.update())
					{
						System.out.println("error update iserror"+orps.lastError());
						System.exit(99);
					}
				}
			}
			else {
				
				if  (crprider.equalGreat(orps.get("policyNo")+orps.get("payPeriod")) && crprider.get("rpNo").compareTo(orps.get("policyNo")+orps.get("payPeriod")) == 0)
					;
				else
					srp.write((orps.get("branch")+":"+orps.get("agent")+":"+orps.get("rpNo")+":"+orps.get("policyNo")+":"+orps.get("payPeriod")+"\r\n").getBytes());

			}
		}
	}
	static String sysDate  = "25550405";
	static Mrecord rcchg;
        static String systime =  Masic.time("commontable").substring(8);	
	public static void insertXrpChange(char type,Record  rc,String newstatus,String userID) throws Exception
        {
                if(rcchg == null)
                        rcchg = CFile.opens("xrpchg@cbranch");
                rte.bl.branch.receipt.CheckAskReceipt.insertXrcchg(rcchg,type,rc,newstatus,sysDate,systime,userID);
        }

}
