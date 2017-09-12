package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import service.bucket.*;
import service.service.PU;
import utility.support.DateInfo;
import utility.claim.CM;
import service.bucket.Passwd;
import java.awt.event.*;
import javax.swing.event.*;
public class DlgCheckMatchData extends Mframe
{
/* $$$-0
L0028003201040033lbDateวันที่นำเข้า
D0115003200970032dfImportData8
R0115007501950033rdDataMarkNข้อมูลที่ Mark สถานะ N แล้วsel
L0019007400800034lbStatusประเภทข้อมูล
R0319007501790033rdMatchSomeข้อมูลที่ Match บาง Fieldsel
L0009002305110124lbKey
B0381003300960033btSearchตรวจสอบ
L0221003300830031lbToDateถึงวันที่
D0272003300990031dfToDate8
A0008017610030493tbDataT0F0171เลขบัตรประชาชนT0F0101เลขกรมธรรม์T0F0215ชื่อ-สกุลD8F0083วันเกิดT0F0046สาขาT0F0192เลขบัตรประชาชนT0F0192ชื่อ-สกุล
A0008015210040025tbHeadT0T0617ข้อมูลของบริษัทT0F0369ข้อมูลของกรมบังคับคดี
B0719010701360034btConfirmยืนยันการ Mark N
B0858010701480034btCancelยกเลิกการ Mark N
B0566010701500035btPrintพิมพ์ข้อมูลในตาราง
R0019010902140032rdBenefitNข้อมูลที่ระงับผลประโยชน์แล้วsel
R0237010902950032rdBenefitCheckข้อมูลที่ระงับปลประโยชน์ Match บาง Fieldsel
G-005000510240712
---- */
	JLabel			lbDate;
	Dfield			dfImportData;
	ButtonGroup		sel;
	JRadioButton	rdDataMarkN;
	JLabel			lbStatus;
	JRadioButton	rdMatchSome;
	JRadioButton	rdMatchSomeName;
	JLabel			lbKey;
	JButton			btSearch;
	JLabel			lbToDate;
	Dfield			dfToDate;
	Mtable			tbData;
	Mtable			tbHead;
	JButton			btConfirm;
	JButton			btCancel;
	JButton			btPrint;
	JRadioButton	rdBenefitN;
	JRadioButton	rdBenefitCheck;

