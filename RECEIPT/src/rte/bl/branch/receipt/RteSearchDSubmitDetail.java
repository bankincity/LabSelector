package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import insure.Insure;
import java.util.*;
import utility.rteutility.PublicRte;
public class RteSearchDSubmitDetail  implements Task
{
	String userID;
	String sysdate;
	String systime;
	String policyNo;
	String policyType;
	String rpMode;
	String[] submitNoArr;
	String[] rpNoArr;
	String[] payDateArr;
	boolean isCancel;
	Mrecord perFile;
	Mrecord operFile;
	Mrecord strFile;
	Vector v = new Vector();
	String[] sArr;
	public Result execute(Object param)
	{
		if(!(param instanceof Object []))
		   return new Result("Invalid Parameter:Object [] {policyType,policyNo,userID,submitNo,rpNo,payDate}",-1);
		Object [] parameter = (Object []) param;
		policyType = (String)parameter[0];
		policyNo = (String)parameter[1];
		userID = (String)parameter[2]; 
		submitNoArr = ((String)parameter[3]).split(":"); 
		rpNoArr = ((String)parameter[4]).split(":"); 
		payDateArr = ((String)parameter[5]).split(":"); 
			
		String  mst = policyType.charAt(0) == 'O' ? "ord": policyType.charAt(0) == 'I' ? "ind":"whl";
		Mrecord	mstFile ;
		String [] sArr = null;
		String fileName= "";
		String dueDate = "";
		sysdate = DateInfo.sysDate();
		systime =  Masic.time("commontable").substring(8);
		try {
		perFile = CFile.opens("person@sales");
		operFile = CFile.opens("oldperson@sales");
		strFile = CFile.opens("struct@sales");
		        searchData();	
		printStringArrInVector();
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result(v,0);
	}
	/*-------------------------------------------------*/
/*	RteSearchDSubmitDetail()
	{
	}
	void test (String s1, String s2,String policyNo)
	{
		try
		{
			perFile = CFile.opens("person@sales");
			operFile = CFile.opens("oldperson@sales");
			strFile = CFile.opens("struct@sales");
			this.policyNo = policyNo;
			System.out.println("policyNo = " + policyNo);
	                submitNoArr = s1.split(":");
                        rpNoArr = s2.split(":");
                	for (int i = 0; i < submitNoArr.length ; i++)
                	{
				System.out.println("submitNoArr[" + i + "] = " + submitNoArr[i] + " :    rpNoArr[" + i + "] = " + rpNoArr[i]);
                    		searchBySubmitNo(submitNoArr[i],rpNoArr[i]);
                	}
			printStringArrInVector();
		}
                catch (Exception e)
                {
			System.out.println("error : " + e.getMessage());
                }
		
	}
	/*-------------------------------------------------*/
	void searchData() throws Exception
	{
System.out.println("submitNoArr.length = " + submitNoArr.length);
System.out.println("rpNoArr.length = " + rpNoArr.length);
		for (int i = 0; i < submitNoArr.length ; i++)
		{
		    System.out.println("submitNoArr[" + i + "] = " + submitNoArr[i] + " :    rpNoArr[" + i + "] = " + rpNoArr[i] + ":      payDate[" + i  + "] = " + payDateArr[i]);
		    searchBySubmitNo(submitNoArr[i], rpNoArr[i], payDateArr[i]);
		}
	}
	/*-------------------------------------------------*/
	void addDataToVector(Record r) throws Exception 
	{
		String  personID = "";
		System.out.println("add" + " -->> ownerSalesID  = " + r.get("ownerSalesID"));
		System.out.println("add1" + " -->> personID = " + personID);
		if (perFile.equal(r.get("ownerSalesID"))) 
		{
		   personID = perFile.get("personID");
		   if (M.itis(personID,'0'))
		   {
			if (strFile.equal(perFile.get("highStrid")))
			    if (strFile.equal(strFile.get("parentStrid")))
			       if (perFile.equal(strFile.get("salesID")))
				   personID = perFile.get("personID");
			       else
			           personID = "-";
			    else
			       personID = "-";
			else
			    personID = "-";
		   }   	
		}
		System.out.println("add2" + " -->> personID = " + personID);
		if (personID.trim().length() == 0  && operFile.equal(r.get("ownerSalesID"))) 
		   personID = operFile.get("personID") + "(N)";
		System.out.println("add3" + " -->> personID = " + personID);
		v.addElement(new String[] {periodAccount(r.get("payDate")),r.get("submitNo"),r.get("rpNo"),personID});
		System.out.println("add4" + " -->> personID = " + personID);
	}
        /*----------------------------*/
	void searchBySubmitNo(String submitNo, String rpNo,String payDate) throws Exception  // dsubName(String submitNo, String rpNo)
	{ 
		Mrecord dsubFile = null;
		dsubFile = CFile.opens("dsubmit@cbranch");
		boolean ok = dsubFile.equalGreat(submitNo + rpNo);
System.out.println("fileName#1 = " + dsubFile.name());
		while (dsubFile.get("submitNo").compareTo(submitNo) == 0 && dsubFile.get("rpNo").compareTo(rpNo) == 0 && ok)
		{
		   if (dsubFile.get("tempPolicyNo").compareTo(policyNo) == 0) 
		   {
System.out.println("found data in file --> " + dsubFile.name());
		      addDataToVector(dsubFile.copy());
		      dsubFile.close();
		      return;
		   }   
		   ok = dsubFile.next();
		}
		dsubFile.close();
System.out.println(Integer.parseInt(DateInfo.sysDate().substring(2,4)));	
		
                String yy = DateInfo.sysDate().substring(2,4);
		int check = 0;
                while (check < 3)
                {
System.out.println("dsubmit" + yy + "@cbranch");
		     if(!CFile.isFileExist("dsubmit" + yy + "@cbranch"))
	             {
			 check++;
			 yy = M.dec(yy);
			 continue;
		     }  		 
System.out.println("to dsubmityy@cbranch");
                     dsubFile = CFile.opens("dsubmit" + yy + "@cbranch");
		     ok = dsubFile.equalGreat(submitNo + rpNo);
		      /*----------------------------*/
                      while (dsubFile.get("submitNo").compareTo(submitNo) == 0 && 
		             dsubFile.get("rpNo").compareTo(rpNo) == 0 && ok)
                      {
                         if (dsubFile.get("tempPolicyNo").compareTo(policyNo) == 0)
                         {
System.out.println("found data in file --> " + dsubFile.name());
		            addDataToVector(dsubFile.copy());
		      	    dsubFile.close();
		      	    return;
                         }
                         ok = dsubFile.next();
                      }
                      dsubFile.close();
		      yy = M.dec(yy);
		}
		v.addElement(new String[] {periodAccount(payDate),submitNo,rpNo,"-"});
	}
	/*----------------------------------------------------------*/
        String periodAccount(String payDate)
        {
        	String s = "00000000";
        	if (!M.itis(payDate, '0'))
        	{
           		String lastDay = payDate.substring(4, 6).compareTo("02") == 0 ? "28" : "30";
           		s = payDate.substring(6, 8).compareTo("15") <= 0 ? payDate.substring(0, 6) + "15" : payDate.substring(0, 6) + lastDay;
        	}
        	return (s);
   	}
	/*----------------------------------------------------------*/
        void printStringArrInVector()
        {
                System.out.println("===============Start print===========================");
                for (int i = 0 ; i < v.size() ; i++)
                {
                        sArr = (String []) v.elementAt(i);
                        System.out.println("sArr[0] = " + sArr[0] + " : sArr[1] = " + sArr[1] + " : sArr[2] = " + sArr[2] +
					   " : sArr[3] = " + sArr[3]);
                }
                System.out.println("=================End print===========================");
        }

}
