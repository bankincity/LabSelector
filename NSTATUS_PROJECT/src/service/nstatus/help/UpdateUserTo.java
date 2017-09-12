package service.nstatus.help;
import manit.*;
public class UpdateUserTo
{
	public static void main(String [] args)
	{
		Mrecord rec  = Masic.opens("insolventtran@srvservice");
		for (boolean st = rec.first();st;st=rec.next())
		{
			rec.set("userID","9003986");
			rec.update();
		}	
	}
}
