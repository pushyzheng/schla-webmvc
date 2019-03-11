# schla-webmvc

## What's that

`schla-web MVC` is a web MVC framework, which can inject service and component to controller automatically.

The obvious merit is `schla-web MVC` is based on Netty that is an asynchronous event-driven network application framework.

## Quick Start

The HelloWorld sample is very easy. The same to SpringBoot, you just call the `run ` method of SchlaWebmvcApplication class then you can start an HTTP application.

```java
public class DemoApplication {
    public static void main(String[] args) {
        SchlaWebmvcApplication.run(DemoApplication.class);
    }
}
```
