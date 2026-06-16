-- Carga inicial de dados para a tabela de configurações do sistema
INSERT INTO system_settings (setting_key, setting_value, description, category) VALUES ('maintenance_mode', 'false', 'Indica se o sistema está em modo manutenção global', 'SEGURANCA');
INSERT INTO system_settings (setting_key, setting_value, description, category) VALUES ('api_max_requests', '1000', 'Quantidade máxima de requisições por IP por minuto', 'PERFORMANCE');
INSERT INTO system_settings (setting_key, setting_value, description, category) VALUES ('theme_default', 'dark', 'Tema padrão da interface administrativa', 'FRONTEND');
