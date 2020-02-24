
import java.net.*;

class MessageReceiver implements Runnable {
	DatagramSocket sock;
	byte buf[];
        LoginClient login;
        
	MessageReceiver(DatagramSocket s, LoginClient login) {
		sock = s;
		buf = new byte[1024];
                login = login;
	}

	// metodo em thread, responsavel por escutar mensagem do servidor
	public void run() {
		while (true) {
			try {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				sock.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				// Verifica se o servidor retornou com um ERRO, que significa um close;
				if (received.equals("ERRO")) {
					login.dialogue.setTxtArea(received);
                                        login.dialogue.scroolAuto();
					Thread.sleep(1000);
					System.exit(0);
				} else if (received.equals("DESCONECTANDO")) {
					login.dialogue.setTxtArea(received);
                                        login.dialogue.scroolAuto();
					Thread.sleep(1000);
					System.exit(0);
				} else {
					login.dialogue.setTxtArea(received + "\n");
                                        login.dialogue.scroolAuto();
				}
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
}

public class Client {
	public Client() {

	}
}