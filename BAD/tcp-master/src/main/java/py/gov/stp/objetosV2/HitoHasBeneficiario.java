package py.gov.stp.objetosV2;

import java.sql.Date;
/**
@author          DGTIC - STP
@email			 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2b4f4c5f42486b585f5b054c445d055b52">[email protected]</a>
*/
public class HitoHasBeneficiario {
	protected int hitoId;
	protected int hitoAccionId;
	protected int beneficiarioId;
	protected Date fechaInsercion;
	protected Date fechaActualizacion;	
    protected String usuarioResponsable;
	protected boolean borrado = false;
	protected int version;
	
	public int getHitoId() {
		return hitoId;
	}
	public void setHitoId(int hitoId) {
		this.hitoId = hitoId;
	}
	public int getHitoAccionId() {
		return hitoAccionId;
	}
	public void setHitoAccionId(int hitoAccionId) {
		this.hitoAccionId = hitoAccionId;
	}
	public int getBeneficiarioId() {
		return beneficiarioId;
	}
	public void setBeneficiarioId(int beneficiarioId) {
		this.beneficiarioId = beneficiarioId;
	}
	public boolean isBorrado() {
		return borrado;
	}
	public void setBorrado(boolean borrado) {
		this.borrado = borrado;
	}
	public void changeBorrado(){
		this.borrado=!borrado;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public Date getFechaInsercion() {
		return fechaInsercion;
	}
	public void setFechaInsercion(Date fechaInsercion) {
		this.fechaInsercion = fechaInsercion;
	}
	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}
	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	public String getUsuarioResponsable() {
		return usuarioResponsable;
	}
	public void setUsuarioResponsable(String usuarioResponsable) {
		this.usuarioResponsable = usuarioResponsable;
	}

}
