package ir.aut.ce.cloud.serverlessauthentication;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequestScope
@RequiredArgsConstructor
public class RequestContext {
    final private HttpServletRequest request;
    public String getRequestIp(){
        return request.getRemoteAddr();
    }
}
