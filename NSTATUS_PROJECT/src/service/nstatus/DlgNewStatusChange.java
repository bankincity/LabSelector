package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import service.bucket.*;
import utility.support.*;
public class DlgNewStatusChange extends Mdialog
{
/* $$$-0
L0018002701000031lbStatus1สถานะ 1
L0351002701090032lbStatusDate1วันที่สถานะ 1
D0437002700900032dfStatusDate18
L0014006800880034lbStatus2สถานะ 2
L0351007101120033lbStatusDate2วันที่สถานะ 2
D0437007100900032dfStatusDate28
L0009001405320101lbStatusFrame
B0365012700870032btOKตกลง
B0458012600830032btCancelยกเลิก
O0074002702720032cmbStatus1
O0075007102730032cmbStatuus2
G0150013105590210
---- */
	JLabel			lbStatus1;
	JLabel			lbStatusDate1;
	Dfield			dfStatusDate1;
	JLabel			lbStatus2;
	JLabel			lbStatusDate2;
	Dfield			dfStatusDate2;
	JLabel			lbStatusFrame;
	JButton			btOK;
	JButton			btCancel;
	TComboBox		cmbStatus1;
	TComboBox		cmbStatuus2;

	void panel_0()
	{
		lbStatus1 = new JLabel(M.stou("สถานะ 1"));
		lbStatus1.setBounds(18, 27, 100, 31);
		addcom(lbStatus1, 0);

		lbStatusDate1 = new JLabel(M.stou("วันที่สถานะ 1"));
		lbStatusDate1.setBounds(351, 27, 109, 32);
		addcom(lbStatusDate1, 0);

		dfStatusDate1 = new Dfield(true);
		dfStatusDate1.setBounds(437, 27, 90, 32);
		addcom(dfStatusDate1, 0);

		lbStatus2 = new JLabel(M.stou("สถานะ 2"));
		lbStatus2.setBounds(14, 68, 88, 34);
		addcom(lbStatus2, 0);

		lbStatusDate2 = new JLabel(M.stou("วันที่สถานะ 2"));
		lbStatusDate2.setBounds(351, 71, 112, 33);
		addcom(lbStatusDate2, 0);

		dfStatusDate2 = new Dfield(true);
		dfStatusDate2.setBounds(437, 71, 90, 32);
		addcom(dfStatusDate2, 0);

		lbStatusFrame = new JLabel();
		lbStatusFrame.setBorder(Mborder.hiEtch());
		lbStatusFrame.setBounds(9, 14, 532, 101);
		addcom(lbStatusFrame, 0);

		btOK = new JButton(M.stou("ตกลง"));
		btOK.setBounds(365, 127, 87, 32);
		addcom(btOK, 0);

		btCancel = new JButton(M.stou("ยกเลิก"));
		btCancel.setBounds(458, 126, 83, 32);
		addcom(btCancel, 0);

		cmbStatus1 = new TComboBox();
		cmbStatus1.setBounds(74, 27, 272, 32);
		addcom(cmbStatus1, 0);

		cmbStatuus2 = new TComboBox();
		cmbStatuus2.setBounds(75, 71, 273, 32);
		addcom(cmbStatuus2, 0);

		setBounds(150, 131, 559, 210);
	}
/* $$$ */
	String [] odata ;
	public DlgNewStatusChange(JFrame parent,String [] odat)
	{
		super(parent, true);
		setTitle("DlgNewStatusChange");
		panel_0();
		odata = odat;
		String [] pstat = StatusPolicy.statusItem(false);
		//cmbStatus1.addItem(M.stou("--------กรุณาเลือก--------"));
		cmbStatus1.addItem(" ");
	//	cmbStatuus2.addItem(M.stou("--------กรุณาเลือก--------"));
		cmbStatuus2.addItem(" ");
		for (int i = 0 ; i < pstat.length;i++)
		{
			cmbStatus1.addItem(pstat[i]);			
			cmbStatuus2.addItem(pstat[i]);			
		}
		if(odata[0].trim().length() == 1)	
		{
			for (int i = 1 ; i< cmbStatus1.getItemCount();i++)
			{
				if (odata[0].charAt(0) == ((String)cmbStatus1.getItemAt(i)).charAt(0))
				{
					cmbStatus1.setSelectedIndex(i);
				}
			}
		}
		else{
			cmbStatus1.setSelectedIndex(0);
		}			
		dfStatusDate1.setText(odata[1]);
		if(odata[2].trim().length() == 1)	
		{
			for (int i = 1 ; i< cmbStatuus2.getItemCount();i++)
			{
				if (odata[2].charAt(0) == ((String)cmbStatuus2.getItemAt(i)).charAt(0))
				{
					cmbStatuus2.setSelectedIndex(i);
				}
			}
		}
		else{
			cmbStatuus2.setSelectedIndex(0);
		}			
		dfStatusDate2.setText(odata[3]);		
		cmbStatus1.setEnabled(false);
		cmbStatuus2.setEnabled(false);
		dfStatusDate1.setEnabled(false);
		dfStatusDate2.setEnabled(false);
		setVisible(true);
	}
	boolean ok = false ;
	String remark = "";
	public void doButton(Object o)
	{
		if (o.equals(btOK))
		{
			DlgSelectReason reason = new DlgSelectReason(this);
			if(reason.ok())
			{
				remark = reason.reason();
				ok = true;
			}
		}
		else if (o.equals(btCancel))
			ok = false ;
		dispose();	
	}
	public boolean ok()
	{
		return ok;
	}
	public String [] getChgStatus()
	{
		String status1 = " ";
		if(cmbStatus1.getSelectedIndex() > 0)
			status1 = (String)cmbStatus1.getSelectedItem();
		status1 = status1.substring(0,1);
		
		String status2 = " ";
		if(cmbStatuus2.getSelectedIndex() > 0)
			status2 = (String)cmbStatuus2.getSelectedItem();
		status2 = status2.substring(0,1);
		return (new String [] {status1,dfStatusDate1.getText(),status2,dfStatusDate2.getText(),remark,Passwd.getEmployeeNo()});
	}
}
