<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
		<title>Northwind Shipping</title>
		<meta name="generator" content="Amaya, see http://www.w3.org/Amaya/" />
	</head>

	<body>
		<table border="0"
			style="width: 80%;border-collapse: collapse;table-layout: fixed">
			<tr>
				<td valign="top">
					<table border="1"
						style="width:90%;background-color:#93D2FF;border-collapse: collapse">
						<tr>
							<td style="text-align:center">
								<h3>Dock Door</h3>
							</td>
						</tr>
						<tr>
							<td>
								<strong>EPC</strong>
							</td>
						</tr>
						<c:forEach items="${model.dockdoor}" var="tag">
							<tr>
								<td style="font-family:monospace;">${tag}</td>
							</tr>
						</c:forEach>
					</table>
				</td>
				<td valign="top">
					<table border="1"
						style="width:90%;background-color:#93D2FF;border-collapse: collapse;float:right">
						<tr>
							<td colspan="4" style="text-align:center">
								<h3>Weigh Station</h3>
							</td>
						</tr>
						<tr>
							<td>
								<strong>EPC</strong>
							</td>
						</tr>
						<c:forEach items="${model.weighstation}" var="tag">
							<tr>
								<td style="font-family:monospace">${tag}</td>
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<p />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table border="1"
						style="width:100%;background-color:#FFA69C;border-collapse: collapse">
						<tr>
							<td>
								<h3 style="text-align:center">Alerts</h3>
							</td>
						</tr>
						<c:forEach items="${model.alerts}" var="alert">
							<tr>
								<td> Tag:<span style="font-family:monospace">${alert.tag_Id}</span>: ${alert.message}
								</td>
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>