package py.gov.stp.objetosV2;

import java.sql.Date;
/**
@author          DGTIC - STP
@email			 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="492d2e3d202a093a3d39672e263f673930">[email protected]</a>
*/
public class Evidencia {
	protected int id;
	protected String nombre;
	protected String descripcion;
	protected int lineaAccionId; 
	protected String lineaAccionNombre;
	protected String url;
	protected int wsId;	
	protected int version;
	protected boolean borrado;
	protected int avanceId;
	protected String avanceFecha;
	protected String urlDocumento;
	protected Double latitud;
	protected Double longitud;
	protected Date fechaInsercion;
	protected Date fechaActualizacion;	
    protected String usuarioResponsable; 
	
		
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
	public int getLineaAccionId() {
		return lineaAccionId;
	}
	public void setLineaAccionId(int lineaAccionId) {
		this.lineaAccionId = lineaAccionId;
	}
	public String getLineaAccionNombre() {
		return lineaAccionNombre;
	}
	public void setLineaAccionNombre(String lineaAccionNombre) {
		this.lineaAccionNombre = lineaAccionNombre;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getWsId() {
		return wsId;
	}
	public void setWsId(int wsId) {
		this.wsId = wsId;
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
	public int getAvanceId() {
		return avanceId;
	}
	public void setAvanceId(int avanceId) {
		this.avanceId = avanceId;
	}	
	public String getAvanceFecha() {
		return avanceFecha;
	}
	public void setAvanceFecha(String avanceFecha) {
		this.avanceFecha = avanceFecha;
	}
	public void changeBorrado(){
		this.borrado=!borrado;
	}
	public String getUrlDocumento() {
		return urlDocumento;
	}
	public void setUrlDocumento(String urlDocumento) {
		this.urlDocumento = urlDocumento;
	}		
	public Double getLatitud() {
		return latitud;
	}
	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}
	public Double getLongitud() {
		return longitud;
	}
	public void setLongitud(Double longitud) {
		this.longitud = longitud;
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
