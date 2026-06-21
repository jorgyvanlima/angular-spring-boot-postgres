package com.lnxjsp.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements Filter {

    // Limite máximo de requisições permitidas por minuto por IP único
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    
    // Armazenamento em memória concorrente para associar IPs às suas contagens
    private final Map<String, RequestCounter> ipRequestMap = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Aplica o filtro de taxa de requisição apenas nas rotas da API
        if (httpRequest.getRequestURI().startsWith("/api")) {
            String clientIp = getClientIp(httpRequest);
            long currentTime = System.currentTimeMillis();

            // Computa atomicamente a contagem para o IP dado
            RequestCounter counter = ipRequestMap.compute(clientIp, (key, value) -> {
                if (value == null || currentTime - value.resetTime > 60000) {
                    return new RequestCounter(new AtomicInteger(1), currentTime);
                } else {
                    value.count.incrementAndGet();
                    return value;
                }
            });

            // Se o limite foi ultrapassado, retorna 429 Too Many Requests
            if (counter.count.get() > MAX_REQUESTS_PER_MINUTE) {
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.getWriter().write("{\"status\": 429, \"error\": \"Too Many Requests\", \"message\": \"Taxa de requisição limite excedida. Aguarde antes de tentar novamente.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Extrai o IP real do cliente. Como o tráfego passa pelo Proxy Reverso (Nginx),
     * a requisição recebida pelo backend vem com o IP interno do contêiner Nginx.
     * Devemos ler o cabeçalho 'X-Forwarded-For' para obter o IP do usuário externo real.
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        // Retorna o primeiro IP da lista (o cliente original)
        return xfHeader.split(",")[0].trim();
    }

    private static class RequestCounter {
        final AtomicInteger count;
        final long resetTime;

        RequestCounter(AtomicInteger count, long resetTime) {
            this.count = count;
            this.resetTime = resetTime;
        }
    }
}
