# schla-webmvc

## What's that

`schla-webmvc` is a web MVC framework, which can inject services and components to controllers automatically.

The obvious merit is `schla-webmvc` is based on Netty that is an asynchronous event-driven network application framework.

## Quick Start

The HelloWorld sample is very easy. The same to `SpringBoot`, you just call the `run ` method of `SchlaWebmvcApplication `class then you can start an HTTP application.

```java
public class DemoApplication {
    public static void main(String[] args) {
        SchlaWebmvcApplication.run(DemoApplication.class);
    }
}
```

You can define an API by `Cotroller`  annotation, you do as follows：

```java
@Controller
public class UserController {
    
    @GET("/users")
    public String main(HttpRequest request, HttpResponse response) {
        return request.getUri();
    }
}
```

what's more, `schla-webmvc` don't like `SpringBoot`, it can define RESTful API by `RestController` annotation and the name of method will be defined as the request method：

```java
@RestController(value = "/posts", contentType = ContentType.JSON)
public class PostRestController {
    
    public String get() {
        return "PostRestController::get";
    }
    
    public String post(@RequestBody UserDTO userDTO) {
        return "PostRestController::post";
    }
    
    public String put(@RequestBody UserDTO userDTO) {
        return "PostRestController::put";
    }

    public String delete(@RequestBody UserDTO userDTO) {
        return "PostRestController::delete";
    }
}
```

## Package

If you want to package your project to jar, you must add the maven plugin as follows in your `pom.xml`:

```xml
<build>
    <plugins>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <filters>
                            <filter>
                                <artifact>*:*</artifact>
                                <excludes>
                                    <exclude>META-INF/*.SF</exclude>
                                    <exclude>META-INF/*.DSA</exclude>
                                    <exclude>META-INF/*.RSA</exclude>
                                </excludes>
                            </filter>
                        </filters>
                        <transformers>
                            <transformer
                                    implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>change your main class</mainClass>
                            </transformer>
                        </transformers>
                        <artifactSet>

                        </artifactSet>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Features

- Asynchronous HTTP request;
- Auto-configure `Spring `with `mybatis`;
- Supports `webSocket` protocol;
- Depends on Spring;
- Contains Redis & Mongo component.

## contact me

if you want to ask some questions or you want join me, you can contact with me by Email：

- pushy.zhengzuqin@gmail.com
- 1437876073@qq.com

## License

```
MIT License

Copyright (c) 2019 Pushy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```

