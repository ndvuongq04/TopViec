package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ExampleRequest;
import com.topviec.topviec_be.dto.response.ExampleResponse;
import com.topviec.topviec_be.entity.ExampleEntity;
import com.topviec.topviec_be.mapper.ExampleMapper;
import com.topviec.topviec_be.repository.ExampleRepository;
import com.topviec.topviec_be.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExampleServiceImpl implements ExampleService {
    @Override
    public ExampleResponse findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public ExampleResponse create(ExampleRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

}
