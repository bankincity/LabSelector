package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteGenTReceiptToMCA  implements Task
{
        public Result execute(Object param)
        {
                String reqDate = DateInfo.sysDate();
                String userID  = "MCA0001";
                try {
                      String rpno = getRange("8",reqDate.substring(2,4));
                      Mrecord trp = CFile.opens("trpctrl@receipt");
                      trp.set('0');
                      trp.set("rpNo",rpno);
                      trp.set("requestDate",reqDate);
                      trp.set("statusDate",reqDate);
                      trp.set("askUserID",userID);
                      trp.set("currentStatus","N");
                      trp.set("originalStatus","N");
                      trp.set("branch","MCA");
                      trp.set("branchFlag"," ");
                      if (!trp.insert())
                        return new Result("Can not Insert trpctrl   : Error  "+trp.lastError(),1);
                     return new Result(rpno,0);  
                }
                catch(Exception e)
                {
                        return new Result(e.getMessage(),1);
                }
        }
        private String getRange(String branch,String year) throws Exception
        {
                Mrecord rprange = CFile.opens("trpnumber@receipt");
                String trp = "000000001";
                String msg = "";
                rprange.lock();
                if(rprange.equal(branch+year))
                {
                        trp = M.inc(rprange.get("currentReceipt"));
                        rprange.set("currentReceipt",trp);
                        if(!rprange.update())
                                msg = "Can not Update range Error :"+M.itoc(rprange.lastError());
                }
                else {
                        rprange.set("start",branch);
                        rprange.set("year",year);
                        rprange.set("currentReceipt",trp);
                        if(!rprange.insert())
                                msg = "Can not Insert range  Error :"+M.itoc(rprange.lastError());

                }
                rprange.release();
                if(msg.trim().length() > 0)
                        throw new Exception(msg);
                return (branch+year+trp);
        }        
}

