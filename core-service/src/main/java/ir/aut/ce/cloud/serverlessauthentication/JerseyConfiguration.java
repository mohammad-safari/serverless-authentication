package ir.aut.ce.cloud.serverlessauthentication;

import java.util.Set;
import java.util.stream.Collectors;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;

@Configuration
@ApplicationPath("/api")
public class JerseyConfiguration extends ResourceConfig {

    private static final String BASE_PACKAGE = "ir.aut.ce.cloud.serverlessauthentication.endpoints";

    @PostConstruct
    public void init() {
        packages(BASE_PACKAGE);
        springUtilizedClasspathComponentScan(BASE_PACKAGE);
    }

    @SuppressWarnings("unchecked")
    private void springUtilizedClasspathComponentScan(String basePackage) {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        
        Set.of(Provider.class, Path.class).stream()
                .map(AnnotationTypeFilter::new)
                .forEach(scanner::addIncludeFilter);
    
        var classes = scanner.findCandidateComponents(basePackage).stream()
                .map(beanDefinition -> ClassUtils.resolveClassName(
                        beanDefinition.getBeanClassName(),
                        getClassLoader()))
                .collect(Collectors.toSet());
        
        registerClasses((Set<Class<?>>) classes);
    }
}