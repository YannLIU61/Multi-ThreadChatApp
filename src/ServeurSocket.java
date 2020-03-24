import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class ServeurSocket {
	static Vector<CommThread> _clients = new Vector<>();

	// Test user's pesudo
	public static boolean isAvailable(String name) {
		for (CommThread ct : ServeurSocket._clients) {
			// if the user is found, return false
			if (ct.getName().equals(name)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) throws IOException {
		ServerSocket conn = new ServerSocket(1234);
		Socket comm;
		String clientPesudo;
		while (true) {
			comm = conn.accept();
			System.out.println("A new client is connected!!");
			// obtain input and output streams, first comm with new client
			DataInputStream dis = new DataInputStream(comm.getInputStream());
			DataOutputStream dos = new DataOutputStream(comm.getOutputStream());
			// Gets user's pesudo
			clientPesudo = dis.readUTF();
			if (isAvailable(clientPesudo)) {
				dos.writeBoolean(true);
				System.out.println("Creating a new thread for this client...");
				// Create a new handler object for handling this request.
				CommThread ct = new CommThread(clientPesudo, dis, dos, comm);
				// add this client to  clients list
				_clients.add(ct);
				//Notif everyone a new user come in
				for (CommThread client : ServeurSocket._clients) {
					// output stream
					client.dos.writeUTF("---- "+ clientPesudo +" joins the chat ----" );
				}
				// Create and start a new Thread with this object.
				Thread t = new Thread(ct);
				t.setName(clientPesudo);
				t.start();
				
			}else
			{
				dos.writeBoolean(false);
			}

		}

	}

}
