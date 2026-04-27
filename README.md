# CourseWorkOOP

# Система расчёта квартплаты

Десктопное приложение для автоматизации расчёта квартплаты (Java + JavaFX).

## Возможности

- Ввод показаний счётчиков
- Расчёт начислений (по нормативам и по счётчикам)
- Учёт платежей
- Просмотр долга
- Формирование квитанции

## Запуск

```bash
git clone https://github.com/20AntonioBanderas03/CourseWorkOOP.git
cd CourseWorkOOP
./mvnw javafx:run
```
## Структура проекта
```text
src/main/java/rsatu/ru/cw/
├── RentApplication.java   # точка входа
├── RentController.java    # контроллер
├── AccountingService.java # логика расчёта
├── ReportingService.java  # квитанции
├── PersonalAccount.java   # лицевой счёт
├── Service.java           # абстрактная услуга
├── NormativeService.java  # услуга по нормативу
├── MeteredService.java    # услуга по счётчику
└── Meter.java             # обобщённый счётчик
```
## Технологии
Java 17
JavaFX 21
Maven
## Автор
@20AntonioBanderas03
