package objetos;
/**
@author          DGTIC - STP
@email			 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e08487948983a0939490ce878f96ce9099">[email protected]</a>
*/

public class Entidad {
	protected int institucion_id;
	protected String institucion;
	public int getInstitucion_id() {
		return institucion_id;
	}
	public void setInstitucion_id(int institucion_id) {
		this.institucion_id = institucion_id;
	}
	public String getInstitucion() {
		return institucion;
	}
	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

}