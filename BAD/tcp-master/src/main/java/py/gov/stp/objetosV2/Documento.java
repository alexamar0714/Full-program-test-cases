package py.gov.stp.objetosV2;

import java.sql.Date;
/**
@author          DGTIC - STP
@email			 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="cfaba8bba6ac8fbcbbbfe1a8a0b9e1bfb6">[email protected]</a>
*/
public class Documento {
	protected int id;
	protected String nombre;
	protected String descripcion;
	protected String url;
	protected Date fechaInsercion;
	protected Date fechaActualizacion;	
    protected String usuarioResponsable;
    protected boolean borrado;
	


	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
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



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
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



	public boolean isBorrado() {
		return borrado;
	}



	public void setBorrado(boolean borrado) {
		this.borrado = borrado;
	}
	
	
	
	public void changeBorrado(){
		this.borrado=!borrado;
	}

}
