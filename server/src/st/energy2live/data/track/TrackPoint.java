package st.energy2live.data.track;
import java.util.Date;

public class TrackPoint {
    
    private Date dateTime;
    private Point point;
        
    public TrackPoint(Date dateTime, double latitude, double longitude){
        this.dateTime = dateTime;
        this.point = new Point(longitude, latitude);
    }
    
    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
 
}
