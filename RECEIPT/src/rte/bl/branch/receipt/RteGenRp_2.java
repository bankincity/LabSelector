package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;
import rte.bl.branch.TempMasicFile ;
import utility.prename.Prename;
import utility.rteutility.PublicRte;
public class RteGenRp_2 implements Task
{
 	Mrecord firc;
 	Mrecord forc;
 	Mrecord fwrc;
 	Mrecord find;
 	Mrecord ford;
 	Mrecord fwhl;
 	Mrecord ful;
 	Mrecord fum;
 	Mrecord fll;
 	Mrecord flm;
 	Mrecord namePerson;
	Mrecord resrange;
 	boolean chk;
 	String date;
 	String branch;
 	String mm;
 	String yyyy;
 	String askSaleID = "";
 	String ownerSaleID = "";
	TempMasicFile temp;
	int count = 0;
	int value;
 	int[] len = { 8,12,80,6,9,12,1,8};
 	String[] field = {"policyNo","rpNo","name","payPeriod","premium","submitNo","type","requestDate"};
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {date,branch}",-1);
 		Object [] parameter = (Object []) param;
 		date = (String)parameter[0];
 		branch = (String)parameter[1];
 		try 
 		{
 			firc = CFile.opens("irctrl@receipt");
 			forc = CFile.opens("orctrl@receipt");
 			fwrc = CFile.opens("wrctrl@receipt");
 			find = CFile.opens("indmast@mstpolicy");
 			ford = CFile.opens("ordmast@mstpolicy");
 			fwhl = CFile.opens("whlmast@mstpolicy");
			if (Masic.fileStatus("ulrctrl@universal") >= 2)
			{
	 			ful = CFile.opens("ulrctrl@universal");
				fum = CFile.opens("universallife@universal");
			}
 			namePerson = CFile.opens("name@mstperson");
 			namePerson = CFile.opens("name@mstperson");
			resrange = CFile.opens("reservedrange@receipt");
 			temp = new TempMasicFile("rptbranch",field,len);
 			temp.keyField(true,true,new String [] {"policyNo","rpNo"});
 			temp.buildTemp();
			searchData(date,branch);
			return new Result(temp.name(),0);

 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
 	}
    private void setName(String pol,String type)
	{

		switch (type.charAt(0))
 		{
 			case 'I' :
						if(find.equal(pol))
 						{
							temp.set("name",getName(find.get("nameID")));
						}
						else
						{
							temp.set("name","");
						}
						break;
			case 'O' :
						if(ford.equal(pol))
 						{
							temp.set("name",getName(ford.get("nameID")));
						}
						else
						{
							temp.set("name","");
						}
						break;
			case 'U' :
						if(fum.equal(pol))
 						{
							temp.set("name",getName(fum.get("nameID")));
						}
						else
						{
							temp.set("name","");
						}
						break;
			case 'W' :
						if(fwhl.equal(pol))
 						{
							temp.set("name",getName(fwhl.get("nameID")));
						}
						else
						{
							temp.set("name","");
						}
						break;
			case 'L' :
						if(flm.equal(pol))
 						{
							temp.set("name",getName(flm.get("nameID")));
						}
						else
						{
							temp.set("name","");
						}
						break;

		}

	}	
    private String  getName(String nameID)
	{
		if(namePerson.equal(nameID))
			return  (Prename.getAbb(namePerson.get("preName").trim())+namePerson.get("firstName").trim()+" "+namePerson.get("lastName").trim());
		return "";
	}

