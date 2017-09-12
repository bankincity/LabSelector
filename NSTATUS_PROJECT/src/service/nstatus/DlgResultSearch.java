package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
public class DlgResultSearch extends Mdialog
{
/* $$$-0
A0011003009590378tbDataT0F0123คำนำหน้าT0F0201ชื่อT0F0229นามสกุลT0F0192เลขบัตรประชาชนT0F0106เลขที่คดีD8F0106วันที่พิทักษ์ทรัพย์
G0033013209940455
---- */
	Mtable			tbData;

	void panel_0()
	{
		String[]	tbData$hdr = {M.stou("คำนำหน้า"), M.stou("ชื่อ"), M.stou("นามสกุล"), M.stou("เลขบัตรประชาชน"), M.stou("เลขที่คดี"), M.stou("วันที่พิทักษ์ทรัพย์")};
		boolean[]	tbData$edt = {false, false, false, false, false, false};
		Object[]	tbData$val = {"", "", "", "", "", "00000000"};
		tbData = new Mtable(tbData$hdr);
		tbData.setCellEditor(0, new Tfield());
		tbData.setCellEditor(1, new Tfield());
		tbData.setCellEditor(2, new Tfield());
		tbData.setCellEditor(3, new Tfield());
		tbData.setCellEditor(4, new Tfield());
		tbData.setCellEditor(5, new Dfield(true));
		tbData.setColumnWidth(0, 123);
		tbData.setColumnWidth(1, 201);
		tbData.setColumnWidth(2, 229);
		tbData.setColumnWidth(3, 192);
		tbData.setColumnWidth(4, 106);
		tbData.setColumnWidth(5, 106);
		tbData.setEditable(tbData$edt);
		tbData.initRow(tbData$val);
		tbData.setBounds(11, 30, 959, 378);
		addcom(tbData, 0);

		setBounds(33, 132, 994, 455);
	}
/* $$$ */

	public DlgResultSearch(Mframe  parent)
	{
		super(parent, true);
		setTitle("DlgResultSearch");
		panel_0();
		Container  pane = (Container)getContentPane().getComponent(0);
                JPanel panel0 =(JPanel) pane.getComponent(0);
		panel0.setBackground(Color.blue);

	}
	public void showme(Vector data)
	{
		int row = tbData.getRowCount();
		for (int i = 0 ; i < row;i++)
			tbData.deleteRow(0);
		for (int i = 0 ; i < data.size();i++)
		{
			String [] tdata = (String [])data.elementAt(i);
			tbData.appendRow(tdata);	
		}
		setVisible(true);
	}
}
