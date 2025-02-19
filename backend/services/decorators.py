import json
from functools import wraps
from flask import request
from services.get_env import JWT_KEY
import jwt


def token_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None
        if 'Authorization' in request.headers:
            token = request.headers['Authorization'].replace('Bearer ', '')
        if not token:
            return {'message': 'token is missing'}, 401
        try:
            jwt.decode(token, JWT_KEY, algorithms=["HS256"])
        except:
            return {'message': 'token is invalid'}, 401
        return f(*args, **kwargs)
    return decorator


def get_user_id(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = request.headers['Authorization'].replace('Bearer ', '')
        data = jwt.decode(token, JWT_KEY, algorithms=["HS256"])
        sub_data = json.loads(data['sub'].replace("'", '"'))
        return f(sub_data['id'], *args, **kwargs)
    return decorator
