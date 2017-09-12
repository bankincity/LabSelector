package rte.bl.service.nstatus;
import manit.*;
public class  TempMasicFile  extends Mrecord
{
        private Layout          layout;
        private MasicSpec       spec;
        private Mrecord file;
        private String server ; 
 	private String[][] keyTemp=null;


      // Constructor
      public TempMasicFile(String server,String [] fieldname,int fieldlen [])
      {
        setField(fieldname,fieldlen);    
        this.server = server;     
      }
      public TempMasicFile(String server,Layout layout)
      {
        setField(layout);    
        this.server = server;     
      }
      public void setField(Layout lay)
      {
                this.layout = lay;
                spec = new MasicSpec(layout);

      }
      public void setField(String []  fieldName,int [] len)
      {
                layout = new Layout();
                for (int i=0;i<fieldName.length && i < len.length;i++)
                        layout.field(fieldName[i], 'T', len[i], 0);
                spec = new MasicSpec(layout);

      }           
      public void keyField(boolean dup,boolean mod ,String[] fkey)
      {
            spec.addKey(dup,mod);
            for (int i=0;i<fkey.length;i++)
                 spec.addSegment(fkey[i]);
      }  
      public void setNoOfKey (int key)
      {
            keyTemp = new String[key][10];
      }
                                                                                                                            
      public void setKey (int keyNo, boolean dup, boolean mod, String[] fkey)
      {
            spec.addKey(dup,mod);
            for (int i=0;i<fkey.length;i++)
                 spec.addSegment(fkey[i]);
            keyTemp[keyNo] = fkey;
      }
	    
      public void buildTemp() throws Exception
      {

	    file = Masic.makeTemp(server,spec);
            duplicate(file); 
      }
      public void close()
      {
          file.close(); 
      }  
}
