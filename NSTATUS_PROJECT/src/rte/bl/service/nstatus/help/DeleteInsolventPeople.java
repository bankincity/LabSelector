package rte.bl.service.nstatus.help;
import manit.*;
public class DeleteInsolventPeople
{
	public static void main(String [] args) throws Exception
	{
		Mrecord rec = Masic.opens("insolventpeople@srvservice");
		for (boolean st = rec.equal(args[0]);st && args[0].compareTo(rec.get("dataDate")) == 0;st=rec.next())
		{
			rec.delete();
		}
	}
}
