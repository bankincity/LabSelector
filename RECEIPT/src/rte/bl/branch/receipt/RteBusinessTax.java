package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
public class RteBusinessTax  implements Task
{
	    /* flag :  * -  service
                                        A - interest of premium
                                        B - general income
                                        C - interest of officer's loan
                                        D - interest of bank                    */
        String[] afield = {"sdate","flag","receipt","amount"};
        int []alen = {8,1,20,12};
        TempMasicFile actx;
        TempMasicFile delactx;
        Mrecord  iext1;
        Mrecord  iext2;
        Mrecord oext1;
        Mrecord oext2;
        Mrecord wext1;
        Mrecord wext2;
        Mrecord iret1;
        Mrecord iret2 ;
        Mrecord oret1;
        Mrecord oret2 ;
        Mrecord wret1;
        Mrecord wret2 ;
	
	Mrecord receipt;
	Mrecord atran;
	Mrecord adet;
	Mrecord rtran;

 	String yyyymm; 
	String branch;
 	public Result execute(Object param)
	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {branch,yyyymm}",-1);
 		Object [] parameter = (Object []) param;
 		branch = (String)parameter[0];
 		yyyymm = (String)parameter[1];
 		try 
		{
			String period1 = yyyymm+"15";
           //   		String period2  = yyyymm+endPeriodOfMonth(yyyymm);
              		String period2  = yyyymm+"30";
			
			actx = new TempMasicFile ("rptbranch",afield,alen);
                        actx.keyField(true,false,new String[]{"sdate","flag","receipt"});
			actx.buildTemp();

			delactx = new TempMasicFile ("rptbranch",afield,alen);
                        delactx.keyField(true,false,new String[]{"sdate","flag","receipt"});
			delactx.buildTemp();

			iext1 =  CFile.opens("lnextni"+period1+"@srvservice");
			oext1 =  CFile.opens("lnextno"+period1+"@srvservice");
			wext1 =  CFile.opens("lnextnw"+period1+"@srvservice");

			iext2 =  CFile.opens("lnextni"+period2+"@srvservice");
			oext2 =  CFile.opens("lnextno"+period2+"@srvservice");
			wext2 =  CFile.opens("lnextnw"+period2+"@srvservice");
			
			iret1 = CFile.opens("lnrtrni"+period1+"@srvservice");
			oret1 = CFile.opens("lnrtrno"+period1+"@srvservice");
			wret1 = CFile.opens("lnrtrnw"+period1+"@srvservice");
			
			iret2 = CFile.opens("lnrtrni"+period2+"@srvservice");
			oret2 = CFile.opens("lnrtrno"+period2+"@srvservice");
			wret2 = CFile.opens("lnrtrnw"+period2+"@srvservice");
			
			receipt = CFile.opens("rcpt"+period1.substring(2,6)+"@accountbranch");
			atran = CFile.opens("atrn"+period2.substring(2,6)+"@accountbranch");
			adet = CFile.opens("adet"+period2.substring(2,6)+"@accountbranch");
			rtran = CFile.opens("rtrn"+period2.substring(2,6)+"@accountbranch");

			getFromLoan();
			System.out.println("before interest of premium");
			getInterestOfPremium();
			System.out.println("after  interest of premium");
			getAcTax();
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
		return new Result(actx.name(),0);
	}
	private void getFromLoan() throws Exception
	{
		System.out.println("get loan.............................");
		getExtend(iext1);
                getExtend(iext2);
                getExtend(oext1);
                getExtend(oext2);
                getExtend(wext1);
                getExtend(wext2);
                getReturn(iret1);
                getReturn(iret2);
                getReturn(oret1);
                getReturn(oret2);
                getReturn(wret1);
                getReturn(wret2);
		System.out.println("after get loan.............................");
                getInterestOfLoan(); // from other Branch  , get  for this Branch
		System.out.println("after get other loan.............................");

	}
	 // form other Branch
        private void getInterestOfLoan() throws Exception
        {
                        receipt.start(1);
                        for (boolean st = rtran.equalGreat(branch);st && branch.compareTo(rtran.get("branch")) == 0;st = rtran.next())
                        {
                                if ("4401.02".compareTo(rtran.get("accode")) == 0 && rtran.get("dcflag").charAt(0) == 'C')
                                {
                                        if (receipt.equal(branch+rtran.get("rcp_no")))
                                        {
                                                if (receipt.get("det_type").charAt(0) == 'T' && receipt.get("post_flag").charAt(0) == 'Y')
                                                {
							if (rtran.get("rcp_no").trim().length()  > 0 )
							{
								if (actx.equal(receipt.get("rcp_date")+"*"+rtran.get("rcp_no")))
									continue;								
							}			
                                                        actx.set("flag","*");
                                        		actx.set("amount",M.setlen(M.undot(rtran.get("amount")),12));
                                                        actx.set("sdate",receipt.get("rcp_date"));
					
							if(receipt.get("rcp_no").trim().length() > 8)
                                              			 actx.set("receipt",receipt.get("rcp_no").substring(0,8));
							else
                                        	      		 actx.set("receipt",receipt.get("rcp_no"));
                                               //         actx.set("receipt",receipt.get("rcp_no").substring(0,8));
                                                        actx.insert();
                                                }
                                        }
                                }
                        }
                        receipt.start(0);
        }
	private void getExtend(Mrecord exttemp) throws Exception
        {
		exttemp.start(1);
		for (boolean st = exttemp.equalGreat(branch);st && branch.compareTo(exttemp.get("submitBranch")) == 0 ;st = exttemp.next())
		{
		//	if (exttemp.get("print").charAt(0) == '1')
		//	{
			actx.set("flag","*");
			actx.set("sdate",exttemp.get("extendDate" ));
			actx.set("receipt",exttemp.get("rcpNo" ));
			actx.set("amount",M.setlen(exttemp.get("interest")+"00",12));
			actx.insert();
		//	}
		}
        }
	private void getReturn(Mrecord  rettemp) throws Exception
        {
		rettemp.start(1);
                for (boolean st = rettemp.equalGreat(branch); st && branch.compareTo(rettemp.get("submitBranch")) == 0 ;st = rettemp.next())
                {
		 	if(rettemp.get("returnDate").compareTo("25520416") <= 0)				
                      		 if (rettemp.get("reserve").charAt(0) !='N')
			 		continue;
			if(M.cmps(rettemp.get("interest"),"0") > 0 )
                        {
                                 actx.set("flag","*");
                                 actx.set("sdate",rettemp.get("returnDate"));
                                 actx.set("receipt",rettemp.get("rcpNo"));
                                 actx.set("amount",M.setlen(rettemp.get("interest")+"00",12));
                                 actx.insert();
                        }
                }
        }
	private void getInterestOfPremium() throws Exception
        {
		receipt.start(1);
		for (boolean st = rtran.equalGreat(branch);st && branch.compareTo(rtran.get("branch")) == 0;st = rtran.next())
		{
			if ("4603.02".compareTo(rtran.get("accode")) == 0 && rtran.get("dcflag").charAt(0) == 'C')
			{
				if (receipt.equal(branch+rtran.get("rcp_no")))
				{
					if ( receipt.get("post_flag").charAt(0) == 'Y')
					{
						actx.set("flag","A");
                                        	actx.set("amount",M.setlen(M.undot(rtran.get("amount")),12));          
						actx.set("sdate",receipt.get("rcp_date"));
						if(receipt.get("rcp_no").trim().length() > 8)
                                              		 actx.set("receipt",receipt.get("rcp_no").substring(0,8));
						else
                                              		 actx.set("receipt",receipt.get("rcp_no"));
                                                actx.insert();
					}
				}
			} // FOR UL : รายได้ค่าธรรม์เนียมต่อสัญญา และ ค่าธรรมเนียมการแจ้งยอดบัญชี
	/*else if ("4619.01".compareTo(rtran.get("accode")) == 0 && rtran.get("dcflag").charAt(0) == 'C')
			{
				if (receipt.equal(branch+rtran.get("rcp_no")))
				{
					if ( receipt.get("post_flag").charAt(0) == 'Y')
					{
						actx.set("flag","U");
                                        	actx.set("amount",M.setlen(M.undot(rtran.get("amount")),12));          
						actx.set("sdate",receipt.get("rcp_date"));
						if(receipt.get("rcp_no").trim().length() > 8)
                                              		 actx.set("receipt",receipt.get("rcp_no").substring(0,8));
						else
                                              		 actx.set("receipt",receipt.get("rcp_no"));
                                                actx.insert();
					}
				}
			}//FOR UL : ดอกเบี้ยเงินกู้ตามกรมธรรม์
			else if ("4514.01".compareTo(rtran.get("accode")) == 0 && rtran.get("dcflag").charAt(0) == 'C')
			{
				if (receipt.equal(branch+rtran.get("rcp_no")))
				{
					if ( receipt.get("post_flag").charAt(0) == 'Y')
					{
						actx.set("flag","V");
                                        	actx.set("amount",M.setlen(M.undot(rtran.get("amount")),12));          
						actx.set("sdate",receipt.get("rcp_date"));
						if(receipt.get("rcp_no").trim().length() > 8)
                                              		 actx.set("receipt",receipt.get("rcp_no").substring(0,8));
						else
                                              		 actx.set("receipt",receipt.get("rcp_no"));
                                                actx.insert();
					}
				}
			}*/
			
		
		}
                receipt.start(0);
        }
	public void getCOI_PF_PWD(String month,String branch) throws Exception 
	{
		Mrecord ulac = CFile.opens("ulac"+month+"@universal");
		for (boolean st =ulac.great(branch);st && branch.compareTo(ulac.get("submitBranch")) == 0;st=ulac.next())
		{
			if (ulac.get("transactionType").compareTo("COI") == 0 || 
				ulac.get("transactionType").compareTo("FEE") == 0 || 
				ulac.get("transactionType").compareTo("PWD") == 0 || 
				ulac.get("transactionType").compareTo("OTH") == 0 || 
				ulac.get("transactionType").compareTo("SUR") == 0 )
			{
				
				if (ulac.get("dcFlag").charAt(0) == 'C')
				{		
					if (ulac.get("acType").compareTo("461") == 0 || 
						ulac.get("acType").compareTo("462") == 0 || 
						ulac.get("acType").compareTo("465") == 0)
					{
						actx.set("flag","V");
                                	        actx.set("amount",M.setlen(M.undot(ulac.get("amount")),12)); 
						actx.set("sdate",ulac.get("transactionDate"));
						actx.set("receipt"," ");
                                	        actx.insert();
					}
					else if (ulac.get("acType").compareTo("451") == 0)
					{
						actx.set("flag","U");
                                	        actx.set("amount",M.setlen(M.undot(ulac.get("amount")),12)); 
						actx.set("sdate",ulac.get("transactionDate"));
						if (ulac.get("submitNo").length() >= 11)
							actx.set("receipt",ulac.get("submitNo").substring(3,11));
						else
							actx.set("receipt"," ");
                                	        actx.insert();
						
					}
					else if (ulac.get("acType").compareTo("467") == 0 || ulac.get("acType").compareTo("466") == 0)
					{
						actx.set("flag","V");
                                	        actx.set("amount",M.setlen(M.undot(ulac.get("amount")),12)); 
						actx.set("sdate",ulac.get("transactionDate"));
						if (ulac.get("submitNo").length() >= 11)
							actx.set("receipt",ulac.get("submitNo").substring(3,11));
						else
							actx.set("receipt"," ");
                                	        actx.insert();						
					}
				}
			}
		}
	}
	public String endPeriodOfMonth(String mm)
        {
                    switch  (M.ctoi(mm.substring (4,6)))
                    {
				case 1:
                                case 3:
                                case 5:
                                case 7:
                                case 8:
                                case 10:
                                case 12:return "31";
                                case  4:
                                case  6:
                                case  9:
                                case  11:return "30";
                                case 2 :if (DateInfo.leapYear(M.ctoi(mm.substring (0,4))))
                                	return "29";
                        }
                        return "28";
        }
	private String getRpDet(String docdate,String docno)
        {
                 if (adet.equal(branch+docdate+docno))
		 {
			if(adet.get("rcp_no").trim().length() > 8)
                  	       return adet.get("rcp_no").substring(0,8);
			else
                  	       return adet.get("rcp_no");
					
		}
                else
                                return "00000000";
        }
	 private void getAcTax() throws Exception
        {
                 boolean insert  = false;
                 String flagac = " " ;
                 String startdate = yyyymm+"01";
                 String enddate = yyyymm+endPeriodOfMonth(yyyymm);
                 for (boolean st = atran.equalGreat(branch) ; st && branch.compareTo(atran.get("branch")) == 0 ; st = atran.next())
                 {
                                insert = false;
                                if (startdate.compareTo(atran.get("docdate")) <=0 && enddate.compareTo(atran.get("docdate"))  >=0)
                                {
                                     //   if (atran.get("dcflag").charAt(0) == 'C')
                                        if ("CD".indexOf(atran.get("dcflag").charAt(0)) >= 0)
					{
                                    		if ("4604.02".compareTo(atran.get("accode")) == 0)
                                      		{
                                              		  flagac = "B";
                                             		  insert = true;
                                       		}
                                    		else if ("4402.02".compareTo(atran.get("accode")) == 0 ||  "4403.02".compareTo(atran.get("accode")) == 0 )
                                        	{
                                                	 flagac = "C";
                                              		 insert = true;
						}
						else if ("4502.02".compareTo(atran.get("accode")) == 0)
                                        	{
                                               		 flagac = "D";
                                              		 insert = true;
                                       		 }
			/*			else if ("4514.01".compareTo(atran.get("accode")) == 0)
						{
                                               		 flagac = "U";
                                              		 insert = true;
						}
						else if ("4619.01".compareTo(atran.get("accode")) == 0)
						{
                                               		 flagac = "V";
                                              		 insert = true;
						}*/
					}
					
                                }
                                if (insert)
                                {
                                        actx.set("flag",flagac);
                                        actx.set("sdate",atran.get("docdate"));
                                        actx.set("receipt",getRpDet(atran.get("docdate"),atran.get("docno")));
                                        actx.set("amount",M.setlen(M.undot(atran.get("amount")),12));
					if(atran.get("dcflag").charAt(0) == 'C')			
						actx.insert();
					else {
						delactx.putBytes(actx.getBytes());
						delactx.insert();
					}
                                }
                }
		getCOI_PF_PWD(yyyymm,branch);
        }
 }


