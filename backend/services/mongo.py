from pymongo import MongoClient
from services.get_env import MONGO_USER, MONGO_PASSWORD, MONGO_HOST, MONGO_PORT

connection_string = f"mongodb://{MONGO_USER}:{MONGO_PASSWORD}@{MONGO_HOST}:{MONGO_PORT}"
MONGO_CLIENT = MongoClient(connection_string)

USERS_ENTRY_CL = MONGO_CLIENT["Users"]["Entry"]
USERS_PREDICTS = MONGO_CLIENT["Users"]["Predicts"]
USERS_FOLLOWERS = MONGO_CLIENT["Users"]["Followers"]
USERS_STATS = MONGO_CLIENT["Users"]["Stats"]
