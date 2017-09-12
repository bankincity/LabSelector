package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import utility.support.*;
import java.io.*;
import java.util.*;
import utility.claim.CM;
import java.awt.event.*;
import javax.swing.event.*;

public class DlgForLoadData extends Mframe
{
/* $$$-0
L0040005800800034lbFirstNameชื่อ
L0628000801770035lbYearข้อมูลที่นำเข้าภายในปี
S0773000800710033isYear4
B0846000801400034btSearchLoad ข้อมูลใหม่
T0071005801400033tfFirstName
L0220005800800034lbLastNameนามสกุล
T0286005801400033tfLastName
L0433005801400034lbIDเลขที่บัตรประชาชน
Q0542006202720029fmIDNS-NNNNS-NNNNNS-NNS-N
B0811005901520034btSearchTableค้นหาข้อมูลในตาราง
L0030004909560055lbFrame
A0027014609560555tbDataT0F0077ลำดับที่T0F0103คำนำหน้าT0F0182ชื่อT0F0182นามสกุลT0F0173เลขที่บัตรประชาชนT0F0133เลขที่คดีD8F0103วันที่พิทักษ์ทรัพย์
B0027011100520029btPrev<
B0084011200500027btNext>
G0002000210180735
---- */
	JLabel			lbFirstName;
	JLabel			lbYear;
	ISfield			isYear;
	JButton			btSearch;
	Tfield			tfFirstName;
	JLabel			lbLastName;
	Tfield			tfLastName;
	JLabel			lbID;
	Mformat			fmID;
	JButton			btSearchTable;
	JLabel			lbFrame;
	Mtable			tbData;
	JButton			btPrev;
	JButton			btNext;

