package DG.DA;

public class MaintenanceWorkDTO {
	public MaintenanceWorkDTO(Double[] coordinates, Integer demand, String type, String waypoint) {
		this.coordinates = coordinates;
		this.demand = demand;
		this.type = type;
		this.waypoint = waypoint;
	}
	
	public Double[] coordinates;
	public Integer demand;
	public String type;
	public String waypoint;	
	
}
