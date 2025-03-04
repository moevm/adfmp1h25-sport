FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY . .

ENV FLASK_APP=flask_server.py
ENV PYTHONUNBUFFERED=1

CMD ["python", "main.py"]