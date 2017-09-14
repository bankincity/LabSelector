package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import utility.prename.Prename;
import utility.rteutility.PublicRte;
public class RteMessageEditRp implements Task
{
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {policy,Vector}",-1);
		Object [] o = (Object[])param;
		String policyNo = (String)o[0];
		Vector vmessage  = (Vector)o[1];
		try {
			Mrecord message = CFile.opens("editreceipt@receipt");
			if(vmessage.size() >  0)
			{
				for (boolean st = message.equalGreat(policyNo);st && policyNo.compareTo(message.get("policyNo")) == 0;st=message.next())
				{
					message.delete();
				}
				System.out.println("vmessage.................."+vmessage.size());
				for (int i = 0 ; i < vmessage.size();i++)
				{
					message.set("policyNo",policyNo);
					message.set("seq",M.setlen(M.itoc(i),3));
					message.set("message",(String)vmessage.elementAt(i));
					System.out.println(i+"........."+message.get("message")+"   "+message.get("seq"));
					if(!message.insert())
						throw new Exception("Can not insert error = "+M.itoc(message.lastError()));
				}
				vmessage.removeAllElements();
			}
			else {
				 for (boolean st = message.equalGreat(policyNo);st && policyNo.compareTo(message.get("policyNo")) == 0;st=message.next())
                                {
                                 	vmessage.addElement(message.get("message"));      
                                }
			}
		}
		catch(Exception e)
		{
			return new Result(e.getMessage(),2);
		}
		return new Result(vmessage ,0);
	}	

}


