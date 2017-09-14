package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteTReceiptToAskOtherBranch  implements Task
{
	Mrecord trptmp;
	Mrecord trprange;
	Mrecord trptmptakaful;
	Mrecord trptakafulrange;
	Mrecord trp;
	Mrecord ask;
	Mrecord ucrp;
	Mrecord cask;
	Mrecord caskother;

	String userID;
	String sysdate;
	String branch;
	String yyyymm;
	String ucrpyyyymm  = "";
	String saleID ;
	String askSaleID;
	String requestDate;
        String salesBranch;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {yyyymm,ucrpyyyymm,userID,branch,saleID,askSaleID,requestDate}",-1);
		Object [] parameter = (Object []) param;
		int countRp = 0 ;
		int countTakaful = 0 ;
		yyyymm = (String)parameter[0];
		ucrpyyyymm = (String)parameter[1];
		userID =(String)parameter[2];
		branch = (String)parameter[3];
		saleID = (String)parameter[4];
		askSaleID = (String)parameter[5]; // if askSaleID== " " not insert To Cask
		requestDate = (String)parameter[6];
		countRp = M.ctoi((String)parameter[7]);
		countTakaful = M.ctoi((String)parameter[8]);
                salesBranch =  (String)parameter[9];
		try {
			ask = CFile.opens("ask"+yyyymm+"@cbranch");
			if(askSaleID.trim().length() == 10)
                        {
				cask = CFile.opens("cask"+yyyymm+"@cbranch");
                                caskother = CFile.openbuild("caskother"+yyyymm+"@receipt");
                        }
			else
				askSaleID = saleID;
			if(ucrpyyyymm.trim().length() > 0)
                        {
                                try {
				        ucrp = CFile.opens(ucrpyyyymm+"@cbranch");
                                }
                                catch (Exception e)
                                {
                                        ucrp = null;
                                }
                        }
			trp = CFile.opens("trpctrl@receipt");

			trptmp = CFile.opens("trptmp@cbranch");
			trptmptakaful = CFile.opens("trptmptakaful@cbranch");

			trprange = CFile.opens("trprange@receipt");
			trptakafulrange = CFile.opens("trptakafulrange@receipt");

			System.out.println("open treceipt suceess.................");
			Result res = searchTReceipt(saleID,requestDate,countRp,countTakaful);	
			System.out.println("search treceipt suceess.................");
			insertAskAndUpdateTrp((Vector)res.value());			
			System.out.println("insert  treceipt suceess.................");
			return (res);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),2);
		}
	}
	private void insertAskAndUpdateTrp(Vector vec) throws Exception
	{
                Mrecord trpfrombranch = CFile.openbuild("trpfrombranch@receipt"); 
		System.out.println("vec of treceipt ======="+vec.size()+"  "+branch+"  "+askSaleID+"  "+saleID+"  "+requestDate);
		ask.set("branch",salesBranch);
		ask.set("askSaleID",askSaleID);
		ask.set("ownerSaleID",saleID);
		ask.set("requestDate",requestDate);
		ask.set("receiptFlag","T");
		System.out.println("vec of treceipt ============="+vec.size());
		for (int i =0 ; i < vec.size();i++)
		{
			ask.set("rpNo",(String)vec.elementAt(i));
			ask.insert();
			if(ucrp != null)
			{
				ucrp.putBytes(ask.copy().getBytes());
				ucrp.insert();
			}
                        
                        trpfrombranch.set("trpNo", ask.get("rpNo"));
                        trpfrombranch.set("ownerBranch", branch);
                        trpfrombranch.set("startUseDate", requestDate);
                        trpfrombranch.insert();
			System.out.println("insert ask ............................");
			updateTrpCtrl(ask.get("rpNo"),requestDate);
		}
		if(cask != null)
		{
			if(vec.size() == 0)
				return ;
			cask.set("askSaleID",askSaleID);
			cask.set("ownerSaleID",saleID);
			cask.set("branch",branch);
			cask.set("requestDate",requestDate);
			cask.set("userID",userID);
			cask.set("status","N");
			cask.set("reserve","O");
			cask.insert();
                        caskother.set("askSaleID",askSaleID);
                        caskother.set("requestDate",requestDate);
                        caskother.set("salesBranch",salesBranch);
                        caskother.insert();

                
		}
	}	
	private void updateTrpCtrl(String rpno,String reqDate) throws Exception
	{
		boolean found = false ;
		for (boolean st = trp.equal(rpno) ; st && rpno.compareTo(trp.get("rpNo")) == 0 ;st = trp.next())
		{
		        trp.set("requestDate",reqDate);
			trp.set("askUserID",userID);
                        trp.set("branch",salesBranch);
			trp.update();
			found = true;
		}
		if(!found)
		{
			trp.set('0');
			trp.set("rpNo",rpno);
			trp.set("requestDate",reqDate);
			trp.set("statusDate",reqDate);
			trp.set("askUserID",userID);
			trp.set("currentStatus","N");
			trp.set("originalStatus","N");
			trp.set("branch",salesBranch);
			trp.set("branchFlag"," ");
			trp.insert();			
		}
	}
	int ret;
	Vector tReceipt;
	public Result searchTReceipt(String saleID,String reqDate,int noOfrp,int countTakaful) throws Exception
	{
		tReceipt = new Vector();
		System.out.println("count --------"+countTakaful+"-------------"+noOfrp);
		if(noOfrp > 0)
			getTrpAndTakaful(saleID,reqDate,noOfrp,trptmp,trprange);
		if(countTakaful > 0)
			getTrpAndTakaful(saleID,reqDate,countTakaful,trptmptakaful,trptakafulrange);
		System.out.println(" treceipt ................"+tReceipt.size());
		return (new Result(tReceipt,0));
	}
	public void getTrpAndTakaful(String saleID,String reqDate,int noOfrp,Mrecord trptmp,Mrecord trprange)
	{
		int count = 0 ;
		boolean st = trptmp.great(branch);
                  // get   from  trptmpxx  where return date = sysdate
		while (st && branch.compareTo(trptmp.get("branch")) == 0){
			if (trptmp.get("ownerSaleID").compareTo(saleID) == 0 && 
				trptmp.get("returnDate").compareTo(reqDate) ==0  &&
				trptmp.get("userID").compareTo(userID) == 0 ){
				tReceipt.addElement(trptmp.get("rpNo"));
				count++;
				trptmp.delete();
			}
			st = trptmp.next();
			if (count  >= noOfrp)
	//			return (new Result(tReceipt,0));
				return ;
		}
                   // get   from  trptmpxx  where return date < sysdate
		st = trptmp.great(branch);
		while (st && branch.compareTo(trptmp.get("branch")) == 0){
			if(trptmp.get("ownerSaleID").compareTo(saleID) == 0 &&
				trptmp.get("returnDate").compareTo(reqDate) < 0){
				tReceipt.addElement(trptmp.get("rpNo"));
				count++;
				trptmp.delete();
			}
			st = trptmp.next();
			if (count  >= noOfrp)
			//return (new Result(tReceipt,0));
				return ;
		}
                  // get   from  trptmpxx  where return date < sysdate && ownderDepNo = "00000"
		st = trptmp.great(branch);
		while (st && branch.compareTo(trptmp.get("branch")) == 0){
			if(trptmp.get("ownerSaleID").compareTo("0000000000") == 0 )
			{
				tReceipt.addElement(trptmp.get("rpNo"));
                                count++;
                                trptmp.delete();
			}
			st = trptmp.next();
			if (count  >= noOfrp)
			//eturn (new Result(tReceipt,0));
				return ;
		}
		//     get    from     trprangexx
		st =  trprange.great(branch+"U");
		if (!st || trprange.get("used").charAt(0) != 'U' || branch.compareTo(trprange.get("branch")) != 0)
		{
		//		return (new Result(tReceipt,1));
			return ;
		}
		String trp;
		String endrp;
		while (st && branch.compareTo(trprange.get("branch")) == 0){
			trp = trprange.get("currentTrp");
			endrp = trprange.get("endTrp");
			tReceipt.addElement(trp);
			count++;
			trp = M.inc(trp);
			trprange.set("currentTrp",trp);
			if (trp.compareTo(endrp) >0){
				trprange.set("used","N");
			}
			trprange.update();
			if (count >= noOfrp)
				break;
			st =  trprange.great(branch+"U");
			if (!st || trprange.get("used").charAt(0) != 'U' || branch.compareTo(trprange.get("branch")) != 0)
			{
			//return (new Result(tReceipt,1));
				return ;
			}
		}
	}
}
