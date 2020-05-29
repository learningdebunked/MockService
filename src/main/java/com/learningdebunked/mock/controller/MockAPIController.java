package com.learningdebunked.mock.controller;

import com.learningdebunked.mock.api.MockAPI;
import com.learningdebunked.mock.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Kapil
 * @project Mock Service
 */
@RestController
public class MockAPIController implements MockAPI {

    @Autowired
    private SearchService searchService;

    public String getGetResponse() {
        return searchService.extractTemplate(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
    }

    @Override
    public String getPostResponse() {
        return searchService.extractTemplate(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
    }

    @Override
    public String getPutResponse() {
        return searchService.extractTemplate(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
    }

    @Override
    public String getDelResponse() {
        return searchService.extractTemplate(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
    }
}
