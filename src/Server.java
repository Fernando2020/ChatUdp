import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
	private final static int limiteUsuario = 100;
	private static Scanner scan;
	private static int port;
	private final static int BUFFER = 1024;
	private int countCli = 0;

	private DatagramSocket socket;
	private ArrayList<InetAddress> clientAddresses; // Armazena endereco dos clientes
	private ArrayList<Integer> clientPorts; // Armazena porta dos clientes
	private HashSet<String> existingClients; // Armazena id cliente

	public Server(String p) throws IOException {
		port = Integer.parseInt(p);
		socket = new DatagramSocket(port);
		clientAddresses = new ArrayList<InetAddress>();
		clientPorts = new ArrayList<Integer>();
		existingClients = new HashSet<String>();
	}

	public void run() {
		byte[] buf = new byte[BUFFER];
		byte[] data;
		String contentPrivate = "";
		while (true) {
			try {
				Arrays.fill(buf, (byte) 0);
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);

				String content = new String(buf, 0, buf.length);

				InetAddress clientAddress = packet.getAddress();
				int clientPort = packet.getPort();

				String id = clientAddress.toString() + "," + clientPort;

				if (content.trim().contains(":TCHAU")) { // if - content = TCHAU, retira usuario das listas e o
															// desconecta
					contentPrivate = "DESCONECTANDO";
					data = (contentPrivate).getBytes();
					packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
					socket.send(packet);
					this.countCli = this.countCli - 1;
					content = content.replace("TCHAU", " SAIU DA CONVERSA!");
				} else { // else - add novo cliente / else - atingido o valor maximo de cliente
					if (!existingClients.contains(id)) {
						if (countCli < limiteUsuario) {
							existingClients.add(id);
							clientPorts.add(clientPort);
							clientAddresses.add(clientAddress);
							this.countCli = this.countCli + 1;

							content = content.replace("OI", "ENTROU NA CONVERSA");
						} else {
							contentPrivate = "ERRO";
							data = (contentPrivate).getBytes();
							packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
							socket.send(packet);
							continue;
						}
					}
				}
				// envia mensagem para todos os usuÃ¡rios online
				data = (content).getBytes();
				for (int i = 0; i < clientAddresses.size(); i++) {
					InetAddress cl = clientAddresses.get(i);
					int cp = clientPorts.get(i);
					packet = new DatagramPacket(data, data.length, cl, cp);
					socket.send(packet);
				}
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}

}