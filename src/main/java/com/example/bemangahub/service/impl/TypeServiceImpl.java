package com.example.bemangahub.service.impl;

import com.example.bemangahub.entity.Type;
import com.example.bemangahub.repository.TypeRepository;
import com.example.bemangahub.service.ITypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TypeServiceImpl implements ITypeService {

    @Autowired
    private TypeRepository typeRepository;

    @Override
    public Boolean isTypeExist(String name) {
        Optional<Type> optionalType = typeRepository.findByName(name);
        if (optionalType.isPresent()) {
            return true;
        } else {
            return false;
        }
    }
}
