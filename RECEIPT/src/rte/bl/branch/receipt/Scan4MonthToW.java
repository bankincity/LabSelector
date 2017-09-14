package rte.bl.branch.receipt;
import manit.*;
import insure.*;
import utility.support.*;
import rte.bl.branch.TempMasicFile;
import java.util.*;
public class Scan4MonthToW
{
        public static void main (String [] args) throws Exception
        {
                 Scan4MonthToW scan4 = new  Scan4MonthToW ();
                 scan4.startScan(args[0],args[1]);
                 scan4.print();
        }
        TempMasicFile temp;
        String []  tfield = {"rpNo","policyNo","payPeriod","dueDate","policyStatus1","policyStatusDate1","policyStatus2","policyStatusDate2","reasonCode"};
        int [] tlen = {12,8,6,8,1,8,1,8,2};
        public Scan4MonthToW() throws Exception
        {
              temp =   new TempMasicFile("rptbranch",tfield,tlen);
              temp.setNoOfKey(1);
              temp.setKey(0,false,false,new String []{"rpNo"});
              temp.buildTemp();
        }
        public void startScan(String polType,String branch) throws Exception
        {
                Mrecord rc  = null;
                Mrecord master = null;
                if (polType.charAt(0) == 'I')
                {
                        rc = Masic.opens("irctrl@receipt");
                        master = Masic.opens("indmast@mstpolicy");
                }
                else if (polType.charAt(0) =='O')
                {
                        rc = Masic.opens("orctrl@receipt");
                        master = Masic.opens("ordmast@mstpolicy");
                }        
                else if (polType.charAt(0) =='W')
                {
                        rc = Masic.opens("wrctrl@receipt");
                        master = Masic.opens("whlmast@mstpolicy");
                }        
                rc.start(3);
                String dueDate = "00000000";
                String sysDate = DateInfo.sysDate();
                for (boolean st = rc.great("N"+branch) ; st && rc.get("currentStatus").charAt(0) == 'N' ; st = rc.next())
                {
                        if (branch.compareTo("000") != 0 && branch.compareTo(rc.get("submitBranch")) != 0)
                                break;
                        if (rc.get("requestDate").compareTo("00000000") != 0)
                                continue;
                        if (master.equal(rc.get("policyNo")))
                        {

                                 if(master.get("policyStatus2").charAt(0) == 'B' || master.get("oldPolicyStatus2").charAt(0) == 'B')
                                 {
                                                GetKBPeriod kbp = new GetKBPeriod();
                                                String pPeriod = kbp.kbPeriod(master.get("policyNo"));
                                                pPeriod =  Insure.nextPayPeriod(pPeriod,master.get("mode"));
                                                dueDate = Insure.dueDate(master.get("mode"),master.get("effectiveDate"),pPeriod);

                                }
                                else if (polType.charAt(0) == 'O' || polType.charAt(0) == 'W')
                                        dueDate = Insure.nextDueDate(master.get("mode"),master.get("effectiveDate"),master.get("payPeriod"));
                                else if (polType.charAt(0) == 'I')
                                        dueDate = DateInfo.nextMonth(rc.get("payPeriod")+"01").substring(0,6)+"01";
                                int day = 120;                                
                                if(insure.PlanType.isPAPlan(master.get("planCode")) || master.get("policyStatus1").charAt(0) == 'F')
                                                day = 31;
                                int cday = DateInfo.calDay(dueDate,sysDate,new byte[3],new byte[3]);
                                if (cday > day)
                                {
                                        temp.set("rpNo",rc.get("rpNo"));
                                        temp.set("policyNo",rc.get("policyNo"));
                                        temp.set("payPeriod",master.get("payPeriod"));
                                        temp.set("dueDate",dueDate);
                                        temp.set("policyStatus1",master.get("policyStatus1"));                        
                                        temp.set("policyStatusDate1",master.get("policyStatusDate1"));                                                               temp.set("policyStatus2",master.get("policyStatus2"));                        
                                        temp.set("policyStatusDate2",master.get("policyStatusDate2"));                       
                                        if (day == 31)
                                                temp.set("reasonCode","25");
                                        else
                                                temp.set("reasonCode","11");
                                        temp.insert(); 
                                }                        
                                
                        }
                        
                }
                
        }
        public Vector tempFileName()
        {
                Vector vret = new  Vector();
                vret.addElement(temp.name());
                vret.addElement(tfield);
                vret.addElement(tlen);
                return vret;
        }
        public void print()
        {
                for (boolean st = temp.first();st;st=temp.next())
                {
                        System.out.println(temp.get("rpNo")+":"+temp.get("policyNo")+":"+temp.get("reasonCode"));
                }
        }
}

