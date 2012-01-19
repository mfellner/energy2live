package st.energy2live.data.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import st.energy2live.data.track.TrackLog;

public class User implements Serializable {
    
	private static final long serialVersionUID = 2023609377647660695L;
	
	private String nickName;
	private String password;
    private String fullName;
    private String email;
    private String homePage;
    private int privacy;
    private List<TrackLog> tracks;

    public User(String nickName, String password, String fullName, String email, 
    		String homePage, int privacy) {
    	
        this.nickName = nickName;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.homePage = homePage;
        this.privacy = privacy;
        
        tracks = new ArrayList<TrackLog>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    
    
    public void addTrackLog(TrackLog trackLog){
        this.tracks.add(trackLog);
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
    
}
