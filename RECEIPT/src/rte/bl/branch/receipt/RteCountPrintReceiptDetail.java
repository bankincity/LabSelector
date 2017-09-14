package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import java.util.*;
import rte.bl.branch.*;
import utility.rteutility.*;
import utility.cfile.*;
public class RteCountPrintReceiptDetail implements Task
{
        String [] field = {"typePol","rpNo","policyNo","payPeriod","premium","statusDate","currentStatus","detail"};
        int [] len = {1,12,8,6,9,8,1,120};
	TempMasicFile temptoprint ;
	public Result execute(Object param)
	{
		if(! (param instanceof String  []))
 			return new Result("Invalid Parameter  : Object [] {branch,printDate,String typeOfPol}",-1);
 		String [] parameter = (String []) param;
  		String branch = (String)parameter[0];
		String printDate = (String)parameter[1];
		String [] typeOfPol ;
                if(parameter[2].charAt(0) == 'O')
                        typeOfPol = new String [] {"O","W"};
                else if(parameter[2].charAt(0) == 'I')

                        typeOfPol = new String [] {"I"};
                else if(parameter[2].charAt(0) == 'B')

                         typeOfPol = new String [] {"I","O","W","U","L"};
                else
                        typeOfPol = new String [] {" "};

		try {
			temptoprint  = new TempMasicFile ("rptreceipt",field,len);
			temptoprint.keyField(false, false, new String[] {"typePol","rpNo"});
        	        temptoprint.buildTemp();

			for (int i = 0 ; i < typeOfPol.length ; i++)
			{
				getAllRp(typeOfPol[i],branch,printDate);
			}
		}
		catch(Exception e)
		{
			return new Result(e.getMessage(),2);	
		}
		return new Result(temptoprint.name(),0);	
 	}
	Mrecord resrange;
	Mrecord rpprintqueue;
	Mrecord rp;
        public void  getAllRp(String typeOfPol,String branch,String printDate) throws Exception
	{
                if(typeOfPol.charAt(0) == 'I')
                        rp = CFile.opens("irctrl@receipt");
                else if(typeOfPol.charAt(0) == 'O')
                        rp = CFile.opens("orctrl@receipt");
                else if(typeOfPol.charAt(0) == 'W')
                        rp = CFile.opens("wrctrl@receipt");
                else if(typeOfPol.charAt(0) == 'U')
                        rp = CFile.opens("ulrctrl@universal");
                else if(typeOfPol.charAt(0) == 'L')
                        rp = CFile.opens("uliprctrl@unitlink");
                resrange = CFile.opens("reservedrange@receipt");
		rpprintqueue = CFile.opens("rpprintqueue@bra"+branch+"app");
                searchData(typeOfPol,branch,printDate);

        }
	 public void searchData(String rpFlag,String branch,String printDate) throws Exception
        {
                if (!rp.start(2))
                {       
	                  throw new Exception(M.stou("ไม่สามารถ start key 2 ของแฟ้มคุมใบเสร็จได้ Erroor :")+M.itoc(rp.lastError()));
                }
                String keysearch = printDate+branch;
         //     Msg.msg(new Mframe(""),keysearch);
                for (boolean st = rp.great(keysearch);st;st=rp.next())
                {
                        if(keysearch.compareTo(rp.get("printedDate")+rp.get("rpNo").substring(0,3)) != 0)
                                break;
                        if(temptoprint != null)
                        {
                                temptoprint.set(' ');
                                temptoprint.set("typePol",rpFlag);
                                temptoprint.set("rpNo",rp.get("rpNo"));
                                temptoprint.set("policyNo",rp.get("policyNo"));
                                temptoprint.set("payPeriod",rp.get("payPeriod"));
                                temptoprint.set("premium",M.setlen(rp.get("premium"),9));
                                temptoprint.set("currentStatus",rp.get("currentStatus"));
                                temptoprint.set("statusDate",rp.get("sysDate"));
				if("NWAX".indexOf(rp.get("currentStatus")) >= 0)
				{
					if(rp.get("requestDate").compareTo("00000000") != 0)
					{				
	                                	temptoprint.set("currentStatus","S");
						temptoprint.set("statusDate",rp.get("requestDate"));
					}
				}	
                                if(checkReserve(rpFlag,rp.get("rpNo")))
                                        temptoprint.set("detail",M.stou("ใบเสร็จสำรอง"));
				else
					temptoprint.set("detail",getReqRpDetail(rpFlag,rp.get("policyNo"),rp.get("payPeriod"),rp.get("rpNo")));
                                temptoprint.insert();
                        }
                }
                rp.start(0);
                if (rpFlag.charAt(0) == 'O')
                {
                        Mrecord aplrc = Masic.opens("aplrctrl@mstscan");
                        for (boolean st = rp.great("A");st && rp.get("rpNo").charAt(0) == 'A';st=rp.next())
                        {
                              //  if (aplrc.equal(rp.get("rpNo")))
                              //  {
                                    if (rp.get("printedDate").compareTo(printDate) == 0 && branch.compareTo(rp.get("submitBranch")) == 0)
                                    {

                                         if(temptoprint != null)
                                         {
                                                temptoprint.set(' ');
                                                temptoprint.set("typePol",rpFlag);
                                                temptoprint.set("rpNo",rp.get("rpNo"));
                                                temptoprint.set("policyNo",rp.get("policyNo"));
                                                temptoprint.set("payPeriod",rp.get("payPeriod"));
                                                temptoprint.set("premium",M.setlen(rp.get("premium"),9));
                                                temptoprint.set("currentStatus",rp.get("currentStatus"));
                                                temptoprint.set("statusDate",rp.get("sysDate"));
                                                if("NWAX".indexOf(rp.get("currentStatus")) >= 0)
                                                {
                                                        if(rp.get("requestDate").compareTo("00000000") != 0)
                                                        {
                                                                temptoprint.set("currentStatus","S");
                                                                temptoprint.set("statusDate",rp.get("requestDate"));
                                                        }
                                                }
                                                if(checkReserve(rpFlag,rp.get("rpNo")))
                                                        temptoprint.set("detail",M.stou("ใบเสร็จสำรอง"));
                                                else
                                                        temptoprint.set("detail",getReqRpDetail(rpFlag,rp.get("policyNo"),rp.get("payPeriod"),rp.get("rpNo")));
                                                temptoprint.insert();
                                        }
                                   }    
                               // }
                        }
                }

        }
	private String getReqRpDetail(String typePol,String policyNo,String payPeriod,String rpNo)
	{
		if(payPeriod.trim().length() == 4)
			payPeriod = payPeriod+"  ";
		if(rpprintqueue.equal("0"+typePol+policyNo+payPeriod+rpNo))
		{
			return (M.stou("ใบเสร็จใน Queue โดย ")+rpprintqueue.get("userName")+"  จน.ครั้งที่สั่งพิมพ์ "+rpprintqueue.get("timeToPrint")); 
		}
		return M.stou("ใบเสร็จกำหนดพิมพ์");
	}
        boolean checkReserve(String type,String rpNo)
        {
                if(type.charAt(0) == 'W')
                        type = "O";
                if(resrange.less(type+rpNo))
                {
			  if(rpNo.compareTo(resrange.get("startRange")) >= 0 && rpNo.compareTo(resrange.get("endRange")) <= 0)
                                return true;
                }
                if(resrange.equal(type+rpNo))
                        return true;
                return false;
        }
}
