import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientSocket {
	final static int PORT = 1234;
	static boolean succesed = false;

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("Serveur: Hello, please enter your pesudo :)");
		Scanner scn = new Scanner(System.in);
		Socket s = new Socket("localhost", PORT);

		DataInputStream dis = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		do {
			String pesudo = scn.nextLine();
			dos.writeUTF(pesudo);
			succesed = dis.readBoolean();
			if (!succesed) {
				System.out.println("Sorry, the pesudo has already been used...Please choose another one.");
			}
		} while (!succesed);

		// sendMessage thread
		Thread sendMessage = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {

					// read the message to deliver.
					String msg = scn.nextLine();

					try {
						dos.writeUTF(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		// readMessage thread
		Thread readMessage = new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					try {
						String msg = dis.readUTF();
						System.out.println(msg);
					} catch (IOException e) {
						// Error comes: When server closed socket
					}
				}
			}
		});
		sendMessage.start();
		readMessage.start();

	}

}
