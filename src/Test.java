import com.wf.server.Server;


public class Test {
	public static void main(String[] args) throws Exception {
		Server server = new Server(9999, "com.servlet") ;
		server.init() ;
	}
}