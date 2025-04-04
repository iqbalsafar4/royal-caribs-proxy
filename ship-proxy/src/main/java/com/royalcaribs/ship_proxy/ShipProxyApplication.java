package com.royalcaribs.ship_proxy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@RestController
public class ShipProxyApplication {

	@Value("${offshore.proxy.url}")
	private String offshoreServer;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {
		SpringApplication.run(ShipProxyApplication.class, args);
	}

	@RequestMapping("/**")
	public DeferredResult<ResponseEntity<byte[]>> handleRequest(HttpServletRequest request) throws IOException {
		DeferredResult<ResponseEntity<byte[]>> deferredResult = new DeferredResult<>();

		executor.submit(() -> {
			try {
				String targetUrl = extractTargetUrl(request);
				if (targetUrl == null) {
					deferredResult.setResult(ResponseEntity.badRequest()
							.body("Could not determine target URL. Use either:\n1. curl -x http://localhost:8080 http://example.com\n2. curl http://localhost:8080 -H 'X-Target-Url: http://example.com'".getBytes()));
					return;
				}

				HttpHeaders headers = new HttpHeaders();
				Enumeration<String> headerNames = request.getHeaderNames();
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					if (!headerName.equalsIgnoreCase("X-Target-Url")) {
						headers.add(headerName, request.getHeader(headerName));
					}
				}
				headers.add("X-Target-Url", targetUrl);

				ResponseEntity<byte[]> response = restTemplate.exchange(
						offshoreServer,
						HttpMethod.valueOf(request.getMethod()),
						new HttpEntity<>(request.getInputStream().readAllBytes(), headers),
						byte[].class
				);

				deferredResult.setResult(response);
			} catch (Exception e) {
				deferredResult.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(("Proxy error: " + e.getMessage()).getBytes()));
			}
		});

		return deferredResult;
	}

	private String extractTargetUrl(HttpServletRequest request) {
		String hostHeader = request.getHeader("Host");
		if (hostHeader != null && !hostHeader.contains("localhost:8080")) {
			return request.getScheme() + "://" + hostHeader + request.getRequestURI();
		}

		String headerUrl = request.getHeader("X-Target-Url");
		if (headerUrl != null) {
			return headerUrl;
		}

		String referer = request.getHeader("Referer");
		if (referer != null && referer.startsWith("http://localhost:8080")) {
			return referer.substring("http://localhost:8080/".length());
		}

		return null;
	}
}
