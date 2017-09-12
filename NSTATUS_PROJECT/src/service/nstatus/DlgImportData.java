package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import utility.cfile.CFile;
import java.util.*;
import service.bucket.Passwd;
import javax.swing.border.*;
import javax.swing.BorderFactory.*;
import java.io.*;
import utility.support.*;
import java.awt.event.*;
import javax.swing.event.*;
public class DlgImportData extends Mframe
{
/* $$$-0
L0021001600930032lbFileNameแฟ้มข้อมูล
T0092001603320032tfFileName
B0429001600850031btBrowseBrowse
B0520001601520031btLoadนำข้อมูลเข้าระบบ
A0015009809590237tbDataT0F0057ลำดับที่T0F0114คำนำหน้าT0F0175ชื่อT0F0166นามสกุลT0F0146บัตรประชาชนT0F0149เลขที่คดีD8F0149วันที่พิทักษ์ทรัพย์
B0823066201430035btCheckตรวจสอบกรมธรรม์
L0009000406810059lbFrame1
L0015007509610062lbFrame2
A0012038509560273tbReadyT0F0063ลำดับที่T0F0113คำนำหน้าT0F0174ชื่อT0F0166นามสกุลT0F0151บัตรประชาชนT0F0143เลขที่คดีD8F0143วันที่พิทักษ์ทรัพย์
B0331034400870031btAddเพิ่ม
B0499034400870031btDelลบ
G0005000009930729
---- */
	JLabel			lbFileName;
	Tfield			tfFileName;
	JButton			btBrowse;
	JButton			btLoad;
	JButton			btLoad1;
	Mtable			tbData;
	JButton			btCheck;
	JLabel			lbFrame1;
	JLabel			lbFrame2;
	Mtable			tbReady;
	JButton			btAdd;
	JButton			btDel;
	JLabel			lbStatus;

