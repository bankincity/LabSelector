package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import java.util.ArrayList;
//import rte.bl.tli.*;
//import rte.bl.dataaccess.receipt.*;
//import layout.unitlink.sales.*;
import utility.cfile.CFile;
public class RteReceiptAPL implements Task
{
    public Result execute(Object o)
    {
        Object[] obj = (Object[]) o;
        String status = (String) obj[0];
        System.out.println("RteReceiptAPL ... status : "+ status);
        try
        {
            if( status.equals("GETRECEIPT") )
            {
                return new Result(getReceipt( (String) obj[1] ), 0);
            }
            else if( status.equals("EDITRECEIPT") )
            {
                editReceipt( (String[]) obj[1] , (String) obj[2], (String) obj[3]);
            }
            else
                return( new Result(M.stou("Do Not Qualify"), 2) );
            return( new Result(M.stou(""), 0) );
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new Result(e.getMessage(), 1);
        }
    }

    public String[] getReceipt(String rpNo)throws Exception
    {
        Mrecord mctrl = CFile.opens("orctrl@receipt");
//        Mrecord mchg = CFile.oepns("xrpchg@cbranch");
        String[] str = new String[]{"", "", "", "", "", ""};
        if( mctrl.equal(rpNo) )
        {
            if( "CRZ".indexOf(mctrl.get("currentStatus")) >= 0 )
            {
                str = new String[]{ rpNo, mctrl.get("policyNo"), mctrl.get("payPeriod"), mctrl.get("premium"), mctrl.get("currentStatus"), "" };
            }
        }
        return str;
    }

    public void editReceipt(String[] a_str, String branch, String userID)throws Exception
    {
        Mrecord mctrl = CFile.opens("orctrl@receipt");
        Mrecord mchg = CFile.opens("xrpchg@cbranch");
        mchg.start(1);
        if( mctrl.equal(a_str[0]) )
        {
            System.out.println("********************> Got IT in orctrl@receipt");
            Record rec = new Record();
            rec =  mctrl.copy();
            rec.set("rpNo", a_str[5]);
            System.out.println("rec rpNo : " + rec.get("rpNo"));
            if( !mctrl.delete() )
                throw new Exception("can't delete "+ mctrl.name()+", error : " + mctrl.lastError());
            System.out.println("after delete mctrl");
            if( !mctrl.insert(rec.copy()) )
                throw new Exception("can't insert "+ mctrl.name()+", error : " + mctrl.lastError());
            System.out.println("after insert mctrl");
            for( boolean b = mchg.equal("O"+a_str[0]); b && (mchg.get("typeOfReceipt")+mchg.get("rpNo")).equals("O"+a_str[0]); b = mchg.next() )
            {
                if( mchg.get("newStatus").equals(rec.get("currentStatus")) )
                {
                    System.out.println("***************************> GOT IT in xrpchg@cbranch");
                    Record rchg = new Record();
                    rchg = mchg.copy();
                    rchg.set("rpNo", a_str[5]);

                    if( !mchg.delete() )
                        throw new Exception("can't delete "+ mchg.name()+", error : " + mchg.lastError());
                    System.out.println("after delete mchg");
                    if( !mchg.insert(rchg.copy()) )
                        throw new Exception("can't insert "+ mchg.name()+", error : " + mchg.lastError());
                    System.out.println("after insert mchg");
                    break;

/*                    mchg.set("sysDate",     "");
                    mchg.set("sysTime",     "");
                    mchg.set("typeOfReceipt", "");
                    mchg.set("rpNo",        "");
                    mchg.set("policyNo",    "");
                    mchg.set("premium",     "");
                    mchg.set("payPeriod",   "");
                    mchg.set("newStatus",   "");
                    mchg.set("oldStatus",   "");
                    mchg.set("branch",      "");
                    mchg.set("userID",      "");*/
                }
            }
        }
    }
}
