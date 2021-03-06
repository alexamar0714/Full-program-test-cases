<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.jasig.cas.client.authentication.AttributePrincipalImpl"%>
<%@ page import="org.jasig.cas.client.authentication.AttributePrincipal"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>


<!DOCTYPE html>
<html>
  <head>
  <!--  ISO-8859-1 -->
 	 <%@ include file="/frames/head.jsp" %>
 	<!-- <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

   	<script src="https://cdnjs.cloudflare.com/ajax/libs/floatthead/1.2.10/jquery.floatThead.min.js"></script> -->	  
	<!-- <script src="frames/entidad.js" type="text/javascript"></script> -->
	<script type="text/javascript" src="dist/canvasjs/canvasjs.min.js" ></script>

	<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
	<link href="bootstrap/css/bootstrapslider.css" rel="stylesheet">

    <style type="text/css">
		/* Example 1 custom styles */
		#ex1Slider .slider-selection {
   			background: #BABABA;
  		}

    	/* Example 3 custom styles */
		#RGB {
    		height: 20px;
    		background: rgb(128, 128, 128);
  		}
		#RC .slider-selection {
		    background: #FF8282;
		}
		#RC .slider-handle {
			background: red;
		}
		#GC .slider-selection {
			background: #428041;
		}
		#GC .slider-handle {
			background: green;
		}
		#BC .slider-selection {
			background: #8283FF;
		}
		#BC .slider-handle {
			border-bottom-color: blue;
		}
		#R, #G, #B {
			width: 300px;
		}
    </style>
    
    



</head>
<body class="skin-blue sidebar-mini sidebar-collapse">

