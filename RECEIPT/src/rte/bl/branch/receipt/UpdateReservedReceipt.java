package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class UpdateReservedReceipt 
{
	public  UpdateReservedReceipt(String typeOfPol ,String month,String userID)
	{
		int countRp = 0 ;
		String sfname = typeOfPol.toLowerCase()+"rpserved";
		try {
			if(Masic.fileStatus(sfname+month+"@cbranch") < 2)
			{
				if(Masic.fileStatus(sfname+month+"@cbranch") < 2)
				{
					throw new Exception(M.stou("ไม่สามรถเปิดแฟ้มข้อมูลการสำรองใบเสร็จได้ ")+sfname);
				}
				
			}
			Mrecord temp = CFile.opens(sfname+month+"@cbranch");
			Vector rpprint = new Vector();
			Receipt rp = new Receipt();
			for (boolean st = temp.first();st;st=temp.next())
			{
				rpprint.addElement(temp.get("rpNo"));
			}
			System.out.println("update ........"+rpprint.size());
			Vector rperr = rp.alreadyPrintRpForm(rpprint,month,typeOfPol,userID);
			
		}
		catch (Exception e)
		{
			System.out.println("receipt.................."+e.getMessage());
		}
	}
	public static void main(String [] args)
	{
		new UpdateReservedReceipt(args[0],args[1],args[2]);
	}
}
