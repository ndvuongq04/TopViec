package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ExampleRequest;
import com.topviec.topviec_be.dto.response.ExampleResponse;

public interface ExampleService {

    ExampleResponse findById(Long id);

    ExampleResponse create(ExampleRequest request);
}
