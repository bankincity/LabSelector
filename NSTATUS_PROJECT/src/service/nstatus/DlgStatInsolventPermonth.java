package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import service.bucket.*;
import utility.support.*;
public class DlgStatInsolventPermonth extends Mdialog
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
	public DlgStatInsolventPermonth(JFrame parent)
	{
		super(parent, false);
		setTitle("DlgStatInsolventPermonth");
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
				TempMasicFile temp = InsolventClient.getStatisticInsolvent(tfYear.getText());
				StatInsolventPermonthView view = new StatInsolventPermonthView(temp,tfYear.getText());
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
	public class StatInsolventPermonthView extends Mview
	{
		Font fn;
		Font fnb;
		Font fn16b;
		public  StatInsolventPermonthView(TempMasicFile temp,String year)
		{
			fn = M.fontSize(M.getFont(),16);
			fnb= M.fontSize(M.getFont(Font.BOLD),18);				
			fn16b= M.fontSize(M.getFont(Font.BOLD),16);				
			head(year);
			setFont(fn);
			float y = 5.1f;
			String sump = "0";
			String sumnp = "0";
			for (boolean st = temp.first();st; st = temp.next())
			{
				setLocation(5.0f,y);
				drawText(temp.get("month").substring(4)+"/"+temp.get("month").substring(0,4),MIDDLE);
				sumnp = M.addnum(sumnp,temp.get("notApprove"));
				sump = M.addnum(sump,temp.get("approve"));
				setLocation(8.87f,y);
				drawText(M.edits(temp.get("notApprove")),MIDDLE);
				setLocation(12.37f,y);
				drawText(M.edits(temp.get("approve")),MIDDLE);
				setLocation(16.37f,y);
				drawText(M.edits(M.addnum(temp.get("approve"),temp.get("notApprove"))),MIDDLE);
				y+=1.0f;
				
			}
			setLocation(5.0f,y);
			drawText(M.stou("รวม"),MIDDLE);	
			setLocation(8.87f,y);
			drawText(M.edits(sumnp),MIDDLE);
			setLocation(12.37f,y);
			drawText(M.edits(sump),MIDDLE);
			setLocation(16.37f,y);
			drawText(M.edits(M.addnum(sump,sumnp)),MIDDLE);
			
			savePage();
		}
		private void head(String year)
		{
			reset();
			setFont(fn);
			setLocation(17.5f,1.2f);
			drawText(M.stou("วันที่พิมพ์ ")+DateInfo.formatDate(1,DateInfo.sysDate()));
			setLocation(10.5f,1.2f);
			setFont(fnb);
			drawText(M.stou("สถิติกรมธรรม์ล้มละลาย(สถานะ N)"),MIDDLE);
			setLocation(10.5f,2.1f);
			drawText(M.stou("ประจำปี ")+year,MIDDLE);
			setLocation(3.0f,3.0f);
			drawRect(15.0f,15.0f);
			float y = 5.0f;
			setFont(fn16b);
			for (int i = 0 ;i < 13;i++)
			{
				setLocation(3.0f,y);
				lineTo(18.0f,y);
				y+=1.0f;
			}
			setLocation(7.0f,3.0f);
			lineTo(7.0f,18.0f);
			setLocation(10.5f,3.0f);
			lineTo(10.5f,18.0f);
			setLocation(14.0f,3.0f);
			lineTo(14.0f,18.0f);
			setLocation(5.0f,3.7f);
			drawText(M.stou("เดือน"),MIDDLE);
			setLocation(8.87f,3.1f);
			drawText(M.stou("กรมธรรม์ที่"),MIDDLE);
			setLocation(8.87f,4.1f);
			drawText(M.stou("ไม่ต้องตรวจสอบ"),MIDDLE);
			setLocation(12.37f,3.1f);
			drawText(M.stou("กรมธรรม์ที่"),MIDDLE);
			setLocation(12.37f,4.1f);
			drawText(M.stou("ต้องตรวจสอบ"),MIDDLE);
			setLocation(16.37f,3.7f);
			drawText(M.stou("รวม"));	
			
		}
	}
	public static void main(String args[] )
	{
		 new DlgStatInsolventPermonth(new Mframe(""));
	}
}
