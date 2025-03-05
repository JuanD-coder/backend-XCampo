package com.rojas.dev.XCampo.dto;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DeliveryMamMatchDto {
    private Long id;
    private String locations;
    private String token;

    /**
     * funcion que retorna la ruta en una lista de veredas
     * @return lista de veredas
     */
    public List<String> getLocationsList(){
        if(locations == null || locations.isEmpty()){
            return List.of();
        }
        return Arrays.stream(locations.split(","))
                .collect(Collectors.toList());
    }
}
