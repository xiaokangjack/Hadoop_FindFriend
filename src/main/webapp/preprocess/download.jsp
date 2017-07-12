<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>


<body>
	<div style="padding-left: 30px;font-size: 20px;padding-top:10px;">HDFS文件下载到本地</div>
	<br>
	<div style="padding-left: 30px;font-size: 20px;padding-top:10px;">
	我们需要把数据去重的结果数据拿到本地，以便后面的数据入库操作。
		<table>
			<tr>
				<td><label for="name">HDFS路径:</label>
				</td>
				<td><input class="easyui-validatebox" type="text"
					id="fromHDFSFileId" data-options="required:true"
					style="width:300px" value="/user/root/_filter/deduplicate" /></td>

			</tr>
			<tr>
				<td><label for="name">本地路径:</label>
				</td>
				<td><input class="easyui-validatebox" type="text"
					id="tolocalFileId" data-options="required:true" style="width:300px"
					value="WEB-INF/classes/deduplicate_users" /></td>

			</tr>
			<tr>
				<td></td>
				<td><a id="downloadId" href="" class="easyui-linkbutton"
					data-options="iconCls:'icon-page_white_put'">下载</a></td>
			</tr>

		</table>
	</div>
	<script type="text/javascript" src="js/preprocess.js"></script>
</body>

