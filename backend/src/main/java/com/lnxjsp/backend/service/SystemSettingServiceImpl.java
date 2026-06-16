package com.lnxjsp.backend.service;

import com.lnxjsp.backend.exception.ResourceNotFoundException;
import com.lnxjsp.backend.model.SystemSetting;
import com.lnxjsp.backend.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemSettingServiceImpl implements SystemSettingService {

    @Autowired
    private SystemSettingRepository repository;

    @Override
    public List<SystemSetting> getAllSettings() {
        return repository.findAll();
    }

    @Override
    public SystemSetting getSettingByKey(String key) {
        return repository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Configuração não encontrada para a chave: " + key));
    }

    @Override
    public SystemSetting saveOrUpdateSetting(SystemSetting setting) {
        // Se houver um registro com a mesma chave, atualiza o valor dele
        repository.findByKey(setting.getKey()).ifPresent(existing -> {
            setting.setId(existing.getId());
        });
        return repository.save(setting);
    }

    @Override
    public void deleteSetting(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Configuração não encontrada para o id: " + id);
        }
        repository.deleteById(id);
    }
}