	void panel_0()
	{
		lbFirstName = new JLabel(M.stou("ชื่อ"));
		lbFirstName.setBounds(40, 58, 80, 34);
		addcom(lbFirstName, 0);

		lbYear = new JLabel(M.stou("ข้อมูลที่นำเข้าภายในปี"));
		lbYear.setBounds(628, 8, 177, 35);
		addcom(lbYear, 0);

		isYear = new ISfield(4);
		isYear.setBounds(773, 8, 71, 33);
		addcom(isYear, 0);

		btSearch = new JButton(M.stou("Load ข้อมูลใหม่"));
		btSearch.setBounds(846, 8, 140, 34);
		addcom(btSearch, 0);

		tfFirstName = new Tfield();
		tfFirstName.setBounds(71, 58, 140, 33);
		addcom(tfFirstName, 0);

		lbLastName = new JLabel(M.stou("นามสกุล"));
		lbLastName.setBounds(220, 58, 80, 34);
		addcom(lbLastName, 0);

		tfLastName = new Tfield();
		tfLastName.setBounds(286, 58, 140, 33);
		addcom(tfLastName, 0);

		lbID = new JLabel(M.stou("เลขที่บัตรประชาชน"));
		lbID.setBounds(433, 58, 140, 34);
		addcom(lbID, 0);

		fmID = new Mformat("NS-NNNNS-NNNNNS-NNS-N");
		fmID.setBounds(542, 62, 272, 29);
		addcom(fmID, 0);

		btSearchTable = new JButton(M.stou("ค้นหาข้อมูลในตาราง"));
		btSearchTable.setBounds(811, 59, 152, 34);
		addcom(btSearchTable, 0);

		lbFrame = new JLabel();
		lbFrame.setBorder(Mborder.hiEtch());
		lbFrame.setBounds(30, 49, 956, 55);
		addcom(lbFrame, 0);

		String[]	tbData$hdr = {M.stou("ลำดับที่"), M.stou("คำนำหน้า"), M.stou("ชื่อ"), M.stou("นามสกุล"), M.stou("เลขที่บัตรประชาชน"), M.stou("เลขที่คดี"), M.stou("วันที่พิทักษ์ทรัพย์")};
		boolean[]	tbData$edt = {false, false, false, false, false, false, false};
		Object[]	tbData$val = {"", "", "", "", "", "", "00000000"};
		tbData = new Mtable(tbData$hdr);
		tbData.setCellEditor(0, new Tfield());
		tbData.setCellEditor(1, new Tfield());
		tbData.setCellEditor(2, new Tfield());
		tbData.setCellEditor(3, new Tfield());
		tbData.setCellEditor(4, new Tfield());
		tbData.setCellEditor(5, new Tfield());
		tbData.setCellEditor(6, new Dfield(true));
		tbData.setColumnWidth(0, 77);
		tbData.setColumnWidth(1, 103);
		tbData.setColumnWidth(2, 182);
		tbData.setColumnWidth(3, 182);
		tbData.setColumnWidth(4, 173);
		tbData.setColumnWidth(5, 133);
		tbData.setColumnWidth(6, 103);
		tbData.setEditable(tbData$edt);
		tbData.initRow(tbData$val);
		tbData.setBounds(27, 146, 956, 555);
		addcom(tbData, 0);

		btPrev = new JButton(M.stou("<"));
		btPrev.setBounds(27, 111, 52, 29);
	//ddcom(btPrev, 0);

		btNext = new JButton(M.stou(">"));
		btNext.setBounds(84, 112, 50, 27);
	//ddcom(btNext, 0);

		setBounds(2, 2, 1018, 735);
	}
/* $$$ */
	DlgResultSearch dlg ;
	public DlgForLoadData(JFrame parent)
	{
	//	super(parent, true);
		setTitle("DlgForLoadData");
		panel_0();
		isYear.setText(DateInfo.sysDate().substring(0,4));
	// btPrev.setEnabled(false);
	//	btNext.setEnabled(false);
		loadData(isYear.getText());
		dlg = new DlgResultSearch(this);
		setVisible(true);
	}
	private void loadData(String year) 
	{
		int row = tbData.getRowCount();
		for (int i = 0 ; i < row;i++)
			tbData.deleteRow(0);
		try {
			RandomAccessFile rand = new RandomAccessFile("/c/service/nstatus/insolvent"+year+".csv","rw");
			String strbuf = rand.readLine();
			if (year.compareTo("2553") == 0)
				strbuf = rand.readLine();
				
			String [] dt =  new String [7];
			int trow= 1;
			while (strbuf != null)
			{
				strbuf = M.stou(strbuf);
				String [] tf = strbuf.split(",");
				dt [0] = M.itoc(trow);
				System.out.println("strbuf--------->"+strbuf);
				for (int k = 0 ; k < tf.length ; k++)
				{
					if(k == 3)
						dt[k+1] =  CM.editCid(tf[k]);
					else	
						dt[k+1] = tf[k];	
				}
				trow++;
				tbData.appendRow(dt);
				strbuf = rand.readLine();
			}
		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
		}
	}
	public void doButton(Object o)
	{
		if(o.equals(btSearch))
		{
			loadData(isYear.getText());
		}
		else if (o.equals(btSearchTable))
		{
			searchIntable(0);
			if (vec == null || vec.size() == 0)
			{
				Msg.msg(this,M.stou("ไม่พบข้อมูลที่ต้องการค้นหา"));
				return ;
			}	
			dlg.showme(vec);
			tfFirstName.setText("");
			tfLastName.setText("");
			fmID.setText("             ");
		}
	}
	Vector vec ;
	private void searchIntable(int startRow)
	{
		vec = new Vector();
		for (int i =startRow ; i < tbData.getRowCount() ; i++)
		{
			if (fmID.getText().trim().length() == 13  && fmID.getText().compareTo("0000000000000") != 0)
			{
				String str = (String)tbData.getValueAt(i,4);
				if(str.indexOf(CM.editCid(fmID.getText())) >= 0)
				{
					String [] data = new String [6];
					if (tfFirstName.getText().trim().length() > 0)
					{
						String str1 = (String)tbData.getValueAt(i,2);
						System.out.println("i === "+i+"   "+str1);
						if(str1.indexOf(tfFirstName.getText()) >= 0)
						{
							if(tfLastName.getText().trim().length() > 0)
							{
								str1 = (String)tbData.getValueAt(i,3);
								if(str1.indexOf(tfLastName.getText()) >= 0)
								{
									data = new String [6];
									for (int j = 1;j < 7;j++)
										data [j-1] = (String)tbData.getValueAt(i,j);
									vec.addElement(data);
								}
							}
							else {
								data = new String [6];
								for (int j = 1;j < 7;j++)
									data [j-1] = (String)tbData.getValueAt(i,j);
								vec.addElement(data);
							}
						}
					}
					else if (tfLastName.getText().trim().length() > 0)
					{
						String str2= (String)tbData.getValueAt(i,3);
						if(str2.indexOf(tfLastName.getText()) >= 0)
						{
							data = new String [6];
							for (int j = 1;j < 7;j++)
								data [j-1] = (String)tbData.getValueAt(i,j);
							vec.addElement(data);
						}				
					}
					else { 	
						
						for (int j = 1;j < 7;j++)
							data [j-1] = (String)tbData.getValueAt(i,j);
						vec.addElement(data);
					}
				}				
			}
			else if(tfFirstName.getText().trim().length() > 0)
			{
				String str = (String)tbData.getValueAt(i,2);
				System.out.println("i === "+i+"   "+str);
				if(str.indexOf(tfFirstName.getText()) >= 0)
				{
					if(tfLastName.getText().trim().length() > 0)
					{
						str = (String)tbData.getValueAt(i,3);
						if(str.indexOf(tfLastName.getText()) >= 0)
						{
							String [] data = new String [6];
							for (int j = 1;j < 7;j++)
								data [j-1] = (String)tbData.getValueAt(i,j);
							vec.addElement(data);
						}
					}
					else {
						String [] data = new String [6];
						for (int j = 1;j < 7;j++)
							data [j-1] = (String)tbData.getValueAt(i,j);
						vec.addElement(data);
					}
				}	
			}
			else if (tfLastName.getText().trim().length() > 0)
			{
				String str = (String)tbData.getValueAt(i,3);
				if(str.indexOf(tfLastName.getText()) >= 0)
				{
					String [] data = new String [6];
					for (int j = 1;j < 7;j++)
						data [j-1] = (String)tbData.getValueAt(i,j);
					vec.addElement(data);
				}				
			}	
		}
	}
	public void processWindowEvent(WindowEvent e)
        {
                if (e.getID() == WindowEvent.WINDOW_CLOSING){
                        dispose();
                }
        }

	public static void main(String [] args) throws Exception
	{
		new DlgForLoadData(new Mframe(""));
	}
}
