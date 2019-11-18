<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
	<base href="<%=basePath%>">
	<!-- 下拉框 -->
	<link rel="stylesheet" href="static/ace/css/chosen.css" />
	<!-- jsp文件头和头部 -->
	<%@ include file="../../system/index/top.jsp"%>
	<!-- 日期框 -->
	<link rel="stylesheet" href="static/ace/css/datepicker.css" />
</head>
<body class="no-skin">
<!-- /section:basics/navbar.layout -->
<div class="main-container" id="main-container">
	<!-- /section:basics/sidebar -->
	<div class="main-content">
		<div class="main-content-inner">
			<div class="page-content">
				<div class="row">
					<div class="col-xs-12">
					
					<form action="concruning/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:120px;text-align: center;padding-top: 13px;">程序代码</td>
								<td>
								<select class="chosen-select form-control" name="CONC_CODE" id="CONC_CODE"   data-placeholder="请选择并发程序" style="vertical-align:top;" style="width:98%;" >
								<option value=""></option>
								<c:forEach items="${concList}" var="var" varStatus="vs">
									<option value="${var.CONC_CODE }">${var.CONC_DESCRIPTION }</option>
								</c:forEach>
								</select>
								</td>
							</tr>
						</table>
						<table id="table_param" class="table table-striped table-bordered table-hover">
							<tr>
								<th style="width:120px;text-align: center;padding-top: 13px;">参数名称</th>
								<th style="text-align: center;padding-top: 13px;">参数值</th>
							</tr>
						</table>
						<table id="table_submit" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="text-align: center;" colspan="10">
									<a class="btn btn-mini btn-primary" onclick="save();">提交</a>
									<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
								</td>
							</tr>
						</table>
						</div>
						<div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><br/><img src="static/images/jiazai.gif" /><br/><h4 class="lighter block green">提交中...</h4></div>
					</form>
					</div>
					<!-- /.col -->
				</div>
				<!-- /.row -->
			</div>
			<!-- /.page-content -->
		</div>
	</div>
	<!-- /.main-content -->
</div>
<!-- /.main-container -->


	<!-- 页面底部js¨ -->
	<%@ include file="../../system/index/foot.jsp"%>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<script type="text/javascript">
		
		$(function() {
			
			//下拉框
			if(!ace.vars['touch']) {
				$('.chosen-select').chosen({allow_single_deselect:true}); 
				$(window)
				.off('resize.chosen')
				.on('resize.chosen', function() {
					$('.chosen-select').each(function() {
						 var $this = $(this);
						 $this.next().css({'width': $this.parent().width()});
					});
				}).trigger('resize.chosen');
				$(document).on('settings.ace.chosen', function(e, event_name, event_val) {
					if(event_name != 'sidebar_collapsed') return;
					$('.chosen-select').each(function() {
						 var $this = $(this);
						 $this.next().css({'width': $this.parent().width()});
					});
				});
				$('#chosen-multiple-style .btn').on('click', function(e){
					var target = $(this).find('input[type=radio]');
					var which = parseInt(target.val());
					if(which == 2) $('#form-field-select-4').addClass('tag-input-style');
					 else $('#form-field-select-4').removeClass('tag-input-style');
				});
			}
		
		});
		
		$(top.hangge());		
		$(document).ready(function(){
			$('#CONC_CODE').change(function(){
				var concCode = $(this).children('option:selected').val();//这就是selected的值
				if(concCode != null){
					$.ajax({
						type: "POST",
						url: '<%=basePath%>concruning/getConcParam.do?CONC_CODE='+concCode,
						dataType:'json',
						cache: false,
						success: function(data){
							delTr('table_param');
							 $.each(data.list, function(i, list){
								 if(list.PARAM_CODE != null && list.PARAM_TYPE == 'String'){
									 var trHtml="<tr align='center'><td style='padding-top:13px;'>"+list.PARAM_NAME+"</td><td><input type='text' style='width:98%;' name="+list.SEQ+":"+list.NULL_FLAG+list.PARAM_NAME+" id="+list.PARAM_NAME+" value="+list.CONC_VALUE+"></input></td></tr>";
									 addTr(trHtml,'table_param', 0);
								 }else if(list.PARAM_CODE != null && list.PARAM_TYPE == 'Date'){
									 var trHtml="<tr align='center'><td style='padding-top:13px;'>"+list.PARAM_NAME+"</td><td><input class='span10 date-picker' type='text' data-date-format='yyyymmdd' readonly='readonly' style='width:98%;' name="+list.SEQ+":"+list.NULL_FLAG+list.PARAM_NAME+" id="+list.PARAM_NAME+" value="+list.CONC_VALUE+"></input></td></tr>";
									 addTr(trHtml,'table_param', 0);
									//日期框
									$('.date-picker').datepicker({
										autoclose: true,
										todayHighlight: true
									});
								 }
							 });
						}
					});
				}
				});
			});
		function addTr(trHtml,tab, row){
		    var $tr=$("#"+tab+" tr").eq(row);
		     if($tr.size()==0){
		        return;
		     }
		     $tr.after(trHtml);
		  }
		 function delTr(tab){
			 $("#"+tab+" tr").eq(0).nextAll().remove();
		 }
		//保存
		function save(){
			for(var f=0;f<document.forms.length;f++){  
			    var form=document.forms[f];  
			    if(form.name=="Form"){  
			        //遍历指定form表单所有元素  
			        for(var i=0;i<form.length;i++){  
			            var element=form[i];  
			            if(element.type=="text"){  
			            	if(element.name.indexOf(":N") > 0 && element.value == ""){
			            		$("#table_param").tips({
			    					side:3,
			    		            msg:'请输入参数:'+element.id,
			    		            bg:'#AE81FF',
			    		            time:2
			    		        });
			            		//alert(element.name+"不能为空!");
			            		return false;
			            	}
			            }  
			        }  
			        break;  
			    }  
			}  
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}
		
		$(function() {
			//日期框
			$('.date-picker').datepicker({autoclose: true,todayHighlight: true});
		});
		
		</script>
</body>
</html>