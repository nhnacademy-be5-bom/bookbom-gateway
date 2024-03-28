package shop.bookbom.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix = "bookbom")
@RequiredArgsConstructor
@ConstructorBinding
public class RouteLocatorConfig {
    private final String shopUri;
    private final String frontUri;
    private final String authUri;
    private final String batchUri;
    private final String couponUri;

    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("bookbom-front",
                        p -> p.path(frontUri).and()
                                .uri("lb://BOOKBOM-FRONT")
                )
                .route("bookbom-shop",
                        p -> p.path(shopUri).and()
                                .uri("lb://BOOKBOM-SHOP")
                )
                .route("bookbom-auth",
                        p -> p.path(authUri).and()
                                .uri("lb://BOOKBOM-AUTH")
                )
                .route("bookbom-batch",
                        p -> p.path(batchUri).and()
                                .uri("lb://BOOKBOM-BATCH")
                )
                .route("bookbom-coupon",
                        p -> p.path(couponUri).and()
                                .uri("lb://BOOKBOM-COUPON")
                )
                .build();

//        return builder.routes()
//                .route("get_route", r -> r.path("/account")
//                        .filters(o->o.addRequestHeader("uuid", UUID.randomUUID().toString()))
//                        .uri("http://httpbin.org"))
//                .build();

    }
}
