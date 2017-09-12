package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import service.bucket.*;
import utility.support.*;
public class DlgCancelPermonth extends Mdialog
{
/* $$$-0
L0017002600830033lbTestประจำปี
T0100002700950032tfYear
B0028008100820033btOKตกลง
B0117008100780032btCancelยกเลิก
G0180011502240151
---- */
	JLabel			lbTest;
	ISfield			tfYear;
	JButton			btOK;
	JButton			btCancel;

	void panel_0()
	{
		lbTest = new JLabel(M.stou("ประจำปี"));
		lbTest.setBounds(17, 26, 83, 33);
		addcom(lbTest, 0);

		tfYear = new ISfield(4);
		tfYear.setBounds(100, 27, 95, 32);
		addcom(tfYear, 0);

		btOK = new JButton(M.stou("ตกลง"));
		btOK.setBounds(28, 81, 82, 33);
		addcom(btOK, 0);

		btCancel = new JButton(M.stou("ยกเลิก"));
		btCancel.setBounds(117, 81, 78, 32);
		addcom(btCancel, 0);

		setBounds(180, 115, 224, 151);
	}
/* $$$ */
	public DlgCancelPermonth(JFrame parent)
	{
		super(parent, false);
		setTitle("DlgCancelPermonth");
		panel_0();
		setVisible(true);
	}
	public void doButton(Object o)
	{
		try {
			if(o.equals(btOK))
			{
				if(tfYear.getText().trim().length() != 4 || tfYear.getText().compareTo("0000") == 0)
					throw new Exception(M.stou("ระบุปีไม่ถูกต้อง"));
				TempMasicFile temp = InsolventClient.getCancelInsolvent(tfYear.getText());
				StatCancelPermonthView view = new StatCancelPermonthView(temp,tfYear.getText());
				Mscreen scr = new Mscreen("");
				Mprint mp = new Mprint("laser");
				scr.setPrinter(mp);
				scr.render(view,0,view.getPageCount() -1);
			}
			else 
				dispose();
		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
		}
	}
	public class StatCancelPermonthView extends Mview
	{
		Font fn;
		Font fnb;
		Font fn16b;
		public  StatCancelPermonthView(TempMasicFile temp,String year)
		{
			fn = M.fontSize(M.getFont(),16);
			fnb= M.fontSize(M.getFont(Font.BOLD),18);				
			fn16b= M.fontSize(M.getFont(Font.BOLD),16);				
			head(year);
			setFont(fn);
			float y = 5.1f;
			String sumnot = "0";
			String sumcancel = "0";
			String other = "0";
			for (boolean st = temp.first();st; st = temp.next())
			{
				setLocation(3.5f,y);
				drawText(temp.get("month").substring(4)+"/"+temp.get("month").substring(0,4),MIDDLE);
				sumnot = M.addnum(sumnot,temp.get("notSamePerson"));
				sumcancel = M.addnum(sumcancel,temp.get("cancelFromCourt"));
				other  = M.addnum(other,temp.get("other"));
				setLocation(6.87f,y);
				drawText(M.edits(temp.get("notSamePerson")),MIDDLE);
				setLocation(10.37f,y);
				drawText(M.edits(temp.get("cancelFromCourt")),MIDDLE);
				setLocation(14.0f,y);
				drawText(M.edits(temp.get("other")),MIDDLE);
				setLocation(18.07f,y);
				String totl = M.addnum(temp.get("notSamePerson"),temp.get("cancelFromCourt"));
				totl = M.addnum(totl,temp.get("other"));
				drawText(M.edits(totl),MIDDLE);
				y+=1.0f;
				
			}
			setLocation(3.5f,y);
			drawText(M.stou("รวม"),MIDDLE);	
			setLocation(6.87f,y);
			drawText(M.edits(sumnot),MIDDLE);
			setLocation(10.37f,y);
			drawText(M.edits(sumcancel),MIDDLE);
			setLocation(14.0f,y);
			drawText(M.edits(other),MIDDLE);

			setLocation(18.07f,y);
			String totl = M.addnum(sumnot,sumcancel);
			totl = M.addnum(totl,other);
			drawText(M.edits(totl),MIDDLE);
			
			
			savePage();
		}
		private void head(String year)
		{
			reset();

			setFont(fn);
                        setLocation(17.5f,1.2f);
                        drawText(M.stou("วันที่พิมพ์ ")+DateInfo.formatDate(1,DateInfo.sysDate()));

			setFont(fnb);
			setLocation(10.5f,1.2f);
			drawText(M.stou("สถิติยกเลิกกรมธรรม์ล้มละลาย(สถานะ N)"),MIDDLE);
			setLocation(10.5f,2.1f);
			drawText(M.stou("ประจำปี ")+year,MIDDLE);
			setLocation(2.0f,3.0f);
			drawRect(18.0f,15.0f);
			float y = 5.0f;
			setFont(fn16b);
			for (int i = 0 ;i < 13;i++)
			{
				setLocation(2.0f,y);
				lineTo(20.0f,y);
				y+=1.0f;
			}
			setLocation(5.0f,3.0f);
			lineTo(5.0f,18.0f);
			setLocation(8.5f,3.0f);
			lineTo(8.5f,18.0f);
			setLocation(12.0f,3.0f);
			lineTo(12.0f,18.0f);
			setLocation(16.0f,3.0f);
			lineTo(16.0f,18.0f);
			setLocation(3.5f,3.7f);
			drawText(M.stou("เดือน"),MIDDLE);
			setLocation(6.8f,3.7f);
			drawText(M.stou("ไม่ใช่คนเดียวกัน"),MIDDLE);
			setLocation(10.37f,3.1f);
			drawText(M.stou("คำสั่งจาก"),MIDDLE);
			setLocation(10.37f,4.1f);
			drawText(M.stou("กรมบังคับคดี"),MIDDLE);
			setLocation(14.07f,3.7f);
			drawText(M.stou("อื่น ๆ"));	
			setLocation(18.07f,3.7f);
			drawText(M.stou("รวม"));	
			
		}
	}
	public static void main(String args[] )
	{
		 new DlgCancelPermonth(new Mframe(""));
	}
}