<% AttributePrincipal user = (AttributePrincipal) request.getUserPrincipal();%>
<% Map attributes = user.getAttributes(); 
if (user != null) { %>

<script>
	
	$(document).ready(function(){
		var entidadCas = "";
		entidadCas ="<%=attributes.get("entidad") %>";
		var usuarios = $.ajax({
			url:'/tablero/ajaxSelects?action=getUsuarios&usuario=<%=user.getName()%>',
		  	type:'get',
		  	dataType:'json',
		  	async:false       
		}).responseText;
		usuarios = JSON.parse(usuarios);
		usuarios = usuarios.usuarios;
		$("#nombreUsuario").append(usuarios[0].correo+" ("+usuarios[0].nivel_id+", "+usuarios[0].entidad_id+")");
		$("#PerfilUsuario").append(usuarios[0].nombre+" ("+usuarios[0].nivel_id+", "+usuarios[0].entidad_id+", "+entidadCas+")");
		
		var ciDestinatarios = $.ajax({
			url:'/tablero/ajaxSelects2?action=getCiDestinatarios',
		  	type:'get',
		  	dataType:'json',
		  	async:false       
		}).responseText;
		ciDestinatarios=JSON.parse(ciDestinatarios);
		
		
		function renderCiDestinatarios(){
			$('.box-body').html('');
			var tablaCiDestinatarios="";
			
			tablaCiDestinatarios ='<div class="table-responsive">'+
			'	              		 <table class="table table-hover table-bordered" id="dataTablesCiDestinatarios">'+
			'							<thead>'+
			'	                			<tr class="active" role="row">'+
			'									<th rowspan="1" colspan="16"><span class="glyphicon glyphicon-check selectAll"> Verificar con Identifc.</span></th>'+
			'								</tr>'+
			'	                			<tr class="active">'+
			'									<th class="text-center">Verificar con Identifc.</th>'+
			'									<th class="text-center">AvanceId</th>'+
			'									<th class="text-center">CI</th>'+
			'									<th class="text-center">Pobreza</th>'+
			'									<th class="text-center">Nombre</th>'+
			'									<th class="text-center">Apellido</th>'+
			'									<th class="text-center">Sexo</th>'+
			'									<th class="text-center">Estado Civil</th>'+
			'									<th class="text-center">Nacionalidad</th>'+
			'									<th class="text-center">Profesion</th>'+
			'									<th class="text-center">Fecha de Nac.</th>'+
			'									<th class="text-center">Año</th>'+
			'									<th class="text-center">Fecha Act</th>'+
			'									<th class="text-center">Fecha Ins</th>'+
			'									<th class="text-center">Editar</th>'+
			'									<th class="text-center">Borrar</th>'+
			'								</tr>'+
			'							</thead>'+
			'	                		<tbody id="cuerpoTablaCiDestinatarios">';

			for(var w=0; w<ciDestinatarios.length;w++)
			{
				if(ciDestinatarios[w].borrado == true)
				{
					tablaCiDestinatarios+='<tr id="ciVer-'+w+'"><td><del>---</del></td><td><del>'+ciDestinatarios[w].avance_id+'</del></td><td><del>'+ciDestinatarios[w].ci+'</del></td><td><del></del></td><td><del>'+ciDestinatarios[w].nombre+'</del></td><td><del>'+ciDestinatarios[w].apellido+'</del></td><td><del>'+ciDestinatarios[w].sexo+'</del></td><td><del>'+ciDestinatarios[w].estado_civil+'</del></td><td><del>'+ciDestinatarios[w].nacionalidad+'</del></td><td><del>'+ciDestinatarios[w].profesion+'</del></td><td><del>'+ciDestinatarios[w].fecha_nacimiento+'</del></td><td><del>'+ciDestinatarios[w].anho+'</del></td><td><del>'+ciDestinatarios[w].version+'</del></td><td><del>'+ciDestinatarios[w].borrado+'</del></td><td><del>'+ciDestinatarios[w].fecha_actualizacion+'</del></td><td><del>'+ciDestinatarios[w].fecha_insercion+'</del></td><td><span class="glyphicon glyphicon-pencil registrosWs" codigoRegistroWs='+w+'></span></td><td><span class="glyphicon glyphicon-trash" id="iconoBorradoWs" parametrosBorradoWs='+ciDestinatarios[w].id+'-'+ciDestinatarios[w].borrado+'></span></td></tr>';
				}else{
					tablaCiDestinatarios+='<tr  id="ciVer-'+w+'"><td><span class="glyphicon glyphicon-check checkId" codigoRegistroWs='+w+' ci="'+ciDestinatarios[w].ci+'"></span></td><td class="destAvanceId">'+ciDestinatarios[w].avance_id+'</td><td class="destAvanceCi">'+ciDestinatarios[w].ci+'</td><td class="destAvancePobreza"></td><td class="destAvanceNombre">'+ciDestinatarios[w].nombre+'</td><td class="destAvanceApellido">'+ciDestinatarios[w].apellido+'</td><td class="destAvanceSexo">'+ciDestinatarios[w].sexo+'</td><td class="destAvanceEC">'+ciDestinatarios[w].estado_civil+'</td><td class="destAvanceNac">'+ciDestinatarios[w].nacionalidad+'</td><td class="destAvanceProf">'+ciDestinatarios[w].profesion+'</td><td class="destAvanceFN">'+ciDestinatarios[w].fecha_nacimiento+'</td><td class="destAvanceAnho">'+ciDestinatarios[w].anho+'</td><td class="destAvanceFA">'+ciDestinatarios[w].fecha_actualizacion+'</td><td class="destAvanceFI">'+ciDestinatarios[w].fecha_insercion+'</td><td><span class="glyphicon glyphicon-pencil registrosWs" codigoRegistroWs='+w+'></span></td><td><span class="glyphicon glyphicon-trash" id="iconoBorradoWs" parametrosBorradoWs='+ciDestinatarios[w].id+'-'+ciDestinatarios[w].borrado+'></span></td></tr>';
				}
			}
			tablaCiDestinatarios +='	</tbody>'+
			'	                	</table>'+
			'	              	</div>'	;		
			
			$('.box-body').html(tablaCiDestinatarios);
			$("#dataTablesCiDestinatarios").DataTable();
		}
		
		renderCiDestinatarios();
		
		function verificarCedulasBeneficiarios(thisObj){
			if (thisObj.css("color") != "green"){
				var selector = thisObj.parent().parent();
				var ci = thisObj.attr("ci");
				var id = thisObj.parent().parent().attr("id");
				var verificadas = $.ajax({
					url:"http://identificaciones.stp.gov.py/identificaciones/?ci="+ci,
				  	type:'get',
				  	dataType:'json',
				  	async:false       
				}).responseText;		
				verificadas=JSON.parse(verificadas);
				var pobreza = $.ajax({
					url:"http://geo.stp.gov.py/user/stp/api/v2/sql?q=SELECT%20estado_de_pobreza%20FROM%20poblacion_con_localizacion_y_situacion_de_pobreza_090816_actual%20where%20nro_cedula%20=%20%27"+ci+"%27&api_key=161ee21f2cc06f29ecbc1f1d29e7886bc85be12a",
				  	type:'get',
				  	dataType:'json',
				  	async:false       
				}).responseText;		
				pobreza=JSON.parse(pobreza);
				pobreza=pobreza.rows[0];
				
				thisObj.parent().parent().find(".destAvancePobreza").html(pobreza.estado_de_pobreza);
				thisObj.parent().parent().find(".destAvanceNombre").html(verificadas.nombre);
				thisObj.parent().parent().find(".destAvanceApellido").html(verificadas.apellidos);
				thisObj.parent().parent().find(".destAvanceSexo").html(verificadas.sexo);
				thisObj.parent().parent().find(".destAvanceEC").html(verificadas.estado_civil);
				thisObj.parent().parent().find(".destAvanceNac").html(verificadas.nacionalidad);
				thisObj.parent().parent().find(".destAvanceProf").html(verificadas.profesion);
				thisObj.parent().parent().find(".destAvanceFN").html(verificadas.fecha_naciemiento);
				thisObj.css("color","green");
			}
		}
		
		$("body").on("click", ".checkId, .selectAll" ,function(event){
			if ($(this).hasClass("selectAll")){
				$( ".checkId" ).each(function(i){	
					verificarCedulasBeneficiarios($(this));
				});
				$(this).css("color","green");
			}else{
				verificarCedulasBeneficiarios($(this));
			}
		});
		
		
		/*
		var ws = $.ajax({
			url:'/tablero/ajaxSelects2?action=getWs',
		  	type:'get',
		  	dataType:'json',
		  	async:false       
		}).responseText;		
		ws=JSON.parse(ws);
		
		renderWs();
		function renderWs(){
			
			$('.box-body').html('');
			var tablaWs="";
			tablaWs = '<table class="table table-hover">'+
						  '<tr class="active"><td colspan="12">Tabla Ws</td><td><a href="#" data-toggle="modal" data-target="#ws"><span class="glyphicon glyphicon-plus"></span></a></td></tr>'+
						  '<tr class="active"><td>Id</td><td>Nombre</td><td>Descripción</td><td>Url</td><td>Metodo</td><td>Usuario</td><td>Pass</td><td>idClave</td><td>idValor</td><td>wsTipoId</td><td>borrado</td><td>Editar</td><td>Borrar</td></tr>';
			for(var w=0; w<ws.length;w++)
			{
				if(ws[w].borrado == true)
				{
					tablaWs+='<tr><td><del>'+ws[w].id+'</del></td><td><del>'+ws[w].nombre+'</del></td><td><del>'+ws[w].descripcion+'</del></td><td><del>'+ws[w].url+'</del></td><td><del>'+ws[w].metodo+'</del></td><td><del>'+ws[w].usuario+'</del></td><td><del>'+ws[w].pass+'</del></td><td><del>'+ws[w].idClave+'</del></td><td><del>'+ws[w].idValor+'</del></td><td><del>'+ws[w].wsTipoId+'</del></td><td><del>'+ws[w].borrado+'</del></td><td><span class="glyphicon glyphicon-pencil registrosWs" codigoRegistroWs='+w+'></span></td><td><span class="glyphicon glyphicon-trash" id="iconoBorradoWs" parametrosBorradoWs='+ws[w].id+'-'+ws[w].borrado+'></span></td></tr>';
				}else{
					tablaWs+='<tr><td>'+ws[w].id+'</td><td>'+ws[w].nombre+'</td><td>'+ws[w].descripcion+'</td><td>'+ws[w].url+'</td><td>'+ws[w].metodo+'</td><td>'+ws[w].usuario+'</td><td>'+ws[w].pass+'</td><td>'+ws[w].idClave+'</td><td>'+ws[w].idValor+'</td><td>'+ws[w].wsTipoId+'</td><td>'+ws[w].borrado+'</td><td><span class="glyphicon glyphicon-pencil registrosWs" codigoRegistroWs='+w+'></span></td><td><span class="glyphicon glyphicon-trash" id="iconoBorradoWs" parametrosBorradoWs='+ws[w].id+'-'+ws[w].borrado+'></span></td></tr>';
				}
			}
			tablaWs +='</table>';				
			
			$('.box-body').html(tablaWs);
		}
		 
		$("body").on("click", ".registrosWs",function(event){
			var codigoRegistro = $(this).attr("codigoRegistroWs");
				
			$("#borradoWs").remove();
			$("#guardarWs").remove();
			$('#ws').modal('show');
			$("#ws").find(".form-horizontal").append('<div class="form-group" id="borradoWs"><div class="col-lg-offset-2 col-lg-10"><button type="submit" class="btn btn-success" id="actualizarWs">Actualizar</button></div></div>');
			$("#idWs").val(ws[codigoRegistro].id);
			$("#nombreWs").val(ws[codigoRegistro].nombre);
			$("#descripcionWs").val(ws[codigoRegistro].descripcion);
			$("#urlWs").val(ws[codigoRegistro].url);
			$("#metodoWs").val(ws[codigoRegistro].metodo);
			$("#usuarioWs").val(ws[codigoRegistro].usuario);
			$("#passWs").val(ws[codigoRegistro].pass);
			$("#idClaveWs").val(ws[codigoRegistro].idClave);
			$("#idValorWs").val(ws[codigoRegistro].idValor);
			$("#wsTipoIdWs").val(ws[codigoRegistro].wsTipoId);
		});
		*/
	});
</script>
	
    <div class="wrapper">

      <header class="main-header">
		  <%@ include file="/frames/mainheader.jsp" %>
      </header>
      <!-- Left side column. contains the logo and sidebar -->
      <aside class="main-sidebar">
  			 <%@ include file="/frames/main-sidebar.jsp" %>
      </aside>

      <!-- Content Wrapper. Contains page content -->
      <div class="content-wrapper">
      
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
            <small>
            <!--  Titulo, donde antes estaba dashboard -->
            </small>
          </h1>
         
        </section>

        <!-- Main content -->
        <section class="content" id="programacion">
	          <!-- Info row de buscador de productos -->
	          <div class="row">
	         <div class="col-md-12">
	          <div class="box" height="1000px">
	            <div class="box-header with-border" height="1000px">
	              <h2 class="box-title text-center" id="tituloTipoPrograma">
	                Editar Registros	
	              </h2>
	              <div class="box-tools pull-right" height="1000px"><button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	              </div>
	            </div>
	            <div class="box-body table-responsive" style="scroll-x:hidden;scroll-y:auto;">
	            

	            </div>
			   </div>
			</div>
          </div><!-- /.row -->
            
    </section><!-- /.content -->
      </div><!-- /.content-wrapper -->

      <footer class="main-footer">
        <div class="pull-right hidden-xs">
          <b>Version</b> 2.0
        </div>
        <strong>Copyright &copy; 2015 <a href="http://www.stp.gov.py">STP</a>.</strong> All rights reserved.
      </footer>

      <!-- Control Sidebar -->
      <aside class="control-sidebar control-sidebar-light">
		<!-- include file="/frames/control-sidebar.jsp"  -->
      </aside><!-- /.control-sidebar -->
      <!-- Add the sidebar's background. This div must be placed
           immediately after the control sidebar -->
      <div class='control-sidebar-bg'></div>

    </div><!-- ./wrapper -->

    <!-- jQuery 2.1.3 
    <script src="plugins/jQuery/jQuery-2.1.3.min.js"></script> -->
    <!-- Bootstrap 3.3.2 JS -->
    <script src="bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <!-- DATA TABES SCRIPT -->
    <script src="plugins/datatables/jquery.dataTables.js" type="text/javascript"></script>
    <script src="plugins/datatables/dataTables.bootstrap.js" type="text/javascript"></script>
    <!-- FastClick -->
    <script src='plugins/fastclick/fastclick.min.js'></script>
    <!-- AdminLTE App -->
    <script src="dist/js/app.min.js" type="text/javascript"></script>
    <!-- Sparkline -->
    <script src="plugins/sparkline/jquery.sparkline.min.js" type="text/javascript"></script>
    <!-- jvectormap -->
    <script src="plugins/jvectormap/jquery-jvectormap-1.2.2.min.js" type="text/javascript"></script>
    <script src="plugins/jvectormap/jquery-jvectormap-world-mill-en.js" type="text/javascript"></script>
    <!-- daterangepicker -->
    <script src="plugins/daterangepicker/daterangepicker.js" type="text/javascript"></script>
    <!-- datepicker -->
    <script src="plugins/datepicker/bootstrap-datepicker.js" type="text/javascript"></script>
    <!-- SlimScroll 1.3.0 -->
    <script src="plugins/slimScroll/jquery.slimscroll.min.js" type="text/javascript"></script>
    <!-- ChartJS 1.0.1 -->
    <script src="plugins/chartjs/Chart.min.js" type="text/javascript"></script>
     <!-- AdminLTE App -->
    <script src="dist/js/app.min.js" type="text/javascript"></script>   
    
    
    <!-- AdminLTE dashboard demo (This is only for demo purposes) 
    <script src="dist/js/pages/dashboard2.js" type="text/javascript"></script>-->

    <!-- Librerias para la rutina de cambio de contraseña -->
    <script src="dist/js/jquerymd5.js" type="text/javascript"></script>    	
    <%@ include file="/frames/pass.jsp" %>
    <%@ include file="/frames/ws.jsp" %>
    <!-- AdminLTE for demo purposes -->
    <script src="dist/js/demo.js" type="text/javascript"></script>
        <%  } else { %>
				est<p>Favor Iniciar Sesion</p>
			<% } %> 

<!-- Piwik -->
<script type="text/javascript">

  var _paq = _paq || [];
  _paq.push(['trackPageView']);
  _paq.push(['enableLinkTracking']);
  (function() {
    var u="//infra.stp.gov.py/monitoreoweb/";
    _paq.push(['setTrackerUrl', u+'piwik.php']);
    _paq.push(['setSiteId', 9]);
    var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];
    g.type='text/javascript'; g.async=true; g.defer=true; g.src=u+'piwik.js'; s.parentNode.insertBefore(g,s);
  })();
</script>
<noscript><p><img src="//infra.stp.gov.py/monitoreoweb/piwik.php?idsite=9" style="border:0;" alt="" /></p></noscript>
<!-- End Piwik Code -->
<script type="text/javascript" src="bootstrap/js/bootstrap-slider.js"></script>
 
    
  </body>
</html>
