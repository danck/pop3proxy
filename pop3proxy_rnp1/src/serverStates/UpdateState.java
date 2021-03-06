package serverStates;

import java.io.IOException;
import java.util.logging.Level;

import main.Mailbox;
import main.Pop3ServerContext;
import main.ProxyServer;

public class UpdateState extends Pop3ServerState {
	private Mailbox mbox;
	
	public UpdateState(Pop3ServerContext context, Mailbox mbox) {
		super(context);
		this.mbox = mbox;
		handleState();
	}

	@Override
	public void handleState() {
		System.out.println("S: Switched to UpdateState");
		mbox.update();
		mbox.reset();
		try {
			okay("Tschuess");
			mbox.unlock();
			context.getReader().close();
			context.getWriter().close();
			context.getSocket().close();
		} catch (IOException e) {
//			ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