	void panel_0()
	{
		lbDate = new JLabel(M.stou("วันที่นำเข้า"));
		lbDate.setBounds(28, 32, 104, 33);
		addcom(lbDate, 0);

		dfImportData = new Dfield(true);
		dfImportData.setBounds(115, 32, 97, 32);
		addcom(dfImportData, 0);

		sel = new ButtonGroup();
		rdDataMarkN = new JRadioButton(M.stou("ข้อมูลที่ Mark สถานะ N แล้ว"));
		sel.add(rdDataMarkN);
		rdDataMarkN.setBounds(115, 75, 195, 33);
		addcom(rdDataMarkN, 0);

		lbStatus = new JLabel(M.stou("ประเภทข้อมูล"));
		lbStatus.setBounds(19, 74, 80, 34);
		addcom(lbStatus, 0);

		rdMatchSome = new JRadioButton(M.stou("ข้อมูลที่ Match เฉพาะบัตรประชาชน"));
		sel.add(rdMatchSome);
		rdMatchSome.setBounds(319, 75, 230, 33);
		addcom(rdMatchSome, 0);

		rdMatchSomeName = new JRadioButton(M.stou("ข้อมูลที่ Match เฉพาะชื่อ-สกุล"));
		sel.add(rdMatchSomeName);
		rdMatchSomeName.setBounds(550, 75, 200, 33);
		addcom(rdMatchSomeName, 0);

		lbKey = new JLabel();
		lbKey.setBorder(Mborder.hiEtch());
		lbKey.setBounds(9, 23, 760, 124);
		addcom(lbKey, 0);

		btSearch = new JButton(M.stou("ตรวจสอบ"));
		btSearch.setBounds(381, 33, 96, 33);
		addcom(btSearch, 0);

		lbToDate = new JLabel(M.stou("ถึงวันที่"));
		lbToDate.setBounds(221, 33, 83, 31);
		addcom(lbToDate, 0);

		dfToDate = new Dfield(true);
		dfToDate.setBounds(272, 33, 99, 31);
		addcom(dfToDate, 0);

		String[]	tbData$hdr = { M.stou("สาขา"), M.stou("วันเกิด"), M.stou("เลขกรมธรรม์"),M.stou("ชื่อ-สกุล"),M.stou("เลขบัตรประชาชน"), M.stou("เลขบัตรประชาชน"), M.stou("ชื่อ-สกุล"),M.stou("เลขที่คดี")};
		boolean[]	tbData$edt = {false, false, false, false, false, false, false,false};
		Object[]	tbData$val = {"", "00000000", "", "", "", "", ""};
		tbData = new Mtable(tbData$hdr);
		tbData.setCellEditor(0, new Tfield());
		tbData.setCellEditor(1, new Dfield(true));
		tbData.setCellEditor(2, new Tfield());
		tbData.setCellEditor(3, new Tfield());
		tbData.setCellEditor(4, new Tfield());
		tbData.setCellEditor(5, new Tfield());
		tbData.setCellEditor(6, new Tfield());
		tbData.setCellEditor(7, new Tfield());
		tbData.setColumnWidth(0, 46 );
		tbData.setColumnWidth(1, 83 );
		tbData.setColumnWidth(2,101 );
		tbData.setColumnWidth(3, 215);
		tbData.setColumnWidth(4, 171);
		tbData.setColumnWidth(5, 100);
		tbData.setColumnWidth(6, 192);
		tbData.setColumnWidth(7, 92);
		tbData.setEditable(tbData$edt);
		tbData.initRow(tbData$val);
		tbData.setBounds(8, 176, 1003, 493);
		addcom(tbData, 0);

		String[]	tbHead$hdr = {M.stou("ข้อมูลของบริษัท"), M.stou("ข้อมูลของกรมบังคับคดี")};
		boolean[]	tbHead$edt = {true , false};
		Object[]	tbHead$val = {"", ""};
		tbHead = new Mtable(tbHead$hdr);
		tbHead.setCellEditor(0, new Tfield());
		tbHead.setCellEditor(1, new Tfield());
		tbHead.setColumnWidth(0, 617);
		tbHead.setColumnWidth(1, 369);
		tbHead.setEditable(tbHead$edt);
		tbHead.initRow(tbHead$val);
		tbHead.setBounds(8, 152, 1004, 25);
		addcom(tbHead, 0);

		btConfirm = new JButton(M.stou("ยืนยันการ Mark N"));
		btConfirm.setBounds(858, 67, 150, 34);
		addcom(btConfirm, 0);

		btCancel = new JButton(M.stou("ยกเลิกการ Mark N"));
		btCancel.setBounds(858, 107, 150, 34);
		addcom(btCancel, 0);

		btPrint = new JButton(M.stou("พิมพ์ข้อมูลในตาราง"));
		btPrint.setBounds(858, 27, 150, 35);
		addcom(btPrint, 0);

		rdBenefitN = new JRadioButton(M.stou("ข้อมูลที่ระงับผลประโยชน์แล้ว"));
		sel.add(rdBenefitN);
		rdBenefitN.setBounds(115, 109, 195, 32);
		addcom(rdBenefitN, 0);

		rdBenefitCheck = new JRadioButton(M.stou("ข้อมูลที่ระงับผลประโยชน์ Match บาง Field"));
		sel.add(rdBenefitCheck);
		rdBenefitCheck.setBounds(319, 109, 280, 32);
		addcom(rdBenefitCheck, 0);

		setBounds(-5, 5, 1024, 712);
	}
/* $$$ */
	public DlgCheckMatchData( JFrame parent ) 
	{
//super(parent, false);
		setTitle("DlgCheckMatchData");
		panel_0();
		try {
		   Passwd passwd = null;
                /*   if(username.trim().length() >  0)
                        passwd  = new Passwd(username,userid);
                   else*/
                   passwd = new Passwd();
                   if (!passwd.passwordOK())
                       throw new Exception(M.stou("รหัสผ่านนี้ไม่สามารถเข้าระบบนี้ได้")) ;

		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
			return ;
		}
		setVisible(true);
	}
	TempMasicFile tempi;
	String insolventType;
	String ben = "0" ;
	public void doButton(Object o)
	{
		try {
			
			if(o.equals(btSearch))
			{
				ben = "0";
				if(rdDataMarkN.isSelected())
				{
					insolventType = "AP";
					btCancel.setEnabled(false);				
					btConfirm.setEnabled(false);				
				}
				else if (rdMatchSome.isSelected())
				{
					btCancel.setEnabled(true);				
					btConfirm.setEnabled(true);				
					insolventType = "I";
				}
				else if (rdMatchSomeName.isSelected())
				{
					btCancel.setEnabled(true);				
					btConfirm.setEnabled(true);				
					insolventType = "S";
				}
				else if (rdBenefitN.isSelected())
				{
					btCancel.setEnabled(false);				
					btConfirm.setEnabled(false);				
					insolventType = "AP";
					ben = "D";
				}
				else if (rdBenefitCheck.isSelected())
				{
					btCancel.setEnabled(true);				
					btConfirm.setEnabled(true);				
					insolventType = "SI";
					ben = "D";
				}
					
				tempi = InsolventClient.getInsolventData(insolventType,dfImportData.getText(),dfToDate.getText(),ben);
				showData();
			}
			else if (o.equals(btCancel))
			{
				int row = tbData.getSelectedRow();
				if(row < 0 )
					throw new  Exception(M.stou("เลือกข้อมูลในตารางก่อน"));
				// Array of {policyNo,firstname,lastname,idNo,caseID,remark,userID}
				String [] data = new String[7];
				
				data[0] = (String)tbData.getValueAt(row,2);
				if(data[0].trim().length() == 0)
					throw new Exception(M.stou("กรุณาเลือกเฉพาะข้อมูลที่มีเลขที่กรมธรรม์"));
				data[4] = (String)tbData.getValueAt(row,7);
				tempi.start(1);
				if(tempi.equalGreat(data[0]+data[4]) && (data[0]+data[4]).compareTo(tempi.get("policyNo")+tempi.get("caseID")) == 0)
				{
					data[1] = tempi.get("firstName");
					data[2] = tempi.get("lastName");
					data[3] = tempi.get("idNo");
					DlgSelectReason dlg = new DlgSelectReason(this);
					if(!dlg.ok())
					{
						Msg.msg(this,M.stou("ยกเลิกการดำเนินการ"));
						return ;
					}
					data[5] = dlg.reason(); //remark
					data[6] = Passwd.getEmployeeNo(); // userID
					InsolventClient.cancelInsolvent(data);
					tempi.delete();
					tbData.deleteRow(row);
				}
				tempi.start(0);
				
			}
			else if (o.equals(btConfirm))
			{
				int row = tbData.getSelectedRow();
				if(row < 0 )
					throw new  Exception(M.stou("เลือกข้อมูลในตารางก่อน"));
				// Array of {policyNo,firstname,lastname,idNo,caseID,chgID,userID}
				String [] data = new String[7];
				
				data[0] = (String)tbData.getValueAt(row,2);
				data[4] = (String)tbData.getValueAt(row,7);
				tempi.start(1);
				if(tempi.equalGreat(data[0]+data[4]) && (data[0]+data[4]).compareTo(tempi.get("policyNo")+tempi.get("caseID")) == 0)
				{
					data[1] = tempi.get("firstName");
					data[2] = tempi.get("lastName");
					data[3] = tempi.get("idNo");
					data[5] = "N"; //remark
					data[6] = Passwd.getEmployeeNo().trim(); // userID
					if(tempi.get("idNo").compareTo(tempi.get("idNo2")) != 0)
					{
						int ans = Msg.msg(this,M.stou("คุณต้องการเปลี่ยน เลขบัตรประชาชนเป็น " +tempi.get("idNo2")+" หรือไม่? "),Msg.QUESTION,Msg.YES+Msg.NO+Msg.CANCEL) ;
						System.out.println("Msg yes --------"+Msg.YES+" -----------------"+ans);
						if (ans == Msg.YES)
						{
							data[5]="Y"+tempi.get("idNo2");
						}
						else if (ans == Msg.CANCEL)
						{
							throw new Exception(M.stou("ยกเลิกการยืนยันข้อมูล"));
						}
					}
					InsolventClient.approveInsolvent(data);
					tempi.delete();
					tbData.deleteRow(row);
				}
				tempi.start(0);
			}
			else if ( o.equals( btPrint ))
			{
				System.out.println("ben------"+ben);
				DlgCheckMatchDataView view = new DlgCheckMatchDataView( tempi, insolventType, dfImportData.getText(), dfToDate.getText(),ben);
				Mprint printer = new Mprint( "laser" );
           			Mscreen rd = new Mscreen("");
           			rd.setPageSize( 8.25d, 11.75d );
           			rd.setPrinter( printer );
           			rd.landscape();
           			rd.render( view, 0, view.getPageCount() - 1 );
			}
		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
		}
	}
	public void doTab(Object o)
	{
		if (o.equals(dfImportData))
		{
			dfToDate.setText(dfImportData.getText());
		}
	}
	public void showData()
	{
		clearTable();
		String [] data = new String[8];
		for (boolean st = tempi.first();st;st=tempi.next())
		{
			if(tempi.get("pstatus").charAt(0) == 'S')
				data[4] = CM.editCid(tempi.get("idNo"))+"***";
			else
				data[4] = CM.editCid(tempi.get("idNo"));
			if(tempi.get("pstatus").charAt(0) == 'H')
			{
				data[2] = "";
				data[3] = "";
			}
			else {
				data[2] = tempi.get("policyNo");
				if(tempi.get("pstatus").charAt(0) == 'I')
					data[3] = tempi.get("preName")+tempi.get("firstName")+" "+tempi.get("lastName")+"***";
				else
					data[3] = tempi.get("preName")+tempi.get("firstName")+" "+tempi.get("lastName");
			}
			data[1] = tempi.get("birthDate");
			data[0] = tempi.get("branch");
			data[5] = CM.editCid(tempi.get("idNo2"));
			data[6] = tempi.get("preName2")+tempi.get("firstName2")+" "+tempi.get("lastName2");
			data[7]= tempi.get("caseID");
			tbData.appendRow(data);
		}
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
class DlgCheckMatchDataView extends Mview
{
	TempMasicFile	tempFile;
	String		insolventType,
			sysDate,
			sDate,
			eDate,
			ben,
			pageNo = "000";
	float		iy = 0.0f;
    	Font []     	font = new Font[3];
	float[] 	bar1 = { 1.5f, 3.0f, 5.0f, 7.0f, 12.5f, 16.0f, 19.5f, 25.0f, 28.5f },
			bar2 = { 1.5f, 16.0f, 28.5f };
	int		row = 0;
	
	DlgCheckMatchDataView( TempMasicFile tempFile, String insolventType, String sDate, String eDate,String ben )
	{
		this.tempFile = tempFile;
		this.insolventType = insolventType;
		this.sDate = sDate;
		this.eDate = eDate;
		this.ben = ben;
		sysDate = DateInfo.sysDate();
 	        font[0]  = M.getFont();
        	font[1] = font[0].deriveFont( 14.0f );
	        font[2] = font[1].deriveFont( Font.BOLD );
		printReport();
		PU.setPage( this );
	}
	private void printReport()
	{
		int	count = 0;

		printHead();
		for ( boolean opCode = tempFile.first(); opCode; opCode = tempFile.next() )
		{
			if ( row >= 25 )
			{
				iy = PU.printLine( this, iy, bar1[0], bar1[8] );
				PU.setPage( this );
				iy = 0.0f;
				printHead();
			}
			iy = PU.printDetailLine( this, PU.getMiddle( bar1[0], bar1[1] ), iy, tempFile.get( "branch" ), "M", true, 0.65f );
			iy = PU.printDetailLine( this, PU.getMiddle( bar1[1], bar1[2] ), iy, DateInfo.formatDate( 1, tempFile.get( "birthDate" )), "M" );
			if(tempFile.get("pstatus").charAt(0) != 'H')
				iy = PU.printDetailLine( this, PU.getMiddle( bar1[2], bar1[3] ), iy, tempFile.get( "policyNo" ), "M" );
			if ( tempFile.get( "pstatus" ).equals( "I" ))
				iy = PU.printDetailLine( this, bar1[3] + 0.1f, iy,tempFile.get( "preName" )+ tempFile.get( "firstName" ) + " " + tempFile.get( "lastName" ) + " ***", "L" );
			else
				iy = PU.printDetailLine( this, bar1[3] + 0.1f, iy,tempFile.get( "preName" )+  tempFile.get( "firstName" ) + " " + tempFile.get( "lastName" ), "L" );
			if(tempFile.get("pstatus").charAt(0) != 'H')
			{
				if ( tempFile.get( "pstatus" ).equals( "S" ))
					iy = PU.printDetailLine( this, PU.getMiddle( bar1[4], bar1[5] ), iy, CM.editCid( tempFile.get( "idNo" )) + " ***", "M" );
				else
					iy = PU.printDetailLine( this, PU.getMiddle( bar1[4], bar1[5] ), iy, CM.editCid( tempFile.get( "idNo" )), "M" );
			}
			iy = PU.printDetailLine( this, PU.getMiddle( bar1[5], bar1[6] ), iy, CM.editCid( tempFile.get( "idNo2" )), "M" );
			iy = PU.printDetailLine( this, bar1[6] + 0.1f, iy,tempFile.get( "preName2" )+  tempFile.get( "firstName2" ) + " " + tempFile.get( "lastName2" ), "L" );
			iy = PU.printDetailLine( this, PU.getMiddle( bar1[7], bar1[8] ), iy, tempFile.get( "caseID" ), "M" );
			PU.printBar( this, iy, bar1 );
			++count;
			++row;
		}
		iy = PU.printLine( this, iy, bar1[0], bar1[8] );
		// print total
		iy = PU.printDetailLine( this, PU.getMiddle( bar1[0], bar1[8] ), iy, M.stou( "รวม  " ) + M.edits( M.itoc( count ) ) + M.stou( " ราย" ) , "M", true, 1.0f );
	}
	private void printHead()
	{
		String	header = "";
		pageNo = M.inc( pageNo );
		setFont( font[1] );
		iy = PU.printDetailLine( this, 28.5f, iy, M.stou( "หน้า  " ) + M.edits( pageNo ), "R", true, 1.0f );
		iy = PU.printDetailLine( this, 1.5f, iy, M.stou( "วันที่  " ) + DateInfo.formatDate( 1, sysDate ), "L" );
		setFont( font[2] );
		if ( insolventType.charAt( 0 ) == 'A' )
		{
			if (ben.charAt(0) =='D')
				header = M.stou( "รายงานกรมธรรม์ที่ระงับผลประโยชน์แล้ว"  );
			else
				header = M.stou( "รายงานกรมธรรม์ที่ข้อมูล  Mark สถานะ N แล้ว"  );
		}
		else {
			if (ben.charAt(0) =='D')
				header = M.stou( "รายงานกรมธรรม์ที่ระงับผลประโยชน์ Match ข้อมูล บาง field (รอตรวจสอบ)"  );
			else
				header = M.stou( "รายงานกรมธรรม์ที่ Match ข้อมูลบาง field (รอตรวจสอบ)" );
		}
		iy = PU.printDetailLine( this, 14.0f, iy, header, "M", true, 0.5f );
		iy = PU.printDetailLine( this, 14.0f, iy, M.stou( "วันที่นำเข้า  " ) + DateInfo.formatDate( 1, sDate ) + "  -  " + DateInfo.formatDate( 1, eDate ), "M", true, 0.65f );
		setFont( font[1] );
		iy = PU.printLine( this, iy, bar2[0], bar2[2] );
		iy += 0.25f;
		iy = PU.printDetailLine( this, PU.getMiddle( bar2[0], bar2[1] ), iy, M.stou( "ข้อมูลของบริษัท" ), "M" ); 
		iy = PU.printDetailLine( this, PU.getMiddle( bar2[1], bar2[2] ), iy, M.stou( "ข้อมูลของกรมบังคับคดี" ), "M" ); 
		iy -= 0.25f;
		PU.printBar( this, iy, bar2 );
		iy = PU.printLine( this, iy, bar2[0], bar2[2] );
		iy += 0.25f;
		iy = PU.printDetailLine( this, PU.getMiddle( bar1[0], bar1[1] ), iy, M.stou( "สาขา" ), "M" );
		iy = PU.printDetailLine( this, PU.getMiddle( bar1[1], bar1[2] ), iy, M.stou( "วันเกิด" ), "M" );
		iy = PU.printDetailLine( this, PU.getMiddle( bar1[2], bar1[3] ), iy, M.stou( "เลขกรมธรรม์" ), "M" );
		iy = PU.printDetailLine( this, PU.getMiddle( bar1[3], bar1[4] ), iy, M.stou( "ชื่อ-สกุล" ), "M" );
		iy = PU.printDetailLine( this, PU.getMiddle( bar1[4], bar1[5] ), iy, M.stou( "เลขบัตรประชาชน" ), "M" );
		iy = PU.printDetailLine( this, PU.getMiddle( bar1[5], bar1[6] ), iy, M.stou( "เลขบัตรประชาชน" ), "M" );
		iy = PU.printDetailLine( this, PU.getMiddle( bar1[6], bar1[7] ), iy, M.stou( "ชื่อ-สกุล" ), "M" );
		iy = PU.printDetailLine( this, PU.getMiddle( bar1[7], bar1[8] ), iy, M.stou( "เลขที่คดี" ), "M" );
		iy -= 0.25f;
		PU.printBar( this, iy, bar1 );
		iy = PU.printLine( this, iy, bar2[0], bar2[2] );
		PU.printBar( this, iy, bar1 );
		row = 6;
	}
}
