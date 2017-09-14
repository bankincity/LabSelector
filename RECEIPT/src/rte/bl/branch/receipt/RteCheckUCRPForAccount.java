package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.branch.sales.BraSales;
import rte.bl.branch.TempMasicFile ;
import utility.cfile.CFile;
import utility.rteutility.PublicRte;
import utility.prename.*;
import utility.support.*;
import utility.sales.*;
import java.util.Vector;

public class RteCheckUCRPForAccount implements Task
{
    public Result execute(Object obj)
    {
        try
        {
            Object[] arrObj = (Object[]) obj;
            String branch = (String) arrObj[0];
            String saleID = (String) arrObj[1];

            return new Result( checkUCRPForAccount(branch, saleID), 0 );
        }
        catch( Exception e )
        {
            return new Result( e.getMessage(), 1 );
        }
    }
    Vector checkUCRPForAccount(String branchID, String saleID) throws Exception
    {
        String[] str;
        Vector v;
        UCRPYYYYMM rte = new UCRPYYYYMM();
        v = rte.checkUCRPForAccount(branchID, saleID);
System.out.println("v.size() = " + v.size());
        for(int i = 0; i < v.size(); i++)
        {
            str = (String[]) v.elementAt(i);
System.out.println("str[" + i + "] : " + str[0] + " / "+ str[1] + " / " + str[2] + " / " + str[3]);
        }
        return v;
    }
    public static void main(String[] args) throws Exception
    {
        String branch = args[0];
        String saleID = args[1];
        Object[] aObj = new Object[]{branch, saleID};
        RteCheckUCRPForAccount rteX = new RteCheckUCRPForAccount();
        rteX.execute( aObj );
    }
}
