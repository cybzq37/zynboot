package com.zynboot.app.controller;

import com.zynboot.infra.es.EsClient;
import com.zynboot.infra.web.version.ApiVersion;
import com.zynboot.kit.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@ApiVersion("1")
@RequestMapping("/demo/es")
public class EsDemoController {

    private EsClient esClient;

    @Autowired(required = false)
    public void setEsClient(EsClient ec) { this.esClient = ec; }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        if (esClient == null) return ApiResponse.fail("ES not configured");
        return ApiResponse.ok("ES OK - client connected");
    }
}
