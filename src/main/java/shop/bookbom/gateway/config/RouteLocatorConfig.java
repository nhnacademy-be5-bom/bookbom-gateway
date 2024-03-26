package shop.bookbom.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookbom")
@ConfigurationPropertiesScan
public class RouteLocatorConfig {
    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder) {

//        return builder.routes()
//                .route("shop",
//                        p -> p.path(shopUrlPattern).and()
//                                .uri("lb://BOOKBOM-SHOP")
//                )
//                .build();

        // eureka 프로젝트 실행한 뒤 front, shop, gateway 을 유레카 인스턴스로 등록(실행) 한 뒤
        // localhost:8080/index.html 으로 호출한 경우 가중치에 맞게 리다이렉트 됨을 확인했습니다
        return builder.routes()
                .route("gateway-test-shop",
                        p -> p.path("/index.html").and().weight("index", 70).uri("http://localhost:8090/")
                )
                .route("gateway-test-front",
                        p -> p.path("/index.html").and().weight("index", 30).uri("http://localhost:8091/")
                )
                .build();

//        return builder.routes()
//                .route("get_route", r -> r.path("/account")
//                        .filters(o->o.addRequestHeader("uuid", UUID.randomUUID().toString()))
//                        .uri("http://httpbin.org"))
//                .build();

        //http://httpbin.org/get

    }
}
