package at.tugraz.kmi.energy2live.remote;

import st.energy2live.data.user.User;
import at.tugraz.kmi.energy2live.model.E2LUser;

public class E2LUserAdapter extends User {
	private static final long serialVersionUID = -5064279140503235850L;

	public E2LUserAdapter(E2LUser u) {
		super(u.getName(), u.getPassword(), u.getFullName(), u.getEmail(), u.getHomePage(), u.getPrivacy());
	}
}
