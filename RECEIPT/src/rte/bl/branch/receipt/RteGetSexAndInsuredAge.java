package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteGetSexAndInsuredAge implements Task
{
	 /**==============================================================<br>
                <pre Vspace=100>
                execute String[] {String type  policyNo}
                        Status :
                                 0 = success
                                -1 = errorMessage or system error</pre>

        =================================================================*/
	public Result execute(Object o)
	{
		if(!(o instanceof Object []))
                        return new Result(M.stou("Parameter must be Array  (typeOfPolicy, Vector of PolicyNo,reasonCode,userID )"),-1);
		Object[] parameter = (Object [])o;
		try {
			String typeOfPol =  (String)parameter[0];
			String policyNo = (String)parameter[1];
			Mrecord master = null;
			if(typeOfPol.charAt(0) == 'I')
				master = CFile.opens("indmast@mstpolicy");
			else if (typeOfPol.charAt(0) == 'U')
				master = CFile.opens("universallife@universal");
			else{
				master = CFile.opens("ordmast@mstpolicy");
				if(!master.equal(policyNo))
					master = CFile.opens("whlmast@mstpolicy");
					
			}
			if(master.equal(policyNo))
			{
				Mrecord  nameMaster  = CFile.opens("name@mstperson");
                     		Mrecord personMaster  = CFile.opens("person@mstperson");
				if(nameMaster.equal(master.get("nameID")))
                                {
                                       if(personMaster.equal(nameMaster.get("personID")))
                                       {
						return new Result(personMaster.get("sex")+master.get("insuredAge"),0);
					}
				}
			}
			throw new Exception(M.stou("ไม่สามารถหาข้อมูล เพศ หรือ อายุผู้เอาประกันได้ ")+"("+policyNo+")");

		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),-1);
		}
	}
}
