package py.gov.stp.objetosV2;
/**
@author          DGTIC - STP
@email			 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7d191a09141e3d0e090d531a120b530d04">[email protected]</a>
*/
public class BeneficiarioGrupo {
	protected int id;
	protected int beneficiarioTipoId;
	protected String nombre;
	protected String descripcion;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBeneficiarioTipoId() {
		return beneficiarioTipoId;
	}
	public void setBeneficiarioTipoId(int beneficiarioTipoId) {
		this.beneficiarioTipoId = beneficiarioTipoId;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	

}
