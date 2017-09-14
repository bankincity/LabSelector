package rte.bl.branch.receipt.help;
import manit.*;
public class HelpBranchRpServed
{
public static void main(String [] args) throws Exception
{
	Mrecord ordmast = Masic.opens("ordmast@mstpolicy");
	Mrecord whlmast = Masic.opens("whlmast@mstpolicy");	
	Mrecord orps = Masic.opens("orpserved@bra"+args[0]+"app");
	int count = 0 ;
	int acount = 0 ;
	for (boolean st = orps.first();st;st=orps.next())
	{
		if (ordmast.equal(orps.get("policyNo")))
		{
			if (ordmast.get("branch").compareTo(args[1]) != 0)
			{
			//orps.delete();
				count++;
			}
			else
				acount++;
		}
	}
	System.out.println("count delete === "+count);
	System.out.println("count print === "+acount);
}
}
