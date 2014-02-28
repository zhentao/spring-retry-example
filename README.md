An example to show how to use Spring Retry

Run:

mvn exec:java -Dexec.mainClass="com.zhentao.retry.RetryRunner" \
	-Druntime.properties=src/main/conf/retry.properties \
	-Dlogback.configurationFile=src/main/conf/logback.xml