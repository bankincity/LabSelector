package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.DateInfo;
import  utility.cfile.CFile;
import  utility.cfile.Rtemp;
import  rte.bl.branch.*;
import  rte.*;
import utility.rteutility.*;
import java.util.Vector;
import  layout.receipt.*;
public class  RteSearchReceiptWriteToBra  implements Task
{
	public Result execute(Object param)
        {
                if(! (param instanceof String []))
                        return new Result("Invalid Parameter  : String  [] {branch,status,typeOfPolicy,policyNo}",-1);
                String  [] args  = (String []) param;
		// branch status typeOfPolicy policyNo
		try {
			Rtemp temp = new Rtemp(field,len);
        		lay = temp.layout();
			temptoprint = new TempMasicFile("bra"+args[0]+"app",lay);
			temptoprint.keyField(false,false,new String [] {"policyNo","payPeriod","rpNo"});
                	temptoprint.buildTemp();
			if(args[2].charAt(0) == 'O')
				getAllRp(args[0],args[1],"W",args[3]);	
			getAllRp(args[0],args[1],args[2],args[3]);	
			getAllRpOtherStatus(args[0],args[1],args[2],args[3]);	
		}
		catch (Exception e)
		{
			return (new Result(e.getMessage(),1));
		}
		Vector vec = new Vector();
		vec.add(temptoprint.name());
		vec.add(field);
		vec.add(len);
		System.out.println("bingo..................................................................ddddfsafsadfdsaf");
		return (new Result(vec,0));
	}
	String [] field = {"policyNo","payPeriod","rpNo","premium","payDate","currentStatus","originalStatus","sysDate","requestDate","effectiveDate","submitNo","mode","reasonCode","typeOfPol"};
	int [] len = {8,6,12,9,8,1,1,8,8,8,12,1,2,1};
	TempMasicFile  temptoprint;
	Layout lay = null;
	Record rprec;
	private void getAllRp(String branch,String status,String typeOfPol,String policyNoOrRpNo) throws Exception
	{		
		if(typeOfPol.charAt(0) =='I')
			rprec = (Record)(new  Rirctrl());
		else if(typeOfPol.charAt(0) =='O')
			rprec = (Record)(new  Rorctrl());
		else if(typeOfPol.charAt(0) =='W')
			rprec = (Record)(new  Rwrctrl());
		else if(typeOfPol.charAt(0) =='U')
			rprec = (Record)(new  layout.universal.Rulrctrl());
		Result res = null;
		if(policyNoOrRpNo.trim().length() == 8) 
		{
			if (typeOfPol.charAt(0) == 'U')
				res= PublicRte.getResult("searchuniversal","rte.search.universal.receipt.SearchByPolicy", new String [] {typeOfPol,policyNoOrRpNo,status});
			else	
				res= PublicRte.getResult("searchreceipt","rte.search.receipt.SearchByPolicy", new String [] {typeOfPol,policyNoOrRpNo,status});
		}
		else {
			if (typeOfPol.charAt(0) == 'U')
				res= PublicRte.getResult("searchreceipt","rte.search.universal.receipt.SearchByRpNo", new String [] {typeOfPol,policyNoOrRpNo,status});
			else
				res= PublicRte.getResult("searchreceipt","rte.search.receipt.SearchByRpNo", new String [] {typeOfPol,policyNoOrRpNo,status});
		}
		System.out.println(typeOfPol+"  @@@@@@ SearchReceiptByFile @@@@@@ Result of rte.search.receipt.SearchByPolicy--->"+res.status());
		if(res.status() != 0 && res.status() != 1)
                        throw new Exception((String)res.value());
		if(res.status() != 1)
		{
			Vector vec = (Vector)res.value();
			moveToTemp(vec,typeOfPol);
		}
	}
	private void getAllRpOtherStatus(String branch,String status,String typeOfPol,String policyNoOrRpNo) throws Exception
	{
		if(status.indexOf("C") < 0)
			return ;		
		if(typeOfPol.charAt(0) =='I')
			rprec = (Record)(new  Rcirctrl());
		else if(typeOfPol.charAt(0) =='O' || typeOfPol.charAt(0) == 'W')
			rprec = (Record)(new  Rcorctrl());
		else if(typeOfPol.charAt(0) =='U')
			rprec = (Record)(new  layout.universal.Rulrctrl());
		Result res = null;
		if(policyNoOrRpNo.trim().length() == 8) 
		{
			if (typeOfPol.charAt(0) == 'U')
				res = PublicRte.getResult("searchreceipt","rte.search.universal.receipt.SearchByPolicyOtherStatus", new String [] {typeOfPol,policyNoOrRpNo,status});
			else
				res = PublicRte.getResult("searchreceipt","rte.search.receipt.SearchByPolicyOtherStatus", new String [] {typeOfPol,policyNoOrRpNo,status});
				
		}
		else {
			if (typeOfPol.charAt(0) == 'U')
				res= PublicRte.getResult("searchreceipt","rte.search.universal.receipt.SearchByRpNoOtherStatus", new String [] {typeOfPol,policyNoOrRpNo,status});
			else
				res= PublicRte.getResult("searchreceipt","rte.search.receipt.SearchByRpNoOtherStatus", new String [] {typeOfPol,policyNoOrRpNo,status});
				
		}
		System.out.println("@@@ RteSearchReceiptWriteToBra @@ Result of rte.search.receipt.SearchByPolicyOtherStatus--->"+res.status());
		if(res.status() != 0 && res.status() != 1)
                        throw new Exception((String)res.value());	
		if(res.status() == 1)
			return ;
		Vector vec = (Vector)res.value();
		moveToTemp(vec,typeOfPol);
	}
	private void moveToTemp(Vector vec,String typeOfPol ) throws Exception
	{	
		for(int i = 0 ; i < vec.size();i++)
		{
			byte [] buffer = (byte [])vec.elementAt(i);
			rprec.putBytes(buffer);
			System.out.println("bingo..........."+rprec.get("rpNo")+"-----"+rprec.get("payPeriod"));
			for (int j = 0 ; j < field.length-1 ; j++)
			{
				if(typeOfPol.charAt(0) == 'I' && j == field.length - 3)
					temptoprint.set(field[j],"0");
				else
					temptoprint.set(field[j],rprec.get(field[j]));
			}
			temptoprint.set("typeOfPol",typeOfPol);
			temptoprint.insert();
		}
	}
}

