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

	public void stopThread(String name) {
		// Stop thread by pesudo
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().equals(name)) {
				t.stop();
				break;
			}
		}
	}

	public void deleteUser() throws IOException {
		// closing resources
		this.s.close();
		this.dis.close();
		this.dos.close();
		// remove from client list
		ServeurSocket._clients.remove(this);
		// stop user's thread
		stopThread(this.name);
	}

	@Override
	public void run() {
		String received;
		String ins;
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
					client.dos.writeUTF(this.name + " : " + received);
				}
			} catch (IOException e) {
				// Exit abnormally, User forcibly exits program
				System.out.println("User <" + this.name + "> disconnected......");
				try {
					deleteUser();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
		// Exit the chat normally by enter "exit"
		try {
			deleteUser();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
