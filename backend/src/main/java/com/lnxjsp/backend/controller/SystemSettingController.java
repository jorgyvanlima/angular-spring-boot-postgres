package com.lnxjsp.backend.controller;

import com.lnxjsp.backend.model.SystemSetting;
import com.lnxjsp.backend.service.SystemSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "Configurações do Sistema", description = "Endpoints CRUD para gerenciar parâmetros globais")
public class SystemSettingController {

    @Autowired
    private SystemSettingService service;

    @GetMapping
    @Operation(summary = "Obtém todas as configurações cadastradas")
    public ResponseEntity<List<SystemSetting>> getAllSettings() {
        return ResponseEntity.ok(service.getAllSettings());
    }

    @GetMapping("/{key}")
    @Operation(summary = "Busca uma configuração específica pela sua chave única")
    public ResponseEntity<SystemSetting> getSettingByKey(@PathVariable String key) {
        return ResponseEntity.ok(service.getSettingByKey(key));
    }

    @PostMapping
    @Operation(summary = "Cria ou atualiza uma configuração (se a chave já existir, atualiza)")
    public ResponseEntity<SystemSetting> saveOrUpdateSetting(@RequestBody SystemSetting setting) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveOrUpdateSetting(setting));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma configuração pelo seu ID")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        service.deleteSetting(id);
        return ResponseEntity.noContent().build();
    }
}
