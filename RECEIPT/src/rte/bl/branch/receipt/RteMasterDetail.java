package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import utility.prename.Prename;
import utility.rteutility.PublicRte;
public class RteMasterDetail implements Task
{
 	public Result execute(Object param)
 	{
 		if(!(param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {policyType,policyNo}",-1);
		Object [] o = (Object[])param;
		String policyType = (String)o[0];
		String policyNo = (String)o[1];
                Vector vResult = new Vector();

                try
                {
                        /*--- Open master  ---*/
                        String masterName = policyType.charAt(0) == 'I' ? "indmast@mstpolicy" :
                                            policyType.charAt(0) == 'O' ? "ordmast@mstpolicy" : "whlmast@mstpolicy";
                      Mrecord masterfile  = Masic.opens(masterName);
                      Mrecord namefile      = Masic.opens("name@mstperson");
                      Mrecord perfile      = Masic.opens("person@mstperson");
                        if (masterfile.equal(policyNo))
                        {
                                System.out.println("policy equal");
                                Vector vmast = new Vector();
                                vResult.addElement(masterfile.copy().getBytes());
                                if (namefile.equal(masterfile.get("nameID")))
                                {
System.out.println("--------> Start Search Master -------->  "+policyNo);
                                        vResult.addElement(namefile.copy().getBytes());
					if (perfile.equal(namefile.get("personID")))
					{
System.out.println("--------> Start Search Master -------->  "+policyNo);
                                           vResult.addElement(perfile.copy().getBytes());
					}
					else
					{
                                           vResult.addElement(null);
					}
                                }
                                else
                                {
                                        vResult.addElement(null);
                                        vResult.addElement(null);
                                }
                        }
                        else
                                throw new Exception(M.stou("ไม่พบกรมธรรม์ ") + policyNo);

                }
		catch(Exception e)
		{
			return new Result(e.getMessage(),2);
		}
		return new Result(vResult ,0);
	}	

}


