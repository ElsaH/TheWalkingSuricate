
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwoWaySerialComm
{
	static Integer bas = 0;
	static Integer med = 0;
	static Integer haut = 0;
	
	public Integer getBas() {
		return bas;
	}
	public Integer getMed() {
		return med;
	}
	public Integer getHaut() {
		return haut;
	}
	
    public TwoWaySerialComm()
    {
        super();
    }
    
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                
                (new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out))).start();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    /** */
    public static class SerialReader implements Runnable 
    {
        InputStream in;
        String datas = "";
        String n="\n";
        String t="\t";
        String datatemp;
        boolean debut = true;
        
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                	if(debut) {
                		debut = false;
                	}
                	else {
                		datatemp = new String(buffer,0,len);
                    	datas=datas+datatemp;
                        Pattern pattern; /* Pour la regex */
                        /* Test savoir si on a tout */
                        //On doit avoir valeur;valeur;valeurS
                        pattern = Pattern.compile("([0-9]{3});([0-9]{3});([0-9]{3})S.*");
                        Matcher m = pattern.matcher(datas);
                       // System.out.println(datas);
                        if(m.matches()){
                            /* On a matché, on récupère les valeurs */
                            datas = datas.substring(12);
                            bas=Integer.parseInt(m.group(1));
                            med=Integer.parseInt(m.group(2));
                            haut=Integer.parseInt(m.group(3));
                            System.out.println(bas+t+med+t+haut);
                        }
                    	
                        //System.out.print(new String(buffer,0,len)+t);	
                	}
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }

    /** */
    public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {                
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {

                    this.out.write(c);
                }                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
    
    public static void main ( String[] args )
    {
        try
        {
            (new TwoWaySerialComm()).connect("COM41");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}