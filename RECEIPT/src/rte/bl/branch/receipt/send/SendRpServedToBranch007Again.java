import manit.*;
import rte.bl.branch.receipt.ReceiptAgain;
import utility.support.DateInfo;
import utility.cfile.*;
public class SendRpServedToBranch007Again
{
	
	public static void main(String [] args ) 
	{
		if(args.length < 2 )
		{
			System.out.println("Usage   : <month>  branch typeOfPol");
			System.exit(99);
		}
		String branch = "";
		String typeOfPol = "";
		String month = "";
		String sai = "";
		try {
			String sdate = DateInfo.sysDate();
			if(args.length == 3)
			{
				month = args[0];
				branch = args[1];
				typeOfPol = args[2];
			}
			else {
				branch = args[0];
				typeOfPol = args[1];
				month = DateInfo.nextMonth(sdate).substring(0,6);
			}
                        System.out.println("branch===="+branch);	
			if (branch.trim().length() == 4)
			{
				sai = branch.substring(3,4);
				branch = branch.substring(0,3);
			}
                        System.out.println("build branch===="+branch+"  "+sai);	
			String fileName = typeOfPol.toLowerCase()+"rp"+sdate+"."+branch+sai+"@sendreceipt";
	
			Mrecord srec = CFile.openbuild(fileName);
			Mrecord crec = CFile.openbuild(sdate+".send@sendreceipt");
			rte.bl.branch.receipt.ReceiptAgain  receipt = new  rte.bl.branch.receipt.ReceiptAgain(typeOfPol,sdate.substring(2,4));
			int ret = receipt.sendToPrint(month,branch+sai,fileName);
			if(ret  == -1)
			{
				Masic.purge(fileName);
				Masic.remove(fileName);	
				System.exit(1);
			}
			else if (ret == 0)
			{
				Masic.purge(fileName);
				Masic.remove(fileName);	
				if(typeOfPol.charAt(0) == 'O')
					typeOfPol = "I";		
				else 
					typeOfPol = "O";
				
				fileName = typeOfPol.toLowerCase()+"rp"+sdate+"."+branch+sai+"@sendreceipt";
				srec = CFile.openbuild(fileName);
				receipt = new  rte.bl.branch.receipt.ReceiptAgain(typeOfPol,sdate.substring(2,4));
				ret = receipt.sendToPrint(month,branch+sai,fileName);
						
			}	
			if(ret  > 0)
			{
				crec.set(' ');
				crec.set("branch",branch);
				if (sai.trim().length() == 1)
					crec.set("blank",sai+"       ");
				crec.set("typerp",typeOfPol);			
				crec.set("newline","\n");			
				crec.insert();
				crec.close();
			}
			else {
				Masic.purge(fileName);
				Masic.remove(fileName);	
				System.exit(1);
			}
			System.exit(0);
		}
		catch(Exception e)
		{
			System.out.println("Error  : "+e.getMessage());
			System.exit(99);
		}
	}
}
