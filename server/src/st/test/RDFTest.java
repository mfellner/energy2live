package st.test;

import st.energy2live.rdf.DataConnection;
import st.energy2live.rdf.RDFController;

public class RDFTest {


	public static void main(String[] args) throws Exception {

		
		RDFController.clearAndLoadIDs();
		
		DataConnection data = new DataConnection();
		
		System.out.println("New user id: " + data.requestNewUserID());
		System.out.println("New user id: " + data.requestNewUserID());
		
		System.out.println("New track id: " + data.requestNewTrackID());
		System.out.println("New track id: " + data.requestNewTrackID());
	}

}
