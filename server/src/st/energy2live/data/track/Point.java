/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.energy2live.data.track;

/**
 *
 * @author Gernot Solic <gsolic@student.tugraz.at>
 */
public class Point {
    
    private double longitude;
    private double latitude;

    public Point(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
}
