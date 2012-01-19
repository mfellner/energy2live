package st.energy2live.data.vehicle;


public abstract class Vehicle {
    
	protected String name = "Abstract Vehicle";
    protected String manuFacturer = "Abstract Manufacturer";
    protected int id = 0; 
    
    // Consumtion L/KM
    private double fuelConsumtion = 0.1;
    
    protected Vehicle(String name, String manuFacturer, double consumtion){
        this.name = name;
        this.manuFacturer = manuFacturer;
        this.fuelConsumtion = consumtion;
    }

    public double getFuelConsumtion() {
        return fuelConsumtion;
    }

    public void setFuelConsumtionPerKM(double consumtion) {
        this.fuelConsumtion = consumtion;
    }

    public String getManuFacturer() {
        return manuFacturer;
    }

    public void setManuFacturer(String manuFacturer) {
        this.manuFacturer = manuFacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
      
}
