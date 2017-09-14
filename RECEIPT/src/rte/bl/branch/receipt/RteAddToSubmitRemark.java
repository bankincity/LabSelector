package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import insure.Insure;
import java.util.*;
import utility.rteutility.PublicRte;
public class RteAddToSubmitRemark implements Task
{
	String sysdate;
	String policyNo;
	String rpNo;
	String period;
	String userID;
	String submitNo;
	Vector vData;
	String systime;
	public Result execute(Object param)
	{
		Object [] parameter = (Object []) param;
		policyNo = (String)parameter[0];
		rpNo = (String)parameter[1];
		period =(String)parameter[2];
		userID = (String)parameter[3]; 
		submitNo = (String)parameter[4]; 
		vData = (Vector) parameter[5]; 
		String fileName= "";
		String dueDate = "";
		String mstMode = "";
		sysdate = DateInfo.sysDate();
		systime =  Masic.time("commontable").substring(8);
		try {
			   insertDsubmitRemark();
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
	/*-------------------------------------------------*/
        void initRecord(Record r)
        {
                String[] fld = r.layout().fieldName();
                for (int i=0; i<fld.length; i++) {
                    if (r.layout().type(fld[i]) == 'N') {
                       if (r.layout().scale(fld[i]) == 0)
                          r.set(fld[i], "0");
                       else
                          r.set(fld[i], "0." + M.clears('0', r.layout().scale(fld[i])));
                    }
                    else if (fld[i].endsWith("Date"))
                       r.set(fld[i], M.clears('0', r.layout().length(fld[i])));
                    else
                       r.set(fld[i], M.clears(' ', r.layout().length(fld[i])));
                }
        }
	/*-------------------------------------------------*/
        void insertDsubmitRemark() throws Exception
        {
System.out.println("insertDsubmitRemark .. start");
                Mrecord rk = CFile.opens("submitremark@cbranch");
                String sysdate = DateInfo.sysDate();
                String seqStart = "00";
                if (rk.equalLess(submitNo+sysdate+"99") &&
                    rk.get("submitNo").compareTo(submitNo)==0 &&
                    rk.get("sysDate").compareTo(sysdate)==0)
                        seqStart = rk.get("seqNo");
		seqStart = M.inc(seqStart);
                rk.set("submitNo", submitNo);
                rk.set("sysDate", sysdate);
                rk.set("seqNo", seqStart);
                rk.set("userID",userID);
                rk.set("description", M.stou("กธ.: ") + policyNo + M.stou(" เลขที่ใบเสร็จ: ") +  rpNo +
		      M.stou(" งวด: ") + period);
                rk.set("reserve"," ");
                if (!rk.insert())
                           throw new AppException("RteSubmitAgentChange(update):submitremark "+
                                BranchMsg.InsertFail+":"+rk.get("submitNo"));

                for (int i=0;i< vData.size();i++) {
                        String rkString  =  (String) vData.elementAt(i);
                        seqStart = M.inc(seqStart);
                        rk.set("submitNo", submitNo);
                        rk.set("sysDate", sysdate);
                        rk.set("seqNo", seqStart);
                        rk.set("userID",userID);
                        rk.set("description",rkString);
                        rk.set("reserve"," ");
                        if (!rk.insert())
                           throw new AppException("RteSubmitAgentChange(update):submitremark "+
                                BranchMsg.InsertFail+":"+rk.get("submitNo"));
                }
		/*seqStart = M.inc(seqStart);
                rk.set("submitNo", submitNo);
                rk.set("sysDate", sysdate);
                rk.set("seqNo", seqStart);
                rk.set("userID",userID);
                rk.set("description", M.stou("เนื่องจากบอกล้างบางส่วน(สินไหม)"));
                rk.set("reserve"," ");
                if (!rk.insert())
                           throw new AppException("RteSubmitAgentChange(update):submitremark "+
                                BranchMsg.InsertFail+":"+rk.get("submitNo"));*/
                rk.close();
System.out.println("insertDsubmitRemark .. end");
        }
}
