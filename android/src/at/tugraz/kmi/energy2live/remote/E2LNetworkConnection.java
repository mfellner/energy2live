package at.tugraz.kmi.energy2live.remote;

import android.util.Log;
import at.tugraz.kmi.energy2live.model.E2LActivity;

import com.thoughtworks.xstream.XStream;

public class E2LNetworkConnection {
	public E2LNetworkConnection() {
	}

	public void sendActivityToServer(E2LActivity activity) {
		E2LTrackLogAdapter adapter = new E2LTrackLogAdapter(activity);
		String xml = serializeObject(adapter.getTrackLog());

		Log.d("E2L", "XML:\n" + xml);
		// TODO send to server!
	}

	private <T> String serializeObject(T object) {
		XStream xstream = new XStream();
		return xstream.toXML(object);
	}
}
