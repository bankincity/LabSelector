package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteUpdateSendRpToPrint  implements Task
{
	String [] type ;
	Vector vec ;
	Mrecord send;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {String action,String Vector}",-1);
		Object [] parameter = (Object []) param;
		try {
			String action = (String)parameter[0];
			String branch = (String)parameter[1];
			send = CFile.opens("sendrptoprint@cbranch");	
			if(action.charAt(0) == 'F')
			{
				type = (String [])parameter[2];
				vec = getData(branch,type);
			}
			else if (action.charAt(0) == 'U')
			{
				vec = (Vector)parameter[2];
				updateData(branch,vec);
				vec = getData(branch,type);
			}			
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result(vec,0);
	}
	private Vector getData(String branch,String [] typeRp)
	{
		Vector tvec = new Vector();
		for (int i = 0 ; i < typeRp.length;i++)
		{
			String [] str = new String [2];
			str[0] = typeRp[i];
			str[1] = "0";
			if (send.equal(typeRp[i].substring(0,1)+branch))
				str[1] = send.get("wantToPrint");
			
			tvec.addElement(str);
		}
		return tvec ;
	}
	private void updateData(String branch,Vector vdata)
	{
		type = new String[vdata.size()];
		for (int i = 0 ; i < vdata.size();i++)
		{
			String [] str = (String[])vdata.elementAt(i);
			type[i] = str[0];
			if (send.equal(str[0].substring(0,1)+branch))
			{
				send.set("wantToPrint",M.setlen(str[1],5));
				send.update();
			}
			else {
				send.set("branch",branch);
				send.set("typeOfReceipt",str[0].substring(0,1));
				send.set("wantToPrint",M.setlen(str[1],5));
				send.insert();
			}	
		}
	}
}
