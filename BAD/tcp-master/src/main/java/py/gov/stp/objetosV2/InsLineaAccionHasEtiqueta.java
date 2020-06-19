package py.gov.stp.objetosV2;
/**
@author          DGTIC - STP
@email			 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4a2e2d3e23290a393e3a642d253c643a33">[email protected]</a>
*/
public class InsLineaAccionHasEtiqueta {
	protected int insLineaAccionId;
	protected int etiquetaId;
	protected int version;
	protected boolean borrado;
	protected String fechaActualizacion;
	protected String fechaInsercion;
	protected String usuarioResponsable;
	
	public int getInsLineaAccionId() {
		return insLineaAccionId;
	}
	public void setInsLineaAccionId(int insLineaAccionId) {
		this.insLineaAccionId = insLineaAccionId;
	}
	public int getEtiquetaId() {
		return etiquetaId;
	}
	public void setEtiquetaId(int etiquetaId) {
		this.etiquetaId = etiquetaId;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public boolean isBorrado() {
		return borrado;
	}
	public void setBorrado(boolean borrado) {
		this.borrado = borrado;
	}
	public String getFechaActualizacion() {
		return fechaActualizacion;
	}
	public void setFechaActualizacion(String fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	public String getFechaInsercion() {
		return fechaInsercion;
	}
	public void setFechaInsercion(String fechaInsercion) {
		this.fechaInsercion = fechaInsercion;
	}
	public String getUsuarioResponsable() {
		return usuarioResponsable;
	}
	public void setUsuarioResponsable(String usuarioResponsable) {
		this.usuarioResponsable = usuarioResponsable;
	}
	public void changeBorrado(){
		this.borrado=!borrado;
	}
}
