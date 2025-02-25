package lab5.itmo.client.io.utility;

/**
 * Интерфейс для проверки валидности данных у классов
 */

public interface Validatable {
    /**
     * Возвращает true, если данные введены в правильном формате
     */
    void validate();
}
