使用说明：
	1. HTTP请求转发器，可以把外网的请求转发到内网的服务上。
	2. 支持 Loadbalance and failover。
	3. 心跳检测，默认为round_robin，代理服务器需要提供心跳检测的接口。
	4. 在配置文件中添加代理服务器，浏览器界面中管理检测的启动或停止。
	5. 当请求的URL中包含login字符串时动态的选择代理服务器，其他请求需要包含token字段才能转发.
	   token格式为iwehfiengpp.server1,server1表示目标服务器的名字。

部署说明：
	1. 本项目用maven构建，需要安装Maven环境，构建成功后执行mvn package 命令将会在targer目录中生成war文件，然后将war文件
		部署到web服务器；
	2. 修改proxy_servers.xml文件，添加代理服务器；
	2. 启动项目；