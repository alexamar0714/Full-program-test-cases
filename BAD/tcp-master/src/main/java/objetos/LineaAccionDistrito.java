package objetos;
/**
@author          DGTIC - STP
@email			 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6f0b081b060c2f1c1b1f41080019411f16">[email protected]</a>
*/

public class LineaAccionDistrito {
	
	protected int institucion_id;
	protected String institucion;
	protected int linea_accion_id;
	protected String linea_accion;
	
	protected String accion_unidad_medida;
	protected double cantidad_ejecutada_hoy; //anteriormente se llamaba "sum"
	protected int anho; // anteriormente se llamaba date_part
	protected double suma_programada_anho; // anteriormente se llamaba suma_programada
	protected double suma_programada_hoy;
	protected double costo_ejecutado;
	protected double costo_programado_anho;
	protected double costo_programado_hoy;
	protected double linea_accion_meta;
	protected double hito_cantidad_ejecutado_hoy;
	protected String orden;
	protected int orden_linea_accion;
	protected int accion_departamento_id;
	protected int accion_unidad_medida_id;
	protected int accion_distrito_id;
	
	
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
	public int getLinea_accion_id() {
		return linea_accion_id;
	}
	public void setLinea_accion_id(int linea_accion_id) {
		this.linea_accion_id = linea_accion_id;
	}
	public String getLinea_accion() {
		return linea_accion;
	}
	public void setLinea_accion(String linea_accion) {
		this.linea_accion = linea_accion;
	}
	public String getAccion_unidad_medida() {
		return accion_unidad_medida;
	}
	public void setAccion_unidad_medida(String accion_unidad_medida) {
		this.accion_unidad_medida = accion_unidad_medida;
	}
	public double getCantidad_ejecutada_hoy() {
		return cantidad_ejecutada_hoy;
	}
	public void setCantidad_ejecutada_hoy(double cantidad_ejecutada_hoy) {
		this.cantidad_ejecutada_hoy = cantidad_ejecutada_hoy;
	}
	public int getAnho() {
		return anho;
	}
	public void setAnho(int anho) {
		this.anho = anho;
	}
	public double getSuma_programada_anho() {
		return suma_programada_anho;
	}
	public void setSuma_programada_anho(double suma_programada_anho) {
		this.suma_programada_anho = suma_programada_anho;
	}
	public double getSuma_programada_hoy() {
		return suma_programada_hoy;
	}
	public void setSuma_programada_hoy(double suma_programada_hoy) {
		this.suma_programada_hoy = suma_programada_hoy;
	}
	public double getCosto_ejecutado() {
		return costo_ejecutado;
	}
	public void setCosto_ejecutado(double costo_ejecutado) {
		this.costo_ejecutado = costo_ejecutado;
	}
	public double getCosto_programado_anho() {
		return costo_programado_anho;
	}
	public void setCosto_programado_anho(double costo_programado_anho) {
		this.costo_programado_anho = costo_programado_anho;
	}
	public double getCosto_programado_hoy() {
		return costo_programado_hoy;
	}
	public void setCosto_programado_hoy(double costo_programado_hoy) {
		this.costo_programado_hoy = costo_programado_hoy;
	}
	public double getLinea_accion_meta() {
		return linea_accion_meta;
	}
	public void setLinea_accion_meta(double linea_accion_meta) {
		this.linea_accion_meta = linea_accion_meta;
	}
	public double getHito_cantidad_ejecutado_hoy() {
		return hito_cantidad_ejecutado_hoy;
	}
	public void setHito_cantidad_ejecutado_hoy(double hito_cantidad_ejecutado_hoy) {
		this.hito_cantidad_ejecutado_hoy = hito_cantidad_ejecutado_hoy;
	}
	public String getOrden() {
		return orden;
	}
	public void setOrden(String orden) {
		this.orden = orden;
	}
	public int getOrden_linea_accion() {
		return orden_linea_accion;
	}
	public void setOrden_linea_accion(int orden_linea_accion) {
		this.orden_linea_accion = orden_linea_accion;
	}
	public int getAccion_departamento_id() {
		return accion_departamento_id;
	}
	public void setAccion_departamento_id(int accion_departamento_id) {
		this.accion_departamento_id = accion_departamento_id;
	}
	public int getAccion_unidad_medida_id() {
		return accion_unidad_medida_id;
	}
	public void setAccion_unidad_medida_id(int accion_unidad_medida_id) {
		this.accion_unidad_medida_id = accion_unidad_medida_id;
	}
	public int getAccion_distrito_id() {
		return accion_distrito_id;
	}
	public void setAccion_distrito_id(int accion_distrito_id) {
		this.accion_distrito_id = accion_distrito_id;
	}
	
	
	
	
	
		
}
