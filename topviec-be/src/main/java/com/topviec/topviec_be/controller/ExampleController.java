package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.request.ExampleRequest;
import com.topviec.topviec_be.dto.response.ExampleResponse;
import com.topviec.topviec_be.service.ExampleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/example")
@RequiredArgsConstructor
public class ExampleController {

    private final ExampleService exampleService;

    @GetMapping("/{id}")
    public ExampleResponse getById(@PathVariable Long id) {
        return exampleService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExampleResponse create(@Valid @RequestBody ExampleRequest request) {
        return exampleService.create(request);
    }
}
