package com.university.service;

public enum RegistrationResult {
    SUCCESS("Kayıt başarılı"),
    PREREQUISITE_NOT_MET("Ön koşul dersi tamamlanmamış"),
    TIME_CONFLICT("Ders saati çakışması var"),
    CAPACITY_FULL("Kontenjan dolu"),
    ALREADY_ENROLLED("Bu derse zaten kayıtlısınız"),
    ADDED_TO_WAITLIST("Bekleme listesine eklendiniz"),
    INVALID_SECTION("Geçersiz ders bölümü"),
    STUDENT_NOT_FOUND("Öğrenci bulunamadı"),
    CREDIT_LIMIT_EXCEEDED("Dönemlik kredi limiti (30 AKTS) aşıldı"),
    ERROR("Bir hata oluştu");

    private final String message;

    RegistrationResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}