	boolean checkReserve(String type,String rpNo)
	{
		if(resrange.less(type+rpNo))
		{
			if(rpNo.compareTo(resrange.get("startRange")) >= 0 && rpNo.compareTo(resrange.get("endRange")) <= 0)
				return true; 
		}
		if(resrange.equal(type+rpNo))
			return true;
		return false;
	}
	private void searchData(String date, String branch) throws Exception
 	{
		String key = date+branch;
		firc.start(2);
		for(boolean ok=firc.equalGreat(key); ok; ok=firc.next())
		{
			if(key.compareTo(firc.get("printedDate")+firc.get("rpNo").substring(0,3)) != 0)
				break;
			if(firc.get("sysDate").substring(6).compareTo("00") == 0 && "25".compareTo(firc.get("sysDate").substring(0,2)) == 0)

				continue;
			if(checkReserve("I",firc.get("rpNo")))
				continue;
			if("CRZY".indexOf(firc.get("currentStatus")) >= 0)	
				continue;
  				temp.set("policyNo",firc.get("policyNo"));
  				temp.set("rpNo",firc.get("rpNo"));
				setName(firc.get("policyNo"),"I");
  				temp.set("payPeriod",firc.get("payPeriod"));
  				temp.set("premium",firc.get("premium"));
  				temp.set("submitNo",firc.get("submitNo"));
  				temp.set("type","I");
  				temp.set("requestDate",firc.get("requestDate"));
				temp.insert();
		}
		forc.start(2);
		for(boolean ok=forc.equalGreat(key); ok; ok=forc.next())
		{
			if(key.compareTo(forc.get("printedDate")+forc.get("rpNo").substring(0,3)) != 0)
				break;
			if(forc.get("sysDate").substring(6).compareTo("00") == 0 && "25".compareTo(forc.get("sysDate").substring(0,2)) == 0)

				continue;
			if(checkReserve("O",forc.get("rpNo")))
				continue;
			if("CRZY".indexOf(forc.get("currentStatus")) >= 0)	
				continue;
  				temp.set("policyNo",forc.get("policyNo"));
  				temp.set("rpNo",forc.get("rpNo"));
				setName(forc.get("policyNo"),"O");
  				temp.set("payPeriod",forc.get("payPeriod"));
  				temp.set("premium",forc.get("premium"));
  				temp.set("submitNo",forc.get("submitNo"));
  				temp.set("type","O");
  				temp.set("requestDate",forc.get("requestDate"));
				temp.insert();
		}
                /* find apl receipt issue by printDate */
                forc.start(0);
                //Msg.msg(new Mframe(""),branch+"  "+date);
                Mrecord aplrc =Masic.opens("aplrctrl@mstscan");
		for(boolean ok=forc.equalGreat("A"); ok; ok=forc.next())
		{
			if("A".indexOf(forc.get("rpNo").charAt(0)) < 0)
				break;
                        if (!aplrc.equal(forc.get("rpNo")))
                                continue;
                        if (branch.compareTo(forc.get("submitBranch")) == 0 && date.compareTo(forc.get("printedDate")) == 0)
                        {
			        if("CRZY".indexOf(forc.get("currentStatus")) >= 0)	
				        continue;
  				temp.set("policyNo",forc.get("policyNo"));
  				temp.set("rpNo",forc.get("rpNo"));
				setName(forc.get("policyNo"),"O");
  				temp.set("payPeriod",forc.get("payPeriod"));
  				temp.set("premium",forc.get("premium"));
  				temp.set("submitNo",forc.get("submitNo"));
  				temp.set("type","O");
  				temp.set("requestDate",forc.get("requestDate"));
				temp.insert();
                        }
		}





		fwrc.start(2);
		for(boolean ok=fwrc.equalGreat(key); ok; ok=fwrc.next())
		{
			if(key.compareTo(fwrc.get("printedDate")+fwrc.get("rpNo").substring(0,3)) != 0)
				break;
			if(fwrc.get("sysDate").substring(6).compareTo("00") == 0 && "25".compareTo(fwrc.get("sysDate").substring(0,2)) == 0)
				continue;
			if(checkReserve("O",fwrc.get("rpNo")))
				continue;
			if("CRZY".indexOf(fwrc.get("currentStatus")) >= 0)	
				continue;
  				temp.set("policyNo",fwrc.get("policyNo"));
  				temp.set("rpNo",fwrc.get("rpNo"));
				setName(fwrc.get("policyNo"),"W");
  				temp.set("payPeriod",fwrc.get("payPeriod"));
  				temp.set("premium",fwrc.get("premium"));
  				temp.set("submitNo",fwrc.get("submitNo"));
  				temp.set("type","W");
  				temp.set("requestDate",fwrc.get("requestDate"));
				temp.insert();
		}
		ful.start(2);
		for(boolean ok=ful.equalGreat(key); ok; ok=ful.next())
		{
			if(key.compareTo(ful.get("printedDate")+ful.get("rpNo").substring(0,3)) != 0)
				break;
			if(ful.get("sysDate").substring(6).compareTo("00") == 0 && "25".compareTo(ful.get("sysDate").substring(0,2)) == 0)
				continue;
			if(checkReserve("U",ful.get("rpNo")))
				continue;
			if("CRZY".indexOf(ful.get("currentStatus")) >= 0)	
				continue;
  			temp.set("policyNo",ful.get("policyNo"));
  			temp.set("rpNo",ful.get("rpNo"));
			setName(ful.get("policyNo"),"U");
  			temp.set("payPeriod",ful.get("payPeriod"));
  			temp.set("premium",ful.get("premium"));
  			temp.set("submitNo",ful.get("submitNo"));
  			temp.set("type","U");
  			temp.set("requestDate",ful.get("requestDate"));
			temp.insert();
		}
                if (fll == null)
                        return ;
		fll.start(2);
		for(boolean ok=fll.equalGreat(key); ok; ok=fll.next())
		{
			if(key.compareTo(fll.get("printedDate")+fll.get("rpNo").substring(0,3)) != 0)
				break;
			if(fll.get("sysDate").substring(6).compareTo("00") == 0 && "25".compareTo(fll.get("sysDate").substring(0,2)) == 0)
				continue;
			if(checkReserve("L",fll.get("rpNo")))
				continue;
			if("CRZY".indexOf(fll.get("currentStatus")) >= 0)	
				continue;
  			temp.set("policyNo",fll.get("policyNo"));
  			temp.set("rpNo",fll.get("rpNo"));
			setName(fll.get("policyNo"),"L");
  			temp.set("payPeriod",fll.get("payPeriod"));
  			temp.set("premium",fll.get("premium"));
  			temp.set("submitNo",fll.get("submitNo"));
  			temp.set("type","L");
  			temp.set("requestDate",fll.get("requestDate"));
			temp.insert();
		}
	}	

}


