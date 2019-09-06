package iHM;

import java.util.*;
import javax.mail.*;
import javax.mail.search.FlagTerm;

public class ReaderMail{
	
	Properties properties = null;
	private Session session = null;
	private Store store = null;
	private Folder inbox = null;
	private String userName = "//xxxxx@provedor";  //xxxxx@provedor
	private String password = "senha";//senha
	
	public ReaderMail() {}

	public void readMails() throws Exception {
	
		properties = new Properties();
		properties.setProperty("mail.host", "imap.gmail.com");
		properties.setProperty("mail.port", "993");
		properties.setProperty("mail.transport.protocol", "imaps");
		
		//teste de conexao atraves de proxy
		
		//properties.setProperty("proxySet", "true");
		//properties.setProperty("socksProxyHost", "0.0.0.0";
		//properties.setProperty("socksProxyPort", "XXXX");
		
			
		//fim do codigo de teste
		
		
		session = Session.getInstance(properties,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});

		store = session.getStore("imaps");
		store.connect();
		inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_ONLY);

		//Message messages[] = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
		Message messages[] = inbox.search(new FlagTerm(new Flags(Flags.Flag.RECENT), false));
		//Message messages[] = inbox.getMessages(NEW)
		System.out.println(" Acessing email server....");
		System.out.println(" Number of mails = " + messages.length);
		System.out.println(" Number of unread messages: "+ inbox.getUnreadMessageCount());//verifica a quantidade de mensagens n�o lidas
		
		String sub;
		String sub_;
		
		for ( Message message : messages ) {//Teste chave=12345678
			//System.out.println("Subject: "+ message.getSubject());//todo ...metodo para pegar a chave
			sub = message.getSubject().toString();
			
			if (sub.length() <14) {sub_ = sub;}//filtra emails cujo tamanho do titulo seja menor que 14
			
			else {sub_ = sub.substring(0,14);}		
			
			if(sub_.equals(Interface.read_txt("status.txt").substring(0,14)))///teste que verifica o titulo
			{
				//
				Address[] from = message.getFrom();//
				System.out.println("-------------------------------");
				System.out.println("Date : " + message.getSentDate());
				System.out.println("From : " + from[0]);
				System.out.println("Subject: " + message.getSubject());
				processSubjetc(message.getSubject().toString());//
				Interface.emailreceveid = true; //indica que houve a confirma��o efetiva de um email de status v�lido.
				//habilita envio da mensagem 
				System.out.println("--------------------------------");
			}
			else
			{
				//System.out.println("not found");			
			}
		}
		inbox.close(true);
		store.close();

	}

	private void processSubjetc(String Subject) {
		//separa o assunto do email pra pegar o contador 	
		long tmp;
		String keepCount ;
		
		//incrementa contador
		tmp = Long.parseLong(Subject.substring(6,14)) + 1; 
		keepCount = Long.toString(tmp);		
		
		//salvar status antigo e contador atual
		Interface.write_txt("ALERTA"+Interface.read_txt("status.txt").substring(6,14)+Interface.read_txt("status.txt").substring(14,23), "statusold.txt");
		//salvar novo contador em status.txt
		Interface.write_txt("ALERTA"+keepCount+Subject.substring(14,23),"status.txt");//escreve no arquivo o estado atual das m�quinas e o valor do contador 
		return;				
	}
}