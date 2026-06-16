package com.lnxjsp.backend.service;

import com.lnxjsp.backend.model.SystemSetting;

import java.util.List;

public interface SystemSettingService {
    List<SystemSetting> getAllSettings();
    SystemSetting getSettingByKey(String key);
    SystemSetting saveOrUpdateSetting(SystemSetting setting);
    void deleteSetting(Long id);
}
