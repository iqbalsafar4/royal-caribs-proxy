package com.royalcaribs.offshoreproxy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Enumeration;

@SpringBootApplication
@RestController
public class OffshoreProxyApplication {

	private final RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		SpringApplication.run(OffshoreProxyApplication.class, args);
	}

	@RequestMapping("/**")
	public ResponseEntity<byte[]> handleRequest(HttpServletRequest request) throws IOException {
		String targetUrl = request.getHeader("X-Target-Url");

		if (targetUrl == null || targetUrl.isEmpty()) {
			return ResponseEntity.badRequest()
					.body("X-Target-Url header is missing".getBytes());
		}

		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			if (!headerName.equalsIgnoreCase("X-Target-Url") &&
					!headerName.equalsIgnoreCase("Host")) {
				headers.add(headerName, request.getHeader(headerName));
			}
		}

		try {
			return restTemplate.exchange(
					targetUrl,
					HttpMethod.valueOf(request.getMethod()),
					new HttpEntity<>(request.getInputStream().readAllBytes(), headers),
					byte[].class
			);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(("Forwarding error: " + e.getMessage()).getBytes());
		}
	}
}
