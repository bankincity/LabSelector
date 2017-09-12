package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import manit.rte.*;
import java.util.*;
import utility.rteutility.*;
import java.awt.event.*;
import javax.swing.event.*;
public class DlgRemoveNstatus extends Mframe
{
/* $$$-0
R0025001301690034rdIdNoเลขที่บัตรประชาชนgrp
R0025005101320032rdNameชื่อ - สกุลgrp
T0206005201400033tfFirstName
T0363005101400033tfLastName
Q0202001502720029fmIDNS-NNNNS-NNNNNS-NNS-N
B0519005000890034btSearchค้นหา
A0021010208350275tbDataT0F0139บัตรประชาชนT0F0123คำนำหน้าT0F0168ชื่อT0F0188นามสกุลT0F0107เลขคดีD8F0107วันที่พิทักษ์ทรัพย์
B0622038802370036btCancelยกเลิกการเป็นบุคคลล้มละลาย
L0020038801460035lbReasonเหตุผลการยกเลิก
T0130038904750035tfReason
F0129009608750460
---- */
	ButtonGroup		grp;
	JRadioButton	rdIdNo;
	JRadioButton	rdName;
	Tfield			tfFirstName;
	Tfield			tfLastName;
	Mformat			fmID;
	JButton			btSearch;
	Mtable			tbData;
	JButton			btCancel;
	JLabel			lbReason;
	Tfield			tfReason;
	boolean 		remote = false ;
	void panel_0()
	{
		grp = new ButtonGroup();
		rdIdNo = new JRadioButton(M.stou("เลขที่บัตรประชาชน"));
		grp.add(rdIdNo);
		rdIdNo.setBounds(25, 13, 169, 34);
		addcom(rdIdNo, 0);

		rdName = new JRadioButton(M.stou("ชื่อ - สกุล"));
		grp.add(rdName);
		rdName.setBounds(25, 51, 132, 32);
		addcom(rdName, 0);

		tfFirstName = new Tfield();
		tfFirstName.setBounds(206, 52, 140, 33);
		addcom(tfFirstName, 0);

		tfLastName = new Tfield();
		tfLastName.setBounds(363, 51, 140, 33);
		addcom(tfLastName, 0);

		fmID = new Mformat("NS-NNNNS-NNNNNS-NNS-N");
		fmID.setBounds(202, 15, 272, 29);
		addcom(fmID, 0);

		btSearch = new JButton(M.stou("ค้นหา"));
		btSearch.setBounds(519, 50, 89, 34);
		addcom(btSearch, 0);

		String[]	tbData$hdr = {M.stou("บัตรประชาชน"), M.stou("คำนำหน้า"), M.stou("ชื่อ"), M.stou("นามสกุล"), M.stou("เลขคดี"), M.stou("วันที่พิทักษ์ทรัพย์")};
		boolean[]	tbData$edt = {false, false, false, false, false, false};
		Object[]	tbData$val = {"", "", "", "", "", "00000000"};
		tbData = new Mtable(tbData$hdr);
		tbData.setCellEditor(0, new Tfield());
		tbData.setCellEditor(1, new Tfield());
		tbData.setCellEditor(2, new Tfield());
		tbData.setCellEditor(3, new Tfield());
		tbData.setCellEditor(4, new Tfield());
		tbData.setCellEditor(5, new Dfield(true));
		tbData.setColumnWidth(0, 139);
		tbData.setColumnWidth(1, 123);
		tbData.setColumnWidth(2, 168);
		tbData.setColumnWidth(3, 188);
		tbData.setColumnWidth(4, 107);
		tbData.setColumnWidth(5, 107);
		tbData.setEditable(tbData$edt);
		tbData.initRow(tbData$val);
		tbData.setBounds(21, 102, 835, 275);
		addcom(tbData, 0);

		btCancel = new JButton(M.stou("ยกเลิกการเป็นบุคคลล้มละลาย"));
		btCancel.setBounds(622, 388, 237, 36);
		addcom(btCancel, 0);

		lbReason = new JLabel(M.stou("เหตุผลการยกเลิก"));
		lbReason.setBounds(20, 388, 146, 35);
		addcom(lbReason, 0);

		tfReason = new Tfield();
		tfReason.setBounds(130, 389, 475, 35);
		addcom(tfReason, 0);

		setBounds(129, 96, 875, 460);
	}
/* $$$ */

	public DlgRemoveNstatus()
	{
		super("DlgRemoveNstatus");
		panel_0();
		tfReason.setLimit(99);
		setVisible(true);
	}
	public void doButton(Object o)
	{
		try {
			if(o.equals(btSearch))
			{
				String id = "";
				String fname = "";
				String lname = "";			
				if (rdIdNo.isSelected())
				{
					id = fmID.getText();
					if(id.trim().length() != 13 || id.compareTo("0000000000000") == 0)
						throw new Exception(M.stou("เลขที่บัตรประชาชนไม่ถูกต้อง"));
				}
				else if (rdName.isSelected())
				{
					fname = tfFirstName.getText();
					lname = tfLastName.getText();
					if (fname.trim().length() == 0 && lname.trim().length() == 0)
						throw new Exception(M.stou("ชื่อ-นามสกุล ไม่ถูกต้อง"));
						
				}	
				PublicRte.setRemote(remote);
            			Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteRemoveNstatus",new String [] {id,fname,lname});
          			System.out.println("result of RteRemoveNstatus -------"+res.status());
				if(res.status() != 0)
               				  throw new Exception((String)res.value());
				int row = tbData.getRowCount();
				for (int i = 0 ; i < row;i++)
					tbData.deleteRow(0);
				Vector vec = (Vector)res.value();
				for (int i = 0 ; i < vec.size();i++)
				{
					String [] str = (String [])vec.elementAt(i);
					tbData.appendRow(str);			
				}
			}
			else if (o.equals(btCancel))
			{
				String id = "";
				String fname = "";
				String lname = "";
				String caseID ="";
				String inforceDate = "";
				String remark = "";			
				int row = tbData.getSelectedRow();
				if (row < 0 )
					throw new Exception (M.stou("เลือกข้อมูลในตารางก่อน"));
				id = (String)tbData.getValueAt(row,0);
				fname = (String)tbData.getValueAt(row,2);
				lname  = (String)tbData.getValueAt(row,3);
				caseID = (String)tbData.getValueAt(row,4);
				inforceDate = (String)tbData.getValueAt(row,5);
				remark = tfReason.getText();
				if (remark.trim().length() == 0)
					throw new Exception(M.stou("กรุณาใส่หมายเหตุการยกเลิกด้วย`"));
				PublicRte.setRemote(remote);
            			Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteRemoveNstatus",new String [] {id,fname,lname,caseID,inforceDate,remark});
          			System.out.println("result of RteRemoveNstatus -------"+res.status());
				if(res.status() != 0)
               				  throw new Exception((String)res.value());
				tbData.deleteRow(row);			
			}
		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
		}
	}
	public void processWindowEvent(WindowEvent e)
        {
                if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
                        dispose();
                }
        }
	public static void main(String[] args)
	{
		new DlgRemoveNstatus();
	}
}
