package at.tugraz.kmi.energy2live.remote;

import android.util.Log;
import at.tugraz.kmi.energy2live.model.E2LActivity;

import com.thoughtworks.xstream.XStream;

public class E2LNetworkConnection {
	public E2LNetworkConnection() {
	}

	public <T> String serializeObject(T object) {
		Log.d("E2L", "Try serializing point...");
		XStream xstream = new XStream();
		// xstream.alias(object.getClass().getName(), object.getClass());
		return xstream.toXML(object);
	}

	public void sendActivityToServer(E2LActivity activity) {
		E2LTrackLogAdapter adapter = new E2LTrackLogAdapter(activity);
		String xml = serializeObject(adapter.getTrackLog());

		Log.d("E2L", "XML:\n" + xml);
		// TODO send to server!
	}
}
