import os
from flask.cli import load_dotenv
load_dotenv('./.env')


def get_env(key: str) -> str:
    value = os.getenv(key)
    if value is None:
        print(f"ERROR: env variable isn't set {key}")
        exit(1)
    return value


PORT = get_env("PORT")
JWT_KEY = get_env("JWT_KEY")

ACCESS_TOKEN_EXPIRE = 30  # minutes
REFRESH_TOKEN_EXPIRE = 7  # days

MONGO_USER = get_env('MONGO_USER')
MONGO_PASSWORD = os.getenv('MONGO_PASSWORD')
MONGO_HOST = get_env('MONGO_HOST')
MONGO_PORT = get_env('MONGO_PORT')
KHL_URL = get_env('KHL_URL')
LAST_TIME = get_env('LAST_TIME')

