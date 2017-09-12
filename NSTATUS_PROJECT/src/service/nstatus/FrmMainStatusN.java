package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
class FrmMainStatusN extends Mframe
{
/* $$$-0
B0011000801030056btImportนำเข้าข้อมูล
B0119000801030056btMatchDataตรวจสอบข้อมูลนำเข้า
B0338000801030056btMarkStatusNสถานะ  N
B0447000801030056btCancelNยกเลิกสถานะ N
B0227000801030056btSearchค้นหาข้อมูล
F0041006609360670
---- */
	JButton			btImport;
	JButton			btMatchData;
	JButton			btMarkStatusN;
	JButton			btCancelN;
	JButton			btSearch;

	void panel_0()
	{
		btImport = new JButton(M.stou("นำเข้าข้อมูล"));
		btImport.setBounds(11, 8, 103, 56);
		addcom(btImport, 0);

		btMatchData = new JButton(M.stou("ตรวจสอบข้อมูลนำเข้า"));
		btMatchData.setBounds(119, 8, 103, 56);
		addcom(btMatchData, 0);

		btMarkStatusN = new JButton(M.stou("สถานะ  N"));
		btMarkStatusN.setBounds(338, 8, 103, 56);
		addcom(btMarkStatusN, 0);

		btCancelN = new JButton(M.stou("ยกเลิกสถานะ N"));
		btCancelN.setBounds(447, 8, 103, 56);
		addcom(btCancelN, 0);

		btSearch = new JButton(M.stou("ค้นหาข้อมูล"));
		btSearch.setBounds(227, 8, 103, 56);
		addcom(btSearch, 0);

		setBounds(41, 66, 936, 670);
	}
/* $$$ */
	String	group = "",
			position = "";

	FrmMainStatusN()
	{
		super("FrmMainStatusN");
		panel_0();
		setVisible(true);
	}
	public void doButton(Object o)
	{
		if(o.equals(btImport))
		{
			new DlgImportData(this);
		}
		else if (o.equals(btMatchData))
		{
			new DlgCheckMatchData(this);
		}
		else if (o.equals(btSearch))
		{
			new DlgMaintainStatusN(this, group, position);
		}	
	}
	public static void main(String[] args)
	{
		new FrmMainStatusN();
	}
}
