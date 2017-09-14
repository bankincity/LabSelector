package rte.bl.branch.receipt.help;
import manit.*;
import manit.rte.*;
import utility.support.*;
import java.util.*;
import utility.rteutility.*;
public class SearchBySaleID  implements  Task
{
	Mrecord rp;
	Mrecord master ;
	public Result execute(Object o)
	{
		if(!(o instanceof String []))
			return new Result(M.stou("Parameter must be array of String {SaleID,status,flagRecord}"),-1);
		String [] parameter = (String [])o;
		return searchAllRp(parameter[0],parameter[1]);		
	}
	Vector [] vcRp;
	public Result searchAllRp(String saleID,String status)
	{
		String [] type ={"O","I","W"};
		vcRp  = new Vector[3];

		for (int i = 0 ; i < 3 ; i++)
			vcRp [i] = new Vector();
		try {		
			Result res = PublicRte.getResult("searchmaster","rte.search.master.SearchMasterBySaleID",new Object[] {saleID,"P"});
                	if(res.status() != 0)
                        	throw new Exception((String)res.value());
			Vector [] mstvec = (Vector [])res.value();
			for (int i = 0 ; i <  type.length;i++)
			{
				for (int j = 0 ; j < mstvec[i].size();j++)
				{
					// policyNo, effectiveDate ,dueDate, policyStatus
					String [] strMaster = (String [])mstvec[i].elementAt(j);
					String effyear = strMaster[1].substring(0,4);
                        		String year = "";
					String eyear = DateInfo.sysDate().substring(0,4);

                        		while (eyear.compareTo(effyear) >= 0)
                        		{
                               			if(!openFile(type[i],year))
                                        		break;
                                		addToVector(i,status,strMaster[0],strMaster[5]);
                                		if(status.indexOf("P") < 0 && status.indexOf("B") < 0 && status.indexOf("E") < 0)
                                        		break;
                               			 eyear  = M.dec(eyear);
                                		 year = eyear;
					}
				}
                        }
			
		}
		catch (Exception e)
		{
			return (new Result("Error :"+e.getMessage(),-1));		
		}
		return (new Result(vcRp,0));
	}
	String [] field = {"rpNo","policyNo","payPeriod","premium","time","currentStatus","reasonCode","dueDate","nameID"};
	int [] len = {12,8,6,9,1,1,2,8,13};
	private void addToVector(int idx,String status,String policyNo,String nameID)
	{
		for (boolean st = rp.equal(policyNo);st;st=rp.next())
		{
			if(policyNo.compareTo(rp.get("policyNo")) != 0)
				break;
			if(status.indexOf(rp.get("currentStatus")) >= 0)
			{
				vcRp[idx].addElement(rp.copy().getBytes());
			}
		}	
	}
	public boolean  openFile(String type,String year) throws Exception
	{
		String filename = type.toLowerCase()+"rctrl"+year;
		filename = filename+"@"+"receipt";
		if(Masic.fileStatus(filename) < 2)
			return false;
		rp = Masic.opens(filename);
		if(rp == null || rp.lastError() != 0)
			throw new Exception("Cannot Opens File "+filename);
		if(!rp.start(1))
		{
			throw new Exception("Can not start key 1 "+filename);  	
		}
		return true;
	}
}
