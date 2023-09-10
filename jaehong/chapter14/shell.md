```shell
javac -d classes hello/src/main/java/com/microsoft/hello/Hello.java hello/src/main/java/module-info.java 
```

```shell

jar --create --file lib/hello.jar --main-class com.microsoft.hello.Hello -C classes .

```

```shell
 java -cp lib/hello.jar Hello  
 ```