package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
class DlgSelectToDeleteInsolvent extends Mdialog
{
/* $$$-0
R0023003003710036rdInsolventPolicyลบแฟ้ม กรมธรรม์บุคคลล้มละลาย (insolvent policy)todel
R0022007205990033rdBothลบแฟ้ม กรมธรรม์บุคคลล้มละลาย และ แฟ้มบุคคลล้มละลาย (insolvent policy and insolvent people)todel
L0007001106260113lbFrame
B0415013700850035btOKตกลง
B0522013600850035btCancelยกเลิก
G0180011506520218
---- */
	ButtonGroup		todel;
	JRadioButton	rdInsolventPolicy;
	JRadioButton	rdBoth;
	JLabel			lbFrame;
	JButton			btOK;
	JButton			btCancel;

	void panel_0()
	{
		todel = new ButtonGroup();
		rdInsolventPolicy = new JRadioButton(M.stou("ลบแฟ้ม กรมธรรม์บุคคลล้มละลาย (insolvent policy)"));
		todel.add(rdInsolventPolicy);
		rdInsolventPolicy.setBounds(23, 30, 371, 36);
		addcom(rdInsolventPolicy, 0);

		rdBoth = new JRadioButton(M.stou("ลบแฟ้ม กรมธรรม์บุคคลล้มละลาย และ แฟ้มบุคคลล้มละลาย (insolvent policy and insolvent people)"));
		todel.add(rdBoth);
		rdBoth.setBounds(22, 72, 599, 33);
		addcom(rdBoth, 0);

		lbFrame = new JLabel();
		lbFrame.setBorder(Mborder.hiEtch());
		lbFrame.setBounds(7, 11, 626, 113);
		addcom(lbFrame, 0);

		btOK = new JButton(M.stou("ตกลง"));
		btOK.setBounds(415, 137, 85, 35);
		addcom(btOK, 0);

		btCancel = new JButton(M.stou("ยกเลิก"));
		btCancel.setBounds(522, 136, 85, 35);
		addcom(btCancel, 0);

		setBounds(180, 115, 652, 218);
	}
/* $$$ */

	DlgSelectToDeleteInsolvent(Mframe parent)
	{
		super(parent, true);
		setTitle("DlgSelectToDeleteInsolvent");
		panel_0();
                rdInsolventPolicy.setSelected(true);
		setVisible(true);
	}
        String selectToDel = "0";
        public void doButton(Object o)
        {
                selectToDel = "0";
                if (o.equals(btOK))
                {
                        if (rdBoth.isSelected())
                                selectToDel = "2";
                        else if (rdInsolventPolicy.isSelected())
                                selectToDel = "1";                      
  
                }
                dispose();
        }
        public String selectToDel()
        {
                return selectToDel;
        }
}
