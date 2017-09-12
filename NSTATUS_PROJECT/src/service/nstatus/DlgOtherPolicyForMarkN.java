package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
class DlgOtherPolicyForMarkN extends Mdialog
{
/* $$$-0
A0016005907220268tbOtherPolicyT0F0111กรมธรรม์T0F0285ชื่อ-สกุลT0F0174เลขที่บัตรD8F0075วันเกิดK0F0074เลือก
L0019001404220038lbHeaderข้อมูลที่ต้องตรวจสอบว่าเป็นบุคคลเดียวกันหรือไม่
B0525034000990034btMarkNMark N
B0634034001020033btCloseจบงาน
G0180011507650420
---- */
	Mtable			tbOtherPolicy;
	JLabel			lbHeader;
	JButton			btMarkN;
	JButton			btClose;

	void panel_0()
	{
		String[]	tbOtherPolicy$hdr = {M.stou("กรมธรรม์"), M.stou("ชื่อ-สกุล"), M.stou("เลขที่บัตร"), M.stou("วันเกิด"), M.stou("เลือก")};
		boolean[]	tbOtherPolicy$edt = {false, false, false, false, true};
		Object[]	tbOtherPolicy$val = {"", "", "", "00000000", new Boolean(true)};
		tbOtherPolicy = new Mtable(tbOtherPolicy$hdr);
		tbOtherPolicy.setCellEditor(0, new Tfield());
		tbOtherPolicy.setCellEditor(1, new Tfield());
		tbOtherPolicy.setCellEditor(2, new Tfield());
		tbOtherPolicy.setCellEditor(3, new Dfield(true));
		tbOtherPolicy.setCellEditor(4, new JCheckBox());
		tbOtherPolicy.setColumnWidth(0, 111);
		tbOtherPolicy.setColumnWidth(1, 285);
		tbOtherPolicy.setColumnWidth(2, 174);
		tbOtherPolicy.setColumnWidth(3, 75);
		tbOtherPolicy.setColumnWidth(4, 74);
		tbOtherPolicy.setEditable(tbOtherPolicy$edt);
		tbOtherPolicy.initRow(tbOtherPolicy$val);
		tbOtherPolicy.setBounds(16, 59, 722, 268);
		addcom(tbOtherPolicy, 0);

		lbHeader = new JLabel(M.stou("ข้อมูลที่ต้องตรวจสอบว่าเป็นบุคคลเดียวกันหรือไม่"));
		lbHeader.setBounds(19, 14, 422, 38);
		addcom(lbHeader, 0);

		btMarkN = new JButton(M.stou("Mark N"));
		btMarkN.setBounds(525, 340, 99, 34);
		addcom(btMarkN, 0);

		btClose = new JButton(M.stou("จบงาน"));
		btClose.setBounds(634, 340, 102, 33);
		addcom(btClose, 0);

		setBounds(180, 115, 765, 420);
	}
/* $$$ */
       // Vector of String [] (prename,fiestname,lastname,idNo,caseID,caseDate,policyNo)

        String [] dataCase ;
	DlgOtherPolicyForMarkN(Mdialog parent,Vector vdata,String [] dataCase)
	{
		super(parent, true);
		setTitle("DlgOtherPolicyForMarkN");
		panel_0();
                Object [] data = {"","","","",new Boolean(false)};
                for (int i = 0 ; i < vdata.size();i++)
                {
                        String [] val = (String [])vdata.elementAt(i);
                        data[0] = val[0];
                        data[1] = val[1];
                        data[2] = val[2];
                        data[3] = val[3];
                        tbOtherPolicy.appendRow(data);
                }
                this.dataCase  = dataCase;
		setVisible(true);
	}
        public void doButton(Object o)
        {
                if (o.equals(btClose))
                        dispose();
                else if (o.equals(btMarkN))
                {
                        try {
                                for (int i = 0 ; i < tbOtherPolicy.getRowCount();i++)
                                {
                                        Boolean sel = (Boolean)tbOtherPolicy.getValueAt(i,4);
                                        if (sel.booleanValue())
                                        {
                                 //     Msg.msg(this,(String)tbOtherPolicy.getValueAt(i,0));

                                                String [] seldata  = new String[7];
                                                seldata[0] = dataCase[0];
                                                seldata[1] = dataCase[1];
                                                seldata[2] = dataCase[2];
                                                seldata[3] = dataCase[3];
                                                seldata[4] = dataCase[4];
                                                seldata[5] = dataCase[5];
                                                seldata[6] = (String)tbOtherPolicy.getValueAt(i,0);
                                                Vector vdata = new Vector();
                                                vdata.addElement(seldata);
                                                System.out.println("by......................policy");
                                                Vector vi  = InsolventClient.sendToMatchDataByPolicy(vdata,true);
                                        }

                              }
                              throw new Exception(M.stou("ปรับสถานะกรมธรรม์ที่เลือกเป็น N (บุคคลล้มละลาย) เรียบร้อบแล้ว"));
                        }
                        catch(Exception e)
                        {
                                Msg.msg(this,e.getMessage());
                        }
                }
        }
}
