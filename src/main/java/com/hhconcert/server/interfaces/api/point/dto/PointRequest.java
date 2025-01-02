package com.hhconcert.server.interfaces.api.point.dto;

public record PointRequest (
        String userId,
        Integer amount
){
}
