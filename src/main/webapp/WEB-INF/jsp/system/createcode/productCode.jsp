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
		<!-- jsp文件头和头部 -->
		<%@ include file="../index/top.jsp"%>
		
		<style type="text/css">
		#dialog-add,#dialog-message,#dialog-comment{width:100%; height:100%; position:fixed; top:0px; z-index:10000; display:none;}
		.commitopacity{position:absolute; width:100%; height:500px; background:#7f7f7f; filter:alpha(opacity=50); -moz-opacity:0.5; -khtml-opacity: 0.5; opacity: 0.5; top:0px; z-index:99999;}
		.commitbox{width:95%; padding-left:42px; padding-top:69px; position:absolute; top:0px; z-index:99999;}
		.commitbox_inner{width:96%; height:235px;  margin:6px auto; background:#efefef; border-radius:5px;}
		.commitbox_top{width:100%; height:233px; margin-bottom:10px; padding-top:10px; background:#FFF; border-radius:5px; box-shadow:1px 1px 3px #e8e8e8;}
		.commitbox_top textarea{width:95%; height:165px; display:block; margin:0px auto; border:0px;}
		.commitbox_cen{width:95%; height:40px; padding-top:10px;}
		.commitbox_cen div.left{float:left;background-size:15px; background-position:0px 3px; padding-left:18px; color:#f77500; font-size:16px; line-height:27px;}
		.commitbox_cen div.left img{width:30px;}
		.commitbox_cen div.right{float:right; margin-top:7px;}
		.commitbox_cen div.right span{cursor:pointer;}
		.commitbox_cen div.right span.save{border:solid 1px #c7c7c7; background:#6FB3E0; border-radius:3px; color:#FFF; padding:5px 10px;}
		.commitbox_cen div.right span.quxiao{border:solid 1px #f77400; background:#f77400; border-radius:3px; color:#FFF; padding:4px 9px;}
		</style>
		
	</head>
<body class="no-skin">
<!-- /section:basics/navbar.layout -->
<div class="main-container" id="main-container">
	<!-- /section:basics/sidebar -->
	<div class="main-content">
					<!-- 添加属性  -->
					<input type="hidden" name="hcdname" id="hcdname" value="" />
					<input type="hidden" name="msgIndex" id="msgIndex" value="" />
					<input type="hidden" name="dtype" id="dtype" value="String" />
					<input type="hidden" name="isQian" id="isQian" value="是" />
					<div id="dialog-add">
						<div class="commitopacity"></div>
					  	<div class="commitbox">
						  	<div class="commitbox_inner">
						        <div class="commitbox_top">
						        	<br/>
						        	<table>
						        		<tr height="42px;">
						        			<td style="padding-left: 16px;text-align: right;">属性名：</td><td><div class="nav-search"><input class="nav-search-input" style="width:210px;" name="dname" id="dname" type="text" value="" placeholder="首字母必须为字母或下划线" title="属性名" /></div></td>
						        			<td style="padding-left: 16px;text-align: right;">属性类型：</td>
						        			<td style="padding-bottom: 5px;">
						        				<label style="float:left;padding-left: 20px;"><input name="form-field-radiot" id="form-field-radio1" onclick="setType('String');" type="radio" value="icon-edit"><span class="lbl">String</span></label>
												<label style="float:left;padding-left: 20px;"><input name="form-field-radiot" id="form-field-radio3" onclick="setType('Date');" type="radio" value="icon-edit"><span class="lbl">Date</span></label>
												<label style="float:left;padding-left: 20px;"><input name="form-field-radiot" id="form-field-radio2" onclick="setType('Integer');" type="radio" value="icon-edit"><span class="lbl">Integer</span></label>
												<label style="float:left;padding-left: 20px;"><input name="form-field-radiot" id="form-field-radio33" onclick="setType('Double');" type="radio" value="icon-edit"><span class="lbl">Double</span></label>
											</td>
						        		</tr>
						        		<tr height="42px;">
						        			<td style="padding-left: 16px;text-align: right;">其备注：</td><td><div class="nav-search"><input class="nav-search-input" style="width:210px;" name="dbz" id="dbz" type="text" value="" placeholder="例如 name的备注为 '姓名'" title="备注"/></div></td>
						        			<td style="padding-left: 16px;text-align: right;">前台录入：</td>
						        			<td>
						        				<div style="float: left;padding-top: 3px;">
						        				<label style="float:left;padding-left: 20px;"><input name="form-field-radioq" id="form-field-radio4" onclick="isQian('是');" type="radio" value="icon-edit" checked="checked"><span class="lbl">是</span></label>
												<label style="float:left;padding-left: 20px;"><input name="form-field-radioq" id="form-field-radio5" onclick="isQian('否');" type="radio" value="icon-edit"><span class="lbl">否</span></label>
												</div>
												<div class="nav-search" style="float: right;padding-right: 5px;">
													长度：<input class="nav-search-input" style="width:66px;" name="flength" id="flength" type="number" value="" placeholder="长度" title="长度" />
														. <input class="nav-search-input" style="width:50px;" name="decimal" id="decimal" type="number" value="" placeholder="小数" title="类型为Double时有效" />
												</div>
											</td>
						        		</tr>
						        		<tr height="42px;">
						        			<td style="padding-left: 16px;text-align: right;">默认值：</td><td><div class="nav-search"><input class="nav-search-input" style="width:210px;" name="ddefault" id="ddefault" type="text" value="" disabled="disabled" placeholder="后台附加值时生效" title="默认值"/></div></td>
						        			<td style="padding-left: 16px;text-align: right;"></td>
						        			<td>
						        			<div class="commitbox_cen">
								                <div class="left" id="cityname"></div>
								                <div class="right" style="padding-right: 28px;"><span class="save" onClick="saveD()">保存</span>&nbsp;&nbsp;<span class="quxiao" onClick="cancel_pl()">取消</span></div>
								            </div>
						        			</td>
						        		</tr>
						        		<tr>
						        			<td style="padding-left: 16px;" colspan="100">
						        				<font color="red" style="font-weight: bold;">
						        					注意：<br/>
						        					  1. 请不要添加  XX_ID 的主键，系统自动生成一个32位无序不重复字符序列作为主键<br/>
						        					  2. 主键为  类名_ID 格式，所有字段的字母均用大写
						        				</font>
											</td>
						        		</tr>
						        	</table>
						        </div>
						  	</div>
					  	</div>
					</div>
				
					<form action="createCode/proCode.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="zindex" id="zindex" value="0">
						<input type="hidden" name="FIELDLIST" id="FIELDLIST" value="">
						<input type="hidden" name="faobject" id="faobject" value="">
						<div id="zhongxin">
						<table style="margin-top: 10px;">
							<tr>
								<td style="width:76px;text-align: right;">模块说明：</td>
								<td><div class="nav-search"><input class="nav-search-input" type="text" autocomplete="off" name="TITLE" id="TITLE" value="${pd.TITLE }" placeholder="这里输入模块说明内容" style="width:543px;" title="说明"/></div></td>
								<td style="width:76px;text-align: right;">模块类型：</td>
								<td>
									<select name="TYPE" id="TYPE" data-placeholder="请选择数据库"  style="vertical-align:top;width: 87px;" onchange="selectType(this.value)">
										<option value="single">单表</option>
										<option value="tree">树形</option>
										<option value="fathertable">主表</option>
										<option value="sontable">明细表</option>
									</select>
								</td>
							</tr>
						</table>
						<table style="margin-top: 5px;">
							<tr>
								<td style="width:76px;text-align: right;">上级包名：</td>
								<td colspan="1"><input type="text" autocomplete="off" name="packageName" id="packageName" value="${pd.PACKAGENAME }" placeholder="包名  (不要输入特殊字符,请用纯字母)" style="width:259px" title="包名称"/></td>
								<td>&nbsp;&nbsp;例如:com.ey.controller.<font color="red" style="font-weight: bold;">system</font>&nbsp;&nbsp;只输入红色部分</td>
								<td style="padding-left: 10px;">
									<select name="faobjectid" id="faobjectid" data-placeholder="请选择" disabled="disabled" style="vertical-align:top;width:150px;background-color: #F5F5F5;" onchange="selectFa(this.value)">
										<option value="">选择主表</option>
										<c:forEach items="${varList}" var="var">
										<option value="${var.CREATECODE_ID }">${var.OBJECTNAME}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
						</table>
						<table style="margin-top: 5px;">
							<tr>
								<td style="width:76px;text-align: right;">处理类名：</td>
								<td><input type="text" autocomplete="off" name="objectName" id="objectName" value="${pd.OBJECTNAME }" placeholder="这里输入处理类名称" style="width:200px" title="类名称"/></td>
								<td>&nbsp;&nbsp;<font color="red" style="font-weight: bold;">类名首字母必须为大写字母或下划线</font></td>
								<td style="width:76px;text-align: right;">表前缀：</td>
								<td><input type="text" autocomplete="off" name="tabletop" id="tabletop" value="${msg == 'add' ? 'TB_' : ''}${fn:split(pd.TABLENAME, ',')[0]}" placeholder="这里输入表前缀" style="width:156px" title="表前缀"/></td>
							</tr>
						</table>
						
						<table id="table_report" class="table table-striped table-bordered table-hover" style="margin-top: 5px;">
								
								<thead>
									<tr>
										<th class="center" style="width:50px;">序号</th>
										<th class="center">属性名</th>
										<th class="center">类型</th>
										<th class="center">长度</th>
										<th class="center" style="width:60px;">小数点</th>
										<th class="center">备注</th>
										<th class="center" style="width:79px;">前台录入</th>
										<th class="center">默认值</th>
										<th class="center" style="width:69px;">操作</th>
									</tr>
								</thead>
														
								<tbody id="fields"></tbody>
								
						</table>
						
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="text-align: center;" colspan="100">
									<a class="btn btn-app btn-success btn-xs" onclick="dialog_open();"><i class="ace-icon glyphicon glyphicon-plus"></i>新增</a>
									<a class="btn btn-app btn-success btn-xs" onclick="save();" id="productc"><i class="ace-icon fa fa-print bigger-160"></i>生成</a>
									<a class="btn btn-app btn-danger btn-xs" onclick="top.Dialog.close();"><i class="ace-icon fa fa-reply icon-only"></i>取消</a>
								</td>
							</tr>
						</table>
						</div>
						
						<div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><img src="static/images/jiazai.gif" /><br/><h4 class="lighter block green"><strong id="second_show">10秒</strong></h4></div>
						
					</form>

	</div>
	<!-- /.main-content -->
</div>
<!-- /.main-container -->	
	
		<!-- 页面底部js¨ -->
		<%@ include file="../index/foot.jsp"%>
		<!--提示框-->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<script src="static/js/myjs/productCode.js"></script>
		<script type="text/javascript">
		$(top.hangge());//关闭加载状态
		$(reductionFields());
		//修改时还原属性列表
		function reductionFields(){
			var msg = '${msg}';
			if('edit' == msg){
				var nowarField = '${pd.FIELDLIST}';
				var fieldarray = nowarField.split('scp'); // 每行分割符
				for(var i=0;i<fieldarray.length;i++){
					if(fieldarray[i] != ''){
					appendC(fieldarray[i]);
					arField[i] = fieldarray[i];
					}
				}
			}
		}
		</script>
	
</body>
</html>