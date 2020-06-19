package py.gov.stp.objetosV2;

import java.sql.Date;
/**
@author          DGTIC - STP
@email			 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a5c1c2d1ccc6e5d6d1d58bc2cad38bd5dc">[email protected]</a>
*/
public class HitoPrueba {
	protected String accion;
	protected String nombreHito;
	protected String tipoHito;
	protected String unidadMedida;
	protected int cantidadPrevista;
	protected int cantidadReal;
	protected Date fechaInsercion;
	protected Date fechaActualizacion;	
    protected String usuarioResponsable;
    
	
	public String getAccion() {
		return accion;
	}
	public void setAccion(String accion) {
		this.accion = accion;
	}
	public String getNombreHito() {
		return nombreHito;
	}
	public void setNombreHito(String nombreHito) {
		this.nombreHito = nombreHito;
	}
	public String getTipoHito() {
		return tipoHito;
	}
	public void setTipoHito(String tipoHito) {
		this.tipoHito = tipoHito;
	}
	public String getUnidadMedida() {
		return unidadMedida;
	}
	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
	public int getCantidadPrevista() {
		return cantidadPrevista;
	}
	public void setCantidadPrevista(int cantidadPrevista) {
		this.cantidadPrevista = cantidadPrevista;
	}
	public int getCantidadReal() {
		return cantidadReal;
	}
	public void setCantidadReal(int cantidadReal) {
		this.cantidadReal = cantidadReal;
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
