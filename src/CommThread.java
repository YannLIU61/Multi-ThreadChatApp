import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class CommThread implements Runnable {
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CommThread(String name, DataInputStream dis, DataOutputStream dos, Socket s) {
		this.name = name;
		this.dis = dis;
		this.dos = dos;
		this.s = s;
	}

	public void stopThread() {
		// Stop thread by pesudo
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().equals(this.name)) {
				t.stop();
				break;
			}
		}
	}

	@Override
	public void run() {
		String received;
		CommThread clientLeft = null;
		while (true) {
			try {
				received = dis.readUTF();

				if (received.equals("exit")) {
					for (CommThread client : ServeurSocket._clients) {

						if (client.getName().equals(this.name)) {
							clientLeft = client;
						}
						client.dos.writeUTF("**** " + this.name + " left the Chat****");
					}
					break;
				}

				// the vector storing client of users
				for (CommThread client : ServeurSocket._clients) {
					// output stream
					client.dos.writeUTF(this.name + " : " + received);
				}
			} catch (IOException e) {
			}

		}
		try {
			// closing resources
			this.s.close();
			this.dis.close();
			this.dos.close();

			// remove from client list
			ServeurSocket._clients.remove(clientLeft);
			// stop user's thread
			stopThread();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
