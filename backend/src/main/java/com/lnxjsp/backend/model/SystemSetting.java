package com.lnxjsp.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "system_settings")
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A chave de configuração é obrigatória")
    @Size(min = 3, max = 100, message = "A chave deve ter entre 3 e 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "A chave deve conter apenas letras, números, sublinhados, traços e pontos")
    @Column(name = "setting_key", unique = true, nullable = false)
    private String key;

    @NotBlank(message = "O valor da configuração é obrigatório")
    @Size(max = 2000, message = "O valor deve ter no máximo 2000 caracteres")
    @Column(name = "setting_value", nullable = false)
    private String value;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Column(name = "description")
    private String description;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
    @Pattern(regexp = "^[A-Z_]+$", message = "A categoria deve conter apenas letras maiúsculas e sublinhados")
    @Column(name = "category")
    private String category;

    // Constructors
    public SystemSetting() {}

    public SystemSetting(String key, String value, String description, String category) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
