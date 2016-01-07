By default `WebLogic` server will load their jars first before the ones from `myschedule.war/WEB-INF/lib`. However the one comes with their server is outdated for `commons-io`, so to workaround this, you can add the following into `WEB-INF/weblogic.xml`
```
<?xml version="1.0" encoding="UTF-8"?>
<weblogic-web-app>
	<container-descriptor>
		<prefer-web-inf-classes>true</prefer-web-inf-classes>
	</container-descriptor>
</weblogic-web-app>
```

You will have to manually add this into the war or build it from source.