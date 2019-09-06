package iHM;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

public class PacketListener implements SerialPortPacketListener {
	
	   private int PackteSize = 1;
	   
	   public void setSizeOfPacket(int size) {PackteSize = size;}	
		
	   @Override
	   public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

	   @Override
	   public int getPacketSize() { return PackteSize; }
	   
	   //public void setPackeSize(int size) {this.setPackeSize(size);}

	   @Override
	   public void serialEvent(SerialPortEvent event)
	   {
		  char[] dados = new char[this.getPacketSize()];
		  int indice = 0;
	      byte[] newData = event.getReceivedData();
	      System.out.println(" Received data of size: " + newData.length);
	      for (int i = 0; i < newData.length; ++i) {
	    	 System.out.print(" "+(char)newData[i]);}
	      System.out.println("\n");
	      for (int i = 0; i < newData.length; ++i) {dados[indice] = (char)newData[indice];indice = indice + 1;
	      }
	      indice = 0;
	      for(int i = 0; i < newData.length; ++i) {
	      	 if((dados[indice] == 'K') ||  dados[indice] == '>' || dados[indice] == 'C' || dados[indice]=='W' || dados[indice] == '+'){
	      		 System.out.println("\n read OK :" +(char)dados[indice]);
	      		 Interface.set_cr(true);
	      		 //checa fim do envio do sms
	      		 if(dados[indice] == '>'){Interface.wr = true;}	 
	      		 if(dados[indice] == 'W') {	      			 
	      			 Interface.CMGW = (int)(dados[9] - '0');	      			 
	      			 System.out.println("\n CMGW Read: "+ Interface.CMGW);	      			 
	      		 }
	      		 if( this.getPacketSize() == 12 && dados[indice] == 'C' && dados[indice + 1] == 'M' && dados[indice+2] == 'S' && dados[indice+3] == 'S' && Interface.CMGW != 10000000)  {
	      			 
	      			 //if sending fails:
	      			 //<CR><LF>+CMS ERROR:<err><CR><LF> total de 16 characters
	      			 
	      			 //if sending successful
	      			 //<CR><LF>+CMSS:<mr>[,<ackpdu>]<CR><LF><CR><LF><OK><CR><LF>
	      			 
	      			Interface.smssent= true;
	      			System.out.println(" Read " +dados[indice]+dados[indice+1]+dados[indice+2]+dados[indice+3]+dados[indice+4]+dados[indice+5]+dados[indice+6]+dados[indice+7]+dados[indice+8]);
	      			//int cmss = (int)(dados[indice+6]-'0')*100 + (int)(dados[indice+7]-'0') * 10 + (int)(dados[indice+8]-'0');
	      			System.out.println(" SMS SENT! ");
	      		}
	      		 
	      	 }     	 
	      	 indice = indice + 1;
	      	 //System.out.println(cr);
	      }
	   
	   }	
	}

