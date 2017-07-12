<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>


<body>
	<div  style="padding-left: 30px;font-size: 20px;padding-top:10px;">去重结果文件解析到数据库</div><br>
    <div style="padding-left: 30px;font-size: 20px;padding-top:10px;">
          这里没有使用xml的解析，而是直接使用字符串的解析。因为在云平台在去重操作的时候，是去掉了原XML文件的第1，2和最后一行，所以xml文件是不完整的，不能使用xml解析。所以这里直接读取文件，然后进行字符串的解析。
			<table>
				<tr>
					<td><label for="name">输入路径:</label></td>
					<td><input class="easyui-validatebox" type="text"
						id="resolveFileId" data-options="required:true"  style="width:300px"
						value="WEB-INF/classes/deduplicate_users"/>
					</td>
					
				</tr>
				<tr>
					<td></td>
					<td>
					<a id="resolveId" href="" class="easyui-linkbutton" data-options="iconCls:'icon-door_in'">解析入库</a>
					</td>
				</tr>
				
			</table>
		</div>
	<script type="text/javascript" src="js/preprocess.js"></script>
</body>

