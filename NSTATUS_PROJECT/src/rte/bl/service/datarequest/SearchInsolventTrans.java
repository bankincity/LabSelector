package rte.bl.service.datarequest;

import java.util.ArrayList;

import manit.M;

import rte.bl.dataaccess.search.ByKey;
import rte.bl.dataaccess.search.Condition;
import rte.bl.dataaccess.search.ConditionIndexOf;
import rte.bl.dataaccess.search.DTable;
import rte.bl.dataaccess.search.Where;
import rte.bl.dataaccess.selector.SelectorPOS;
import utility.cfile.XTempList;

public class SearchInsolventTrans {
	public XTempList searchData(String typeOfTran ,String policyNo,String caseID) throws Exception
	{
		ArrayList<DTable> fromTable = new ArrayList<DTable>();
		
		Condition [] cond_tab1 = new Condition[] {new ConditionIndexOf(new ByKey("typeOfTran",typeOfTran))};
		
		// select .... from table1 ,table2;
		DTable t1 = new DTable("insolventtran","srvservice",new String [] {"policyNo","caseID","tranDate","tranTime","typeOfTran","userID","newStatus1","newStatusDate1","newStatus2","newStatusDate2","oldStatus1","oldStatusDate1","oldStatus2","oldStatusDate2"},cond_tab1);
		
		fromTable.add(t1);
		ByKey k1 = new ByKey();
		k1.addKey("policyNo", policyNo);
		k1.addKey("caseID", caseID);
		// set where
		XTempList out =SelectorPOS.leftJoint(fromTable, new Where(k1),null);
		out.addKey(new String []{"policyNo","caseID","tranDate","tranTime"});
		out.start(0);
		return out;
	}
	public static void main(String [] args) throws Exception
	{
		String policyNo = "25001231";
		String caseID = M.stou("ล.8348/2553");
		if (args.length == 2)
		{
			policyNo = args[0];
			caseID = args[1];
		}
		XTempList out = (new SearchInsolventTrans()).searchData("ASIP", policyNo, caseID);
		ArrayList<String> field = out.getDefaultFields();
		for (boolean st = out.first();st;st=out.next())
		{
			for (String f : field)
			{
				System.out.print(out.get(f));
			}
			System.out.println("");
		}
		
		
	}

}
