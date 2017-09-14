package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import insure.Insure;
import java.util.*;
import utility.rteutility.PublicRte;
public class RteScan4MonthToW  implements Task
{

        public Result execute(Object param)
        {
                if(!(param instanceof Object []))
                   return new Result("Invalid Parameter:Object [] {policyType,branch}",-1);
                try {
                        Object [] o = (Object [])param;
                        String polType = (String)o[0];
                        String branch = (String)o[1];
                        Scan4MonthToW sc4m  = new Scan4MonthToW();
                        sc4m.startScan(polType,branch);
                        return new Result(sc4m.tempFileName(),0);
                }
                catch(Exception e)
                {
                        return new Result(e.getMessage(),1);
                }
        }

}


