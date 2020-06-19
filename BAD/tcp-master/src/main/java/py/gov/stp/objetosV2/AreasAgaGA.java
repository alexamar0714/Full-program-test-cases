package py.gov.stp.objetosV2;

import java.util.ArrayList;
import java.util.List;
/**
@author          DGTIC - STP
@email			 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="187c7f6c717b586b6c68367f776e366861">[email protected]</a>
*/
public class AreasAgaGA {
	protected String name;
	protected String colour = "#39ac39";
	protected List<LineaAccionGA> children = new ArrayList<LineaAccionGA>();
	
	public String getNombre() {
		return name;
	}
	public void setNombre(String name) {
		this.name = name;
	}
	public List<LineaAccionGA> getLineaAccion() {
		return children;
	}
	public void setLineaAccion(List<LineaAccionGA> children) {
		this.children = children;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	
}
