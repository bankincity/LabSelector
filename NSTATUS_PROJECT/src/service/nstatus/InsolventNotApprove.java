package service.nstatus;
import java.awt.*;
import manit.*;
import utility.support.DateInfo;
import utility.claim.CM;
public class InsolventNotApprove
{
	public InsolventNotApprove(Mframe frm)
	{
		try {
			Mrecord temp = (Mrecord)InsolventClient.AllNotApprove();
			InsolventNotApproveView view = new  InsolventNotApproveView(temp);
			Mscreen sc  = new Mscreen("");
			Mprint mp = new Mprint("laser");
			sc.setPrinter(mp);
			
			sc.render(view,0,view.getPageCount() - 1);
		}
		catch(Exception e)
		{
			Msg.msg(frm,e.getMessage());
		}
	
	}
	class InsolventNotApproveView extends Mview 
	{
		Font fn14 ;
		Font fn14B;
		Font fn16B;
		InsolventNotApproveView(Mrecord temp)
		{
			fn14 = M.fontSize(M.getFont(),14);
			fn14B = M.fontSize(M.getFont(Font.BOLD),14);
			fn16B = M.fontSize(M.getFont(Font.BOLD),16);
			int line = 0 ;
			int total = 0 ;
			float y = 3.4f;
			for (boolean st = temp.first();st;)
			{
				if (line == 0)
				{
					headTable();
					y = 3.4f;
				}
				setLocation(1.6f,y+0.1f);
				drawText(DateInfo.formatDate(1,temp.get("dataDate")));
				setLocation(3.9f,y+0.1f);
				drawText(temp.get("policyNo"));
				setLocation(6.5f,y+0.1f);
				drawText(temp.get("preName")+temp.get("firstName")+" "+temp.get("lastName"));
				setLocation(12.6f,y+0.1f);
				drawText(CM.editCid(temp.get("idNo")));

				setLocation(1.5f,y);
				lineTo(1.5f,y+0.7f);
				
				setLocation(3.8f,y);
				lineTo(3.8f,y+0.7f);
			
				setLocation(6.4f,y);
				lineTo(6.4f,y+0.7f);

				setLocation(12.5f,y);
				lineTo(12.5f,y+0.7f);

				setLocation(17.0f,y);
				lineTo(17.0f,y+0.7f);

				setLocation(20.495f,y);
				lineTo(20.495f,y+0.7f);
				
				setLocation(1.5f,y+0.7f);
				lineTo(20.495f,y+0.7f);
				line++;
				total++;
				y+=0.7f;
				st = temp.next();
				if(line >= 30)
				{
					line = 0 ;	
					if(!st)
					{
						setFont(fn16B);
						setLocation(17.0f,y+0.3f);
						drawText(M.stou("รวมทั้งสิ้น     ")+M.itoc(total)+ M.stou("     ราย"),RIGHT);
					}
					savePage();
				}
			}
			if(line > 0 )
			{
				setFont(fn16B);
				setLocation(17.0f,y+0.3f);
				drawText(M.stou("รวมทั้งสิ้น     ")+M.itoc(total)+ M.stou("     ราย"),RIGHT);
				savePage();
			}
		}
		void headTable()
		{
			reset();
			setFont(fn16B); 
			setLocation(10.5f,1.9f);
			drawText(M.stou("รายงานกรมธรรม์สถานะ N ที่ยังไม่ได้ตรวจสอบ"),MIDDLE);
			setFont(fn14);
			setLocation(17.5f,1.5f);
			drawText(M.stou("วันที่พิมพ์ ") +DateInfo.formatDate(1,DateInfo.sysDate()));
			setFont(fn14B);
			setLocation(1.5f,2.7f);
			drawRect(19.0f,0.7f);
			
			setLocation(1.6f,2.7f);
			drawText(M.stou("วันที่นำเข้า"));
			
			setLocation(3.8f,2.7f);
			lineTo(3.8f,3.4f);
			
			setLocation(3.9f,2.7f);
			drawText(M.stou("เลขที่กรมธรรม์"));
			
			setLocation(6.4f,2.7f);
			lineTo(6.4f,3.4f);

			setLocation(7.5f,2.7f);
			drawText(M.stou("ชื่อ - สกุล"));
			
			setLocation(12.5f,2.7f);
			lineTo(12.5f,3.4f);
			
			setLocation(12.6f,2.7f);
			drawText(M.stou("เลขประจำตัวประชาชน"));
			
			setLocation(17.0f,2.7f);
			lineTo(17.0f,3.4f);
		
			setLocation(17.2f,2.7f);
			drawText(M.stou("หมายเหตุ"));
			setFont(fn14);
		}
	}
	public static void main(String [] args) throws Exception
	{
		new InsolventNotApprove(new Mframe(""));
	}	
}
