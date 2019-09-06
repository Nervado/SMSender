package iHM;

import com.fazecast.jSerialComm.SerialPort;
//import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public final class Interface {	
	final static int BAUD_RATE = 9600;
	final static int BIT_PARITY = 0;
	final static int STOP_BITS = 1;
	final static int DATA_BITS = 8;
	public static int CMGW = 10000000;	
	public static boolean cr = false;
	public static boolean wr = false;
	public static boolean emailreceveid = false;
	public static boolean smsatempt = false;
	public static boolean smssent= false;
	public static String MSG;
	public static String CELNUBR1 = "\"+55XXXXXXXXXXX\"";//phonenumber
	
	public static String MYPATH = "C:\\Users\\adm\\eclipse-workspace\\SMSender\\Enviadas";
	//public static String MYPATH = "C:\\Documents and Settings\\Administrador\\Desktop\\APP\\Enviadas";
	public static void main(String[] args) {	
		
		//cria arquivos
		createFiles(); 
		
		//cria escaner para parada do programa
		Scanner pare = new Scanner(System.in);
		//cria inteiro para receber o comando de parada		
		//int stop = 0 ;		
		
		
		System.out.println("Digite 1 para parar o programa: ");
		
		pare.close();
		  
		while(true) {
				
		//System.out.println("Vou parar pois voce digitou o: "+stop);				
		//cria um leitor de email com protocolo imap		
		ReaderMail myInBox = new ReaderMail(); 		
		try {			
			//le caixa de email
			myInBox.readMails(); //indica na variavel emailreceveid setada em true quando um email novo � identificado			
			if(emailreceveid == true) {//cheggou email				
				//inicia porta serial		
				SerialPort serialPort = iniciaSerialPort(); //inicia a porta serial
				//envia sms
				sendSMS(serialPort,CELNUBR1);//envia sms
				smsatempt = true;
				if(smssent== true){//a variavel smssent � setada em true indicando que o sms foi enviado com sucesso
					//fecha a porta
					closeSerialPort(serialPort); //feche a porta
					smssent = false; //smssent em false
					emailreceveid = false; //email tratado
					CMGW = 10000000; //cmgw nulo
					smsatempt = false;
				}
				else {
					closeSerialPort(serialPort); 
					smssent = false; //smssent em false
					emailreceveid = false; //email tratado
					CMGW = 10000000; //cmgw nulo
					if(smsatempt == false) {}
					else {System.out.println("Falha no envio do sms");}
					
					
				} 
				//envio do sms falhou e agoraa???
				
			}			
		} catch (Exception e) {
			System.out.println("Failed to connect email server...");
			e.printStackTrace();
		}			
		//espera um tempo de 10s 		
		//stop = pare.nextInt();
		try { Thread.sleep(10000); } catch (Exception e) { e.printStackTrace(); }		
		}		
		
		
	}
    public static void closeSerialPort(SerialPort COMMx) {
    	
    	String portName  = COMMx.getDescriptivePortName();
		COMMx.closePort();
		System.out.println("\n "+portName+" closed ");
    	
    }
	public static void sendDATA(SerialPort COMMx, String dado){
		//OutPutStream out = new Outputstrea;
		if(COMMx.isOpen()) {
			COMMx.writeBytes(dado.getBytes(),dado.length());
			System.out.println("\n Send: \n "+ dado);		
		}
		else {System.out.println("\n "+COMMx.getSystemPortName()+" not ready!");}
	}
	public static void set_cr(boolean b){
		cr = b;
	}
	public static boolean get_cr(){
		return cr;
	}	
	public static String read_txt(String fileName) {

		//instancia pasta e arquivo
		File Enviadas = new File(MYPATH);
		//File Enviadas = new File("C:\\Users\\U4LW\\Desktop");
		File status = new File(Enviadas,fileName);
		//fim das instancias

		//String que ira receber cada linha do arquivo
		String line = "";
		String line_ = "";

		try {
			//indica o arquivo a ser lido
			FileReader fileReader = new FileReader(status);
			//criar o objeto bufferdReader que possui o metodo de leitura readLine()
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine())!= null) {
				line_ = line_ + line + "\n";
			}
			fileReader.close();
			bufferedReader.close();
			//return line_;
		} catch (IOException e){e.printStackTrace();}
		return line_;
	}
	public static void count_txt() {

		//cria o MAP e um string
		Map<Character,Integer> map = new HashMap<Character,Integer>();
		StringBuffer string = new StringBuffer();
		//le o arquivo e armazena em um StringBuffer
		try {
			//Cria o File reader, String linha e o armazena a leitura no BufferedReader
			FileReader reader = new FileReader(MYPATH+"\\status.txt");
			//File Enviadas = new File("C:\\Users\\U4LW\\Desktop");//modificar o caminho para o computador usado
			BufferedReader leitor = new BufferedReader(reader);
			String linha = null;
			while((linha = leitor.readLine()) != null){
				string.append(linha+"\n");
			}
			leitor.close();//fecha leitor
		}catch (Exception ex) {ex.printStackTrace();}
		//boolean endof = false;

		//armazena a quantidade cada letra no MAP
		int contador = 0;
		for(int i = 0; i < string.length();i++)
			if(map.get(string.charAt(i))==null) {
				map.put(string.charAt(i),1);
				contador = contador + 1;
			}
			else {
				map.put(string.charAt(i),map.get(string.charAt(i))+1);
				contador = contador + 1;}
		//imprime a quantidade de cada letra
		//Object[] a = map.keySet().toArray();
		//Iterator<Integer> i = map.values().iterator();
		//System.out.println("Quantidade de Letras ");

		//for(int j =0;j<a.length;j++) {
		//System.out.println(a[j] +" = "+ i.next() + "\n");
		//}
		System.out.println(" Total de caracteres no arquivo status.txt: "+contador);
		if(contador < 160) {System.out.println(" Quantidade dentro dos limites de sms...");}
		else {System.out.println(" Quantidade fora dos limites de sms...");}
		return;		
	}
	public static void exibir(String S) {
		JOptionPane.showMessageDialog(null, "\n " + S + " \n");
		return;
	}
	public static void createFiles() {//cria pasta enviadas e arquivos status.txt e statusold;
		
		
		//Cria pasta onde ser�o armazendas as mensagens enviadas em txt.
				File Enviadas = new File(MYPATH);	
				//File Enviadas = new File("C:\\Users\\U4LW\\Desktop");
				if(Enviadas.exists()){}//Nothing to do
				else{boolean statusDir = Enviadas.mkdir();	System.out.print(statusDir);}
				//diretorio criado

				//criar arquivo de texto old e atual
				File status = new File(Enviadas,"status.txt");
				File statusold = new File(Enviadas,"statusold.txt");
				if(status.exists() && statusold.exists()){
					count_txt(); //conta carcters			
				}//Something to do
				else{

					try {
						boolean statusFile = status.createNewFile();
						boolean statusFileold = statusold.createNewFile();
						System.out.print(statusFile +","+ statusFileold);
						count_txt(); //conta carcters
					} catch(IOException e){e.printStackTrace();}	

				}		

				//fim da criacao do arquivo	

		
	}
	public static void write_txt(String texto, String fileName) {
		// TODO Auto-generated method stub
		//escreve num arquivo especificado 		
		//le o arquivo e armazena em um StringBuffer
		try {
			//Cria o File reader, String linha e o armazena a leitura no BufferedReader
			FileWriter writer = new FileWriter(MYPATH+"\\"+fileName);
			//File Enviadas = new File("C:\\Users\\U4LW\\Desktop");//modificar o caminho para o computador usado
			BufferedWriter escritor = new BufferedWriter(writer);
			escritor.write(texto);
			escritor.close();//fecha leitor
		}catch (Exception ex) {ex.printStackTrace();}		
		
	}
	public static SerialPort iniciaSerialPort() {		
		
		//sleciona porta do sistema operacional
		SerialPort[] ports = SerialPort.getCommPorts();//pega lista de portas
		System.out.println("\n Select a port: \n");
		//exibir("\n Select a port: \n");
		int i = 1;
		//String ms;
		for(SerialPort port : ports) //conta as portas abertas disponiveis pelo sistema operacional
		{
			//ms = port.getSystemPortName();			
			System.out.println(" "+ i++ + ": " + port.getSystemPortName());//recupera o nome da porta
			//exibir(" "+ (i - 1) + ": " + port.getSystemPortName());
		}
		//Scanner s = new Scanner(System.in); //Scanner de entrada de texto
		//int chosenPort = 4;//= s.nextInt();		
		SerialPort serialPort = SerialPort.getCommPort("COM9");
		//SerialPort serialPort = SerialPort.getCommPort("COM2");
		if(serialPort.openPort()) {//informa se a porta foi aberta com sucesso			
			System.out.println("\n Port opened successfully.");
			serialPort.setComPortParameters(9600,8,1,SerialPort.NO_PARITY);
			serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);		
			return serialPort;}
		else {
			System.out.println("\n Unable to open port");
			//s.close();
			return null;
		}//fim da capiturra, informa se possivel a abertura da porta 

		//set por parametrics
		
		//

		//end of parametros
		
		
		
		
		
	}
	public static String set_sms(String status, String statusold) {
	
	
	
	/** 
	 * 
	 * [      status      ]
	 *   A B C A B C D A B
	 *   1 2 3 4 5 6 7 8 9
	 * 
	 * A B C = Compressores
	 * A B C D = Geradores
	 * A B = Urvs 
	 * 
	 * */
	
	
	
	//conta maquinas ligadas
	int conta = 0;
	int conta_mcs = 0;
	int conta_tgs = 0;
	int conta_urv = 0;
	
	//chars
	String ad = "ABCD";
	String tmp = "";
	String flag = ""; 
	char[] abcd = ad.toCharArray();	
	
	
	//old events
	char[] eventos_compressao_old = statusold.substring(0,3).toCharArray(); //eventos relativos a compress�o
	char[] eventos_geracao_old = statusold.substring(3,7).toCharArray(); // eventos relativos a gera��o
	char[] eventos_urv_old = statusold.substring(7,9).toCharArray(); //eventos relativos a urv		
	
	//new events
	char[] eventos_compressao = status.substring(0,3).toCharArray(); //eventos relativos a compress�o
	char[] eventos_geracao = status.substring(3,7).toCharArray(); // eventos relativos a gera��o
	char[] eventos_urv = status.substring(7,9).toCharArray(); //eventos relativos a urv	
	 
	for(int i = 0;i <eventos_compressao_old.length;i++) {		
		if(eventos_compressao_old[i] != eventos_compressao[i]) {flag = "\n MCS STATUS CHANGED! ";}
			//tmp = tmp + "Evento na compressao:";
			
			if(eventos_compressao[i] == '1') { //ligou
				conta = conta + 1;
				conta_mcs = conta_mcs + 1;
				tmp= tmp + "\n MC"+abcd[i]+": ON";
				//System.out.println("\n MC"+abcd[i]+": ON");				
			}
			else {//desligou				
				tmp= tmp + "\n MC"+abcd[i]+": OFF";
				//System.out.println("\n MC"+abcd[i]+":OFF");				
			}					
		//}
	}
	tmp = tmp + flag;
	flag = "";//esvazia flag	
	for(int i = 0;i <eventos_geracao_old.length;i++) {		
		if(eventos_geracao_old[i] != eventos_geracao[i]) {flag = "\n TGS STATUS CHANGED! ";}
			//tmp = tmp + "Evento na geracao:";
			
			if((char)eventos_geracao[i] == '1') { //ligou
				conta_tgs = conta_tgs + 1;
				conta = conta + 1;
				tmp= tmp + "\n TG"+abcd[i]+": ON";
				//System.out.println("\n TG"+abcd[i]+": ON");				
			}
			else {//desligou				
				tmp= tmp + "\n TG"+abcd[i]+": OFF";
				//System.out.println("\n TG"+abcd[i]+":OFF");				
			}					
		//}
	}
	
	tmp = tmp + flag;//esvazia flag
	flag = "";
	
	for(int i = 0;i <eventos_urv_old.length;i++) {		
		if(eventos_urv_old[i] != eventos_urv[i]) {flag = "\n URV STATUS CHANGED! ";}
			//tmp = tmp + "Evento na urv:";
			//conta = conta + 1;
			if(eventos_urv[i] == '1') { //ligou
				conta = conta + 1;
				conta_urv = conta_urv + 1;
				tmp = tmp + "\n CB"+abcd[i]+": ON";
				//System.out.println("\n CB"+abcd[i]+": ON");				
			}
			else {//desligou				
				tmp = tmp + "\n CB"+abcd[i]+": OFF";
				//System.out.println("\n CB"+abcd[i]+":OFF");				
			}					
		//}
	}

	tmp = tmp + flag;
	flag = "";//esvazia a flag
System.out.println(tmp);	
if(conta < 2 ) {tmp = "P53 SD3 EVENT OR P.I PROBLEM";}

if(tmp == "") {tmp = "NO EVENTS";}

return tmp;	
}
	public static void sendSMS(SerialPort serialPort, String cellnumber) {
		
				//AT+CMGD=<index>[,<delflag>] : sintaxe de comando para apagar mensangens da memoria do modem
				String msg  = "AT+CMGD=7,4\r\n";//apaga todas as mensagens do modem
				sendDATA(serialPort,msg);
				//cria listener para ouvir a porta
				PacketListener listener = new PacketListener();
				//associa listener a porta
				serialPort.addDataListener(listener);
				//ajusta o tamanho de pacote ouvido para 1 byte
				listener.setSizeOfPacket(1);
				//espera resposta da porta por 2 segundos
				try { Thread.sleep(2000); } catch (Exception e) { e.printStackTrace(); }
				serialPort.removeDataListener();
				//escreva na porta comando AT para iniciar negocia��o
				msg = "AT\r\n";
				sendDATA(serialPort,msg);
				serialPort.addDataListener(listener);
				listener.setSizeOfPacket(1);
				try { Thread.sleep(2000); } catch (Exception e) { e.printStackTrace(); }
				serialPort.removeDataListener();
				//serialPort.closePort();
				//fim da
				if(cr== true){			
					cr = false;
					sendDATA(serialPort,"AT+CMGF=1\r\n");
					serialPort.addDataListener(listener);		
					try { Thread.sleep(2000); } catch (Exception e) { e.printStackTrace(); }
					serialPort.removeDataListener();		
				}
				if(cr== true){			
					cr = false;
					//sendDATA(serialPort,"AT+CMGW=\"+5521980076262\"\r\n");	
					sendDATA(serialPort,"AT+CMGW="+cellnumber+"\r\n");
					serialPort.addDataListener(listener);			
					listener.setSizeOfPacket(12);
					try { Thread.sleep(2000); } catch (Exception e) { e.printStackTrace(); }
					serialPort.removeDataListener();

				}//

				if(wr == true){	

					wr = false;
					char controlz = 26;
					MSG = (String)Interface.set_sms(Interface.read_txt("status.txt").substring(14,23), read_txt("statusold.txt").substring(14,23));
					//sendDATA(serialPort,(String)Interface.set_sms(Interface.read_txt("status.txt").substring(14,23), read_txt("statusold.txt").substring(14,23))+(char)controlz);		
					sendDATA(serialPort,MSG+"\n "+controlz);			
					serialPort.addDataListener(listener);	
					listener.setSizeOfPacket(12);
					try { Thread.sleep(2000); } catch (Exception e) { e.printStackTrace(); }
					serialPort.removeDataListener();
					if(cr== true){			
						cr = false;
						sendDATA(serialPort,"AT+CMSS="+CMGW+"\r\n");		//comentar essa linha pra o SMS n�o ser enviado.	
						serialPort.addDataListener(listener); //+ C M Sxzxz S :   1 5 2
						try { Thread.sleep(5000); } catch (Exception e) { e.printStackTrace(); }
						serialPort.removeDataListener();}			

				}

	}
	}