	void panel_0()
	{
		lbFileName = new JLabel(M.stou("แฟ้มข้อมูล"));
		lbFileName.setBounds(21, 16, 93, 32);
		addcom(lbFileName, 0);

		tfFileName = new Tfield();
		tfFileName.setBounds(92, 16, 332, 32);
		addcom(tfFileName, 0);

		btBrowse = new JButton(M.stou("Browse"));
		btBrowse.setBounds(429, 16, 85, 31);
		addcom(btBrowse, 0);

		btLoad = new JButton(M.stou("มีคำนำหน้าชื่อ"));
		btLoad.setBounds(520, 16, 130, 31);
		addcom(btLoad, 0);
		
		btLoad1 = new JButton(M.stou("ไม่มีคำนำหน้าชื่อ"));
		btLoad1.setBounds(660, 16, 150, 31);
		addcom(btLoad1, 0);


		String[]	tbData$hdr = {M.stou("ลำดับที่"), M.stou("คำนำหน้า"), M.stou("ชื่อ"), M.stou("นามสกุล"), M.stou("บัตรประชาชน"), M.stou("เลขที่คดี"), M.stou("วันที่พิทักษ์ทรัพย์")};
		boolean[]	tbData$edt = {false, true, true, true, true, true, true};
		Object[]	tbData$val = {"", "", "", "", "", "", "00000000"};
		tbData = new Mtable(tbData$hdr);
		tbData.setCellEditor(0, new Tfield());
		tbData.setCellEditor(1, new Tfield());
		tbData.setCellEditor(2, new Tfield());
		tbData.setCellEditor(3, new Tfield());
		tbData.setCellEditor(4, new Tfield());
		tbData.setCellEditor(5, new Tfield());
		tbData.setCellEditor(6, new Dfield(true));
		tbData.setColumnWidth(0, 57);
		tbData.setColumnWidth(1, 114);
		tbData.setColumnWidth(2, 175);
		tbData.setColumnWidth(3, 166);
		tbData.setColumnWidth(4, 146);
		tbData.setColumnWidth(5, 149);
		tbData.setColumnWidth(6, 149);
		tbData.setEditable(tbData$edt);
		tbData.initRow(tbData$val);
		tbData.setBounds(15, 98, 959, 237);
		addcom(tbData, 0);

		btCheck = new JButton(M.stou("ตรวจสอบกรมธรรม์"));
		btCheck.setBounds(823, 662, 143, 35);
		addcom(btCheck, 0);

		lbFrame1 = new JLabel();
		lbFrame1.setBorder(Mborder.hiEtch());
		lbFrame1.setBounds(9, 4, 840, 59);
		addcom(lbFrame1, 0);

		lbFrame2 = new JLabel();
		lbFrame2.setBorder(Mborder.hiEtch());
		lbFrame2.setBounds(15, 75, 961, 62);
		addcom(lbFrame2, 0);

		String[]	tbReady$hdr = {M.stou("ลำดับที่"), M.stou("คำนำหน้า"), M.stou("ชื่อ"), M.stou("นามสกุล"), M.stou("บัตรประชาชน"), M.stou("เลขที่คดี"), M.stou("วันที่พิทักษ์ทรัพย์")};
		boolean[]	tbReady$edt = {false, false, false, false, false, false, false};
		Object[]	tbReady$val = {"", "", "", "", "", "", "00000000"};
		tbReady = new Mtable(tbReady$hdr);
		tbReady.setCellEditor(0, new Tfield());
		tbReady.setCellEditor(1, new Tfield());
		tbReady.setCellEditor(2, new Tfield());
		tbReady.setCellEditor(3, new Tfield());
		tbReady.setCellEditor(4, new Tfield());
		tbReady.setCellEditor(5, new Tfield());
		tbReady.setCellEditor(6, new Dfield(true));
		tbReady.setColumnWidth(0, 63);
		tbReady.setColumnWidth(1, 113);
		tbReady.setColumnWidth(2, 174);
		tbReady.setColumnWidth(3, 166);
		tbReady.setColumnWidth(4, 151);
		tbReady.setColumnWidth(5, 143);
		tbReady.setColumnWidth(6, 143);
		tbReady.setEditable(tbReady$edt);
		tbReady.initRow(tbReady$val);
		tbReady.setBounds(12, 385, 956, 273);
		addcom(tbReady, 0);

		btAdd = new JButton(new ImageIcon(picturePath+"/"+"down.gif"));
		btAdd.setBounds(331, 335, 48, 48);
		addcom(btAdd, 0);

		btDel = new JButton(new ImageIcon(picturePath+"/"+"up.gif"));
		btDel.setBounds(499, 335, 48, 48);
		addcom(btDel, 0);
			
		btAdd.setBorder(null);
		btDel.setBorder(null);

		lbStatus = new JLabel("");
		lbStatus.setBounds(3,662,816,35);
		addcom(lbStatus);
		lbStatus.setForeground(Color.red);
		lbStatus.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		lbStatus.setHorizontalAlignment(JLabel.CENTER);
		setBounds(5, 0, 993, 729);
	}
	String picturePath ;
/* $$$ */
	Mrecord pncode ;
	public DlgImportData(JFrame parent)
	{
//		super(parent, true);
		setTitle("DlgImportData");
		picturePath = M.classPath("service/picture");
		System.out.println("picture path ----->"+picturePath);
		panel_0();
		try {
			Passwd passwd = null;
                /*   if(username.trim().length() >  0)
                        passwd  = new Passwd(username,userid);
                   else*/
			passwd = new Passwd();
			if (!passwd.passwordOK())
				throw new Exception(M.stou("รหัสผ่านนี้ไม่สามารถเข้าระบบนี้ได้")) ;

			pncode = CFile.opens("prenamecode@commontable");
		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
			return ;
		}
		setVisible(true);
	}
	public void doButton(Object o)
	{
		try {
			if(o.equals(btBrowse))
			{
				FileDialog dgFile = new FileDialog(new Frame());
				dgFile.show();
       		                if (dgFile.getFile() == null || dgFile.getFile().trim().length() == 0)
					 throw new Exception (M.stou("ชื่อ แฟ้มข้อมูลไม่ถูกต้อง "));
				tfFileName.setText(dgFile.getDirectory()+dgFile.getFile());				
			}
			else if (o.equals(btLoad) || o.equals(btLoad1))
			{
				RandomAccessFile rand = new RandomAccessFile(tfFileName.getText(),"r");
				String str = rand.readLine();
				int line = 1 ;
				int eline = 1 ;
				String  [] data = new String[7];
				while (str!=null && str.trim().length() != 0)
				{
				//	String [] split = str.split(":");
					String [] split = str.split(",");
					if(split.length < 4 )
					{
						data[0]= "";
						data[1] = "";
						data[2] = M.stou(str);
						data[3] = "";
						data[4] = "";
						data[5] = "";
						data[6] = "";	
					}
					else {
						String [] tname = split[0].split(" ");
						if(tname.length != 4 && tname.length != 2)
						{
							data[0]= "";
							data[1] = "";
							data[2] = M.stou(split[0]);
							data[3] = ""; 
							data [4] = split[1];
							data [5] = M.stou(split[2]);
							data [6] = chgDate(split[3]); 
						}
						else
						{
							data [2] = M.stou(tname[0]);
							data [3] = M.stou(tname[1]);
							if (o.equals(btLoad))
							{
								tname = splitPrename(data[2],true);
							}
							else{
								tname = splitPrename(data[2],false);
							}
							data [1] = tname[0];
							data [2] = tname[1];	
							data [4] = split[1];
							data [5] = M.stou(split[2]);
							data [6] = chgDate(split[3]); 
						}
					}
					if(data[6].compareTo("00000000") == 0)
					{					
							data [0] = M.itoc(eline);
							tbData.appendRow(data);
							eline++;
					}
					else if (data[1].trim().length() == 0 && o.equals(btLoad))
					{
						data [0] = M.itoc(eline);
						tbData.appendRow(data);
						eline++;
						
					}
					else {
						data [0] = M.itoc(line);
						tbReady.appendRow(data);
						line++;
					}
					str =  rand.readLine();
		/*			if((line+eline) > 500)
						break;*/
				}
			}
			else if (o.equals(btAdd))
			{
				int row = tbData.getSelectedRow();
				if (row < 0)
					throw new Exception(M.stou("เลือกข้อมูลในตารางก่อน"));
				String  [] data = new String[7];
				for (int i = 0 ; i < 7 ;i++)
				{
					data[i] = (String)tbData.getValueAt(row,i);		
				}
				tbData.deleteRow(row);				
				int tr = tbData.getRowCount();
				for (int i = row ; i  < tr ;i++)
				{
					tbData.setValueAt(M.itoc(++row),i,0);		
				}
				row = tbReady.getRowCount();
				data[0] = M.itoc(++row);	 
				tbReady.appendRow(data);
			}			
			else if (o.equals(btDel))
			{
				int row = tbReady.getSelectedRow();
				if (row < 0)
					throw new Exception(M.stou("เลือกข้อมูลในตารางก่อน"));
				String  [] data = new String[7];
				for (int i = 0 ; i < 7 ;i++)
				{
					data[i] = (String)tbReady.getValueAt(row,i);		
				}
				tbReady.deleteRow(row);
				int tr = tbReady.getRowCount();
				for (int i = row ; i  < tr ;i++)
				{
					tbReady.setValueAt(M.itoc(++row),i,0);		
				}
				row = tbData.getRowCount();
				data[0] = M.itoc(++row);	 				
				tbData.appendRow(data);
			}
			else if (o.equals(btCheck))
			{
				SendThread sthread = new SendThread();
				sthread.start();
				
		/*		int row = tbReady.getRowCount();
				Vector vdata = new Vector();
				int count = 0 ;
				for (int i = 0 ; i < row ; i++) 
			//	for (int i = row-1 ; i > 0 ; i--) 
				{
					count++;
					String [] data  = new String [6];
					for (int j = 1 ; j < 7 ; j++)
					{
						if (j == 4)
							data[j-1] = (chgIDNo((String)tbReady.getValueAt(i,j))).trim();
						else 
							data[j-1] = ((String)tbReady.getValueAt(i,j)).trim();
					}
					vdata.addElement(data);
					if ( count % 3  == 0 && count != 0)
					{
						System.out.println("sending ........");
						InsolventClient.sendToMatchData(vdata);
						vdata = new Vector();
						Thread.sleep(500);
						lbStatus.setText(M.stou("ข้อมูลตรวจสอบแล้ว   :  ")+M.itoc(count));
						Thread.sleep(1000);
					}							
				}
				if (vdata.size() > 0)
				{
						System.out.println("sending ........");
						InsolventClient.sendToMatchData(vdata);
				}
				
				lbStatus.setText(M.stou("ข้อมูลตรวจสอบแล้ว   :  ")+M.itoc(count));*/
			//	throw new Exception(M.stou("ตรวจสอบข้อมูลเรียบร้อยแล้ว"));
			}
		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
		}
	}
	private void sendToCheck() throws Exception
	{
		
				RandomAccessFile  frand = new RandomAccessFile("/c/service/nstatus/insolvent"+DateInfo.sysDate().substring(0,4)+".csv","rw");
				int row = tbReady.getRowCount();
				frand.seek(frand.length());
				String strbuff = "";
				lbStatus.setText(M.stou("กำลังบันทึกข้อมูลลงแฟ้ม"));
				for (int i = 0 ; i < row;i++)
				{
					strbuff = "";
					for (int j = 1 ; j < 7 ; j++)
					{
						if (j == 4)
							strbuff +=  (chgIDNo((String)tbReady.getValueAt(i,j))).trim();
						else 
							strbuff += ((String)tbReady.getValueAt(i,j)).trim();
						if( j < 6)
							strbuff+=",";
					}
					strbuff+="\r\n";
					frand.write(M.utos(strbuff));
					
				}
				frand.close();
				Vector vdata = new Vector();
				int count = 0 ;
				lbStatus.setText(M.stou("ข้อมูลตรวจสอบแล้ว   :  ")+M.itoc(count)+M.stou(" จาก ")+M.itoc(row));
				for (int i = 0 ; i < row ; i++) 
			//	for (int i = row-1 ; i > 0 ; i--) 
				{
					count++;
					String [] data  = new String [6];
					for (int j = 1 ; j < 7 ; j++)
					{
						if (j == 4)
							data[j-1] = (chgIDNo((String)tbReady.getValueAt(i,j))).trim();
						else 
							data[j-1] = ((String)tbReady.getValueAt(i,j)).trim();
					}
					vdata.addElement(data);
					if ( count % 100  == 0 && count != 0)
					{
						System.out.println("sending ........");
						InsolventClient.sendToMatchData(vdata);
						vdata = new Vector();
						lbStatus.setText(M.stou("ข้อมูลตรวจสอบแล้ว   :  ")+M.itoc(count)+M.stou(" จาก ")+M.itoc(row));
						Thread.sleep(1000);
					}							
				}
				if (vdata.size() > 0)
				{
						System.out.println("sending ........");
						InsolventClient.sendToMatchData(vdata);
				}
				lbStatus.setText(M.stou("ข้อมูลตรวจสอบแล้ว   :  ")+M.itoc(count)+M.stou(" จาก ")+M.itoc(row));
	}
	private void showMsg(String errMsg)
	{
		Msg.msg(this,errMsg);
	}
	private class SendThread extends Thread
	{
		public void run()
		{
			btCheck.setEnabled(false);
			try {
				sendToCheck();
				throw new Exception(M.stou("ตรวจสอบข้อมูลเรียบร้อยแล้ว"));
			}
			catch(Exception e)
			{
				showMsg(e.getMessage());
			}
			btCheck.setEnabled(true);
		}
	}
	private String [] splitPrename(String name,boolean havePreName)
	{
		if (!havePreName)
			return (new String [] {"",name});
		String [] pn = {M.stou("นส."),M.stou("นางสาว"),M.stou("น.ส."),M.stou("นาย"),M.stou("นาง")};
		String []  nm = new String[2];
		int idx = -1;
		for (int i = 0 ; i < pn.length;i++)
		{
			idx = name.indexOf(pn[i]);
			if(idx == 0)
			{
				nm[0] = name.substring(0,pn[i].length());
				nm[1] = name.substring(pn[i].length());
				break;
			}
		}
		if(idx != 0 && havePreName)
		{
			System.out.println("name -------->"+name);
			for (boolean st = pncode.first();st;st=pncode.next())
			{
				idx = name.indexOf(pncode.get("abbName"));
				if(idx != 0)
				{
					idx = name.indexOf(pncode.get("fullName"));
					if(idx == 0)
					{
						nm[0] = name.substring(0,pncode.get("fullName").length());	
						nm[1] = name.substring(pncode.get("fullName").length());	
						break;
					}		
				}
				else {
					nm[0] = name.substring(0,pncode.get("abbName").length());	
					nm[1] = name.substring(pncode.get("abbName").length());	
					break;
				}				
			}
			if(idx != 0)
			{
				nm[0] = "";
				nm[1] = name;
			}
		}
		return nm;			
	}
	private String chgDate(String dateInput)
	{
		try {
			String [] dd = dateInput.split("/");
			if(dd[2].compareTo("3000") > 0)
				dd[2] = M.subnum(dd[2],"543");
			else if (dd[2].compareTo("2500") < 0 )
				dd[2] = M.addnum(dd[2],"543");			
			dateInput = dd[2]+M.setlen(dd[1],2)+M.setlen(dd[0],2);
		}
		catch(Exception e)
		{
			dateInput = "00000000";	
		}
		return dateInput;
	}
	private String chgIDNo(String idno)
	{
		String [] tid =  idno.split("-");
		idno ="";
		for (int i = 0 ; i < tid.length;i++)
		{
			idno = idno+tid[i];
		}
		return idno;
	}
	private void clearTable()
	{
		int row = tbData.getRowCount();
		for (int i = 0 ;i < row;i++)
			tbData.deleteRow(0);		
	}
	public void processWindowEvent(WindowEvent e)
        {
                if (e.getID() == WindowEvent.WINDOW_CLOSING){
                        dispose();
                }
        }

}
