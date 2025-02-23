import schedule
import time
from datetime import datetime
import logging

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('service.log'),
        logging.StreamHandler()
    ]
)


class DailyService:
    def __init__(self):
        self.logger = logging.getLogger(__name__)

    def run_daily_task(self):
        try:
            self.logger.info("Начало выполнения ежедневной задачи")

            # Здесь ваша логика
            self._process_task()

            self.logger.info("Задача успешно выполнена")
        except Exception as e:
            self.logger.error(f"Ошибка при выполнении задачи: {str(e)}")

    def _process_task(self):
        """
        Основная логика вашего сервиса
        """
        # Пример:
        self.logger.info("Выполняется обработка...")
        # Ваш код здесь


def main():
    service = DailyService()

    # Настройка расписания (например, запуск каждый день в 00:00)
    schedule.every().day.at("00:00").do(service.run_daily_task)

    logging.info("Сервис запущен")

    while True:
        schedule.run_pending()
        time.sleep(60)  # Проверка каждую минуту


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        logging.info("Сервис остановлен")