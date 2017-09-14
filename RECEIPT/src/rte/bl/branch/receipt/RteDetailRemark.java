package rte.bl.branch.receipt;

import java.io.PrintStream;
import java.util.Vector;
import manit.Mrecord;
import manit.rte.Result;
import manit.rte.Task;
import utility.cfile.CFile;

public class RteDetailRemark implements Task
{

    Mrecord fremark;
    Mrecord fpol;
    String policyNo;
    public Result execute(Object obj)
    {
        if(!(obj instanceof Object[]))
        {
            return new Result("Invalid Parameter  : Object [] {policyNo}", -1);
        }
        Object ob[] = (Object[])obj;
        policyNo = (String)ob[0];
		try
		{
        	Result result;
        	fremark = CFile.opens("remark@mstpolicy");
        	fpol = CFile.opens("policy_r@cbranch");
        	result = DetailData(policyNo);
        	return result;
		}
		catch(Exception e)
		{
        	return new Result(e.getMessage(), 2);
		}
    }

    private Result DetailData(String s)throws Exception
    {
        System.out.println("POLICY" + s);
        Vector vector = new Vector();
        for(boolean flag = fremark.equalGreat(s); flag && fremark.get("policyNo").equals(s); flag = fremark.next())
        {
            String as[] = new String[3];
            as[0] = fremark.get("policyNo");
            as[1] = fremark.get("sequence");
            as[2] = fremark.get("message");
            vector.addElement(as);
        }

        return new Result(vector, 0);
    }
}
