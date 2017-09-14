package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import  utility.branch.sales.BraSales;
import  rte.bl.branch.*;
import java.util.*;
import rte.*;
import utility.rteutility.*;
import layout.receipt.*;
import utility.cfile.Rtemp;
import utility.prename.Prename;
import utility.address.*;
public class  RteGetRPByPolicyToAsk implements  Task
{
	Mrecord ord;
        Mrecord whl;
        Mrecord ind;
        Mrecord mastinv;
        Mrecord address;

	public Result execute(Object param) 
        {
		Object [] parameter = (Object [])param;
		// remote, yyyymm,saleID,typeOfPol ,Vector
		try {
			String fileName = RteRpt.getTempName("rptbranch");
                	System.out.println(" fileName=" + fileName);
			ord = CFile.opens("ordmast@mstpolicy");
         		whl = CFile.opens("whlmast@mstpolicy");
            		ind = CFile.opens("indmast@mstpolicy");

         	        mastinv = CFile.opens("masterinvalid@mstlogfile");
              		address = CFile.opens("address@mstperson");
	   	 	invaddress = new TempMasicFile("rptbranch",invfield,invlen);
	                invaddress.keyField(false,false,new String [] {"policyNo"});
           	        invaddress.buildTemp();


			getReceiptToAsk((String)parameter[1],(String)parameter[2],(String)parameter[3],(Vector)parameter[4]);
		
			System.out.println("End startSearch process! ");
			rte.RteRpt.recToTemp(temptoprint, fileName);
			temptoprint.close();
			return new Result(fileName,0);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
	}
	TempMasicFile  invaddress;
	TempMasicFile  temptoprint;
        String [] invfield = {"policyNo","name","address1","address2"};
        int [] invlen = {8,80,130,130};

	Mrecord nameMaster;
	String [] field = {"policyNo","rpNo","payPeriod","premium","receiptFlag","currentStatus","reasonCode","dueDate","name","time","flagForReinsured"};
	int [] len = {8,12,6,9,1,1,2,8,80,1,5};
	// type of Ask '1' ind  , '2' ord
	private void getReceiptToAsk(String yyyymm,String saleID,String typeOfPol,Vector vecpol) throws Exception
	{

		nameMaster  = Masic.opens("name@mstperson"); 
		if(nameMaster == null || nameMaster.lastError() != 0)
			throw new Exception("Can not open name@mstperson");

		temptoprint = new TempMasicFile("rptbranch",field,len);
                temptoprint.keyField(false,false,new String [] {"policyNo","rpNo"});
                temptoprint.buildTemp();

                System.out.println("open file get all request rp ");

		Result res = PublicRte.getResult("searchreceipt","rte.search.receipt.SearchBySaleID", new Object [] {saleID,"NAXW",typeOfPol+yyyymm,vecpol});
		if(res.status() != 0)
			throw new Exception((String)res.value());
		Vector [] vRp = (Vector[])res.value();		
		if(typeOfPol.charAt(0) == 'O')
		{
			temptoprint.set("receiptFlag","O");
			moveDataToTemp(vRp[0]);		
			temptoprint.set("receiptFlag","W");
			moveDataToTemp(vRp[2]);		
		}
		else
		{
			temptoprint.set("receiptFlag","I");
			moveDataToTemp(vRp[2]);		
		}
	}
	String [] tfield = {"rpNo","policyNo","payPeriod","premium","time","currentStatus","reasonCode","dueDate","nameID","rpDueDate","submitBrnach"};
	int [] tlen = {12,8,6,9,1,1,2,8,13,8,3};

	private void moveDataToTemp(Vector vec )
	{
		Rtemp  rec = new Rtemp(tfield,tlen);
		invaddress = null;
		for (int i = 0 ; i < vec.size();i++)
		{
			rec.putBytes((byte [])vec.elementAt(i));
			temptoprint.set("rpNo",rec.get("rpNo"));
			temptoprint.set("policyNo",rec.get("policyNo"));
			temptoprint.set("payPeriod",rec.get("payPeriod"));
			temptoprint.set("premium",rec.get("premium"));
			temptoprint.set("time",M.inc(rec.get("time")));
			temptoprint.set("currentStatus",rec.get("currentStatus"));
			temptoprint.set("dueDate",rec.get("rpDueDate"));
			temptoprint.set("reasonCode",rec.get("reasonCode"));
			temptoprint.set("name",getName(rec.get("nameID")));
			temptoprint.set("flagForReinsured",getFlagForReinsured());
			temptoprint.insert();
			String flagReceipt = temptoprint.get("receiptFlag");
			if(mastinv.equal(flagReceipt+rec.get("policyNo")+"MA"))
                        {

                        System.out.println("...................missing address..............."+rec.get("policyNo"));
                                invaddress.set("policyNo",rec.get("policyNo"));
                                invaddress.set("name",temptoprint.get("name"));
                                String [] addr = getInvalidAddress(flagReceipt,rec.get("policyNo"));
                                invaddress.set("address1",addr[0]);
                                invaddress.set("address2",addr[1]);
                                invaddress.insert();
                        }

		}
		if(invaddress.fileSize() > 0)
                {
                        System.out.println("...........................................missing address...............");
                        temptoprint.set(' ');
                        temptoprint.set("rpNo","XXXXXXXXXXXX");
                        temptoprint.set("name",invaddress.name());
                        temptoprint.insert();
                }

			
	}
	private String []  getInvalidAddress(String type,String policyNo)
        {
                Mrecord tmast = null;
                String [] taddress = {"",""};
                if(type.charAt(0) == 'I')
                        tmast = ind;
                else if(type.charAt(0) == 'O')
                        tmast =ord;
                else if(type.charAt(0) == 'W')
                        tmast = whl;
                if(tmast.equal(policyNo))
                {
                        if(address.equal(tmast.get("contactAddressID")))
                        {
                                taddress = AddressFnc.format2LineAddress(false,address.get("address") ,address.get("tumbon"),address.get("zipCode"));
                        }
                }
                return taddress;
        }

	private String  getName(String nameID)
	{
		if(nameMaster.equal(nameID))
			return  (Prename.getAbb(nameMaster.get("preName").trim())+nameMaster.get("firstName").trim()+" "+nameMaster.get("lastName").trim());
		return " ";
	}
	public String getFlagForReinsured()
	{
		return " ";
	}
	
}


