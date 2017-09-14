package rte.bl.branch.receipt;
import manit.*;
import rte.bl.branch.TempMasicFile ;
public class TestUCRPYYYYMM
{
	public static void main(String [] args) throws Exception
	{
		UCRPYYYYMM uc = new UCRPYYYYMM();
//		TempMasicFile temp = uc.searchUCRPToSai("T","255308","1");
		TempMasicFile temp = uc.getForUnclear("255605","212","LOO5S","G12254G") ;
		for (boolean st = temp.first();st;st=temp.next())
		{
			System.out.println(new String(temp.copy().getBytes()));
		}
	}
}
